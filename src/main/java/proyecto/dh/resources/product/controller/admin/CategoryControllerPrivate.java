package proyecto.dh.resources.product.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.category.ProductCategoryDTO;
import proyecto.dh.resources.product.dto.category.ProductCategorySaveDTO;
import proyecto.dh.resources.product.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryControllerPrivate {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ProductCategoryDTO> create(@RequestBody ProductCategorySaveDTO categorySaveDTO) throws BadRequestException, NotFoundException {
        ProductCategoryDTO savedCategory = categoryService.save(categorySaveDTO);
        return ResponseEntity.ok(savedCategory);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ProductCategoryDTO> update(@PathVariable Long id, @RequestBody ProductCategorySaveDTO categorySaveDTO) throws NotFoundException, BadRequestException {
        ProductCategoryDTO updatedCategory = categoryService.updateCategory(id, categorySaveDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable Long id) throws BadRequestException, NotFoundException {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}