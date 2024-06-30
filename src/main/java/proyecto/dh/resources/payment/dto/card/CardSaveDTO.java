package proyecto.dh.resources.payment.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardSaveDTO{

    @NotNull(message = "User is required")
    private Long userId;

    @NotNull(message = "Card number is required")
    private String cardNumber;

    @NotNull(message = "Holder name is required")
    private String holderName;

    @NotNull(message = "Expiration date is required")
    private String expirationDate;

    @NotNull(message = "CVV is required")
    private String cvv;

    @NotNull(message = "Amount is required")
    private Double amount;

    @NotNull(message = "Product is required")
    @Size(min = 1, message = "At least one product is required")
    private List<Long> productIds;

    @NotNull(message = "Card number is required")
    private List<Long> paymentIds;

}
