package proyecto.dh.resources.auth.controller;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.auth.dto.AuthenticationRequestDto;
import proyecto.dh.resources.auth.dto.AuthenticationResponseDto;
import proyecto.dh.resources.auth.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    @Qualifier("secretKey")
    private SecretKey jwtSecret;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDto authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        } catch (Exception e) {
            throw new BadRequestException("Email y/o usuario incorrectos", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthenticationResponseDto(jwt));
    }

    private String generateToken(String username) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getEncoded());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
