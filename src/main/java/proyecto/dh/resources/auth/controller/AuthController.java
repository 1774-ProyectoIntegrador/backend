package proyecto.dh.resources.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.dh.resources.auth.dto.AuthenticationRequestDto;
import proyecto.dh.resources.auth.dto.AuthenticationResponseDto;
import proyecto.dh.resources.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDto authRequest) throws Exception {
        AuthenticationResponseDto response = authService.authenticate(authRequest);
        return ResponseEntity.ok(response);
    }
}
