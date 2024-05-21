package proyecto.dh.resources.product.dto;

import lombok.Getter;
import lombok.Setter;
import proyecto.dh.common.enums.RentType;

@Getter
@Setter
public class CreateProductDTO {
    private String name;
    private String description;
    private Integer stock;
    private Double price;
    private RentType rentType;
}
