package proyecto.dh.resources.product.controller.secure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.CategoryDTO;
import proyecto.dh.resources.product.dto.CategorySaveDTO;
import proyecto.dh.resources.product.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategorySecuredController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<CategoryDTO> create(@RequestBody CategorySaveDTO categorySaveDTO) throws BadRequestException, NotFoundException {
        CategoryDTO savedCategory = categoryService.save(categorySaveDTO);
        return ResponseEntity.ok(savedCategory);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategorySaveDTO categorySaveDTO) throws NotFoundException, BadRequestException {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categorySaveDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<Void> delete(@PathVariable Long id) throws BadRequestException, NotFoundException {
        categoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}