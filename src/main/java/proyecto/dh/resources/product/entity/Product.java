package proyecto.dh.resources.product.entity;

import jakarta.persistence.*;
import lombok.*;
import proyecto.dh.common.enums.PriceType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ImageProduct> images;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private PriceType priceType;

    @Column(nullable = false)
    private String trademark;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int stock;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_details_id")
    private ProdutDetails produtDetails;


}
