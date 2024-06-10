package proyecto.dh.resources.product.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.dto.*;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.entity.ProductFeature;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;
import proyecto.dh.resources.product.repository.ProductFeatureRepository;
import proyecto.dh.resources.product.repository.ProductRepository;
import proyecto.dh.resources.product.repository.ProductSearchRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final AttachmentService attachmentService;
    private final ModelMapper modelMapper;
    private final ProductSearchRepository productSearchRepository;

    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, AttachmentService attachmentService, ModelMapper modelMapper, ProductSearchRepository productSearchRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.attachmentService = attachmentService;
        this.modelMapper = modelMapper;
        this.productSearchRepository = productSearchRepository;
    }

    @Transactional
    public ProductDTO save(ProductSaveDTO productSaveDTO) throws NotFoundException, BadRequestException {
        if (productRepository.existsByName(productSaveDTO.getName())) {
            throw new BadRequestException("Producto con nombre '" + productSaveDTO.getName() + "' ya existe");
        }
        Product product = convertToEntity(productSaveDTO);
        setProductCategory(product, productSaveDTO.getCategoryId());
        setProductAttachments(product, productSaveDTO.getAttachments());
        setProductFeatures(product, productSaveDTO.getFeatures());

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

    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String searchText, Long categoryId) throws NotFoundException {
        return productSearchRepository.searchProducts(searchText, categoryId);
    }

    private void setProductCategory(Product product, Long categoryId) throws NotFoundException {
        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
        product.setCategory(category);
    }

    private void setProductAttachments(Product product, List<Long> attachmentsIds) throws BadRequestException, NotFoundException {
        if (attachmentsIds != null) {
            List<Attachment> attachments = new ArrayList<>();
            for (Long attachmentId : attachmentsIds) {
                Attachment attachment = attachmentService.findById(attachmentId);
                attachmentService.validateFileTypeImages(attachment);
                attachment.setProduct(product);
                attachments.add(attachment);
            }
            product.setAttachments(attachments);
        }
    }

    private void setProductFeatures(Product product, List<ProductFeatureSaveDTO> features) throws NotFoundException, BadRequestException {
        Set<ProductFeature> featureSet = new HashSet<>();

        if (features != null) {
            for (ProductFeatureSaveDTO featureSaveDTO : features) {
                ProductFeature feature = modelMapper.map(featureSaveDTO, ProductFeature.class);
                if (feature.getProduct() == null) {
                    feature.setProduct(new HashSet<>());
                }
                feature.getProduct().add(product);
                featureSet.add(feature);
            }
        }

        product.setProductFeatures(featureSet);
    }

    private void updateCategory(Product product, Long categoryId) throws NotFoundException {
        if (categoryId != null) {
            ProductCategory category = productCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
            product.setCategory(category);
        }
    }

    @Transactional
    public void updateAttachments(Product product, List<Long> attachmentsIds) throws BadRequestException, NotFoundException {
        if (attachmentsIds != null) {
            product.getAttachments().clear();
            for (Long attachmentId : attachmentsIds) {
                Attachment attachment = attachmentService.findById(attachmentId);
                attachmentService.validateFileTypeImages(attachment);
                product.addAttachment(attachment);
            }
        }
    }

    private void updateFeatures(Product product, List<ProductFeatureSaveDTO> features) throws NotFoundException, BadRequestException {
        product.getProductFeatures().clear();
        setProductFeatures(product, features);
    }

    private Product findByIdEntity(Long id) throws NotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El producto no existe."));
    }

    public ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

        if (product.getCategory() != null) {
            CategoryDTO categoryDTO = modelMapper.map(product.getCategory(), CategoryDTO.class);
            productDTO.setCategory(categoryDTO);
        }

        if (product.getAttachments() != null) {
            List<AttachmentDTO> attachments = product.getAttachments().stream()
                    .map(attachment -> modelMapper.map(attachment, AttachmentDTO.class))
                    .collect(Collectors.toList());
            productDTO.setAttachments(attachments);
        }

        if (product.getProductFeatures() != null) {
            List<ProductFeatureDTO> features = product.getProductFeatures().stream()
                    .map(feature -> modelMapper.map(feature, ProductFeatureDTO.class))
                    .collect(Collectors.toList());
            productDTO.setFeatures(features);
        }

        return productDTO;
    }

    public Product convertToEntity(ProductSaveDTO productSaveDTO) {
        return modelMapper.map(productSaveDTO, Product.class);
    }
}