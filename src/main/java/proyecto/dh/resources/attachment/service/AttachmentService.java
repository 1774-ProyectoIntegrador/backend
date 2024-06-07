package proyecto.dh.resources.attachment.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.repository.AttachmentRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttachmentService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Attachment uploadAttachment(MultipartFile file) throws IOException, BadRequestException {
        String fileKey = s3Service.uploadFile(file);
        String attachmentUrl = s3Service.getFileUrl(fileKey);

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileKey(fileKey);
        attachment.setUrl(attachmentUrl);

        return attachmentRepository.save(attachment);
    }

    public List<Attachment> uploadAttachments(List<MultipartFile> files) throws IOException, BadRequestException {
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            Attachment attachment = uploadAttachment(file);
            attachments.add(attachment);
        }
        return attachments;
    }

    public Attachment findById(Long id) throws BadRequestException {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("ID de archivo adjunto no válido"));
    }


    public List<AttachmentDTO> findAll() {
        List<Attachment> attachmentsList = attachmentRepository.findAll();
        return attachmentsList.stream()
                .map(this::convertToDto)
                .toList();
    }

    public void deleteAttachments(List<Long> attachmentIds) throws BadRequestException {
        for (Long id : attachmentIds) {
            deleteAttachment(id);
        }
    }

    public void deleteAttachment(Long attachmentId) throws BadRequestException {
        Attachment attachment = findById(attachmentId);
        s3Service.deleteFile(attachment.getFileKey());
        attachmentRepository.delete(attachment);
    }

    public void deleteAttachmentsByEntities(List<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            s3Service.deleteFile(attachment.getFileKey());
            attachmentRepository.delete(attachment);
        }
    }

    // Métodos de conversión
    public AttachmentDTO convertToDto(Attachment attachment) {
        AttachmentDTO attachmentDTO = modelMapper.map(attachment, AttachmentDTO.class);
        if (attachment.getProduct() != null) {
            attachmentDTO.setProductsIds(List.of(attachment.getProduct().getId()));
        }
        return attachmentDTO;
    }
    public Attachment convertToEntity(AttachmentDTO attachmentDTO) {
        return modelMapper.map(attachmentDTO, Attachment.class);
    }

    public void validateFileTypeImages(Attachment attachment) throws BadRequestException {
        String contentType = attachment.getFileName().substring(attachment.getFileName().lastIndexOf('.') + 1).toLowerCase();
        if (!"jpeg".equals(contentType) && !"png".equals(contentType) && !"jpg".equals(contentType) && !"webp".equals(contentType)) {
            throw new BadRequestException("Solo se permiten archivos WEBP, JPEG, PNG y JPG");
        }
    }
}