package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;
import proyecto.dh.resources.product.dto.category.ProductCategoryDTO;
import proyecto.dh.resources.product.dto.category.ProductCategorySaveDTO;
import proyecto.dh.resources.product.service.ProductCategoryService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<ProductCategoryDTO>> findAll() {
        List<ProductCategoryDTO> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryDTO> findById(@PathVariable Long id) throws NotFoundException {
        ProductCategoryDTO category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<ProductCategoryDTO> create(@RequestBody ProductCategorySaveDTO categorySaveDTO) throws BadRequestException {
        ProductCategoryDTO savedCategory = categoryService.save(categorySaveDTO);
        return ResponseEntity.ok(savedCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryDTO> update(@PathVariable Long id, @RequestBody ProductCategorySaveDTO categorySaveDTO) throws NotFoundException, BadRequestException {
        ProductCategoryDTO updatedCategory = categoryService.updateCategory(id, categorySaveDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<ProductCategoryDTO> uploadAttachments(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException, NotFoundException {
        ProductCategoryDTO categoryDTO = categoryService.uploadCategoryAttachment(id, file);
        return ResponseEntity.ok(categoryDTO);
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<AttachmentDTO> getAttachments(@PathVariable Long id) throws NotFoundException {
        AttachmentDTO attachment = categoryService.getCategoryAttachment(id);
        return ResponseEntity.ok(attachment);
    }
}