package proyecto.dh.resources.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proyecto.dh.common.enums.RentType;

@Getter
@Setter
public class UpdateProductDTO {
    private String name;
    private String description;
    private Integer stock;
    private Double price;
    private RentType rentType;
    private Long categoryId;
}
