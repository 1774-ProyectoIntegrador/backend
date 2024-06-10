package proyecto.dh.resources.product.service;

import com.amazonaws.services.kms.model.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.dh.resources.product.dto.ProductFeatureDTO;
import proyecto.dh.resources.product.dto.ProductFeatureSaveDTO;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.entity.ProductFeature;
import proyecto.dh.resources.product.repository.ProductFeatureRepository;
import proyecto.dh.resources.product.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeatureService {

    private final ProductFeatureRepository featureRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public FeatureService(ProductFeatureRepository featureRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.featureRepository = featureRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ProductFeatureDTO save(ProductFeatureSaveDTO featureSaveDTO) {
        ProductFeature feature = convertToEntity(featureSaveDTO);
        syncFeatureWithProducts(feature, featureSaveDTO.getProductIds());
        ProductFeature savedFeature = featureRepository.save(feature);
        return convertToDTO(savedFeature);
    }

    @Transactional
    public ProductFeatureDTO updateFeature(Long id, ProductFeatureSaveDTO featureSaveDTO) throws NotFoundException {
        ProductFeature existingFeature = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Caracteristica con ID " + id + " no encontrada"));

        modelMapper.map(featureSaveDTO, existingFeature);
        existingFeature.getProduct().clear();
        syncFeatureWithProducts(existingFeature, featureSaveDTO.getProductIds());

        ProductFeature savedFeature = featureRepository.save(existingFeature);
        return convertToDTO(savedFeature);
    }

    public List<ProductFeatureDTO> findAll() {
        return featureRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id) throws NotFoundException {
        ProductFeature feature = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Caracteristica con ID " + id + " no encontrada"));

        for (Product product : feature.getProduct()) {
            product.getProductFeatures().remove(feature);
        }

        featureRepository.deleteById(id);
    }

    public ProductFeatureDTO findById(Long id) throws NotFoundException {
        ProductFeature feature = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Caracteristica con ID " + id + " no encontrada"));
        return convertToDTO(feature);
    }

    private Optional<ProductFeature> findByIdEntity(Long id) {
        return featureRepository.findById(id);
    }

    private ProductFeatureDTO convertToDTO(ProductFeature feature) {
        ProductFeatureDTO featureDTO = modelMapper.map(feature, ProductFeatureDTO.class);
        featureDTO.setProductIds(
                feature.getProduct().stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())
        );
        return featureDTO;
    }

    private ProductFeature convertToEntity(ProductFeatureSaveDTO featureSaveDTO) {
        return modelMapper.map(featureSaveDTO, ProductFeature.class);
    }

    private void syncFeatureWithProducts(ProductFeature feature, List<Long> productIds) {
        if (productIds != null) {
            for (Long productId : productIds) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new NotFoundException("El producto no existe"));
                feature.getProduct().add(product);
                product.getProductFeatures().add(feature);
            }
        }
    }
}
