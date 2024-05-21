package proyecto.dh.resources.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;

import java.io.IOException;
import java.util.List;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository repository;

    @Autowired
    private AttachmentService attachmentService;

    public List<ProductCategory> findAll() {
        return repository.findAll();
    }

    public ProductCategory findById(Long id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
    }

    public ProductCategory save(ProductCategory category) throws BadRequestException {
        if (repository.existByName(category.getName())) {
            throw new BadRequestException("Categoría con el nombre '" + category.getName() + "' ya existe");
        } else if (repository.existsBySlug(category.getSlug())) {
            throw new BadRequestException("Categoría con el slug '" + category.getSlug() + "' ya existe");
        }
        return repository.save(category);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public ProductCategory uploadCategoryAttachment(Long categoryId, MultipartFile file) throws IOException, NotFoundException {
        ProductCategory category = findById(categoryId);
        Attachment attachment = attachmentService.uploadAttachment(file);

        category.setAttachment(attachment);
        return repository.save(category);
    }

    public Attachment getCategoryAttachment(Long categoryId) throws NotFoundException {
        ProductCategory category = findById(categoryId);
        return category.getAttachment();
    }
}
