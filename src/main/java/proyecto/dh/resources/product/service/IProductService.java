package proyecto.dh.resources.product.service;

import proyecto.dh.resources.product.dto.ProductDto;

import java.util.List;

public interface IProductService {

    ProductDto createProduct(ProductDto productDto);

    ProductDto getProduct(Long productId);

    List<ProductDto> getAllProducts();
}
