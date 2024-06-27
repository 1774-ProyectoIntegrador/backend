package proyecto.dh.resources.users.controller.secured;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.BadRequestException;
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
    public ResponseEntity<UserDTO> getUserDetails(@AuthenticationPrincipal UserDetails token) throws BadRequestException {
        return ResponseEntity.ok(userService.getUserDetails(token));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsersByAdmin(@AuthenticationPrincipal UserDetails currentUser) throws BadRequestException {
        return ResponseEntity.ok(userService.getAllUsersExceptAdmin(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) throws BadRequestException, AccessDeniedException {
        return ResponseEntity.ok(userService.getUserByIdByAdmin(id, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO, @AuthenticationPrincipal UserDetails currentUser) throws BadRequestException {
        UserDTO updatedUser = userService.updateUser(id, userUpdateDTO, currentUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) throws BadRequestException, AccessDeniedException {
        userService.deleteUserById(id, currentUser);
        return ResponseEntity.noContent().build();
    }
/*    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO, @AuthenticationPrincipal UserDetails currentUser) throws BadRequestException {
        UserDTO createdUser = userService.createByAdmin(userCreateDTO, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }*/

/*    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAdminAccount(@AuthenticationPrincipal UserDetails currentUser) throws BadRequestException, AccessDeniedException {
        userService.deleteAdminAccount(currentUser);
        return ResponseEntity.noContent().build();
    }*/

}
