package proyecto.dh.resources.users.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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

    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) throws BadRequestException {
        UserDTO createdUser = userService.create(userCreateDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(userService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) throws BadRequestException {
        User user = userService.getUserById(id);
        UserDTO userDTO = userService.convertToDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/me")
    public UserDTO getUserDetails(@AuthenticationPrincipal Jwt jwt) {
        return userService.getUserDetails(jwt);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserCreateDTO userCreateDTO) throws BadRequestException {
        User updatedUser = userService.updateUser(id, userCreateDTO);
        UserDTO userDTO = userService.convertToDTO(updatedUser);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable Long id, @RequestBody List<String> roles) throws BadRequestException {
        User updatedUser = userService.updateUserRole(id, roles);
        UserDTO userDTO = userService.convertToDTO(updatedUser);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

}
