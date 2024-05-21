package proyecto.dh.common.responses;

import jakarta.annotation.Nullable;
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
