package proyecto.dh.resources.users.dto;


import lombok.Data;

@Data
public class RegisterRequestDto {
    private String email;
    private String password;
    private String userName;
    private String firstName;
    private String lastName;
}
