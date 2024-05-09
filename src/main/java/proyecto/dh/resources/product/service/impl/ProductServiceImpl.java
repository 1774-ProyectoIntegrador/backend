package proyecto.dh.resources.product.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.dh.resources.product.dto.ProductDto;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.exception.ResourceNotFoundException;
import proyecto.dh.resources.product.mapper.ProductMapper;
import proyecto.dh.resources.product.repository.IProductRepository;
import proyecto.dh.resources.product.service.IProductService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements IProductService {

    private IProductRepository iProductRepository;
    @Override
    public ProductDto createProduct(ProductDto productDto) {

        Product product = ProductMapper.mapToProduct(productDto);
        Product savedProduct = iProductRepository.save(product);
        return ProductMapper.mapToProductDto(savedProduct);
    }

    @Override
    public ProductDto getProduct(Long productId) {
        Product product = iProductRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product is not exist with given id: " + productId));
        return ProductMapper.mapToProductDto(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> products = iProductRepository.findAll();
        return products.stream().map((product) -> ProductMapper.mapToProductDto(product))
                .collect(Collectors.toList());
    }
}
