package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.entity.ProductPolicy;
import proyecto.dh.resources.product.service.PolicyService;

import java.util.List;

@RestController
@RequestMapping("/public/products/policies")
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
}
