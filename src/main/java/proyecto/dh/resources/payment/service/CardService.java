package proyecto.dh.resources.payment.service;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.dh.common.enums.PaymentType;
import proyecto.dh.resources.payment.dto.card.CardDTO;
import proyecto.dh.resources.payment.dto.card.CardSaveDTO;
import proyecto.dh.resources.payment.entity.Card;
import proyecto.dh.resources.payment.repository.CardRepository;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.repository.ProductRepository;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private final CardRepository cardRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final ModelMapper modelMapper;

    public CardService(CardRepository cardRepository, UserRepository userRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CardDTO save(@Valid CardSaveDTO cardSaveDTO) throws NotFoundException{
        User user = userRepository.findById(cardSaveDTO.getUserId())
                .orElseThrow(()-> new NotFoundException("Usuario no encontrado"));

        Card card = convertToEntity(cardSaveDTO);

        if (card.getAmount() == null) {
            throw new IllegalArgumentException("El monto no puede ser nulo");
        }

        card.setUser(user);
        card.setPaymentType(PaymentType.CARD);

        syncCardWithProducts(card, cardSaveDTO.getProductIds());

        Card savedCard = cardRepository.save(card);

        return convertToDTO(savedCard);
    }

    @Transactional
    public CardDTO updateCard(Long id, CardSaveDTO cardSaveDTO) {
        Card existingCard = findByEntity(id)
                .orElseThrow(() -> new NotFoundException("Tarjeta con el id " + id + " no encontrada"));

        modelMapper.map(cardSaveDTO, existingCard);

        if(existingCard.getProducts() != null) {
            existingCard.getProducts().forEach(product -> product.getPayments().remove(existingCard));
            existingCard.getProducts().clear();
        }

        syncCardWithProducts(existingCard, cardSaveDTO.getProductIds());
        Card savedCard = cardRepository.save(existingCard);
        return convertToDTO(savedCard);
    }

    public List<CardDTO> findAll() {
        return cardRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CardDTO findById(Long id) throws NotFoundException{
        Card card = findByEntity(id)
                .orElseThrow(() -> new NotFoundException("Tarjeta con id: " + id + " No encontrada"));

        return convertToDTO(card);
    }

    public void deleteById(Long id) {
        Card card = findByEntity(id)
                .orElseThrow(() -> new NotFoundException("Tarjeta con el id " + id + " no encontrada"));

        for (Product product: card.getProducts()){
            product.getPayments().remove(card);
        }

        cardRepository.deleteById(id);
    }

    public CardDTO convertToDTO(Card card){
        CardDTO cardDTO = modelMapper.map(card, CardDTO.class);
        cardDTO.setProductIds(
                card.getProducts().stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())
        );
        cardDTO.setUserId(card.getUser().getId());
        return cardDTO;
    }

    public Card convertToEntity(CardSaveDTO cardSaveDTO){
        Card card = modelMapper.map(cardSaveDTO, Card.class);
        card.setPaymentType(PaymentType.CARD);

        card.setAmount(cardSaveDTO.getAmount());

        return card;
    }

    private void syncCardWithProducts(Card card, List<Long> productIds) throws NotFoundException {
        if (productIds != null) {
            for (Long productId : productIds) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new NotFoundException("El producto no existe"));

                // Se añade para manejar los valores nulos
                if (card.getProducts() == null) {
                    card.setProducts(new HashSet<>());
                }

                card.getProducts().add(product);
                product.getPayments().add(card);
            }
        }
    }

    public Optional<Card> findByEntity(Long id){
        return cardRepository.findById(id);
    }
}
