package proyecto.dh.resources.product.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import proyecto.dh.common.enums.RentType;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.favorite.entity.ProductFavorite;
import proyecto.dh.resources.payment.entity.Card;
import proyecto.dh.resources.payment.entity.Payment;
import proyecto.dh.resources.reservation.entity.Reservation;

import java.util.*;

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

    @OneToMany(mappedBy = "product", cascade = CascadeType.DETACH)
    private List<Attachment> attachments = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "products_features", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "feature_id"))
    private Set<CategoryFeature> features = new LinkedHashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "products_favorites", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "favorite_id"))
    private Set<ProductFavorite> favorites = new LinkedHashSet<>();

    @JoinColumn(name = "reservation_id")
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Reservation reservation;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "products_cards",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_id"))
    private Set<Card> cards = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "products", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<Payment> payments = new LinkedHashSet<>();


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
