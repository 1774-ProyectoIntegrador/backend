package proyecto.dh.resources.product.controller.secure;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.common.responses.ResponseDTO;
import proyecto.dh.common.responses.ResponseHandler;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.ProductFeatureDTO;
import proyecto.dh.resources.product.dto.ProductFeatureSaveDTO;
import proyecto.dh.resources.product.entity.ProductFeature;
import proyecto.dh.resources.product.service.FeatureService;

@RestController
@RequestMapping("/products/features")
public class FeatureSecuredController {

    private final FeatureService featureService;

    public FeatureSecuredController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @PostMapping
    public ResponseEntity<ProductFeatureDTO> create(@RequestBody ProductFeatureSaveDTO featureSaveDTO) throws NotFoundException {
        ProductFeatureDTO createdFeature = featureService.save(featureSaveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFeature);
    }

    @PutMapping("/{featureId}")
    public ResponseEntity<ProductFeatureDTO> update(@PathVariable Long featureId, @RequestBody ProductFeatureSaveDTO featureSaveDTO) throws NotFoundException {
        ProductFeatureDTO updatedFeature = featureService.updateFeature(featureId, featureSaveDTO);
        return ResponseEntity.ok(updatedFeature);
    }

    @DeleteMapping("/{featureId}")
    public ResponseEntity<Void> delete(@PathVariable Long featureId) throws NotFoundException {
        featureService.deleteById(featureId);
        return ResponseEntity.ok().build();
    }
}
