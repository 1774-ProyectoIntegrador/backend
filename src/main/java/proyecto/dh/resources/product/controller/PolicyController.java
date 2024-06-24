package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.CategoryPolicyDTO;
import proyecto.dh.resources.product.service.PolicyService;

import java.util.List;

@RestController
@RequestMapping("/public/categories/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @GetMapping
    public ResponseEntity<List<CategoryPolicyDTO>> findAll() {
        List<CategoryPolicyDTO> policies = policyService.findAll();
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<CategoryPolicyDTO> findById(@PathVariable Long policyId) throws NotFoundException {
        CategoryPolicyDTO policy = policyService.findById(policyId);
        return ResponseEntity.ok(policy);
    }
}
