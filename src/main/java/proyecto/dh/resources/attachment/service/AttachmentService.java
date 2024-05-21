package proyecto.dh.resources.attachment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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

    public Attachment uploadAttachment(MultipartFile file) throws IOException {
        String fileKey = s3Service.uploadFile(file);
        String attachmentUrl = s3Service.getFileUrl(fileKey);

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileKey(fileKey);
        attachment.setUrl(attachmentUrl);

        return attachmentRepository.save(attachment);
    }

    public List<Attachment> uploadAttachments(List<MultipartFile> files) throws IOException {
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            Attachment attachment = uploadAttachment(file);
            attachments.add(attachment);
        }
        return attachments;
    }

    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId).orElseThrow(() -> new IllegalArgumentException("Invalid attachment ID"));
        s3Service.deleteFile(attachment.getFileKey());
        attachmentRepository.delete(attachment);
    }

    public void deleteAttachments(List<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            s3Service.deleteFile(attachment.getFileKey());
            attachmentRepository.delete(attachment);
        }
    }
}
