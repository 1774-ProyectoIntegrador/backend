package proyecto.dh.resources.product.service;

import com.amazonaws.services.kms.model.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.product.dto.ProductFeatureDTO;
import proyecto.dh.resources.product.dto.ProductFeatureSaveDTO;
import proyecto.dh.resources.product.dto.ProductPolicyDTO;
import proyecto.dh.resources.product.dto.ProductPolicySaveDTO;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.entity.ProductFeature;
import proyecto.dh.resources.product.entity.ProductPolicy;
import proyecto.dh.resources.product.repository.ProductPolicyRepository;
import proyecto.dh.resources.product.repository.ProductRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private final ProductPolicyRepository policyRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public PolicyService(ProductPolicyRepository policyRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.policyRepository = policyRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }


    // ---- Funciones CRUD ----

    @Transactional
    public ProductPolicyDTO save(ProductPolicySaveDTO policySaveDTO) {
        ProductPolicy policy = convertToEntity(policySaveDTO);
        syncPolicyWithProducts(policy, policySaveDTO.getProductIds());
        ProductPolicy savedPolicy = policyRepository.save(policy);
        return convertToDTO(savedPolicy);
    }

    @Transactional
    public ProductPolicyDTO updatePolicy(Long id, ProductPolicySaveDTO policySaveDTO) throws com.amazonaws.services.kms.model.NotFoundException {
        ProductPolicy existingPolicy = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Política con ID " + id + " no encontrada"));

        modelMapper.map(policySaveDTO, existingPolicy);

        // Limpia la relación antigua en ambas entidades
        if (existingPolicy.getProduct() != null) {
            existingPolicy.getProduct().forEach(product -> product.getProductPolicies().remove(existingPolicy));
            existingPolicy.getProduct().clear();
        }

        syncPolicyWithProducts(existingPolicy, policySaveDTO.getProductIds());

        ProductPolicy savedPolicy = policyRepository.save(existingPolicy);
        return convertToDTO(savedPolicy);
    }

    public List<ProductPolicyDTO> findAll() {
        return policyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id) throws NotFoundException {
        ProductPolicy policy = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Política con ID " + id + " no encontrada"));

        for (Product product : policy.getProduct()) {
            product.getProductPolicies().remove(policy);
        }

        policyRepository.deleteById(id);
    }

    public ProductPolicyDTO findById(Long id) throws NotFoundException {
        ProductPolicy policy = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Política con ID " + id + " no encontrada"));
        return convertToDTO(policy);
    }

    // ---- Funciones complementarias ----


    private ProductPolicy convertToEntity(ProductPolicySaveDTO policySaveDTO) {
        return modelMapper.map(policySaveDTO, ProductPolicy.class);
    }


    private void syncPolicyWithProducts(ProductPolicy policy, List<Long> productIds) {


        if (productIds != null) {
            for (Long productId : productIds) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new com.amazonaws.services.kms.model.NotFoundException("La política no existe"));

                // Se añade para manejar los valores nulos
                if (policy.getProduct() == null) {
                    policy.setProduct(new HashSet<>());
                }
                policy.getProduct().add(product);
                product.getProductPolicies().add(policy);
            }
        }
    }

    private ProductPolicyDTO convertToDTO(ProductPolicy policy) {
        ProductPolicyDTO policyDTO = modelMapper.map(policy, ProductPolicyDTO.class);
        policyDTO.setProductIds(
                policy.getProduct().stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())
        );
        return policyDTO;
    }

    private Optional<ProductPolicy> findByIdEntity(Long id) {
        return policyRepository.findById(id);
    }

}
