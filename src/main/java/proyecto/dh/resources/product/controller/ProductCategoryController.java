package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.service.ProductCategoryService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllCategories() {
        List<ProductCategory> categories = productCategoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ProductCategory> getCategoryById(@PathVariable Long id) {
        try {
            ProductCategory category = productCategoryService.findById(id);
            return ResponseEntity.ok(category);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<ProductCategory> createCategory(@RequestBody ProductCategory category) {
        ProductCategory createdCategory = productCategoryService.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        productCategoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{categoryId}/attachment")
    public ResponseEntity<ProductCategory> uploadCategoryAttachment(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            ProductCategory updatedCategory = productCategoryService.uploadCategoryAttachment(id, file);
            return ResponseEntity.ok(updatedCategory);
        } catch (IOException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{categoryId}/attachment")
    public ResponseEntity<Attachment> getCategoryAttachment(@PathVariable Long id) {
        try {
            Attachment attachment = productCategoryService.getCategoryAttachment(id);
            return ResponseEntity.ok(attachment);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
