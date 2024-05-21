package proyecto.dh.resources.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.common.service.S3Service;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.entity.ImageProduct;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.repository.ImageProductRepository;
import proyecto.dh.common.responses.ResponseDTO;
import proyecto.dh.common.responses.ResponseHandler;

import java.io.IOException;
import java.util.List;

@Service
public class ImageProductService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageProductRepository imageProductRepository;

    public ResponseEntity<ResponseDTO<String>> uploadProductImage(Long productId, MultipartFile file) throws BadRequestException {
        try {
            Product product = productService.findById(productId);
            String fileKey = s3Service.uploadFile(file);
            String imageUrl = s3Service.getFileUrl(fileKey); // Obtener la URL completa del archivo

            ImageProduct imageProduct = new ImageProduct();
            imageProduct.setUrl(imageUrl); // URL completa del archivo subido
            imageProduct.setFileName(file.getOriginalFilename()); // Nombre del archivo original
            imageProduct.setFileKey(fileKey); // Clave del archivo generado
            imageProduct.setProduct(product);

            imageProductRepository.save(imageProduct);
            return ResponseHandler.generateResponse("Image uploaded successfully", HttpStatus.OK, imageUrl);
        } catch (Exception e) {
            throw new BadRequestException("Error uploading file: " + e.getMessage());
        }
    }

    public ResponseEntity<ResponseDTO<String>> deleteProductImage(Long productId, Long imageId) throws BadRequestException {
        try {
            Product product = productService.findById(productId);
            ImageProduct imageProduct = imageProductRepository.findById(imageId)
                    .orElseThrow(() -> new NotFoundException("Image not found"));

            s3Service.deleteFile(imageProduct.getFileKey()); // Usar la clave del archivo generado
            imageProductRepository.delete(imageProduct);

            return ResponseHandler.generateResponse("Image deleted successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            throw new BadRequestException("Error deleting file: " + e.getMessage());
        }
    }

    public ResponseEntity<List<ImageProduct>> getProductImages(Long productId) throws NotFoundException {
        Product product = productService.findById(productId);
        return ResponseEntity.ok(product.getImages());
    }
}
