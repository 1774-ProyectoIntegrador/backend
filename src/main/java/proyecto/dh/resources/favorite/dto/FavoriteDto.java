package proyecto.dh.resources.favorite.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import proyecto.dh.resources.product.dto.ProductDTO;
import proyecto.dh.resources.users.dto.UserDTO;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavoriteDto {

    private Long id;
    private UserDTO user;
    private ProductDTO product;
}
