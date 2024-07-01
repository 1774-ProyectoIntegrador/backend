package proyecto.dh.resources.payment.dto.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferDTO {

    private Long userId;

    private List<Long> productIds;

    private String aliasOrCbu;

    private String bankName;

    private Double amount;
}
