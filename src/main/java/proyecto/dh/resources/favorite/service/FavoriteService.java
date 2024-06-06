package proyecto.dh.resources.favorite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.dh.resources.favorite.dto.FavoriteDto;
import proyecto.dh.resources.favorite.entity.Favorite;
import proyecto.dh.resources.favorite.repository.FavoriteRepository;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.users.entity.User;

import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public List<Favorite> getFavoritesByUser(User user){
        return favoriteRepository.findByUser(user);
    }

    public Favorite addFavorite(User user, Product product){
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        return favoriteRepository.save(favorite);
    }

    public void deleteFavorite(User user,Product product){
       Favorite favorite = favoriteRepository.findByUserAndProduct(user, product);
       if (favorite != null){
           favoriteRepository.delete(favorite);
       }
    }
}
