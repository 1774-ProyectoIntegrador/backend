package proyecto.dh.resources.reservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.reservation.dto.ReservationDTO;
import proyecto.dh.resources.reservation.dto.ReservationSaveDTO;
import proyecto.dh.resources.reservation.service.ReservationService;
import proyecto.dh.resources.users.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("public/products/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> findAll() {
        List<ReservationDTO> reservations = reservationService.findAll();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> findById(@Valid @PathVariable Long reservationId) throws NotFoundException {
        ReservationDTO reservation = reservationService.findById(reservationId);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationSaveDTO reservationSaveDTO) throws NotFoundException {
        ReservationDTO createdReservation = reservationService.save(reservationSaveDTO);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable Long reservationId, @RequestBody ReservationSaveDTO reservationSaveDTO) throws NotFoundException{
        ReservationDTO updatedReservation = reservationService.updateReservation(reservationId, reservationSaveDTO);
        return ResponseEntity.ok(updatedReservation);
    };

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> delete(@PathVariable Long reservationId) throws  NotFoundException {
        reservationService.deleteById(reservationId);
        return ResponseEntity.ok().build();
    }
}
