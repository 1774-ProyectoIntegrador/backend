package proyecto.dh.resources.product.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.dto.category.ProductCategoryDTO;
import proyecto.dh.resources.product.dto.category.ProductCategorySaveDTO;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;

import java.util.List;
import java.util.Optional;
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
    public ProductCategoryDTO save(ProductCategorySaveDTO categorySaveDTO) throws BadRequestException, NotFoundException {
        validateSlug(categorySaveDTO.getSlug());

        if (repository.existsByName(categorySaveDTO.getName())) {
            throw new BadRequestException("Categoría con el nombre '" + categorySaveDTO.getName() + "' ya existe");
        } else if (repository.existsBySlug(categorySaveDTO.getSlug())) {
            throw new BadRequestException("Categoría con el slug '" + categorySaveDTO.getSlug() + "' ya existe");
        }
        ProductCategory category = convertToEntity(categorySaveDTO);

        if (categorySaveDTO.getAttachmentId() != null) {
            Attachment attachment = attachmentService.findById(categorySaveDTO.getAttachmentId());
            attachmentService.validateFileTypeImages(attachment);
            category.setAttachment(attachment);
        }

        ProductCategory savedCategory = repository.save(category);
        return convertToDTO(savedCategory);
    }

    @Transactional
    public ProductCategoryDTO updateCategory(Long id, ProductCategorySaveDTO categorySaveDTO) throws NotFoundException, BadRequestException {
        ProductCategory existingCategory = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Categoría con ID " + id + " no encontrada."));
        validateSlug(categorySaveDTO.getSlug());

        modelMapper.map(categorySaveDTO, existingCategory);

        if (categorySaveDTO.getName() != null && repository.existsByName(categorySaveDTO.getName()) && !existingCategory.getName().equals(categorySaveDTO.getName())) {
            throw new BadRequestException("Categoría con el nombre '" + categorySaveDTO.getName() + "' ya existe");
        }

        if (categorySaveDTO.getSlug() != null && repository.existsBySlug(categorySaveDTO.getSlug()) && !existingCategory.getSlug().equals(categorySaveDTO.getSlug())) {
            throw new BadRequestException("Categoría con el slug '" + categorySaveDTO.getSlug() + "' ya existe");
        }

        if (categorySaveDTO.getAttachmentId() != null) {
            Attachment attachment = attachmentService.findById(categorySaveDTO.getAttachmentId());
            attachmentService.validateFileTypeImages(attachment);
            existingCategory.setAttachment(attachment);
        }

        ProductCategory savedCategory = repository.save(existingCategory);
        return convertToDTO(savedCategory);
    }

    public List<ProductCategoryDTO> findAll() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id) throws BadRequestException, NotFoundException {
        ProductCategory category = findByIdEntity(id)
                .orElseThrow(() -> new NotFoundException("Categoría con ID " + id + " no encontrada."));

        if (category.getAttachment() != null) {
            attachmentService.deleteAttachment(category.getAttachment().getId());
        }

        repository.deleteById(id);
    }

    public ProductCategoryDTO findById(Long id) throws NotFoundException {
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

    private ProductCategoryDTO convertToDTO(ProductCategory category) {
        return modelMapper.map(category, ProductCategoryDTO.class);
    }

    private ProductCategory convertToEntity(ProductCategorySaveDTO categorySaveDTO) {
        return modelMapper.map(categorySaveDTO, ProductCategory.class);
    }
}
