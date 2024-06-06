package proyecto.dh.resources.users.controller;


import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.users.dto.UserCreateDTO;
import proyecto.dh.resources.users.dto.UserDTO;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) throws BadRequestException {
        UserDTO createdUser = userService.create(userCreateDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public UserDTO getUserDetails(@AuthenticationPrincipal UserDetails token) throws BadRequestException {
        return userService.getUserDetails(token);
    }

}
