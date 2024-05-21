package proyecto.dh.resources.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.dh.resources.product.entity.ProductFeature;

public interface ProductFeatureRepository extends JpaRepository<ProductFeature, Long> {
}
