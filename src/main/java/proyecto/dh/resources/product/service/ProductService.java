package proyecto.dh.resources.product.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.dto.CreateProductDTO;
import proyecto.dh.resources.product.dto.ProductDTO;
import proyecto.dh.resources.product.dto.UpdateProductDTO;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.entity.ProductFeature;
import proyecto.dh.resources.product.repository.ProductRepository;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

    public ProductDTO save(CreateProductDTO userObject) throws NotFoundException, BadRequestException {
        if (productRepository.existsByName(userObject.getName())) {
            throw new BadRequestException("Producto con nombre '" + userObject.getName() + "' ya existe");
        }
        Product product = modelMapper.map(userObject, Product.class);
        ProductCategory category = productCategoryRepository.findById(userObject.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
        product.setCategory(category);

        if (userObject.getFeatures() != null) {
            List<ProductFeature> features = userObject.getFeatures().stream()
                    .peek(feature -> feature.setProduct(product))
                    .toList();
            product.setProductFeatures(new HashSet<>(features));
        }

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    public ProductDTO updateProduct(Long id, UpdateProductDTO productUpdateDTO) throws NotFoundException {
        Product existingProduct = findById(id);
        modelMapper.map(productUpdateDTO, existingProduct);

        if (productUpdateDTO.getCategoryId() != null) {
            ProductCategory category = productCategoryRepository.findById(productUpdateDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
            existingProduct.setCategory(category);
        }

        if (productUpdateDTO.getFeatures() != null) {
            existingProduct.getProductFeatures().clear();
            existingProduct.getProductFeatures().addAll(productUpdateDTO.getFeatures().stream()
                    .peek(feature -> feature.setProduct(existingProduct))
                    .toList());
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    public void delete(Long id) throws NotFoundException {
        Product findProduct = findById(id);
        attachmentService.deleteAttachments(findProduct.getAttachments());
        productRepository.deleteById(id);
    }

    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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

    public ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setCategoryName(product.getCategory().getName());

        if (product.getProductFeatures() != null) {
            productDTO.setFeatures(new ArrayList<>(product.getProductFeatures()));
        }
        return productDTO;
    }
}
