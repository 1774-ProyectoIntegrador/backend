package proyecto.dh.resources.payment.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import proyecto.dh.common.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDTO {

    private Long id;

    private Long userId;

    private List<Long> productIds;

    private Long reservationId;

    private Double amount;

    private PaymentType paymentType;

    private List<Long> cardsId;

    private LocalDateTime paymentDateTime;

}
