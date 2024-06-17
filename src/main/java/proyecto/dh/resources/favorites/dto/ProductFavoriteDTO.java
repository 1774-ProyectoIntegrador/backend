package proyecto.dh.resources.favorites.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductFavoriteDTO {

    private Long id;
    private Long userId;
    private List<Long> productIds;
}
