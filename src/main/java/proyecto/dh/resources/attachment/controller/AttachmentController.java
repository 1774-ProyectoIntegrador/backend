package proyecto.dh.resources.attachment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.common.responses.ResponseDTO;
import proyecto.dh.common.responses.ResponseHandler;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;

import java.io.IOException;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<ResponseDTO<Attachment>> uploadAttachment(@RequestParam("file") MultipartFile file) {
        try {
            Attachment attachment = attachmentService.uploadAttachment(file);
            return ResponseHandler.generateResponse("Attachment uploaded successfully", HttpStatus.OK, attachment);
        } catch (IOException e) {
            return ResponseHandler.generateResponse("Error uploading attachment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteAttachment(@PathVariable Long id) {
        try {
            attachmentService.deleteAttachment(id);
            return ResponseHandler.generateResponse("Attachment deleted successfully", HttpStatus.OK, null);
        } catch (IllegalArgumentException e) {
            return ResponseHandler.generateResponse("Error deleting attachment: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }
}
