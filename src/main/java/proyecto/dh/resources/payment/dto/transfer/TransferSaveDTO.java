package proyecto.dh.resources.payment.dto.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
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
public class TransferSaveDTO {

    @NotNull(message = "User is required")
    private Long userId;

    @NotNull(message = "Product is required")
    private List<Long> productIds;

    @NotNull(message = "AliasOrCbu is required")
    private String aliasOrCbu;

    @NotNull(message = "BankName is required")
    private String bankName;

    @NotNull(message = "Amount is required")
    private Double amount;
}
