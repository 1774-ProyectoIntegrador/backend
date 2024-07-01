package proyecto.dh.resources.reservation.service;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.repository.ProductRepository;
import proyecto.dh.resources.reservation.dto.ReservationDTO;
import proyecto.dh.resources.reservation.dto.ReservationSaveDTO;
import proyecto.dh.resources.reservation.entity.Reservation;
import proyecto.dh.resources.reservation.repository.ReservationRepository;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public ReservationService(ReservationRepository reservationRepository, ProductRepository productRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.reservationRepository = reservationRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ReservationDTO save(@Valid ReservationSaveDTO reservationSaveDTO) throws NotFoundException{
        User user = userRepository.findById(reservationSaveDTO.getUserId())
                .orElseThrow(()-> new NotFoundException("Usuario no encontrado"));

        Product product = productRepository.findById(reservationSaveDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        LocalDate startDate = reservationSaveDTO.getStartDate();
        LocalDate endDate = reservationSaveDTO.getEndDate();

        if (startDate.isBefore(LocalDate.now())){
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a la fecha actual");
        }

        if (endDate.isBefore(startDate)){
            throw new IllegalArgumentException("La fecha de finalización debe ser igual o posterior a la fecha de inicio de alquiler");
        }

        Reservation reservation = convertToEntity(reservationSaveDTO);
        reservation.setUser(user);
        reservation.setProduct(product);
        reservation.setCreationDateTime(LocalDateTime.now());

        //reservation.setCreationDateTime(LocalDateTime.now());

        syncReservationWithProducts(reservation, reservationSaveDTO.getProductId()/*, startDate, endDate*/);

        Reservation savedReservation = reservationRepository.save(reservation);

        return convertToDTO(savedReservation);

    }

    public ReservationDTO updateReservation(Long id, ReservationSaveDTO reservationSaveDTO) throws NotFoundException {
        Reservation existingReservation = findByEntity(id)
                .orElseThrow(() -> new NotFoundException("Reserva con id: " + id + "no encontrada"));
        User user = userRepository.findById(reservationSaveDTO.getUserId())
                .orElseThrow(()-> new NotFoundException("Usuario no encontrado"));
        Product newProduct = productRepository.findById(reservationSaveDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        LocalDate startDate = reservationSaveDTO.getStartDate();
        LocalDate endDate = reservationSaveDTO.getEndDate();

        if (startDate.isBefore(LocalDate.now())){
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a la fecha actual");
        }

        if (endDate.isBefore(startDate)){
            throw new IllegalArgumentException("La fecha de finalización debe ser igual o posterior a la fecha de inicio de alquiler");
        }


        modelMapper.map(reservationSaveDTO, existingReservation);
        existingReservation.setUser(user);

        /*if(existingReservation.getProduct() == null) {
            existingReservation.getProduct().forEach(product -> product.getReservations().remove(existingReservation));
            existingReservation.getProduct().clear();
        }*/

        Long productId = reservationSaveDTO.getProductId();
        if (productId != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
            existingReservation.setProduct(product);
            product.setReservation(existingReservation);
        }

        Reservation reservation = convertToEntity(reservationSaveDTO);
        reservation.setUser(user);
        reservation.setCreationDateTime(LocalDateTime.now());

        syncReservationWithProducts(reservation, reservationSaveDTO.getProductId());

        Reservation sevedReservation = reservationRepository.save(existingReservation);
        return convertToDTO(sevedReservation);
    }

    public List<ReservationDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }

    public ReservationDTO findById(Long id) throws NotFoundException {
        Reservation reservation = findByEntity(id)
                .orElseThrow(()-> new NotFoundException("Reserva con id: " + id + " no encontrada"));

        return convertToDTO(reservation);
    }

    public void deleteById(Long id) throws NotFoundException {
        Reservation reservation = findByEntity(id)
                .orElseThrow(()-> new NotFoundException("Reserva con id: " + id + " no encontrada"));
        /*
        for (Product product: reservation.getProduct()){
            product.getReservation().remove(reservation);
        }*/

        if (reservation.getProduct() != null) {

            reservation.getProduct().setReservation(null);
        }

        reservationRepository.deleteById(id);
    }

    public Reservation convertToEntity(ReservationSaveDTO reservationSaveDTO){
        return modelMapper.map(reservationSaveDTO, Reservation.class);
    }

    private void syncReservationWithProducts(Reservation reservation, Long productId) throws NotFoundException {
        if (productId != null){
            //for (Long productId: productIds){
                Product product = productRepository.findById(productId)
                        .orElseThrow(()-> new NotFoundException("El producto no existe"));


                if (product.getStock() <= 0) {
                    throw new IllegalArgumentException("El producto " + product.getName() + " no tiene stock disponible disponible");
                }

                /*if (reservation.getProduct() == null) {
                    reservation.setProduct(new HashSet<>());
                }*/
                /*
                boolean isProductAvailable = product.getReservations().stream()
                                .noneMatch(r -> r.getStartDate().isBefore(endDate) && r.getEndDate().isAfter(startDate));
                */
                /*reservation.getProduct().add(product);
                product.getReservation().add(reservation);*/

                reservation.setProduct(product);
                product.setReservation(reservation);

                product.setStock(product.getStock() - 1);
            //}
        }
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO reservationDTO = modelMapper.map(reservation, ReservationDTO.class);
        /*reservationDTO.setProductId(
                reservation.getProduct().stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())
        );*/

        reservationDTO.setProductId(reservation.getProduct() != null ? reservation.getProduct().getId() : null);

        reservationDTO.setUserId(reservation.getUser().getId());
        return reservationDTO;
    }

    private Optional<Reservation> findByEntity(Long id) {
        return reservationRepository.findById(id);
    }
}
