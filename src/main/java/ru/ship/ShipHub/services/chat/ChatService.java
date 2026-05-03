package ru.ship.ShipHub.services.chat;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;
import ru.ship.ShipHub.ChatServiceGrpc;
import ru.ship.ShipHub.ClaimMessageService;
import ru.ship.ShipHub.repositories.ClaimRepository;
import ru.ship.ShipHub.util.exceptions.BadRequestException;

import java.util.concurrent.ConcurrentHashMap;

@GrpcService
@Service
public class ChatService extends ChatServiceGrpc.ChatServiceImplBase {

    private final ClaimRepository claimRepository;

    private ConcurrentHashMap<Long, StreamObserver<ClaimMessageService.MessageRequest>> subscribers = new ConcurrentHashMap<>();

    public ChatService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Override
    public StreamObserver<ClaimMessageService.MessageRequest> chat(StreamObserver<ClaimMessageService.Message> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(ClaimMessageService.MessageRequest request) {
                if (request.getIsSubscribeRequest()){
                    if (subscribers.contains(request.getUserId())) throw new BadRequestException("Пользователь уже подписан на этот чат");
                    System.out.println(request.getContent());
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }

    private void validateMessage(ClaimMessageService.MessageRequest message){

    }
}
