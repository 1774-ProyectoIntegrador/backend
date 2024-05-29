package proyecto.dh.resources.users.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.users.dto.UserCreateDTO;
import proyecto.dh.resources.users.dto.UserDTO;
import proyecto.dh.resources.users.service.UserService;

/**
 *  TODO:
 *  ! Se requiere implementar los endpoints faltantes:
 *  DONE - POST = Para la creación de usuarios
 *  PENDING - GET = Para el listado de usuarios
 *  PENDING - GET ("/{id}") = Para la búsqueda por ID
 *  PENDING - GET ("/me") = Para obtener la información del usuario actual (para el front-end)
 *  PENDING - PUT ("/{id}") = Para actualizar un usuario
 *  ? Implementar logica correspondiente para el UserService de cada endpoint.
 */

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) throws BadRequestException, BadRequestException {
        UserDTO createdUser = userService.create(userCreateDTO);
        return ResponseEntity.ok(createdUser);
    }
}
