package proyecto.dh.resources.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.entity.ImageProduct;
import proyecto.dh.resources.product.service.ImageProductService;
import proyecto.dh.common.responses.ResponseDTO;

import java.util.List;

@RestController
@RequestMapping("products/{productId}/images")
public class ImageProductController {

    @Autowired
    private ImageProductService imageProductService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> uploadProductImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) throws BadRequestException {
        return imageProductService.uploadProductImage(productId, file);
    }

    @DeleteMapping("{imageId}")
    public ResponseEntity<ResponseDTO<String>> deleteProductImage(@PathVariable Long productId, @PathVariable Long imageId) throws BadRequestException {
        return imageProductService.deleteProductImage(productId, imageId);
    }

    @GetMapping
    public ResponseEntity<List<ImageProduct>> getProductImages(@PathVariable Long productId) throws NotFoundException {
        return imageProductService.getProductImages(productId);
    }
}
