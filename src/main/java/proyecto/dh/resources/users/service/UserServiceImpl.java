package proyecto.dh.resources.users.service;


import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.dh.resources.users.dto.UserSignupDto;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public User save(UserSignupDto userSignupDto) {
        User user = new User();
        user.setFirstName(userSignupDto.getFirstName());
        user.setLastName(userSignupDto.getLastName());
        user.setEmail(userSignupDto.getEmail());
        user.setPassword(passwordEncoder.encode(userSignupDto.getPassword()));


        // Enviar correo de confirmación
        try {
            String subject = "Confirmación de Registro";
            String text = String.format(
                    "Hola %s,\n\nTu registro en nuestro sitio ha sido exitoso.\n\n" +
                            "Usuario: %s\nCorreo Electrónico: %s\n\n" +
                            "Puedes iniciar sesión en tu cuenta utilizando el siguiente enlace:\n" +
                            "<a href=\"http://localhost:8080/login\">Iniciar sesión</a>",
                    user.getFirstName(), user.getFirstName(), user.getEmail());

            emailService.sendRegistrationConfirmationEmail(user.getEmail(), subject, text);
        } catch (MessagingException e) {
            // Manejar la excepción (por ejemplo, registrar el error)
            e.printStackTrace();
        }


        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
