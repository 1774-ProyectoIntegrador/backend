package proyecto.dh.resources.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.entity.ProductFeature;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;
import proyecto.dh.resources.product.repository.ProductFeatureRepository;

import java.io.IOException;
import java.util.List;

@Service
public class ProductFeatureService {

    @Autowired
    private ProductFeatureRepository repository;

    public List<ProductFeature> findAll() {
        return repository.findAll();
    }

    public ProductFeature findById(Long id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Feature not found"));
    }

    public ProductFeature save(ProductFeature feature) {
        return repository.save(feature);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
