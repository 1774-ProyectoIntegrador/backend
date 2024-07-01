package proyecto.dh.resources.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.payment.dto.card.CardDTO;
import proyecto.dh.resources.payment.dto.card.CardSaveDTO;
import proyecto.dh.resources.payment.service.CardService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("public/products/payments/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public ResponseEntity<List<CardDTO>> findAll() {
        List<CardDTO> cards = cardService.findAll();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardDTO> findById(@Valid @PathVariable Long cardId) throws NotFoundException {
        CardDTO card = cardService.findById(cardId);
        return ResponseEntity.ok(card);
    }

    @PostMapping
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardSaveDTO cardSaveDTO) throws NotFoundException {
        CardDTO createdCard = cardService.save(cardSaveDTO);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<CardDTO> updateCard(@PathVariable Long cardId,@Valid @RequestBody CardSaveDTO cardSaveDTO) throws NotFoundException {
        CardDTO updatedCard = cardService.updateCard(cardId, cardSaveDTO);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteById(cardId);
        return ResponseEntity.ok().build();
    }
}
