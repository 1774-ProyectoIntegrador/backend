package proyecto.dh.resources.product.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.dto.category.ProductCategoryDTO;
import proyecto.dh.resources.product.dto.category.ProductCategorySaveDTO;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository repository;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private ModelMapper modelMapper;

    public List<ProductCategoryDTO> findAll() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductCategoryDTO findById(Long id) throws NotFoundException {
        ProductCategory category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
        return convertToDTO(category);
    }

    public ProductCategoryDTO save(ProductCategorySaveDTO categorySaveDTO) throws BadRequestException {
        if (repository.existsByName(categorySaveDTO.getName())) {
            throw new BadRequestException("Categoría con el nombre '" + categorySaveDTO.getName() + "' ya existe");
        } else if (repository.existsBySlug(categorySaveDTO.getSlug())) {
            throw new BadRequestException("Categoría con el slug '" + categorySaveDTO.getSlug() + "' ya existe");
        }
        ProductCategory category = convertToEntity(categorySaveDTO);
        ProductCategory savedCategory = repository.save(category);
        return convertToDTO(savedCategory);
    }

    public ProductCategoryDTO updateCategory(Long id, ProductCategorySaveDTO categorySaveDTO) throws NotFoundException, BadRequestException {
        ProductCategory existingCategory = findByIdEntity(id);

        modelMapper.map(categorySaveDTO, existingCategory);

        if (categorySaveDTO.getName() != null && repository.existsByName(categorySaveDTO.getName()) && !existingCategory.getName().equals(categorySaveDTO.getName())) {
            throw new BadRequestException("Categoría con el nombre '" + categorySaveDTO.getName() + "' ya existe");
        }

        if (categorySaveDTO.getSlug() != null && repository.existsBySlug(categorySaveDTO.getSlug()) && !existingCategory.getSlug().equals(categorySaveDTO.getSlug())) {
            throw new BadRequestException("Categoría con el slug '" + categorySaveDTO.getSlug() + "' ya existe");
        }
        ProductCategory savedCategory = repository.save(existingCategory);
        return convertToDTO(savedCategory);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public ProductCategoryDTO uploadCategoryAttachment(Long categoryId, MultipartFile file) throws IOException, NotFoundException {
        ProductCategory category = findByIdEntity(categoryId);
        Attachment attachment = attachmentService.uploadAttachment(file);
        category.setAttachment(attachment);
        ProductCategory savedCategory = repository.save(category);
        return convertToDTO(savedCategory);
    }

    public AttachmentDTO getCategoryAttachment(Long categoryId) throws NotFoundException {
        ProductCategory category = findByIdEntity(categoryId);
        Attachment attachment = category.getAttachment();
        return modelMapper.map(attachment, AttachmentDTO.class);
    }

    private ProductCategory findByIdEntity(Long id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
    }

    private ProductCategoryDTO convertToDTO(ProductCategory category) {
        return modelMapper.map(category, ProductCategoryDTO.class);
    }

    private ProductCategory convertToEntity(ProductCategorySaveDTO categorySaveDTO) {
        return modelMapper.map(categorySaveDTO, ProductCategory.class);
    }
}