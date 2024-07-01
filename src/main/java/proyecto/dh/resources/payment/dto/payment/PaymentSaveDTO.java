package proyecto.dh.resources.payment.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import proyecto.dh.common.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentSaveDTO {

    private Long userId;

    private List<Long> productIds;

    private Long reservationId;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double amount;

    private PaymentType paymentType;

    private List<Long> cardsId;

    private LocalDateTime paymentDateTime;

    //private List<Long> cardsId;
}
