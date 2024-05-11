package proyecto.dh.resources.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proyecto.dh.common.enums.PriceType;
import proyecto.dh.resources.users.entity.User;

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

    @Column
    private String image;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_details_id")
    private ProdutDetails produtDetails;
}
