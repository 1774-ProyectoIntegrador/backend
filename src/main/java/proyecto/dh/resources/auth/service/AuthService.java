package proyecto.dh.resources.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.auth.dto.AuthenticationRequestDto;
import proyecto.dh.resources.auth.dto.AuthenticationResponseDto;
import proyecto.dh.resources.auth.service.UserDetailsServiceImpl;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private SecretKey jwtSecret;


    public AuthenticationResponseDto authenticate(AuthenticationRequestDto authRequest) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = generateToken(userDetails);

            return new AuthenticationResponseDto(jwt);
    }

    private String generateToken(UserDetails userDetails) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getEncoded());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("scope", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(" ")))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
