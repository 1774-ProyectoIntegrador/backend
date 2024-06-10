package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.CategoryDTO;
import proyecto.dh.resources.product.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("public/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> categories = categoryService.findAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) throws NotFoundException {
        CategoryDTO category = categoryService.findById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }
}