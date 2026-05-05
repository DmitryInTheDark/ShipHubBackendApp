package ru.ship.ShipHub.util;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.ship.ShipHub.config.security.PersonDetails;
import ru.ship.ShipHub.models.entity.PersonEntity;
import ru.ship.ShipHub.repositories.PersonRepository;
import ru.ship.ShipHub.util.exceptions.BadRequestException;
import ru.ship.ShipHub.util.exceptions.PersonNotFoundException;

@Component
@GrpcGlobalServerInterceptor
public class JwtGrpcInterceptor implements ServerInterceptor {

    private final JWTUtil jwtUtil;
    private final PersonRepository personRepository;

    public JwtGrpcInterceptor(JWTUtil jwtService, PersonRepository personRepository) {
        this.jwtUtil = jwtService;
        this.personRepository = personRepository;
    }

    private static final Metadata.Key<String> AUTHORIZATION =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String header = headers.get(AUTHORIZATION);
        if (header != null && !header.isBlank() && header.startsWith("Bearer ")){
            String token = header.substring(7);
            if (token.isBlank()){
                throw new BadRequestException("Невалидный токен");
            }
            try{
                Long id = jwtUtil.validateTokenAndGetPersonId(token);
                PersonEntity person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
                PersonDetails personDetails = new PersonDetails(person);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        personDetails, null, personDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }catch (JWTVerificationException e){
                throw new BadRequestException("Невалидный токен");
            }
        }

        return next.startCall(call, headers);
    }
}
