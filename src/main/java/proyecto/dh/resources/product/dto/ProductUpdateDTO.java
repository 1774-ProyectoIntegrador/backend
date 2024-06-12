package proyecto.dh.resources.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import proyecto.dh.common.enums.RentType;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.product.entity.ProductFeature;

import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductUpdateDTO {
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    @Min(value = 1, message = "Stock must be at least 1")
    private Integer stock;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    private RentType rentType;

    private Long categoryId;

    private List<Long> attachmentsIds;

    private List<ProductFeatureSaveDTO> features;

    private List<ProductPolicySaveDTO> policies;
}