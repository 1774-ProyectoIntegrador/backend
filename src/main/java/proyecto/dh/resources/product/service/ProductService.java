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
import proyecto.dh.resources.product.repository.ProductRepository;
import proyecto.dh.resources.product.repository.ProductSearchRepository;

import java.util.ArrayList;
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

    /**
     * Guarda un nuevo producto.
     *
     * @param productSaveDTO DTO con la información del producto a guardar.
     * @return El producto guardado convertido a DTO.
     * @throws NotFoundException Si no se encuentra la categoría.
     * @throws BadRequestException Si el nombre del producto ya existe.
     */
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

    /**
     * Actualiza un producto existente.
     *
     * @param id ID del producto a actualizar.
     * @param productUpdateDTO DTO con la información del producto a actualizar.
     * @return El producto actualizado convertido a DTO.
     * @throws NotFoundException Si no se encuentra el producto o la categoría.
     * @throws BadRequestException Si hay algún problema con las características o los archivos adjuntos.
     */
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

    /**
     * Elimina un producto por su ID.
     *
     * @param id ID del producto a eliminar.
     * @throws NotFoundException Si no se encuentra el producto.
     */
    @Transactional
    public void delete(Long id) throws NotFoundException {
        Product findProduct = findByIdEntity(id);
        attachmentService.deleteAttachmentsByEntities(findProduct.getAttachments());
        productRepository.delete(findProduct);
    }

    /**
     * Obtiene todos los productos.
     *
     * @return Lista de productos convertidos a DTO.
     */
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un producto por su ID.
     *
     * @param id ID del producto a buscar.
     * @return El producto encontrado convertido a DTO.
     * @throws NotFoundException Si no se encuentra el producto.
     */
    public ProductDTO findById(Long id) throws NotFoundException {
        Product productSearched = findByIdEntity(id);
        return convertToDTO(productSearched);
    }

    /**
     * Busca productos por texto y categoría.
     *
     * @param searchText Texto a buscar.
     * @param categoryId ID de la categoría a buscar.
     * @return Lista de productos encontrados convertidos a DTO.
     * @throws NotFoundException Si no se encuentran productos.
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String searchText, Long categoryId) throws NotFoundException {
        return productSearchRepository.searchProducts(searchText, categoryId);
    }

    private void setProductCategory(Product product, Long categoryId) throws NotFoundException {
        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
        product.setCategory(category);
    }

    private void setProductAttachments(Product product, List<Long> attachmentsIds) throws BadRequestException {
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

    private void setProductFeatures(Product product, List<ProductFeatureSaveDTO> features) {
        Set<ProductFeature> featureSet = features.stream()
                .map(featureSaveDTO -> modelMapper.map(featureSaveDTO, ProductFeature.class))
                .peek(feature -> {
                    if (feature.getProduct() == null) {
                        feature.setProduct(Set.of(product));
                    } else {
                        feature.getProduct().add(product);
                    }
                })
                .collect(Collectors.toSet());

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
    public void updateAttachments(Product product, List<Long> attachmentsIds) throws BadRequestException {
        if (attachmentsIds != null) {
            product.getAttachments().clear();
            setProductAttachments(product, attachmentsIds);
        }
    }

    private void updateFeatures(Product product, List<ProductFeatureSaveDTO> features) {
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
            productDTO.setCategory(modelMapper.map(product.getCategory(), CategoryDTO.class));
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
