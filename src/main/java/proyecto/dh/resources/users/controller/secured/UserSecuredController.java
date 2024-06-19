package proyecto.dh.resources.users.controller.secured;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.users.dto.UserCreateDTO;
import proyecto.dh.resources.users.dto.UserDTO;
import proyecto.dh.resources.users.dto.UserUpdateDTO;
import proyecto.dh.resources.users.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserSecuredController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public UserDTO getUserDetails(@AuthenticationPrincipal UserDetails token) throws BadRequestException {
        return userService.getUserDetails(token);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO, @AuthenticationPrincipal UserDetails currentUser) throws BadRequestException {
        UserDTO createdUser = userService.createByAdmin(userCreateDTO, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsersByAdmin(@AuthenticationPrincipal UserDetails currentUser) throws BadRequestException {
        return ResponseEntity.ok(userService.getUsersManagedByAdmin(currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserByAdmin(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO, @AuthenticationPrincipal UserDetails currentUser) throws BadRequestException {
        UserDTO updatedUser = userService.updateUser(id, userUpdateDTO, currentUser);
        return ResponseEntity.ok(updatedUser);
    }
}
