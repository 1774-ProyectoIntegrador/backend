package proyecto.dh.resources.product.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProductCategoryDTO {
    private String name;
    private String slug;
    private String description;
}
