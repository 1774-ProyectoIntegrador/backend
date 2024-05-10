package proyecto.dh.resources.product.controller;

import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.service.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
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

    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id) throws NotFoundException {
        return productService.findById(id);
    }
}