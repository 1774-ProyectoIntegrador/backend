package proyecto.dh.resources.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import proyecto.dh.resources.users.dto.UserSignupDto;
import proyecto.dh.resources.users.service.UserService;

import javax.validation.Valid;

@Controller
public class UserSignupController {

    private UserService userService;

    @Autowired
    public UserSignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new UserSignupDto());
        return "signup";
    }

    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("user") @Valid UserSignupDto userDto,
                                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "signup";
        }

        // Check if the email already exists
        if (userService.findByEmail(userDto.getEmail()) != null) {
            result.rejectValue("email", null, "Ya existe una cuenta con ese correo electrónico");
            return "signup";
        }

        userService.save(userDto);
        return "redirect:/signup?success";
    }
}
