package proyecto.dh.exceptions.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDetails {
    private Date timestamp;
    private String message;
    private String details;
}
