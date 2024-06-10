package proyecto.dh.resources.product.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import proyecto.dh.common.enums.RentType;
import proyecto.dh.resources.attachment.entity.Attachment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "products")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, length = 800)
    private String description;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private RentType rentType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
    @JoinColumn(nullable = false)
    private ProductCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private ProductPolicy policy;

    @OneToMany(mappedBy = "product", cascade = CascadeType.DETACH)
    private List<Attachment> attachments = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "products_features",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "features_id"))
    private Set<ProductFeature> productFeatures = new LinkedHashSet<>();


    // Métodos para sincronizar las relaciones
    public void addAttachment(Attachment attachment) {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        attachment.setProduct(this);
        attachments.add(attachment);
    }

    public void removeAttachment(Attachment attachment) {
        if (attachments != null) {
            attachment.setProduct(null);
            attachments.remove(attachment);
        }
    }

}
