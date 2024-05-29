package proyecto.dh.exceptions.handler;



import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import proyecto.dh.exceptions.dto.ExceptionDetails;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ExceptionDetails details = new ExceptionDetails();
        details.setTimestamp(new Date());
        details.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
        details.setMessage("Acceso denegado: No tienes permisos para acceder a este recurso");
        details.setPath(request.getRequestURI());

        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, details);
        out.flush();
    }
}
