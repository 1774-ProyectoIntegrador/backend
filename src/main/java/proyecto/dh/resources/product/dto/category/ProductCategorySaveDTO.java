package proyecto.dh.resources.product.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategorySaveDTO {
    @NotBlank(message = "El nombre el obligatorio")
    @Size(max = 30, message = "El nombre no puede tener más de 30 caracteres")
    private String name;
    @Size(max = 300, message = "La descripción no puede tener más de 300 caracteres")
    private String description;
    @NotBlank(message = "El slug el obligatorio")
    private String slug;
    private Long attachmentId;
}