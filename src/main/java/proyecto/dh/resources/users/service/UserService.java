package proyecto.dh.resources.users.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.dh.common.enums.Role;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.users.dto.UserCreateDTO;
import proyecto.dh.resources.users.dto.UserDTO;
import proyecto.dh.resources.users.dto.UserUpdateDTO;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

import java.util.List;
import java.util.Optional;
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
        userEntity.setRole(Role.ROLE_USER); // Set default role here
        userEntity.setPassword(passwordEncoder.encode(userObject.getPassword()));
        User createdUser = userRepository.save(userEntity);
        return convertToDTO(createdUser);
    }

    public UserDTO createByAdmin(UserCreateDTO userObject, UserDetails currentUser) throws BadRequestException {
        User admin = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new BadRequestException("Admin no encontrado"));
        System.out.println(admin.toString());

        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("No tienes permiso para crear usuarios");
        }

        Boolean findUser = userRepository.existsByEmail(userObject.getEmail());
        if (findUser) {
            throw new BadRequestException("Usuario con email '" + userObject.getEmail() + "' ya existe");
        }

        User userEntity = modelMapper.map(userObject, User.class);

        if (userEntity.getRole() == null) {
            userEntity.setRole(Role.ROLE_USER);
        }

        userEntity.setPassword(passwordEncoder.encode(userObject.getPassword()));
        userEntity.setAdmin(admin); // Vincula al usuario con el administrador
        User createdUser = userRepository.save(userEntity);
        return convertToDTO(createdUser);
    }

    public UserDTO updateUser(Long id, UserUpdateDTO userObject, UserDetails currentUser) throws BadRequestException {
        User userPerformingUpdate = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        // Actualizar los campos permitidos
        existingUser.setFirstName(userObject.getFirstName());
        existingUser.setLastName(userObject.getLastName());
        existingUser.setEmail(userObject.getEmail());

        if (userObject.getPassword() != null && !userObject.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userObject.getPassword()));
        }

        // Solo el administrador puede cambiar el rol del usuario
        if (userPerformingUpdate.getRole() == Role.ROLE_ADMIN && userObject.getRole() != null) {
            existingUser.setRole(userObject.getRole());
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    public User getUserById(Long id) throws BadRequestException {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO getUserDetails(UserDetails userDetails) throws BadRequestException {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        return convertToDTO(user);
    }

    public List<UserDTO> getUsersManagedByAdmin(UserDetails currentUser) throws BadRequestException {
        User adminUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new BadRequestException("Admin no encontrado"));

        if (adminUser.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("No tienes permiso para ver esta información");
        }


        List<User> userList = userRepository.findByAdmin(adminUser);

        // Convertir entidades a DTOs
        List<UserDTO> userDTOs = userList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return userDTOs;

        // return userRepository.findByAdmin(admin);
    }

/*    public List<UserDTO> getUsersManagedByAdmin(Long adminId) throws BadRequestException {
        List<User> users = userRepository.findByAdminId(adminId);

        // Convertir entidades a DTOs
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return userDTOs;
    }*/

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
