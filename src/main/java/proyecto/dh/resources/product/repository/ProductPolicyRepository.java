package proyecto.dh.resources.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.dh.resources.product.entity.ProductPolicy;

public interface ProductPolicyRepository extends JpaRepository<ProductPolicy, Long> {
}
