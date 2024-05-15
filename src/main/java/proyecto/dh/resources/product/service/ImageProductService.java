package proyecto.dh.resources.product.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.entity.ImageProduct;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.repository.ImageProductRepository;

import java.util.List;

@Service
public class ImageProductService {
    @Autowired
    private ImageProductRepository imageProductRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ImageProduct saveImageProduct(ImageProduct userObject) {
        return imageProductRepository.save(userObject);
    }

    public ImageProduct update(Long id, ImageProduct userObject) throws NotFoundException {
        ImageProduct existingImage = findById(id);
        modelMapper.map(userObject, existingImage);
        return imageProductRepository.save(existingImage);
    }

    public void delete(Long id) throws NotFoundException {
        ImageProduct findProduct = findById(id);
        imageProductRepository.deleteById(id);
    }

    public ImageProduct findById(Long id) throws NotFoundException {
        return imageProductRepository.findById(id).orElseThrow(() -> new NotFoundException("la imagen no existe"));
    }

    public List<ImageProduct> findAll() {
        return imageProductRepository.findAll();
    }

}
