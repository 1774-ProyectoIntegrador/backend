package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.entity.ImageProduct;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.service.ProductService;
import proyecto.dh.resources.product.service.S3Service;
import proyecto.dh.responses.ResponseDTO;
import proyecto.dh.responses.ResponseHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private S3Service s3Service;


    @PostMapping("create")
    public Product create(@RequestBody Product userObject) {
        return productService.save(userObject);
    }

    @PutMapping("update/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product userObject) throws NotFoundException {
        return productService.update(id, userObject);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) throws NotFoundException {
        productService.delete(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }

    @GetMapping
    public List<Product> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id) throws NotFoundException {
        return productService.findById(id);
    }

    @PostMapping("/{productId}/images")
    public ResponseEntity<ResponseDTO<String>> uploadProductImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) {
        try {
            Product product = productService.findById(productId);
            if (product == null) {
                return ResponseHandler.generateResponse("Product not found", HttpStatus.NOT_FOUND, null);
            }

            ImageProduct imageProduct = s3Service.uploadFile(file, product);
            String imageUrl = s3Service.generatePresignedUrl(imageProduct.getFileName()).toString();
            return ResponseHandler.generateResponse("Image uploaded successfully", HttpStatus.OK, imageUrl);
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Error uploading file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/{productId}/images")
    public ResponseEntity<List<String>> getProductImages(@PathVariable Long productId) throws NotFoundException {
        Product product = productService.findById(productId);

        List<String> imageUrls = product.getImages().stream()
                .map(imageProduct -> s3Service.generatePresignedUrl(imageProduct.getFileName()).toString())
                .collect(Collectors.toList());

        return ResponseEntity.ok(imageUrls);
    }
}
