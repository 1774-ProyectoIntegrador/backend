package proyecto.dh.resources.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.entity.ImageProduct;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.repository.ImageProductRepository;
import proyecto.dh.responses.ResponseDTO;
import proyecto.dh.responses.ResponseHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageProductRepository imageProductRepository;

    public ResponseEntity<ResponseDTO<String>> uploadProductImage(Long productId, MultipartFile file) throws BadRequestException {
        try {
            Product product = productService.findById(productId);
            ImageProduct imageProduct = s3Service.uploadFile(file, product);
            String imageUrl = s3Service.generatePresignedUrl(imageProduct.getFileName()).toString();
            return ResponseHandler.generateResponse("Image uploaded successfully", HttpStatus.OK, imageUrl);
        } catch (Exception e) {
            throw new BadRequestException("Error uploading file: " + e.getMessage());
        }
    }

    public ResponseEntity<ResponseDTO<String>> deleteProductImage(Long productId, Long imageId) throws BadRequestException {
        try {
            Product product = productService.findById(productId);

            ImageProduct imageProduct = product.getImages().stream()
                    .filter(image -> image.getId().equals(imageId))
                    .findFirst()
                    .orElse(null);

            if (imageProduct == null) {
                throw new NotFoundException("Image not found");
            }

            s3Service.deleteFile(imageProduct.getFileName());
            imageProductRepository.delete(imageProduct);

            return ResponseHandler.generateResponse("Image deleted successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            throw new BadRequestException("Error deleting file: " + e.getMessage());
        }
    }

    public ResponseEntity<List<ImageProduct>> getProductImages(Long productId) throws NotFoundException {
        Product product = productService.findById(productId);
        if (product == null) {
            throw new NotFoundException("Product not found");
        }

        List<ImageProduct> images = product.getImages();
        s3Service.signImageUrls(images);

        return ResponseEntity.ok(images);
    }
}
