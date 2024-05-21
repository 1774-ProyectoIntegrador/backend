package proyecto.dh.resources.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;

import java.io.IOException;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private AttachmentService attachmentService;

    public ProductCategory findById(Long id) throws NotFoundException {
        return productCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    public ProductCategory save(ProductCategory category) {
        return productCategoryRepository.save(category);
    }

    public void deleteById(Long id) {
        productCategoryRepository.deleteById(id);
    }

    public ProductCategory uploadCategoryAttachment(Long categoryId, MultipartFile file) throws IOException, NotFoundException {
        ProductCategory category = findById(categoryId);
        Attachment attachment = attachmentService.uploadAttachment(file);

        category.setAttachment(attachment);
        return productCategoryRepository.save(category);
    }

    public Attachment getCategoryAttachment(Long categoryId) throws NotFoundException {
        ProductCategory category = findById(categoryId);
        return category.getAttachment();
    }
}
