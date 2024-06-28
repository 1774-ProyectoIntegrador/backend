package proyecto.dh.resources.users.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.users.dto.UserAddressDTO;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.entity.UserAddress;
import proyecto.dh.resources.users.repository.UserAddressRepository;
import proyecto.dh.resources.users.repository.UserRepository;

@Service
public class UserAddressService {
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final ModelMapper modelMapper;

    public UserAddressService(UserRepository userRepository, UserAddressRepository userAddressRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public UserAddressDTO createAddress(UserAddressDTO addressDTO, UserDetails currentUser) throws BadRequestException {
        User user = userRepository.findByEmail(currentUser.getUsername()).orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        UserAddress addressEntity = modelMapper.map(addressDTO, UserAddress.class);
        user.setAddress(addressEntity);
        userRepository.save(user);

        return modelMapper.map(addressEntity, UserAddressDTO.class);
    }

    public UserAddressDTO getCurrentUserAddress(UserDetails currentUser) throws BadRequestException {
        User user = userRepository.findByEmail(currentUser.getUsername()).orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        UserAddress address = user.getAddress();
        if (address == null) {
            throw new BadRequestException("No se encontró dirección para el usuario");
        }

        return modelMapper.map(address, UserAddressDTO.class);
    }
}
