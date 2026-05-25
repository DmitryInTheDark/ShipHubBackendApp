package ru.ship.ShipHub.config.websocket;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import ru.ship.ShipHub.config.security.PersonDetails;
import ru.ship.ShipHub.models.entity.PersonEntity;
import ru.ship.ShipHub.repositories.PersonRepository;
import ru.ship.ShipHub.util.JWTUtil;

import java.util.List;
import java.util.Map;

@Component
public class JwtWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtWebSocketHandshakeInterceptor.class);
    private final JWTUtil jwtUtil;
    private final PersonRepository personRepository;

    public JwtWebSocketHandshakeInterceptor(JWTUtil jwtUtil, PersonRepository personRepository) {
        this.jwtUtil = jwtUtil;
        this.personRepository = personRepository;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        String token = extractToken(request);
        if (token == null || token.isBlank()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        try {
            Long personId = jwtUtil.validateTokenAndGetPersonId(token);
            PersonEntity person = personRepository.findById(personId).orElseThrow();
            attributes.put("personDetails", new PersonDetails(person));
            return true;
        } catch (JWTVerificationException | IllegalArgumentException e) {
            log.warn("WebSocket authorization failed: {}", e.getMessage());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractToken(ServerHttpRequest request) {
        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders != null) {
            for (String header : authHeaders) {
                if (header != null && header.startsWith("Bearer ")) {
                    return header.substring(7);
                }
            }
        }
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String[] tokens = servletRequest.getServletRequest().getParameterValues("token");
            if (tokens != null && tokens.length > 0) {
                return tokens[0];
            }
        }
        return null;
    }
}
