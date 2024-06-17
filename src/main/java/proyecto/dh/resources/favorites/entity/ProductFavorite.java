package proyecto.dh.resources.favorites.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.users.entity.User;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "favorites")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //@JsonIgnore
    @ManyToMany(mappedBy = "productFavorite", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<Product> product = new HashSet<>();
    public ProductFavorite() {
        this.product = new HashSet<>();
    }

}
