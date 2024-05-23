package proyecto.dh.resources.users.service;

import proyecto.dh.resources.users.dto.UserSignupDto;
import proyecto.dh.resources.users.entity.User;

public interface UserService {
    User save(UserSignupDto registrationDto);

    User findByEmail(String email);
}
