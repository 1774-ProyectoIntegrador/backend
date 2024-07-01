package proyecto.dh.resources.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.payment.dto.transfer.TransferDTO;
import proyecto.dh.resources.payment.dto.transfer.TransferSaveDTO;
import proyecto.dh.resources.payment.service.TransferService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("public/products/payments/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @GetMapping
    public ResponseEntity<List<TransferDTO>> findAll() {
        List<TransferDTO> transfers = transferService.findAll();
        return ResponseEntity.ok(transfers);
    }

    @GetMapping("/{transferId}")
    public ResponseEntity<TransferDTO> findById(@Valid @PathVariable Long transferId) throws NotFoundException {
        TransferDTO transfer = transferService.findById(transferId);
        return ResponseEntity.ok(transfer);
    }

    @PostMapping
    public ResponseEntity<TransferDTO> createTransfer(@Valid @RequestBody TransferSaveDTO transferSaveDTO) throws NotFoundException {
        TransferDTO createdTransfer = transferService.save(transferSaveDTO);
        return new ResponseEntity<>(createdTransfer, HttpStatus.CREATED);
    }

    @PutMapping("/{transferId}")
    public ResponseEntity<TransferDTO> updateCard(@PathVariable Long transferId, @Valid @RequestBody TransferSaveDTO transferSaveDTO) throws NotFoundException {
        TransferDTO updatedTransfer = transferService.updatetransfer(transferId, transferSaveDTO);
        return ResponseEntity.ok(updatedTransfer);
    }

    @DeleteMapping("/{transferId}")
    public ResponseEntity<Void> deleteTransfer(@PathVariable Long transferId) {
        transferService.deleteById(transferId);
        return ResponseEntity.ok().build();
    }

}
