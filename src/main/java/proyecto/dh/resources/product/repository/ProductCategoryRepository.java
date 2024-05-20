package proyecto.dh.resources.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.dh.resources.product.entity.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
}
