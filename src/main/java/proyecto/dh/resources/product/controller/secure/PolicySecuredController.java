package proyecto.dh.resources.product.controller.secure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.common.responses.ResponseDTO;
import proyecto.dh.common.responses.ResponseHandler;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.ProductFeatureDTO;
import proyecto.dh.resources.product.dto.ProductFeatureSaveDTO;
import proyecto.dh.resources.product.dto.ProductPolicyDTO;
import proyecto.dh.resources.product.dto.ProductPolicySaveDTO;
import proyecto.dh.resources.product.entity.ProductPolicy;
import proyecto.dh.resources.product.service.PolicyService;

@RestController
@RequestMapping("/products/policies")
public class PolicySecuredController {

    private final PolicyService policyService;

    public PolicySecuredController(PolicyService service) {
        this.policyService = service;
    }

    @PostMapping
    public ResponseEntity<ProductPolicyDTO> create(@RequestBody ProductPolicySaveDTO policySaveDTO) {
        ProductPolicyDTO createdPolicy = policyService.save(policySaveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
    }

    @PutMapping("/{policyId}")
    public ResponseEntity<ProductPolicyDTO> update(@PathVariable Long policyId, @RequestBody ProductPolicySaveDTO policySaveDTO) throws NotFoundException {
        ProductPolicyDTO updatedPolicy = policyService.updatePolicy(policyId, policySaveDTO);
        return ResponseEntity.ok(updatedPolicy);
    }

    @DeleteMapping("/{policyId}")
    public ResponseEntity<Void> delete(@PathVariable Long policyId) throws NotFoundException {
        policyService.deleteById(policyId);
        return ResponseEntity.ok().build();
    }
}
