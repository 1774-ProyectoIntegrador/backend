package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.entity.ImageProduct;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.service.ImageService;
import proyecto.dh.resources.product.service.ProductService;
import proyecto.dh.responses.ResponseDTO;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @PostMapping("")
    public Product create(@RequestBody Product userObject) {
        return productService.save(userObject);
    }

    @PutMapping("{id}")
    public Product update(@PathVariable Long id, @RequestBody Product userObject) throws NotFoundException {
        return productService.update(id, userObject);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) throws NotFoundException {
        productService.delete(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }

    @GetMapping
    public List<Product> findAll() {
        return productService.findAll();
    }

    @GetMapping("{id}")
    public Product findById(@PathVariable Long id) throws NotFoundException {
        return productService.findById(id);
    }

    @PostMapping("{productId}/images")
    public ResponseEntity<ResponseDTO<String>> uploadProductImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) throws BadRequestException {
        return imageService.uploadProductImage(productId, file);
    }

    @DeleteMapping("{productId}/images/{imageId}")
    public ResponseEntity<ResponseDTO<String>> deleteProductImage(@PathVariable Long productId, @PathVariable Long imageId) throws BadRequestException {
        return imageService.deleteProductImage(productId, imageId);
    }

    @GetMapping("{productId}/images")
    public ResponseEntity<List<ImageProduct>> getProductImages(@PathVariable Long productId) throws NotFoundException {
        return imageService.getProductImages(productId);
    }
}
