package proyecto.dh.resources.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import proyecto.dh.common.enums.RentType;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;
import proyecto.dh.resources.favorite.dto.ProductFavoriteDTO;
import proyecto.dh.resources.payment.dto.card.CardDTO;
import proyecto.dh.resources.payment.dto.payment.PaymentDTO;
import proyecto.dh.resources.reservation.dto.ReservationDTO;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    @Schema(hidden = true)
    private Long id;
    private String name;
    private String description;
    private int stock;
    private double price;
    private RentType rentType;
    private CategoryDTO category;
    private List<AttachmentDTO> attachments;
    private List<ProductFeatureDTO> features;
    private List<ProductPolicyDTO> policies;
    private List<ProductFavoriteDTO> favorites;
    private ReservationDTO reservation;
    private List<PaymentDTO> payments;
    private List<CardDTO> cards;
    //private List<PaymentDTO> payments;
}
