package proyecto.dh.resources.users.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.dh.common.enums.Role;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.users.dto.UserCreateDTO;
import proyecto.dh.resources.users.dto.UserDTO;
import proyecto.dh.resources.users.dto.UserUpdateDTO;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

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
        if (userRepository.existsByEmail(userObject.getEmail())) {
            throw new BadRequestException("Usuario con email '" + userObject.getEmail() + "' ya existe");
        }
        User userEntity = modelMapper.map(userObject, User.class);
        userEntity.setRole(Role.ROLE_USER); // Set default role here
        userEntity.setPassword(passwordEncoder.encode(userObject.getPassword()));
        User createdUser = userRepository.save(userEntity);
        return convertToDTO(createdUser);
    }

    public UserDTO createByAdmin(UserCreateDTO userObject, UserDetails currentUser) throws BadRequestException {
        User admin = getCurrentAdmin(currentUser);

        if (userRepository.existsByEmail(userObject.getEmail())) {
            throw new BadRequestException("Usuario con email '" + userObject.getEmail() + "' ya existe");
        }

        User userEntity = modelMapper.map(userObject, User.class);

        if (userEntity.getRole() == null) {
            userEntity.setRole(Role.ROLE_USER);
        } else if (userEntity.getRole() == Role.ROLE_ADMIN) {
            throw new BadRequestException("No se puede crear un usuario con rol ADMIN");
        }

        userEntity.setPassword(passwordEncoder.encode(userObject.getPassword()));
        userEntity.setAdmin(admin); // Vincula al usuario con el administrador

        User createdUser = userRepository.save(userEntity);
        return convertToDTO(createdUser);
    }

    public UserDTO updateUser(Long id, UserUpdateDTO userObject, UserDetails currentUser) throws BadRequestException {
        User userPerformingUpdate = getCurrentUser(currentUser);
        User existingUser = getUserById(id);

        boolean invalidEmail = userRepository.existsByEmail(userObject.getEmail());
        boolean emailIsTheSame = userObject.getEmail().equals(existingUser.getEmail());

        if (!isAuthorizedToUpdate(userPerformingUpdate, existingUser)) {
            throw new AccessDeniedException("No tienes permiso para actualizar este usuario");
        }

        if (invalidEmail && !emailIsTheSame) {
            throw new BadRequestException("Usuario con email '" + userObject.getEmail() + "' ya existe");
        }

        // Actualizar otros campos antes de validar el rol
        updateUserDetails(existingUser, userObject);
        updateUserPassword(existingUser, userObject);

        // Validar si el usuario puede actualizar su rol
        validateUserForRoleUpdate(existingUser, userObject, userPerformingUpdate);

        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    public User getUserById(Long id) throws BadRequestException {
        return userRepository.findById(id).orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO getUserDetails(UserDetails userDetails) throws BadRequestException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
        return convertToDTO(user);
    }

    public List<UserDTO> getUsersManagedByAdmin(UserDetails currentUser) throws BadRequestException {
        User adminUser = getCurrentAdmin(currentUser);
        List<User> userList = userRepository.findByAdmin(adminUser);
        return userList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserById(Long id, UserDetails currentUser) throws BadRequestException, AccessDeniedException {
        User admin = getCurrentAdmin(currentUser);
        User userToDelete = getUserById(id);

        if (!userToDelete.getAdmin().equals(admin)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este usuario");
        }

        userRepository.delete(userToDelete);
    }

    @Transactional
    public void deleteAdminAccount(UserDetails currentUser) throws BadRequestException, AccessDeniedException {
        User admin = getCurrentAdmin(currentUser);
        List<User> usersManaged = userRepository.findByAdmin(admin);
        if (!usersManaged.isEmpty()) {
            throw new BadRequestException("No se puede eliminar el administrador porque tiene usuarios asignados");
        }
        userRepository.delete(admin);
    }

    // Métodos privados auxiliares

    private boolean isAuthorizedToUpdate(User userPerformingUpdate, User existingUser) {
        if (userPerformingUpdate.getRole() == Role.ROLE_ADMIN) {
            // Admin can only update their own profile or users they manage
            return userPerformingUpdate.getId().equals(existingUser.getId()) || existingUser.getAdmin() != null && existingUser.getAdmin().getId().equals(userPerformingUpdate.getId());
        } else {
            // Non-admin can only update their own profile
            return userPerformingUpdate.getId().equals(existingUser.getId());
        }
    }

    private User getCurrentUser(UserDetails currentUser) throws BadRequestException {
        return userRepository.findByEmail(currentUser.getUsername()).orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
    }

    private User getCurrentAdmin(UserDetails currentUser) throws BadRequestException {
        User admin = getCurrentUser(currentUser);
        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("No tienes permiso para realizar esta acción");
        }
        return admin;
    }

    private void updateUserDetails(User existingUser, UserUpdateDTO userObject) {
        existingUser.setFirstName(userObject.getFirstName());
        existingUser.setLastName(userObject.getLastName());
        existingUser.setEmail(userObject.getEmail());
    }

    private void updateUserPassword(User existingUser, UserUpdateDTO userObject) {
        if (userObject.getPassword() != null && !userObject.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userObject.getPassword()));
        }
    }

    private void validateUserForRoleUpdate(User existingUser, UserUpdateDTO userObject, User userPerformingUpdate) throws BadRequestException {
        // Verificar si el usuario tiene un administrador asignado
        User admin = existingUser.getAdmin();
        // Verificar si el usuario tiene usuarios bajo su administración
        List<User> managedUsers = userRepository.findByAdmin(existingUser);

        // Si hay un administrador asignado
        if (admin != null || !managedUsers.isEmpty()) {
            // No permitir cambiar su propio rol si tiene un administrador asignado
            if (userPerformingUpdate.getId().equals(existingUser.getId())) {
                if (userObject.getRole() != null && userObject.getRole() != existingUser.getRole()) {
                    throw new BadRequestException("No puedes cambiar tu rol si tienes usuarios asignados");
                }
            } else if (userPerformingUpdate.getRole() == Role.ROLE_ADMIN) {
                // Si el ADMIN está intentando cambiar el rol de un usuario asignado a él
                if (userObject.getRole() == Role.ROLE_ADMIN) {
                    throw new BadRequestException("No puedes cambiar el rol a ADMIN");
                }
            }
        }

        // Permitir la actualización del rol si no hay un administrador asignado
        if (userObject.getRole() != null && userObject.getRole() != existingUser.getRole()) {
            existingUser.setRole(userObject.getRole());
        }
    }

    private UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}