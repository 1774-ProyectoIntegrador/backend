package proyecto.dh.resources.attachment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attachments")
@Tag(name = "Attachments Controller", description = "Controlador para la gestión de archivos")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @Operation(summary = "Subir archivos adjuntos", description = "Esta operación sube archivos adjuntos y devuelve sus IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivos subidos correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al subir archivos"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/upload")
    public ResponseEntity<List<AttachmentDTO>> uploadAttachments(
            @Parameter(description = "Los archivos a subir", required = true, content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "array", format = "binary"))) @RequestParam("files") List<MultipartFile> files) throws IOException, BadRequestException {
        List<AttachmentDTO> attachmentDTOs = attachmentService.uploadAttachments(files).stream()
                .map(attachment -> new AttachmentDTO(attachment.getId(), attachment.getUrl(), attachment.getFileName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(attachmentDTOs);
    }

    @Operation(summary = "Eliminar uno o varios archivos adjuntos por ID", description = "Esta operación elimina uno o varios archivos adjuntos del S3 basados en sus IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivos adjuntos eliminados correctamente"),
            @ApiResponse(responseCode = "400", description = "IDs de archivos adjuntos no válidos"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping
    public ResponseEntity<String> deleteAttachments(@RequestBody List<Long> ids) throws BadRequestException {
        attachmentService.deleteAttachments(ids);
        return ResponseEntity.ok("Archivos adjuntos eliminados correctamente");
    }

    @Operation(summary = "Obtener detalles de un archivo adjunto por ID", description = "Esta operación devuelve los detalles de un archivo adjunto basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo adjunto encontrado"),
            @ApiResponse(responseCode = "404", description = "Archivo adjunto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AttachmentDTO> getAttachmentById(@PathVariable Long id) throws BadRequestException {
        Attachment attachment = attachmentService.findById(id);
        AttachmentDTO attachmentDTO = new AttachmentDTO(attachment.getId(), attachment.getUrl(), attachment.getFileName());
        return ResponseEntity.ok(attachmentDTO);
    }
}
