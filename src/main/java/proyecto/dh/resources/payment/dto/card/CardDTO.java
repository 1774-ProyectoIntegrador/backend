package proyecto.dh.resources.payment.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDTO{

    private Long userId;

    private String cardNumber;

    private String holderName;

    private String expirationDate;

    private String cvv;

    private Double amount;

    private List<Long> productIds;

    private List<Long> paymentIds;


}
