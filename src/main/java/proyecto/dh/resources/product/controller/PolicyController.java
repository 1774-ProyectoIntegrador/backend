package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.ProductPolicyDTO;
import proyecto.dh.resources.product.entity.ProductPolicy;
import proyecto.dh.resources.product.service.PolicyService;

import java.util.List;

@RestController
@RequestMapping("/public/products/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @GetMapping
    public ResponseEntity<List<ProductPolicyDTO>> findAll() {
        List<ProductPolicyDTO> policies = policyService.findAll();
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<ProductPolicyDTO> findById(@PathVariable Long policyId) throws NotFoundException {
        ProductPolicyDTO policy = policyService.findById(policyId);
        return ResponseEntity.ok(policy);
    }
}
