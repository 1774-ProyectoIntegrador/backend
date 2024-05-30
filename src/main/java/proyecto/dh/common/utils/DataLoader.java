package proyecto.dh.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import proyecto.dh.common.enums.Role;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

import java.util.Collections;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {
        createAdminUser();
    }

    private void createAdminUser() {
        String adminEmail = "admin@admin.com";
        User findAdmin = userRepository.findByEmail(adminEmail);
        try {
            userRepository.delete(findAdmin);
            System.out.println("[DATA-LOADER] Admin User deleted: " + findAdmin);
        } catch (Exception e) {
            System.err.println("[DATA-LOADER] Admin User NOT deleted: " + findAdmin);
        }

            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin")); // Encripta la contraseña
            admin.setRoles(Collections.singletonList(Role.ADMIN));
            admin.setFirstName("admin");
            admin.setLastName("admin");

            userRepository.save(admin);
            System.out.println("[DATA-LOADER] Admin user created: " + admin);
    }
}