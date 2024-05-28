package proyecto.dh.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import proyecto.dh.common.enums.Role;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificar si el usuario admin ya existe
        if (userRepository.findByEmail("admin@admin.com") == null) {
            // Crear el usuario admin
            User admin = new User();
            admin.setEmail("admin@admin.com");
            admin.setPassword(passwordEncoder.encode("admin")); // Encripta la contraseña
            admin.setRole(Role.ADMIN);
            admin.setUserName("admin");
            admin.setFirstName("admin");
            admin.setLastName("admin");

            userRepository.save(admin);
            System.out.println("Admin user created: " + admin);
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}
