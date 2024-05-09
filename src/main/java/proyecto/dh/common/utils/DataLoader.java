package proyecto.dh.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import proyecto.dh.common.enums.PriceType;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.repository.ProductRepository;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadProductData();
    }
    private void loadProductData() {
        productRepository.save(new Product(null, "Smartphone", "High-end smartphone with 128GB storage", null, 799.99, PriceType.BY_DAY));
        productRepository.save(new Product(null, "Laptop", "Gaming laptop with high performance specs", null, 1200.00, PriceType.BY_DAY));
        productRepository.save(new Product(null, "Coffee Maker", "Brews coffee and has programmable features", null, 99.99, PriceType.BY_DAY));
        productRepository.save(new Product(null, "Bluetooth Speaker", "Portable bluetooth speaker with excellent sound quality", null, 150.00, PriceType.BY_DAY));
        productRepository.save(new Product(null, "Electric Kettle", "1.7L electric kettle with auto shut-off", null, 35.99, PriceType.BY_DAY));
    }
}
