package proyecto.dh.resources.product.dto.category;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategorySaveDTO {
    private String name;
    private String description;
    private String slug;
}
