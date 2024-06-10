package proyecto.dh.resources.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import proyecto.dh.resources.attachment.entity.Attachment;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "product_category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(nullable = false, unique = true)
    private String slug;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<Product> products;

    @ManyToOne
    @JoinColumn(name = "attachment_id", unique = false)
    private Attachment attachment;
}
