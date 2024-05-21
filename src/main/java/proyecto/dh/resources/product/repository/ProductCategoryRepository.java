package proyecto.dh.resources.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.entity.ProductFeature;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Optional<ProductCategory> findByName(String name);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
