package proyecto.dh.resources.reservation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationDTO {

    private Long id;

    private Long userId;

    private Long productId;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime creationDateTime;

    private Double amount;

    private boolean cancelled;
}
