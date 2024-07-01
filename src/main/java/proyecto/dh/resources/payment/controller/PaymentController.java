package proyecto.dh.resources.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.payment.dto.payment.PaymentDTO;
import proyecto.dh.resources.payment.dto.payment.PaymentSaveDTO;
import proyecto.dh.resources.payment.service.PaymentService;
import proyecto.dh.resources.users.repository.UserRepository;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("public/products/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> findAll() {
         List<PaymentDTO> payments = paymentService.findAll();
         return ResponseEntity.ok(payments);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> findById(@Valid @PathVariable Long paymentId){
        PaymentDTO payment = paymentService.findById(paymentId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping//("/card")
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentSaveDTO paymentSaveDTO) throws NotFoundException {
        PaymentDTO createdPayment = paymentService.save(paymentSaveDTO);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    /*@PostMapping("/transfer")
    public ResponseEntity<PaymentDTO> createTransferPayment(@Valid @RequestBody TransferPaymentSaveDTO transferPaymentSaveDTO) throws NotFoundException {
        PaymentDTO createdPayment = paymentService.saveTransferPayment(transferPaymentSaveDTO);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }*/

    @PutMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Long paymentId,@Valid @RequestBody PaymentSaveDTO paymentSaveDTO) throws NotFoundException {
        PaymentDTO updatedPayment = paymentService.updatePayment(paymentId, paymentSaveDTO);
        return ResponseEntity.ok(updatedPayment);
    }

    /*@PutMapping("/transfer/{paymentId}")
    public ResponseEntity<PaymentDTO> updateTransferPayment(@PathVariable Long paymentId, @Valid @RequestBody TransferPaymentSaveDTO transferPaymentSaveDTO) throws NotFoundException {
        PaymentDTO updatedPayment = paymentService.updateTransferPayment(paymentId, transferPaymentSaveDTO);
        return ResponseEntity.ok(updatedPayment);
    }*/

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long paymentId) throws NotFoundException {
        paymentService.deleteById(paymentId);
        return ResponseEntity.ok().build();
    }
}
