package ru.ship.ShipHub.util;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.ship.ShipHub.repositories.PersonRepository;
import ru.ship.ShipHub.util.exceptions.PersonNotFoundException;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final PersonRepository personRepository;

    public JWTFilter(JWTUtil jwtUtil, PersonRepository personRepository) {
        this.jwtUtil = jwtUtil;
        this.personRepository = personRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && !header.isBlank() && header.startsWith("Bearer ")){
            String token = header.substring(7);
            if (token.isBlank()){
                response.sendError(401, "Blank JWT token");
                return;
            }else{
                try{
                    Integer id = jwtUtil.validateTokenAndGetPersonId(token);
                    personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
                }catch (JWTVerificationException e){
                    response.sendError(401, "invalid JWT token");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
