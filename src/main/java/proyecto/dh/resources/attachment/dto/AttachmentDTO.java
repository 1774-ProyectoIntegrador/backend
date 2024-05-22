package proyecto.dh.resources.attachment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachmentDTO {

    private Long id;

    private String url;

    private String fileName;


}
