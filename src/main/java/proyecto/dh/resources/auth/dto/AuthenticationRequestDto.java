package proyecto.dh.resources.auth.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AuthenticationRequestDto {
    private String email;
    private String password;
}
