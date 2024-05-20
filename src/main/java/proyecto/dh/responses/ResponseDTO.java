package proyecto.dh.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    private Date timestamp;
    private String message;
    private String status;
    private T data;
}
