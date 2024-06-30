package proyecto.dh.resources.payment.service;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.dh.common.enums.PaymentType;
import proyecto.dh.resources.payment.dto.transfer.TransferDTO;
import proyecto.dh.resources.payment.dto.transfer.TransferSaveDTO;
import proyecto.dh.resources.payment.entity.Transfer;
import proyecto.dh.resources.payment.repository.TransferRepository;
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
public class TransferService {

    @Autowired
    private final TransferRepository transferRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final ModelMapper modelMapper;

    public TransferService(TransferRepository transferRepository, UserRepository userRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public TransferDTO save(@Valid TransferSaveDTO transferSaveDTO) throws NotFoundException{
        User user = userRepository.findById(transferSaveDTO.getUserId())
                .orElseThrow(()-> new NotFoundException("Usuario no encontrado"));

        Transfer transfer = convertToEntity(transferSaveDTO);

        if (transfer.getAmount() == null) {
            throw new IllegalArgumentException("El monto no puede ser nulo");
        }

        transfer.setUser(user);
        transfer.setPaymentType(PaymentType.TRANSFER);

        syncCardWithProducts(transfer, transferSaveDTO.getProductIds());

        Transfer savedtransfer = transferRepository.save(transfer);

        return convertToDTO(savedtransfer);
    }

    @Transactional
    public TransferDTO updatetransfer(Long id, TransferSaveDTO transferSaveDTO) {
        Transfer existingTransfer = findByEntity(id)
                .orElseThrow(() -> new NotFoundException("Transferencia con el id " + id + " no encontrada"));

        modelMapper.map(transferSaveDTO, existingTransfer);

        if(existingTransfer.getProducts() != null) {
            existingTransfer.getProducts().forEach(product -> product.getPayments().remove(existingTransfer));
            existingTransfer.getProducts().clear();
        }

        syncCardWithProducts(existingTransfer, transferSaveDTO.getProductIds());
        Transfer savedTransfer = transferRepository.save(existingTransfer);
        return convertToDTO(savedTransfer);
    }

    public List<TransferDTO> findAll() {
        return transferRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TransferDTO findById(Long id) throws NotFoundException{
        Transfer transfer = findByEntity(id)
                .orElseThrow(() -> new NotFoundException("Transferencia con id: " + id + " No encontrada"));

        return convertToDTO(transfer);
    }

    public void deleteById(Long id) {
        Transfer transfer = findByEntity(id)
                .orElseThrow(() -> new NotFoundException("Transferencia con el id " + id + " no encontrada"));

        for (Product product: transfer.getProducts()){
            product.getPayments().remove(transfer);
        }

        transferRepository.deleteById(id);
    }

    public TransferDTO convertToDTO(Transfer transfer){
        TransferDTO transferDTO = modelMapper.map(transfer, TransferDTO.class);
        transferDTO.setProductIds(
                transfer.getProducts().stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())
        );
        transferDTO.setUserId(transfer.getUser().getId());
        return transferDTO;
    }

    public Transfer convertToEntity(TransferSaveDTO transferSaveDTO){
        Transfer transfer = modelMapper.map(transferSaveDTO, Transfer.class);
        transfer.setPaymentType(PaymentType.TRANSFER);

        transfer.setAmount(transferSaveDTO.getAmount());

        return transfer;
    }

    private void syncCardWithProducts(Transfer transfer, List<Long> productIds) throws NotFoundException {
        if (productIds != null) {
            for (Long productId : productIds) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new NotFoundException("El producto no existe"));

                // Se añade para manejar los valores nulos
                if (transfer.getProducts() == null) {
                    transfer.setProducts(new HashSet<>());
                }

                transfer.getProducts().add(product);
                product.getPayments().add(transfer);
            }
        }
    }

    public Optional<Transfer> findByEntity(Long id){
        return transferRepository.findById(id);
    }
}
