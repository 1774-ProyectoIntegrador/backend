package proyecto.dh.resources.favorites.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductFavoriteSaveDTO {

    private Long userId;
    private List<Long> productIds;
}
