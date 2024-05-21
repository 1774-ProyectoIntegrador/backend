package proyecto.dh.resources.attachment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.common.responses.ResponseDTO;
import proyecto.dh.common.responses.ResponseHandler;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.dto.ProductDTO;

import java.io.IOException;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @Operation(summary = "Eliminar un archivo adjunto por ID", description = "Esta operación elimina un archivo adjunto del S3 basado en el ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo adjunto eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "ID de archivo adjunto no válido"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteAttachment(@PathVariable Long id) throws BadRequestException {
        try {
            attachmentService.deleteAttachment(id);
            return ResponseHandler.generateResponse("Archivo adjunto eliminado correctamente", HttpStatus.OK, null);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Error al borrar archivo adjunto: " + e.getMessage());
        }
    }
}
