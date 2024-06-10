package proyecto.dh.resources.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.product.entity.ProductFavorite;
import proyecto.dh.resources.product.repository.ProductFavoriteRepository;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.users.entity.User;

import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private ProductFavoriteRepository productFavoriteRepository;

    public ProductFavorite findById(Long id) throws BadRequestException {
        return productFavoriteRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Id de favorito no válido"));
    }

    public ProductFavorite save(ProductFavorite favorite){
        return productFavoriteRepository.save(favorite);
    }

    public List<ProductFavorite> getFavoritesByUser(User user){
        return productFavoriteRepository.findByUser(user);
    }

    public ProductFavorite addFavorite(User user, Product product){
        ProductFavorite existingFavorite = productFavoriteRepository.findByUserAndProduct(user, product);
        if(existingFavorite != null){
            return existingFavorite;
        }

        ProductFavorite productFavorite = new ProductFavorite();
        productFavorite.setUser(user);
        productFavorite.setProduct(product);
        return productFavoriteRepository.save(productFavorite);
    }

    public void deleteFavorite(User user,Product product){
       ProductFavorite productFavorite = productFavoriteRepository.findByUserAndProduct(user, product);
       if (productFavorite != null){
           productFavoriteRepository.delete(productFavorite);
       }
    }
}
