package proyecto.dh.resources.payment.service;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.dh.resources.payment.dto.payment.PaymentDTO;
import proyecto.dh.resources.payment.dto.payment.PaymentSaveDTO;
import proyecto.dh.resources.payment.entity.Payment;
import proyecto.dh.resources.payment.repository.PaymentRepository;
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
public class PaymentService {
    @Autowired
    private final PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final ModelMapper modelMapper;


    public PaymentService(PaymentRepository paymentRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public PaymentDTO save(@Valid PaymentSaveDTO paymentSaveDTO) throws NotFoundException{
        User user = userRepository.findById(paymentSaveDTO.getUserId())
                .orElseThrow(()-> new NotFoundException("Usuario no encontrado"));

        Payment payment = convertToEntity(paymentSaveDTO);

       /* if (paymentSaveDTO.getPaymentType() == PaymentType.CARD) {
            payment = new Card();
            updateCardDetails((Card) payment, paymentSaveDTO);
        } else {
            payment = new Payment();
        }*/

        payment.setUser(user);

        syncPaymentWithProduct(payment, paymentSaveDTO.getProductIds());

        Payment savedPayment = paymentRepository.save(payment);
        return convertToDTO(savedPayment);
    }
    /*
    private void updateCardDetails(Card card, PaymentSaveDTO paymentSaveDTO) {
        card.setCardNumber(paymentSaveDTO.getCardNumber());
        card.setHolderName(paymentSaveDTO.getHolderName());
        card.setExpirationDate(paymentSaveDTO.getExpirationDate());
        card.setCvv(paymentSaveDTO.getCvv());
    }*/
    public PaymentDTO updatePayment(Long id, PaymentSaveDTO paymentSaveDTO) {
        Payment existingPayment = findByEntity(id)
                .orElseThrow(() -> new NotFoundException("Pago con id " + id + " cno encontrado"));

        modelMapper.map(paymentSaveDTO, existingPayment);

        if(existingPayment != null) {
            existingPayment.getProducts().forEach(product -> product.getPayments().remove(existingPayment));
            existingPayment.getProducts().clear();
        }

        syncPaymentWithProduct(existingPayment, paymentSaveDTO.getProductIds());

        Payment savedPayment = paymentRepository.save(existingPayment);
        return convertToDTO(savedPayment);
    }

    public List<PaymentDTO> findAll(){
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws NotFoundException {
        Payment payment = findByEntity(id)
                .orElseThrow(() -> new NotFoundException("Pago con id " + id + " no encontrado"));

        for (Product product: payment.getProducts()){
            product.getPayments().remove(payment);
        }

        paymentRepository.deleteById(id);
    }

    public PaymentDTO findById(Long id) throws NotFoundException{
        Payment payment = findByEntity(id)
                .orElseThrow(() -> new NotFoundException("Payment con el id " + id + " no encontrado"));

        return convertToDTO(payment);
    }

    public Payment convertToEntity(PaymentSaveDTO paymentSaveDTO){
        //Payment payment;
        /*if (paymentSaveDTO.getPaymentType() == PaymentType.CARD) {
            payment = new Card();
            updateCardDetails((Card) payment, paymentSaveDTO);
        } else {
            payment = new Payment();
        }*/
        //modelMapper.map(paymentSaveDTO, payment);
        //return payment;
        return modelMapper.map(paymentSaveDTO, Payment.class);
    }

    public void syncPaymentWithProduct(Payment payment, List<Long> productIds) throws NotFoundException{
        if (productIds != null) {
            for (Long productId : productIds) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new NotFoundException("El producto con el id " + productId + " no existe"));

                if (payment.getProducts() == null) {
                    payment.setProducts(new HashSet<>());
                }

                payment.getProducts().add(product);
                product.getPayments().add(payment);
            }
        }

    }

    public PaymentDTO convertToDTO(Payment payment){
        PaymentDTO paymentDTO = modelMapper.map(payment, PaymentDTO.class);
        paymentDTO.setProductIds(
                payment.getProducts().stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())
        );

        paymentDTO.setUserId(payment.getUser().getId());

        /*if (payment instanceof Card) {
            Card card = (Card) payment;
            paymentDTO.setCardNumber(card.getCardNumber());
            paymentDTO.setHolderName(card.getHolderName());
            paymentDTO.setExpirationDate(card.getExpirationDate());
            paymentDTO.setCvv(card.getCvv());
        }*/

        return paymentDTO;
    }

    public Optional<Payment> findByEntity(Long id){
        return paymentRepository.findById(id);
    }
}
