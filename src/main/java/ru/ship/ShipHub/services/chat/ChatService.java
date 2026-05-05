package ru.ship.ShipHub.services.chat;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ship.ShipHub.ChatServiceGrpc;
import ru.ship.ShipHub.ClaimMessageService;
import ru.ship.ShipHub.repositories.ClaimRepository;
import ru.ship.ShipHub.util.exceptions.BadRequestException;

import java.util.HashMap;
import java.util.Map;

@GrpcService
@Service
public class ChatService extends ChatServiceGrpc.ChatServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final ClaimRepository claimRepository;
    private final HashMap<Long, HashMap<Long, StreamObserver<ClaimMessageService.Message>>> subscribers = new HashMap<>();

    public ChatService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Override
    public StreamObserver<ClaimMessageService.MessageRequest> chat(StreamObserver<ClaimMessageService.Message> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(ClaimMessageService.MessageRequest request) {
                if (request.getIsSubscribeRequest()){
                    var claimOptional = claimRepository.findById(request.getClaimId());
                    if (claimOptional.isEmpty()){
                        onError(new Throwable("Заявка с таким id не найдена"));
                        return;
                    }
                    var claim = claimOptional.get();
                    var subscribe = subscribers.get(claim.getId());
                    if (subscribe != null && subscribe.containsKey(request.getUserId())) {
                        onError(new Throwable("Пользователь уже подписан на этот чат"));
                    }
                    else {
                        var newSubscribe = new HashMap<Long, StreamObserver<ClaimMessageService.Message>>();
                        newSubscribe.put(request.getUserId(), responseObserver);
                        var currentSubscribe = subscribers.get(claim.getId());
                        if (currentSubscribe != null){
                            currentSubscribe.put(request.getUserId(), responseObserver);
                            subscribers.put(claim.getId(), currentSubscribe);
                        }else{
                            subscribers.put(claim.getId(), newSubscribe);
                        }
                        var response = ClaimMessageService.Message.newBuilder()
                                .setContent("Success")
                                .build();
                        responseObserver.onNext(response);
                    }
                }else {
                    if (request.getContent().isEmpty()) return;
                    var message = ClaimMessageService.Message.newBuilder()
                            .setContent(request.getContent())
                            .build();
                    broadcastMessage(message, request.getClaimId(), request.getUserId());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                var message = ClaimMessageService.Message.newBuilder()
                        .setContent("error: " + throwable.getLocalizedMessage())
                        .build();
                responseObserver.onNext(message);
            }

            @Override
            public void onCompleted() {

            }
        };
    }

    private void broadcastMessage(ClaimMessageService.Message message, Long claimId, Long sender){
        var chatSubscribers = subscribers.get(claimId);
        if (chatSubscribers == null) return;
        for (Map.Entry<Long, StreamObserver<ClaimMessageService.Message>> entry : chatSubscribers.entrySet()) {
            if (!entry.getKey().equals(sender)) {
                entry.getValue().onNext(message);
            }
        }
    }

    private void badRequest(String error){ throw new BadRequestException(error); }
}
