package proyecto.dh.resources.users.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UserCreateDTO {

    @NotEmpty(message = "El nombre es obligatorio")
    @Size(min = 2, max = 30, message = "El nombre debe tener entre 2 y 30 caracteres")
    private String firstName;

    @NotEmpty(message = "El apellido es obligatorio")
    @Size(min = 2, max = 30, message = "El apellido debe tener entre 2 y 30 caracteres")
    private String lastName;

    @NotEmpty(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    private String email;

    @NotEmpty(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

}
