package ru.ship.ShipHub.services.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.ship.ShipHub.config.security.PersonDetails;
import ru.ship.ShipHub.models.dto.claim.MessageCreateDTO;
import ru.ship.ShipHub.models.dto.claim.MessageDTO;
import ru.ship.ShipHub.models.entity.ClaimEntity;
import ru.ship.ShipHub.repositories.ClaimRepository;
import ru.ship.ShipHub.services.MessageService;
import ru.ship.ShipHub.util.exceptions.BadRequestException;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final ClaimRepository claimRepository;
    private final MessageService messageService;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<Long, Set<WebSocketSession>> sessionsByClaim = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> sessionClaim = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PersonDetails> sessionPerson = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ClaimRepository claimRepository,
                                MessageService messageService,
                                ObjectMapper objectMapper) {
        this.claimRepository = claimRepository;
        this.messageService = messageService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connection established: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            ChatWebSocketMessage request = objectMapper.readValue(message.getPayload(), ChatWebSocketMessage.class);
            if (request.type == null || request.type.isBlank()) {
                sendError(session, "Missing message type");
                return;
            }
            switch (request.type) {
                case "subscribe" -> handleSubscribe(session, request);
                case "chat" -> handleChat(session, request);
                default -> sendError(session, "Unexpected message type: " + request.type);
            }
        } catch (IOException e) {
            log.warn("Failed to parse WebSocket payload for session {}: {}", session.getId(), e.getMessage());
            sendError(session, "Malformed JSON payload");
        }
    }

    private void handleSubscribe(WebSocketSession session, ChatWebSocketMessage request) {
        if (request.claimId == null) {
            sendError(session, "claimId is required for subscribe");
            return;
        }
        if (session.getAttributes().get("personDetails") instanceof PersonDetails personDetails) {
            ClaimEntity claim = claimRepository.findById(request.claimId).orElse(null);
            if (claim == null) {
                sendError(session, "Claim not found");
                return;
            }
            if (!isAllowedInClaim(claim, personDetails)) {
                sendError(session, "Access denied to claim " + request.claimId);
                return;
            }
            sessionsByClaim.computeIfAbsent(request.claimId, id -> ConcurrentHashMap.newKeySet()).add(session);
            sessionClaim.put(session.getId(), request.claimId);
            sessionPerson.put(session.getId(), personDetails);
            sendInfo(session, "subscribed", "Subscribed to claim " + request.claimId, request.claimId);
            log.info("WebSocket session {} subscribed to claim {}", session.getId(), request.claimId);
        } else {
            sendError(session, "Unauthorized session");
        }
    }

    private void handleChat(WebSocketSession session, ChatWebSocketMessage request) {
        Long subscribedClaimId = sessionClaim.get(session.getId());
        if (subscribedClaimId == null) {
            sendError(session, "Session has not subscribed to a claim");
            return;
        }
        if (!subscribedClaimId.equals(request.claimId)) {
            sendError(session, "claimId mismatch or missing claimId");
            return;
        }
        if (request.text == null || request.text.isBlank()) {
            sendError(session, "Message text is required");
            return;
        }
        PersonDetails personDetails = sessionPerson.get(session.getId());
        if (personDetails == null) {
            sendError(session, "Unauthorized session");
            return;
        }
        try {
            MessageDTO saved = messageService.createMessage(new MessageCreateDTO(request.claimId, request.text), personDetails);
            ChatWebSocketMessage broadcast = new ChatWebSocketMessage(
                    "chat",
                    saved.claimId,
                    saved.id,
                    saved.senderId,
                    personDetails.getPerson().getUsername(),
                    saved.text,
                    saved.dateCreated.toString(),
                    null,
                    null
            );
            broadcastToClaim(request.claimId, broadcast, session);
            sendInfo(session, "ack", "Message saved and broadcast", saved.claimId);
        } catch (BadRequestException e) {
            sendError(session, e.getMessage());
        } catch (Exception e) {
            log.error("Error saving chat message for session {}: {}", session.getId(), e.getMessage(), e);
            sendError(session, "Unable to save message");
        }
    }

    private boolean isAllowedInClaim(ClaimEntity claim, PersonDetails personDetails) {
        return personDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"))
                || claim.getWhoCreate().getId().equals(personDetails.getPerson().getId());
    }

    private void broadcastToClaim(Long claimId, ChatWebSocketMessage payload, WebSocketSession origin) {
        Set<WebSocketSession> sessions = sessionsByClaim.get(claimId);
        if (sessions == null) return;
        sessions.removeIf(session -> !session.isOpen());
        for (WebSocketSession target : sessions) {
            if (target.isOpen() && !target.getId().equals(origin.getId())) {
                sendJson(target, payload);
            }
        }
    }

    private void sendJson(WebSocketSession session, ChatWebSocketMessage payload) {
        try {
            if (!session.isOpen()) return;
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
        } catch (IOException e) {
            log.warn("Failed to send WebSocket message to session {}: {}", session.getId(), e.getMessage());
        }
    }

    private void sendError(WebSocketSession session, String error) {
        sendJson(session, new ChatWebSocketMessage("error", null, null, null, null, null, null, error, null));
    }

    private void sendInfo(WebSocketSession session, String type, String info, Long claimId) {
        sendJson(session, new ChatWebSocketMessage(type, claimId, null, null, null, null, null, null, info));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        cleanup(session);
        log.info("WebSocket connection closed: {} status={} ", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("WebSocket transport error on session {}: {}", session.getId(), exception.getMessage());
        cleanup(session);
    }

    private void cleanup(WebSocketSession session) {
        Long claimId = sessionClaim.remove(session.getId());
        sessionPerson.remove(session.getId());
        if (claimId != null) {
            Set<WebSocketSession> sessions = sessionsByClaim.get(claimId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    sessionsByClaim.remove(claimId);
                }
            }
        }
    }
}
