package proyecto.dh.resources.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.dh.resources.product.entity.Product;

public interface IProductRepository extends JpaRepository<Product, Long> {
}
