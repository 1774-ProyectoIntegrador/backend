package proyecto.dh.resources.product.dto.favorite;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductFavoriteDto {

    private Long id;
    private Long userId;
    private Long productId;
}
