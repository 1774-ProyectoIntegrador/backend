package proyecto.dh.resources.reservation.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import proyecto.dh.resources.payment.entity.Payment;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.users.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //@ManyToMany(mappedBy = "reservations", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    //@JoinColumn(name = "product_id", nullable = false)
    //private Set<Product> products;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product", nullable = false)
    private Product product;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    private Set<Payment> payments;// = new HashSet<>();

    @Column(name = "creation_date_time", nullable = false)
    private LocalDateTime creationDateTime;


    public Reservation(){
        //this.products = new HashSet<>();
        this.creationDateTime = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist(){
        this.creationDateTime = LocalDateTime.now();
    }
}
