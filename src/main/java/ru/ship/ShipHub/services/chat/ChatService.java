package ru.ship.ShipHub.services.chat;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ship.ShipHub.ChatServiceGrpc;
import ru.ship.ShipHub.ClaimMessageService;
import ru.ship.ShipHub.config.security.PersonDetails;
import ru.ship.ShipHub.models.entity.MessageEntity;
import ru.ship.ShipHub.repositories.ClaimRepository;
import ru.ship.ShipHub.repositories.MessageRepository;
import ru.ship.ShipHub.util.exceptions.BadRequestException;

import java.time.LocalDateTime;
import java.util.Map;

@GrpcService
@Service
public class ChatService extends ChatServiceGrpc.ChatServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final ClaimRepository claimRepository;
    private final MessageRepository messageRepository;
    // claimId -> (subscriptionId -> observer). subscriptionId is unique per connection.
    private final java.util.concurrent.ConcurrentMap<Long, java.util.concurrent.ConcurrentMap<String, StreamObserver<ClaimMessageService.Message>>> subscribers = new java.util.concurrent.ConcurrentHashMap<>();
    // recent messages cache to prevent duplicate saves/broadcasts (userId -> [content, timestamp])
    private final java.util.concurrent.ConcurrentHashMap<Long, RecentMessage> recentMessages = new java.util.concurrent.ConcurrentHashMap<>();

    private static class RecentMessage {
        final String content;
        final long ts;

        RecentMessage(String content, long ts) {
            this.content = content;
            this.ts = ts;
        }
    }

    public ChatService(ClaimRepository claimRepository, MessageRepository messageRepository) {
        this.claimRepository = claimRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public StreamObserver<ClaimMessageService.MessageRequest> chat(StreamObserver<ClaimMessageService.Message> responseObserver) {
        return new StreamObserver<>() {

            private Long userId;
            private Long claimId;
            private String subscriptionId;
            private boolean isSubscribed = false;

            @Override
            public void onNext(ClaimMessageService.MessageRequest request) {
                var userDetails = (PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                userId = userDetails.getPerson().getId();

                var claimOptional = claimRepository.findById(request.getClaimId());
                if (claimOptional.isEmpty()) {
                    onError(new BadRequestException("Заявка не найдена"));
                    return;
                }
                claimId = claimOptional.get().getId();

                if (request.getIsSubscribeRequest()) {
                    subscribe(userId, claimId, responseObserver);
                } else {
                    if (request.getContent().isBlank()) return;
                    var sender = userDetails.getPerson();
                    long now = System.currentTimeMillis();
                    var last = recentMessages.get(sender.getId());
                    if (last != null && last.content.equals(request.getContent()) && (now - last.ts) < 2000) {
                        log.info("Ignoring duplicate message from user {} for claim {}: '{}'", sender.getId(), claimId, request.getContent());
                        return;
                    }
                    var saved = messageRepository.save(new MessageEntity(
                            request.getContent(), LocalDateTime.now(), claimOptional.get(), sender
                    ));
                    recentMessages.put(sender.getId(), new RecentMessage(request.getContent(), now));
                    log.info("Saved chat message id={} claimId={} fromUser={}", saved.getId(), claimId, sender.getId());
                    var message = ClaimMessageService.Message.newBuilder()
                            .setContent(request.getContent())
                            .setSenderId(sender.getId())
                            .build();
                    broadcastMessage(message, claimId, subscriptionId);
                }
            }

            private void subscribe(Long userId, Long claimId, StreamObserver<ClaimMessageService.Message> responseObserver) {
                var subId = java.util.UUID.randomUUID().toString();
                this.subscriptionId = subId;
                var userMap = subscribers.computeIfAbsent(claimId, key -> new java.util.concurrent.ConcurrentHashMap<>());
                userMap.put(subId, responseObserver);
                isSubscribed = true;
                log.info("User subscribed: claimId={} userId={} subscriptionId={} subscribersNow={}", claimId, userId, subId, userMap.keySet());

                var success = ClaimMessageService.Message.newBuilder()
                    .setContent("Success")
                    .build();
                responseObserver.onNext(success);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("gRPC error for user {} claim {}: {}", userId, claimId, t.getMessage());
                cleanup();
            }

            @Override
            public void onCompleted() {
                cleanup();
            }

            private void cleanup() {
                if (claimId != null && subscriptionId != null) {
                    var map = subscribers.get(claimId);
                    if (map != null) {
                        map.remove(subscriptionId);
                        if (map.isEmpty()) {
                            subscribers.remove(claimId);
                        }
                    }
                }
            }
        };
    }

    private void broadcastMessage(ClaimMessageService.Message message, Long claimId, String originatingSubscriptionId){
        var chatSubscribers = subscribers.get(claimId);
        if (chatSubscribers == null) return;
        log.debug("Broadcasting message from sender={} to subscribers={}", message.getSenderId(), chatSubscribers.keySet());
        var toRemove = new java.util.ArrayList<String>();
        int attempted = 0;
        int sent = 0;
        for (Map.Entry<String, StreamObserver<ClaimMessageService.Message>> entry : chatSubscribers.entrySet()) {
            var subId = entry.getKey();
            if (subId.equals(originatingSubscriptionId)) continue;
            attempted++;
            try {
                entry.getValue().onNext(message);
                sent++;
                log.debug("Delivered message to subscription {} (claim {})", subId, claimId);
            } catch (Exception e) {
                log.warn("Failed to send message to subscription {}: {}. Marking for removal.", subId, e.getMessage());
                toRemove.add(subId);
            }
        }
        if (!toRemove.isEmpty()) {
            var map = subscribers.get(claimId);
            if (map != null) {
                for (String id : toRemove) {
                    map.remove(id);
                    log.info("Removed dead subscriber {} for claim {}", id, claimId);
                }
                if (map.isEmpty()) subscribers.remove(claimId);
            }
        }
        log.info("Broadcast complete for claim {}: attempted={}, delivered={}", claimId, attempted, sent);
    }

    private void badRequest(String error){ throw new BadRequestException(error); }
}
