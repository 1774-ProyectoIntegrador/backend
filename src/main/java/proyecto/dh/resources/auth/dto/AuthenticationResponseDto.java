package proyecto.dh.resources.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticationResponseDto {
    private String jwt;
    //private String role;
}
