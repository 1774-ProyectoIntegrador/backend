package proyecto.dh.resources.users.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import proyecto.dh.common.enums.Role;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.users.dto.UserCreateDTO;
import proyecto.dh.resources.users.dto.UserDTO;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        userEntity.setRoles(Collections.singletonList(Role.ROLE_USER)); // Set default role here
        userEntity.setPassword(passwordEncoder.encode(userObject.getPassword()));
        User createdUser = userRepository.save(userEntity);
        return convertToDTO(createdUser);
    }

    public User updateUser(Long id, UserCreateDTO userObject) throws BadRequestException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
        existingUser.setFirstName(userObject.getFirstName());
        existingUser.setLastName(userObject.getLastName());
        existingUser.setEmail(userObject.getEmail());
        existingUser.setPassword(passwordEncoder.encode(userObject.getPassword()));
        // Otras actualizaciones de atributos de usuario
        return userRepository.save(existingUser);
    }

    public User getUserById(Long id) throws BadRequestException {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
    }

    public User updateUserRole(Long id, List<String> roles) throws BadRequestException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        List<Role> validRoles = roles.stream()
                .map(role -> {
                    try {
                        return Role.valueOf(role);
                    } catch (IllegalArgumentException e) {
                        try {
                            throw new BadRequestException("Rol no válido: " + role);
                        } catch (BadRequestException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                })
                .collect(Collectors.toList());

        user.setRoles(validRoles);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO getUserDetails(Jwt jwt){
        String email = jwt.getSubject();

        User user = findByEmail(email);

        return convertToDTO(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserDTO convertToDTO(User userObject) {
        return modelMapper.map(userObject, UserDTO.class);
    }
}
