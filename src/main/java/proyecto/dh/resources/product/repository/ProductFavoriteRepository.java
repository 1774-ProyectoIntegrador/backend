package proyecto.dh.resources.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.dh.resources.product.entity.ProductFavorite;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.users.entity.User;

import java.util.List;

public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Long> {
    List<ProductFavorite> findByUser(User user);
    ProductFavorite findByUserAndProduct(User user, Product product);
}
