package proyecto.dh.resources.product.dto;

import lombok.Data;
import proyecto.dh.common.enums.RentType;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.product.entity.ProductFeature;

import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private int stock;
    private double price;
    private RentType rentType;
    private Long categoryId;
    private String categoryName;
    private List<Attachment> attachments;
    private List<ProductFeature> features;
}
