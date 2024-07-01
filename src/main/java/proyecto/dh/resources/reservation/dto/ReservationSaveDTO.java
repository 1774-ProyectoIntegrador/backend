package proyecto.dh.resources.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationSaveDTO {

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    @NotNull(message = "Product ID is mandatory")
    private Long productId;

    @FutureOrPresent(message = "La fecha no puede ser anterior a al actual")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @FutureOrPresent(message = "La fecha no puede ser anterior a al actual")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private List<Long> paymentIds;

    private LocalDateTime creationDateTime;

    @AssertTrue(message = "La fecha de devolución debe ser igual o posterior a la fecha de alquiler")
    private boolean isValidEndDate() {
        return endDate == null || !endDate.isBefore(startDate);
    }

}
