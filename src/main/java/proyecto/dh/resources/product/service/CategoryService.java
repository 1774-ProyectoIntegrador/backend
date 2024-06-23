package proyecto.dh.resources.product.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.dto.*;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.entity.ProductCategoryFeature;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private ProductCategoryRepository repository;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public CategoryDTO save(CategorySaveDTO categorySaveDTO) throws BadRequestException, NotFoundException {
        validateSlug(categorySaveDTO.getSlug());
        checkCategoryExistence(categorySaveDTO.getName(), categorySaveDTO.getSlug());

        ProductCategory category = convertToEntity(categorySaveDTO);

        setCategoryFeatures(category, categorySaveDTO.getFeatures());
        handleAttachment(category, categorySaveDTO.getAttachmentId());

        ProductCategory savedCategory = repository.save(category);
        return convertToDTO(savedCategory);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategorySaveDTO categorySaveDTO) throws NotFoundException, BadRequestException {
        ProductCategory existingCategory = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Categoría con ID " + id + " no encontrada."));
        validateSlug(categorySaveDTO.getSlug());
        checkCategoryExistenceForUpdate(categorySaveDTO.getName(), categorySaveDTO.getSlug(), existingCategory);

        modelMapper.map(categorySaveDTO, existingCategory);
        handleAttachment(existingCategory, categorySaveDTO.getAttachmentId());

        ProductCategory savedCategory = repository.save(existingCategory);
        return convertToDTO(savedCategory);
    }

    public List<CategoryDTO> findAll() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id) throws BadRequestException, NotFoundException {
        ProductCategory category = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Categoría con ID " + id + " no encontrada."));

        repository.deleteById(id);
    }

    public CategoryDTO findById(Long id) throws NotFoundException {
        ProductCategory category = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Categoría con ID " + id + " no encontrada."));
        return convertToDTO(category);
    }

    private Optional<ProductCategory> findByIdEntity(Long id) {
        return repository.findById(id);
    }

    private void validateSlug(String slug) throws BadRequestException {
        if (slug == null || !slug.equals(slug.toLowerCase()) || slug.contains(" ")) {
            throw new BadRequestException("El slug debe estar en minúsculas y no debe contener espacios");
        }
    }

    private CategoryDTO convertToDTO(ProductCategory category) {
        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
        if (category.getProductCategoryFeatures() != null) {
            List<CategoryFeatureDTO> features = category.getProductCategoryFeatures().stream()
                    .map(feature -> modelMapper.map(feature, CategoryFeatureDTO.class))
                    .collect(Collectors.toList());
            categoryDTO.setFeatures(features);
        }

/*        if (category.getProductPolicies() != null) {
            List<ProductPolicyDTO> policies = category.getProductPolicies().stream()
                    .map(policy -> modelMapper.map(policy, ProductPolicyDTO.class))
                    .collect(Collectors.toList());
            categoryDTO.setPolicies(policies);
        }*/

        return categoryDTO;
    }

    private ProductCategory convertToEntity(CategorySaveDTO categorySaveDTO) {
        return modelMapper.map(categorySaveDTO, ProductCategory.class);
    }

    private void checkCategoryExistence(String name, String slug) throws BadRequestException {
        if (repository.existsByName(name)) {
            throw new BadRequestException("Categoría con el nombre '" + name + "' ya existe");
        }
        if (repository.existsBySlug(slug)) {
            throw new BadRequestException("Categoría con el slug '" + slug + "' ya existe");
        }
    }

    private void checkCategoryExistenceForUpdate(String name, String slug, ProductCategory existingCategory) throws BadRequestException {
        if (name != null && repository.existsByName(name) && !existingCategory.getName().equals(name)) {
            throw new BadRequestException("Categoría con el nombre '" + name + "' ya existe");
        }
        if (slug != null && repository.existsBySlug(slug) && !existingCategory.getSlug().equals(slug)) {
            throw new BadRequestException("Categoría con el slug '" + slug + "' ya existe");
        }
    }

    private void handleAttachment(ProductCategory category, Long attachmentId) throws NotFoundException, BadRequestException {
        if (attachmentId != null) {
            Attachment attachment = attachmentService.findById(attachmentId);
            attachmentService.validateFileTypeImages(attachment);
            category.setAttachment(attachment);
        }
    }



    private void setCategoryFeatures(ProductCategory category, List<CategoryFeatureSaveDTO> features) {
        // Se añade para manejar los valores nulos
        Set<ProductCategoryFeature> featureSet = Optional.ofNullable(features)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(featureSaveDTO -> modelMapper.map(featureSaveDTO, ProductCategoryFeature.class))
                .peek(feature -> {
                    if (feature.getCategories() == null) {
                        feature.setCategories(Set.of(category));
                    } else {
                        feature.getCategories().add(category);
                    }
                })
                .collect(Collectors.toSet());

        category.setProductCategoryFeatures(featureSet);
    }

/*    private void updateFeatures(ProductCategory category, List<CategoryFeatureSaveDTO> features) {
        category.getProductCategoryFeatures().clear();
        setCategoryFeatures(category, features);
    }*/

}