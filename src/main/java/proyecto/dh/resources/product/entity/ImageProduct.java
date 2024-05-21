package proyecto.dh.resources.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "image_products")
public class ImageProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    @JsonIgnore
    private String fileKey; // Clave del archivo en S3

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
