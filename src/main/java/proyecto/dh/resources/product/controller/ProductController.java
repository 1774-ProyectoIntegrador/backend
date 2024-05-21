package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.product.dto.UpdateProductDTO;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.service.ProductService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("")
    public Product create(@RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping("{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody UpdateProductDTO productUpdateDTO) throws NotFoundException {
        Product updatedProduct = productService.updateProduct(id, productUpdateDTO);
        return ResponseEntity.ok(updatedProduct);
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

    @PostMapping("{productId}/attachments")
    public ResponseEntity<Product> uploadProductAttachments(@PathVariable Long productId, @RequestParam("files") List<MultipartFile> files) {
        try {
            Product product = productService.uploadProductAttachments(productId, files);
            return ResponseEntity.ok(product);
        } catch (IOException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("{productId}/attachments")
    public ResponseEntity<List<Attachment>> getProductAttachments(@PathVariable Long productId) {
        try {
            List<Attachment> attachments = productService.getProductAttachments(productId);
            return ResponseEntity.ok(attachments);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
