package proyecto.dh.resources.product.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.dto.UpdateProductDTO;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.repository.ProductRepository;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private ModelMapper modelMapper;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, UpdateProductDTO productUpdateDTO) throws NotFoundException {
        Product existingProduct = findById(id);

        // Map the fields from the DTO to the existing product entity
        modelMapper.map(productUpdateDTO, existingProduct);

        // Update the category if provided in the DTO
        if (productUpdateDTO.getCategoryId() != null) {
            ProductCategory category = productCategoryRepository.findById(productUpdateDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            existingProduct.setCategory(category);
        }

        return productRepository.save(existingProduct);
    }

    public void delete(Long id) throws NotFoundException {
        Product findProduct = findById(id);
        productRepository.deleteById(id);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) throws NotFoundException {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("El producto no existe"));
    }

    public Product uploadProductAttachments(Long productId, List<MultipartFile> files) throws IOException, NotFoundException {
        Product product = findById(productId);
        List<Attachment> attachments = attachmentService.uploadAttachments(files);

        for (Attachment attachment : attachments) {
            attachment.setProduct(product);
        }
        product.getAttachments().addAll(attachments);

        return productRepository.save(product);
    }

    public List<Attachment> getProductAttachments(Long productId) throws NotFoundException {
        Product product = findById(productId);
        return product.getAttachments();
    }
}
