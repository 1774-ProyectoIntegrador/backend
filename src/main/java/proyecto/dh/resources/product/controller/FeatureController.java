package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.ProductFeatureDTO;
import proyecto.dh.resources.product.dto.ProductFeatureSaveDTO;
import proyecto.dh.resources.product.service.FeatureService;

import java.util.List;

@RestController
@RequestMapping("/public/products/features")
public class FeatureController {

    @Autowired
    private FeatureService featureService;

    @GetMapping
    public ResponseEntity<List<ProductFeatureDTO>> findAll() {
        List<ProductFeatureDTO> features = featureService.findAll();
        return ResponseEntity.ok(features);
    }

    @GetMapping("/{featureId}")
    public ResponseEntity<ProductFeatureDTO> findById(@PathVariable Long featureId) throws NotFoundException {
        ProductFeatureDTO feature = featureService.findById(featureId);
        return ResponseEntity.ok(feature);
    }
}
