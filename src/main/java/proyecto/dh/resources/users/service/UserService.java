package proyecto.dh.resources.users.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.dh.common.enums.Role;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.users.dto.UserCreateDTO;
import proyecto.dh.resources.users.dto.UserDTO;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

import java.util.Collections;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO create(UserCreateDTO userObject) throws BadRequestException {
        Boolean findUser = userRepository.existsByEmail(userObject.getEmail());
        if (findUser) {
            throw new BadRequestException("Usuario con email '" + userObject.getEmail() + "' ya existe");
        }
        User userEntity = modelMapper.map(userObject, User.class);
        userEntity.setRoles(userObject.getRoles() != null ? userObject.getRoles() : Collections.singletonList(Role.USER));
        userEntity.setPassword(passwordEncoder.encode(userObject.getPassword()));
        User createdUser = userRepository.save(userEntity);
        return convertToDTO(createdUser);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserDTO convertToDTO(User userObject) {
        return modelMapper.map(userObject, UserDTO.class);
    }
}
