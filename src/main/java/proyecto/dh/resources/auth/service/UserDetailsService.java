package proyecto.dh.resources.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import proyecto.dh.resources.users.repository.UserRepository;

import java.util.Collections;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        proyecto.dh.resources.users.entity.User user = userRepository.findByEmail(email);
        if (user  == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Convert the Role enum to SimpleGrantedAuthority
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)

        );    }
}
