package proyecto.dh.resources.users.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import proyecto.dh.common.enums.Role;
import proyecto.dh.resources.favorite.dto.ProductFavoriteDTO;
import proyecto.dh.resources.payment.dto.payment.PaymentDTO;
import proyecto.dh.resources.payment.entity.Card;
import proyecto.dh.resources.payment.entity.Transfer;
import proyecto.dh.resources.reservation.dto.ReservationDTO;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private List<ProductFavoriteDTO> favorites;
    private List<ReservationDTO> reservations;
    private List<PaymentDTO> payments;
    private List<Card> cards;
    private List<Transfer> transfers;
}
