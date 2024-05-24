package proyecto.dh.resources.product.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategoryDTO {
    @Schema(hidden = true)
    private Long id;
    private String name;
    private String description;
    private String slug;
    private AttachmentDTO attachment;
}
