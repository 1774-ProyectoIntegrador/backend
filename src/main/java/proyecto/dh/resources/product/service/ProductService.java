package proyecto.dh.resources.product.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.entity.ProductFavorite;
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

/**
 * Servicio para gestionar productos.
 * Contiene métodos para crear, actualizar, eliminar y recuperar productos.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Guarda un nuevo producto en el sistema.
     *
     * @param productSaveDTO el DTO que contiene los datos del producto a crear
     * @return el producto creado
     * @throws NotFoundException si la categoría del producto no se encuentra
     * @throws BadRequestException si el producto ya existe o los datos de entrada son inválidos
     */
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

        if (productSaveDTO.getFavorites() != null) {
            List<ProductFavorite> favorites = new ArrayList<>();
            for (Long favoriteId : productSaveDTO.getFavorites()){
                ProductFavorite favorite = favoriteService.findById(favoriteId);
                favorite.setProduct(product);
                favorites.add(favorite);
            }
            product.setFavorites(favorites);
            //List<Favorite> favorites = productSaveDTO.getFavorites().stream()
            //        .peek(favorite -> favorite.setProduct(product))
            //         .toList();
            //product.setFavorites(favorites);
        }

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    /**
     * Actualiza un producto existente en el sistema.
     *
     * @param id el ID del producto a actualizar
     * @param productUpdateDTO el DTO que contiene los nuevos datos del producto
     * @return el producto actualizado
     * @throws NotFoundException si el producto o la categoría no se encuentran
     * @throws BadRequestException si los datos de entrada son inválidos
     */
    public ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO) throws NotFoundException, BadRequestException {
        Product existingProduct = findByIdEntity(id);
        modelMapper.map(productUpdateDTO, existingProduct);

        updateCategory(existingProduct, productUpdateDTO.getCategoryId());
        updateAttachments(existingProduct, productUpdateDTO.getAttachmentsIds());
        updateFeatures(existingProduct, productUpdateDTO.getFeatures());
        updateFavorites(existingProduct, productUpdateDTO.getFavorites());

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    /**
     * Elimina un producto del sistema.
     *
     * @param id el ID del producto a eliminar
     * @throws NotFoundException si el producto no se encuentra
     */
    public void delete(Long id) throws NotFoundException {
        Product findProduct = findByIdEntity(id);
        attachmentService.deleteAttachmentsByEntities(findProduct.getAttachments());
        productRepository.deleteById(id);
    }

    /**
     * Recupera todos los productos en el sistema.
     *
     * @return una lista de todos los productos
     */
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recupera un producto por su ID.
     *
     * @param id el ID del producto a recuperar
     * @return el producto recuperado
     * @throws NotFoundException si el producto no se encuentra
     */
    public ProductDTO findById(Long id) throws NotFoundException {
        Product productSearched = findByIdEntity(id);
        return convertToDTO(productSearched);
    }

    /**
     * Actualiza la categoría de un producto.
     *
     * @param product el producto a actualizar
     * @param categoryId el ID de la nueva categoría
     * @throws NotFoundException si la categoría no se encuentra
     */
    private void updateCategory(Product product, Long categoryId) throws NotFoundException {
        if (categoryId != null) {
            ProductCategory category = productCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
            product.setCategory(category);
        }
    }

    /**
     * Actualiza los adjuntos de un producto.
     *
     * @param product el producto a actualizar
     * @param attachmentsIds los IDs de los nuevos adjuntos
     * @throws NotFoundException si algún adjunto no se encuentra
     * @throws BadRequestException si el tipo de archivo es inválido
     */
    private void updateAttachments(Product product, List<Long> attachmentsIds) throws NotFoundException, BadRequestException {
        if (attachmentsIds != null) {
            List<Attachment> existingAttachments = new ArrayList<>(product.getAttachments());

            // Eliminar los adjuntos que no están en la nueva lista de ID
            for (Attachment currentAttach : existingAttachments) {
                if (!attachmentsIds.contains(currentAttach.getId())) {
                    product.removeAttachment(currentAttach);
                }
            }

            // Agregar los nuevos adjuntos
            for (Long attachmentId : attachmentsIds) {
                Attachment attachment = attachmentService.findById(attachmentId);
                if (!product.getAttachments().contains(attachment)) {
                    product.addAttachment(attachment);
                }
            }
        }
    }

    /**
     * Actualiza las características de un producto.
     *
     * @param product el producto a actualizar
     * @param features las nuevas características del producto
     */
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

    /**
     * Actualiza los favoritos de un producto.
     *
     * @param product el producto a actualizar
     * @param favorites los nuevos favoritos del producto
     */
    private void updateFavorites(Product product, List<ProductFavorite> favorites) {
        if (favorites != null) {
            List<ProductFavorite> newFavorites = new ArrayList<>();
            for (ProductFavorite favorite : favorites) {
                favorite.setProduct(product);
                newFavorites.add(favorite);
            }

            product.getFavorites().clear();
            product.getFavorites().addAll(newFavorites);
        }
    }


    /**
     * Valida el tipo de archivo de un adjunto.
     *
     * @param attachment el adjunto a validar
     * @throws BadRequestException si el tipo de archivo es inválido
     */
    private void validateFileType(Attachment attachment) throws BadRequestException {
        String contentType = attachment.getFileName().substring(attachment.getFileName().lastIndexOf('.') + 1).toLowerCase();
        if (!"jpeg".equals(contentType) && !"png".equals(contentType) && !"jpg".equals(contentType) && !"webp".equals(contentType)) {
            throw new BadRequestException("Solo se permiten archivos WEBP, JPEG, PNG y JPG");
        }
    }

    /**
     * Encuentra un producto por su ID.
     *
     * @param id el ID del producto a buscar
     * @return el producto encontrado
     * @throws NotFoundException si el producto no se encuentra
     */
    private Product findByIdEntity(Long id) throws NotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El producto no existe."));
    }

    /**
     * Convierte una entidad Product a su DTO correspondiente.
     *
     * @param product la entidad Product a convertir
     * @return el DTO del producto
     */
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

    /**
     * Convierte un DTO de guardado de producto a su entidad correspondiente.
     *
     * @param productSaveDTO el DTO de guardado de producto a convertir
     * @return la entidad Product resultante
     */
    public Product convertToEntity(ProductSaveDTO productSaveDTO) {
        return modelMapper.map(productSaveDTO, Product.class);
    }

    /**
     * Recupera un producto por su ID.
     *
     * @param id el ID del producto a recuperar
     * @return el producto recuperado
     * @throws NotFoundException si el producto no se encuentra
     */
    public Product getProductById(Long id) throws NotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con ID: " + id));
    }
}