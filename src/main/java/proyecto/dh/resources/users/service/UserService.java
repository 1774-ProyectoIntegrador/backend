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

    public UserDTO updateUser(Long id, UserUpdateDTO userObject, UserDetails currentUser) throws BadRequestException {
        User userPerformingUpdate = getCurrentUser(currentUser);
        User userToEdit = getUserById(id);

        if (!isAuthorizedToUpdate(userPerformingUpdate, userToEdit)) {
            throw new AccessDeniedException("No tienes permiso para actualizar este usuario");
        }

        // Actualizar otros campos solo si el valor no es nulo o vacío en UserUpdateDTO
        if (userObject.getFirstName() != null && !userObject.getFirstName().isEmpty()) {
            userToEdit.setFirstName(userObject.getFirstName());
        }
        if (userObject.getLastName() != null && !userObject.getLastName().isEmpty()) {
            userToEdit.setLastName(userObject.getLastName());
        }
        if (userObject.getEmail() != null && !userObject.getEmail().isEmpty()) {
            // Valida si el email existe
            boolean invalidEmail = userRepository.existsByEmail(userObject.getEmail());
            boolean emailIsTheSame = userObject.getEmail().equals(userToEdit.getEmail());
            if (invalidEmail && !emailIsTheSame) {
                throw new BadRequestException("Usuario con email '" + userObject.getEmail() + "' ya existe");
            }
            userToEdit.setEmail(userObject.getEmail());
        }
        if (userObject.getPhone() != null && !userObject.getPhone().isEmpty()) {
            userToEdit.setPhone(userObject.getPhone());
        }
        if (userObject.getPassword() != null && !userObject.getPassword().isEmpty()) {
            userToEdit.setPassword(passwordEncoder.encode(userObject.getPassword()));
        }

        // Validar si el usuario puede actualizar su rol
        validateUserForRoleUpdate(userToEdit, userObject, userPerformingUpdate);

        User updatedUser = userRepository.save(userToEdit);
        return convertToDTO(updatedUser);
    }

    public UserDTO getUserByIdByAdmin(Long id, UserDetails currentUser) throws BadRequestException, AccessDeniedException {
        // Verificar si el usuario actual es administrador
        User userIsAdmin = getCurrentUser(currentUser);
        verifyAdminAccess(userIsAdmin);

        User userToGet = userRepository.findById(id).orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        return convertToDTO(userToGet);
    }

    public User getUserById(Long id) throws BadRequestException {
        return userRepository.findById(id).orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserDTO> getAllUsersExceptAdmin(UserDetails currentUser) throws BadRequestException {
        // Verificar si el usuario actual es administrador
        User userIsAdmin = getCurrentUser(currentUser);
        verifyAdminAccess(userIsAdmin);

        // Obtener y filtrar la lista de usuarios excluyendo administradores
        List<User> nonAdminUsers = userRepository.findAll().stream().filter(user -> user.getRole() != Role.ROLE_ADMIN).toList();

        // Convertir la lista de User a UserDTO y devolverla
        return nonAdminUsers.stream().map(this::convertToDTO).toList();
    }

    private void verifyAdminAccess(User user) {
        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("No tienes permiso para acceder a los usuarios");
        }
    }

    public UserDTO getUserDetails(UserDetails userDetails) throws BadRequestException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
        return convertToDTO(user);
    }


    @Transactional
    public void deleteUserById(Long id, UserDetails currentUser) throws AccessDeniedException, BadRequestException {
        User userPerformingDelete = getCurrentUser(currentUser);
        User userToDelete = getUserById(id);

        boolean canDeleteUser = userPerformingDelete.getId().equals(userToDelete.getId()) || userPerformingDelete.getRole() == Role.ROLE_ADMIN;

        if (!canDeleteUser) {
            throw new AccessDeniedException("No tienes permiso para eliminar este usuario");
        }

        if (userToDelete.getRole() == Role.ROLE_ADMIN && userPerformingDelete.getId().equals(userToDelete.getId())) {
            throw new AccessDeniedException("No puedes eliminarte a ti mismo como administrador");
        }

        userRepository.delete(userToDelete);
    }

    /*    public UserDTO createByAdmin(UserCreateDTO userObject, UserDetails currentUser) throws BadRequestException {
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
    }*/

    /*    public List<UserDTO> getUsersManagedByAdmin(UserDetails currentUser) throws BadRequestException {
        User adminUser = getCurrentAdmin(currentUser);
        List<User> userList = userRepository.findByAdmin(adminUser);
        return userList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }*/


