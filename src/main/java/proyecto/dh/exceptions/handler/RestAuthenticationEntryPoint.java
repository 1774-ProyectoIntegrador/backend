package proyecto.dh.exceptions.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import proyecto.dh.exceptions.dto.ExceptionDetails;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ExceptionDetails details = new ExceptionDetails();
        details.setTimestamp(new Date());
        details.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        details.setMessage("Token inválido o expirado");
        details.setPath(request.getRequestURI());

        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, details);
        out.flush();
    }
}
