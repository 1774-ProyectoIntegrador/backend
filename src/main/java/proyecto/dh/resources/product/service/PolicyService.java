package proyecto.dh.resources.product.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.category.ProductCategorySaveDTO;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.entity.ProductPolicy;
import proyecto.dh.resources.product.repository.ProductPolicyRepository;

import java.util.List;

@Service
public class PolicyService {

    @Autowired
    private ProductPolicyRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public ProductPolicy save(ProductPolicy policy) {
        return repository.save(policy);
    }

    public List<ProductPolicy> findAll() {
        return repository.findAll();
    }

    public ProductPolicy findById(Long id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Policy not found"));
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public ProductPolicy updatePolicy(Long id, ProductPolicy policy) throws NotFoundException, BadRequestException {
        ProductPolicy existingPolicy = findById(id);
        modelMapper.map(policy, existingPolicy);

        return repository.save(existingPolicy);
    }

}
