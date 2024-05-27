package proyecto.dh.resources.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import proyecto.dh.resources.users.dto.UserSignupDto;
import proyecto.dh.resources.users.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserSignupController {

    private UserServiceImpl userService;

    @Autowired
    public UserSignupController(UserServiceImpl userService) {
        this.userService = userService;
    }



    @PostMapping("/signup")
    public ResponseEntity<?> registerUserAccount(@RequestBody @Valid UserSignupDto userDto,
                                                 BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        if (userService.findByEmail(userDto.getEmail()) != null) {
            return new ResponseEntity<>("Ya existe una cuenta con ese correo electrónico", HttpStatus.BAD_REQUEST);
        }

        userService.save(userDto);
        return new ResponseEntity<>("Registro exitoso", HttpStatus.OK);
    }



}
