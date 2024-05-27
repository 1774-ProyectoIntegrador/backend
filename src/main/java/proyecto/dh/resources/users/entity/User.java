package proyecto.dh.resources.users.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proyecto.dh.common.enums.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Role role;

    @NotEmpty(message = "Name is required")
    private String firstName;

    @NotEmpty(message = "Surname is required")
    private String lastName;

    @Column(unique = true, nullable = false)
    private String userName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email")
    @Column(unique = true, nullable = false)
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;



//    @OneToOne
//    private Profile profile;
}
