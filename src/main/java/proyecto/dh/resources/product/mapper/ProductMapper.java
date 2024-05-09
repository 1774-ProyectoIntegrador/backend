package proyecto.dh.resources.product.mapper;

import proyecto.dh.resources.product.dto.ProductDto;
import proyecto.dh.resources.product.entity.Product;

public class ProductMapper {

    public static ProductDto mapToProductDto(Product product){
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }

    public static Product mapToProduct(ProductDto productDto){
      return new Product(
              productDto.getId(),
              productDto.getName(),
              productDto.getDescription(),
              productDto.getPrice()
      );
    };
}
