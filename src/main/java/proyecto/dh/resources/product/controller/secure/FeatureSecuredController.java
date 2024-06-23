package proyecto.dh.resources.product.controller.secure;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.CategoryFeatureDTO;
import proyecto.dh.resources.product.dto.CategoryFeatureSaveDTO;
import proyecto.dh.resources.product.service.FeatureService;

@RestController
@RequestMapping("/products/features")
public class FeatureSecuredController {

    private final FeatureService featureService;

    public FeatureSecuredController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @PostMapping
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<CategoryFeatureDTO> create(@RequestBody CategoryFeatureSaveDTO featureSaveDTO) throws NotFoundException {
        CategoryFeatureDTO createdFeature = featureService.save(featureSaveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFeature);
    }

/*    @PutMapping("/{featureId}")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<CategoryFeatureDTO> update(@PathVariable Long featureId, @RequestBody CategoryFeatureSaveDTO featureSaveDTO) throws NotFoundException {
        CategoryFeatureDTO updatedFeature = featureService.updateFeature(featureId, featureSaveDTO);
        return ResponseEntity.ok(updatedFeature);
    }

    @DeleteMapping("/{featureId}")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<Void> delete(@PathVariable Long featureId) throws NotFoundException {
        featureService.deleteById(featureId);
        return ResponseEntity.ok().build();
    }*/
}
