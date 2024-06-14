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
import proyecto.dh.resources.users.service.UserService;

@RestController
@RequestMapping("/users")
public class UserSecuredController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public UserDTO getUserDetails(@AuthenticationPrincipal UserDetails token) throws BadRequestException {
        return userService.getUserDetails(token);
    }

}