/*    @Transactional
    public void deleteUserById(Long id, UserDetails currentUser) throws BadRequestException, AccessDeniedException {
        User admin = getCurrentAdmin(currentUser);
        User userToDelete = getUserById(id);

        if (!userToDelete.getAdmin().equals(admin)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este usuario");
        }

        userRepository.delete(userToDelete);
    }*/

/*    @Transactional
    public void deleteAdminAccount(UserDetails currentUser) throws BadRequestException, AccessDeniedException {
        User admin = getCurrentAdmin(currentUser);
        List<User> usersManaged = userRepository.findByAdmin(admin);
        if (!usersManaged.isEmpty()) {
            throw new BadRequestException("No se puede eliminar el administrador porque tiene usuarios asignados");
        }
        userRepository.delete(admin);
    }*/

    // -------------- Métodos privados auxiliares --------------

    private boolean isAuthorizedToUpdate(User userPerformingUpdate, User existingUser) {
        if (userPerformingUpdate.getRole() != Role.ROLE_ADMIN) {
            // Non-admin can only update their own profile
            return userPerformingUpdate.getId().equals(existingUser.getId());
        } else {
            return true;
        }
    }

    private User getCurrentUser(UserDetails currentUser) throws BadRequestException {
        return userRepository.findByEmail(currentUser.getUsername()).orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
    }

    private void validateUserForRoleUpdate(User existingUser, UserUpdateDTO userObject, User userPerformingUpdate) throws BadRequestException {
        if (userPerformingUpdate.getRole() == Role.ROLE_ADMIN) {
            // Admin cannot change their own role
            if (existingUser.getId().equals(userPerformingUpdate.getId())) {
                if (userObject.getRole() != null && userObject.getRole() != existingUser.getRole()) {
                    throw new BadRequestException("No puedes cambiar tu propio rol");
                }
            } else {
                // Admin can change role of others to ROLE_USER or ROLE_EDITOR
                if (userObject.getRole() == Role.ROLE_USER || userObject.getRole() == Role.ROLE_EDITOR) {
                    existingUser.setRole(userObject.getRole());
                } else if (userObject.getRole() == Role.ROLE_ADMIN) {
                    throw new BadRequestException("No puedes asignar el rol ADMIN a otro usuario");
                }
            }
        } else {
            // Non-admin users cannot change roles
            if (userObject.getRole() != null && userObject.getRole() != existingUser.getRole()) {
                throw new BadRequestException("No tienes permiso para cambiar roles");
            }
        }
    }

    private UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }


    /*    private boolean isAuthorizedToUpdate(User userPerformingUpdate, User existingUser) {
        if (userPerformingUpdate.getRole() == Role.ROLE_ADMIN) {
            // Admin can only update their own profile or users they manage
            return userPerformingUpdate.getId().equals(existingUser.getId()) || existingUser.getAdmin() != null && existingUser.getAdmin().getId().equals(userPerformingUpdate.getId());
        } else {
            // Non-admin can only update their own profile
            return userPerformingUpdate.getId().equals(existingUser.getId());
        }
    }*/



/*    private User getCurrentAdmin(UserDetails currentUser) throws BadRequestException {
        User admin = getCurrentUser(currentUser);
        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("No tienes permiso para realizar esta acción");
        }
        return admin;
    }*/

/*    private void updateUserDetails(User existingUser, UserUpdateDTO userObject) {
        existingUser.setFirstName(userObject.getFirstName());
        existingUser.setLastName(userObject.getLastName());
        existingUser.setPhone(userObject.getPhone());
        if (userObject.getEmail() != null && !userObject.getEmail().isEmpty()) {
            existingUser.setEmail(userObject.getEmail());
        }
    }

    private void updateUserPassword(User existingUser, UserUpdateDTO userObject) {
        if (userObject.getPassword() != null && !userObject.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userObject.getPassword()));
        }
    }*/



 /*   private void validateUserForRoleUpdate(User existingUser, UserUpdateDTO userObject, User userPerformingUpdate) throws BadRequestException {
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
    }*/


}