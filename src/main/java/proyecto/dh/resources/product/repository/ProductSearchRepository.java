package proyecto.dh.resources.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.ProductDTO;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.entity.ProductFeature;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public List<ProductDTO> searchProducts(String searchText, Long categoryId) throws NotFoundException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);
        Join<Product, ProductFeature> feature = product.join("productFeatures", JoinType.LEFT);
        Join<Product, ProductCategory> category = product.join("category", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (searchText != null && !searchText.isEmpty()) {
            String normalizedSearchText = Normalizer.normalize(searchText, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

            Predicate namePredicate = cb.like(
                    cb.function("unaccent", String.class, cb.lower(product.get("name"))),
                    "%" + normalizedSearchText.toLowerCase() + "%"
            );
            Predicate descriptionPredicate = cb.like(
                    cb.function("unaccent", String.class, cb.lower(product.get("description"))),
                    "%" + normalizedSearchText.toLowerCase() + "%"
            );
            Predicate featureNamePredicate = cb.like(
                    cb.function("unaccent", String.class, cb.lower(feature.get("name"))),
                    "%" + normalizedSearchText.toLowerCase() + "%"
            );
            Predicate featureDescriptionPredicate = cb.like(
                    cb.function("unaccent", String.class, cb.lower(feature.get("description"))),
                    "%" + normalizedSearchText.toLowerCase() + "%"
            );

            predicates.add(cb.or(namePredicate, descriptionPredicate, featureNamePredicate, featureDescriptionPredicate));
        }

        if (categoryId != null) {
            if (!productCategoryRepository.existsById(categoryId)) {
                throw new NotFoundException("Categoría no encontrada");
            }
            Predicate categoryPredicate = cb.equal(
                    product.get("category").get("id"),
                    categoryId
            );
            predicates.add(categoryPredicate);
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Product> results = entityManager.createQuery(query).getResultList();
        return results.stream()
                .map(productEntity -> modelMapper.map(productEntity, ProductDTO.class))
                .collect(Collectors.toList());
    }
}
