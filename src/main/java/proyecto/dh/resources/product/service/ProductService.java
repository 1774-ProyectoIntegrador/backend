package proyecto.dh.resources.product.service;

import org.hibernate.collection.spi.PersistentList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO) throws NotFoundException, BadRequestException {
        Product existingProduct = findByIdEntity(id);
        modelMapper.map(productUpdateDTO, existingProduct);

        if (productUpdateDTO.getCategoryId() != null) {
            ProductCategory category = productCategoryRepository.findById(productUpdateDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
            existingProduct.setCategory(category);
        }

        if (productUpdateDTO.getAttachmentsIds() != null) {
            List<Attachment> existingAttachments = new ArrayList<>(existingProduct.getAttachments());

            // Eliminar los adjuntos que no están en la nueva lista de IDs
            for (Attachment currentAttach : existingAttachments) {
                if (!productUpdateDTO.getAttachmentsIds().contains(currentAttach.getId())) {
                    existingProduct.removeAttachment(currentAttach);
                }
            }

            // Agregar los nuevos adjuntos
            for (Long attachmentId : productUpdateDTO.getAttachmentsIds()) {
                Attachment attachment = attachmentService.findById(attachmentId);
                if (attachment.getProduct() != existingProduct) {
                    existingProduct.addAttachment(attachment);
                }
            }
        }

        if (productUpdateDTO.getFeatures() != null) {
            Set<ProductFeature> newFeatures = new HashSet<>();
            for (ProductFeature feature : productUpdateDTO.getFeatures()) {
                feature.setProduct(existingProduct);
                newFeatures.add(feature);
            }

            existingProduct.getProductFeatures().clear();
            existingProduct.getProductFeatures().addAll(newFeatures);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }


    public void delete(Long id) throws NotFoundException {
        Product findProduct = findByIdEntity(id);
        attachmentService.deleteAttachmentsByEntities(findProduct.getAttachments());
        productRepository.deleteById(id);
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

    private void validateFileType(Attachment attachment) throws BadRequestException {
        String contentType = attachment.getFileName().substring(attachment.getFileName().lastIndexOf('.') + 1).toLowerCase();
        if (!"jpeg".equals(contentType) && !"png".equals(contentType) && !"jpg".equals(contentType)) {
            throw new BadRequestException("Solo se permiten archivos JPEG, PNG y JPG");
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