package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.product.dto.CreateProductDTO;
import proyecto.dh.resources.product.dto.ProductDTO;
import proyecto.dh.resources.product.dto.UpdateProductDTO;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.service.ProductService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("")
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody CreateProductDTO createProductDTO) {
        try {
            Product product = productService.save(createProductDTO);
            ProductDTO productDTO = productService.convertToDTO(product);
            return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateProductDTO productUpdateDTO) throws NotFoundException {
        Product updatedProduct = productService.updateProduct(id, productUpdateDTO);
        ProductDTO productDTO = productService.convertToDTO(updatedProduct);
        return ResponseEntity.ok(productDTO);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) throws NotFoundException {
        productService.delete(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> findAll() {
        List<ProductDTO> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) throws NotFoundException {
        Product product = productService.findById(id);
        ProductDTO productDTO = productService.convertToDTO(product);
        return ResponseEntity.ok(productDTO);
    }

    @PostMapping("{productId}/attachments")
    public ResponseEntity<ProductDTO> uploadProductAttachments(@PathVariable Long productId, @RequestParam("files") List<MultipartFile> files) {
        try {
            Product product = productService.uploadProductAttachments(productId, files);
            ProductDTO productDTO = productService.convertToDTO(product);
            return ResponseEntity.ok(productDTO);
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
