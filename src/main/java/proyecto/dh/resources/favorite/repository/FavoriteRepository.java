package proyecto.dh.resources.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.dh.resources.favorite.entity.Favorite;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.users.entity.User;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    Favorite findByUserAndProduct(User user, Product product);
}
