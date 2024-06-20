package proyecto.dh.resources.users.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import proyecto.dh.common.enums.Role;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
