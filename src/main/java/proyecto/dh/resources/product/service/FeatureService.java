package proyecto.dh.resources.product.service;

import com.amazonaws.services.kms.model.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.dh.resources.product.dto.CategoryFeatureDTO;
import proyecto.dh.resources.product.dto.CategoryFeatureSaveDTO;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.entity.ProductCategoryFeature;
import proyecto.dh.resources.product.repository.CategoryFeatureRepository;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeatureService {

    private final CategoryFeatureRepository featureRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public FeatureService(CategoryFeatureRepository featureRepository, ProductCategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.featureRepository = featureRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CategoryFeatureDTO save(CategoryFeatureSaveDTO featureSaveDTO) {
        ProductCategoryFeature feature = convertToEntity(featureSaveDTO);
        syncFeatureWithCategories(feature, featureSaveDTO.getCategoryIds());
        ProductCategoryFeature savedFeature = featureRepository.save(feature);
        return convertToDTO(savedFeature);
    }


    public List<CategoryFeatureDTO> findAll() {
        return featureRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryFeatureDTO findById(Long id) throws NotFoundException {
        ProductCategoryFeature feature = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Caracteristica con ID " + id + " no encontrada"));
        return convertToDTO(feature);
    }

    private Optional<ProductCategoryFeature> findByIdEntity(Long id) {
        return featureRepository.findById(id);
    }

/*
    @Transactional
    public CategoryFeatureDTO updateFeature(Long id, CategoryFeatureSaveDTO featureSaveDTO) throws NotFoundException {
        ProductCategoryFeature existingFeature = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Caracteristica con ID " + id + " no encontrada"));

        modelMapper.map(featureSaveDTO, existingFeature);

        // Limpia la relación antigua en ambas entidades
        if (existingFeature.getProduct() != null) {
            existingFeature.getProduct().forEach(product -> product.getProductCategoryFeatures().remove(existingFeature));
            existingFeature.getProduct().clear();
        }
        syncFeatureWithCategories(existingFeature, featureSaveDTO.getProductIds());

        ProductCategoryFeature savedFeature = featureRepository.save(existingFeature);
        return convertToDTO(savedFeature);
    }

    @Transactional
    public void deleteById(Long id) throws NotFoundException {
        ProductCategoryFeature feature = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Caracteristica con ID " + id + " no encontrada"));

        for (Product product : feature.getProduct()) {
            product.getProductCategoryFeatures().remove(feature);
        }

        featureRepository.deleteById(id);
    }




*/

    private CategoryFeatureDTO convertToDTO(ProductCategoryFeature feature) {
        CategoryFeatureDTO featureDTO = modelMapper.map(feature, CategoryFeatureDTO.class);
        featureDTO.setCategoryIds(feature.getCategories().stream().map(ProductCategory::getId).collect(Collectors.toList()));
        return featureDTO;
    }

    private ProductCategoryFeature convertToEntity(CategoryFeatureSaveDTO featureSaveDTO) {
        return modelMapper.map(featureSaveDTO, ProductCategoryFeature.class);
    }

    private void syncFeatureWithCategories(ProductCategoryFeature feature, List<Long> categoryIds) {
        if (categoryIds != null) {
            for (Long categoryId : categoryIds) {
                ProductCategory category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("La categoría no existe"));

                // Se añade para manejar los valores nulos
                if (feature.getCategories() == null) {
                    feature.setCategories(new HashSet<>());
                }

                feature.getCategories().add(category);
                category.getProductCategoryFeatures().add(feature);
            }
        }
    }
}
