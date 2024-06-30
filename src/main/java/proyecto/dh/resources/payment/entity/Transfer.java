package proyecto.dh.resources.payment.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Generated@Setter
@Table(name = "transfers")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transfer extends Payment{

    @Column(nullable = false)
    private String aliasOrCbu;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private Double amount;
}
