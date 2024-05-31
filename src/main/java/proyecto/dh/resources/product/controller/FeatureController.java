package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.common.responses.ResponseDTO;
import proyecto.dh.common.responses.ResponseHandler;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.entity.ProductFeature;
import proyecto.dh.resources.product.service.FeatureService;

import java.util.List;

@RestController
@RequestMapping("/products/features")
public class FeatureController {

    @Autowired
    private FeatureService service;

    @GetMapping
    public ResponseEntity<List<ProductFeature>> findAll() {
        List<ProductFeature> features = service.findAll();
        return ResponseEntity.ok(features);
    }

    @GetMapping("/{featureId}")
    public ResponseEntity<ProductFeature> findById(@PathVariable Long id) throws NotFoundException {
            ProductFeature feature = service.findById(id);
            return ResponseEntity.ok(feature);
    }

    @PostMapping
    public ResponseEntity<ProductFeature> create(@RequestBody ProductFeature userObject) {
        ProductFeature createdFeature = service.save(userObject);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFeature);
    }

    @DeleteMapping("/{featureId}")
    public ResponseEntity<ResponseDTO<Void>> deleteCategory(@PathVariable Long id) throws BadRequestException {
        try {
            service.deleteById(id);
            return ResponseHandler.generateResponse("Feature deleted successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
