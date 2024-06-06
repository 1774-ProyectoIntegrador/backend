package proyecto.dh.resources.product.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.dto.ProductDTO;
import proyecto.dh.resources.product.dto.ProductSaveDTO;
import proyecto.dh.resources.product.dto.ProductUpdateDTO;
import proyecto.dh.resources.product.dto.category.ProductCategoryDTO;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.entity.ProductFeature;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;
import proyecto.dh.resources.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Transactional
    public ProductDTO save(ProductSaveDTO productSaveDTO) throws NotFoundException, BadRequestException {
        if (productRepository.existsByName(productSaveDTO.getName())) {
            throw new BadRequestException("Producto con nombre '" + productSaveDTO.getName() + "' ya existe");
        }
        Product product = convertToEntity(productSaveDTO);
        ProductCategory category = productCategoryRepository.findById(productSaveDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
        product.setCategory(category);

        if (productSaveDTO.getAttachments() != null) {
            List<Attachment> attachments = new ArrayList<>();
            for (Long attachmentId : productSaveDTO.getAttachments()) {
                Attachment attachment = attachmentService.findById(attachmentId);
                validateFileType(attachment);
                attachment.setProduct(product);
                attachments.add(attachment);
            }
            product.setAttachments(attachments);
        }

        if (productSaveDTO.getFeatures() != null) {
            List<ProductFeature> features = productSaveDTO.getFeatures().stream()
                    .peek(feature -> feature.setProduct(product))
                    .toList();
            product.setProductFeatures(new HashSet<>(features));
        }

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO) throws NotFoundException, BadRequestException {
        Product existingProduct = findByIdEntity(id);
        modelMapper.map(productUpdateDTO, existingProduct);

        updateCategory(existingProduct, productUpdateDTO.getCategoryId());
        updateAttachments(existingProduct, productUpdateDTO.getAttachmentsIds());
        updateFeatures(existingProduct, productUpdateDTO.getFeatures());

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    @Transactional
    public void delete(Long id) throws NotFoundException {
        Product findProduct = findByIdEntity(id);
        attachmentService.deleteAttachmentsByEntities(findProduct.getAttachments());
        productRepository.delete(findProduct);
    }

    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO findById(Long id) throws NotFoundException {
        Product productSearched = findByIdEntity(id);
        return convertToDTO(productSearched);
    }

    private void updateCategory(Product product, Long categoryId) throws NotFoundException {
        if (categoryId != null) {
            ProductCategory category = productCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
            product.setCategory(category);
        }
    }

    private void updateAttachments(Product product, List<Long> attachmentsIds) throws NotFoundException, BadRequestException {
        if (attachmentsIds != null) {
            List<Attachment> existingAttachments = new ArrayList<>(product.getAttachments());

            for (Attachment currentAttach : existingAttachments) {
                if (!attachmentsIds.contains(currentAttach.getId())) {
                    product.removeAttachment(currentAttach);
                }
            }

            for (Long attachmentId : attachmentsIds) {
                Attachment attachment = attachmentService.findById(attachmentId);
                if (!product.getAttachments().contains(attachment)) {
                    product.addAttachment(attachment);
                }
            }
        }
    }

    private void updateFeatures(Product product, List<ProductFeature> features) {
        if (features != null) {
            Set<ProductFeature> newFeatures = new HashSet<>();
            for (ProductFeature feature : features) {
                feature.setProduct(product);
                newFeatures.add(feature);
            }

            product.getProductFeatures().clear();
            product.getProductFeatures().addAll(newFeatures);
        }
    }

    private void validateFileType(Attachment attachment) throws BadRequestException {
        String contentType = attachment.getFileName().substring(attachment.getFileName().lastIndexOf('.') + 1).toLowerCase();
        if (!"jpeg".equals(contentType) && !"png".equals(contentType) && !"jpg".equals(contentType) && !"webp".equals(contentType)) {
            throw new BadRequestException("Solo se permiten archivos WEBP, JPEG, PNG y JPG");
        }
    }

    private Product findByIdEntity(Long id) throws NotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El producto no existe."));
    }

    public ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

        if (product.getCategory() != null) {
            ProductCategoryDTO categoryDTO = modelMapper.map(product.getCategory(), ProductCategoryDTO.class);
            productDTO.setCategory(categoryDTO);
        }

        if (product.getAttachments() != null) {
            productDTO.setAttachments(product.getAttachments().stream()
                    .map(attachment -> modelMapper.map(attachment, AttachmentDTO.class))
                    .collect(Collectors.toList()));
        }

        if (product.getProductFeatures() != null) {
            productDTO.setFeatures(new ArrayList<>(product.getProductFeatures()));
        }
        return productDTO;
    }

    public Product convertToEntity(ProductSaveDTO productSaveDTO) {
        return modelMapper.map(productSaveDTO, Product.class);
    }
}
