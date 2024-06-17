package proyecto.dh.resources.favorites.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.dh.resources.favorites.entity.ProductFavorite;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.users.entity.User;

import java.util.List;

@Repository
public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Long> {
    List<ProductFavorite> findByUser(User user);
    ProductFavorite findByUserAndProduct(User user, Product product);
}
