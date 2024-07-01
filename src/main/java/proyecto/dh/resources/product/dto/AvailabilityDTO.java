package proyecto.dh.resources.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class AvailabilityDTO {
    private Long productId;
    private List<LocalDate> occupiedDates;
}
