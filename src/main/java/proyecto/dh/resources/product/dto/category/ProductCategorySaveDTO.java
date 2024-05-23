package proyecto.dh.resources.product.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategorySaveDTO {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String slug;
    private Long attachmentId;
}