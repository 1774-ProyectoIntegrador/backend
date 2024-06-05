package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.common.responses.ResponseDTO;
import proyecto.dh.common.responses.ResponseHandler;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.category.ProductCategoryDTO;
import proyecto.dh.resources.product.dto.category.ProductCategorySaveDTO;
import proyecto.dh.resources.product.entity.ProductPolicy;
import proyecto.dh.resources.product.service.PolicyService;

import java.util.List;

@RestController
@RequestMapping("/products/policies")
public class PolicyController {

    @Autowired
    private PolicyService service;

    @GetMapping
    public ResponseEntity<List<ProductPolicy>> findAll() {
        List<ProductPolicy> policies = service.findAll();
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<ProductPolicy> findById(@PathVariable Long policyId) throws NotFoundException {
        ProductPolicy policy = service.findById(policyId);
        return ResponseEntity.ok(policy);
    }

    @PostMapping
    public ResponseEntity<ProductPolicy> create(@RequestBody ProductPolicy userObject) {
        ProductPolicy createdPolicy = service.save(userObject);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
    }

    @PutMapping("/{policyId}")
    public ResponseEntity<ProductPolicy> update(@PathVariable Long policyId, @RequestBody ProductPolicy policy) throws NotFoundException, BadRequestException {
        ProductPolicy updatedPolicy = service.updatePolicy(policyId, policy);
        return ResponseEntity.ok(updatedPolicy);
    }

    @DeleteMapping("/{policyId}")
    public ResponseEntity<ResponseDTO<Void>> deletePolicy(@PathVariable Long policyId) throws BadRequestException {
        try {
            service.deleteById(policyId);
            return ResponseHandler.generateResponse("Policy deleted successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
