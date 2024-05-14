package proyecto.dh.resources.product.service;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;


    public Product save(Product userObject) {
        Optional<Product> existingProduct = productRepository.findByName(userObject.getName());
        if(existingProduct.isPresent()){
            throw new IllegalArgumentException("The product name is already in use");
        }
        return productRepository.save(userObject);
    }
    public Product update(Long id, Product userObject) throws NotFoundException {
        Product existingProduct = findById(id);
        modelMapper.map(userObject, existingProduct);
        return productRepository.save(existingProduct);
    }
    public void delete(Long id) throws NotFoundException {
        Product findProduct = findById(id);
        productRepository.deleteById(id);
    }
    public List<Product> findAll() { return productRepository.findAll(); }
    public Product findById(Long id) throws NotFoundException {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("El producto no existe"));
    }
}
