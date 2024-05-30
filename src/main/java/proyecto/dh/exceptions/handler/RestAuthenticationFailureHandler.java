package proyecto.dh.exceptions.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import proyecto.dh.exceptions.dto.ExceptionDetails;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@Component
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ExceptionDetails details = new ExceptionDetails();
        details.setTimestamp(new Date());
        details.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        details.setMessage("Correo electrónico y/o contraseña son incorrectos");
        details.setPath(request.getRequestURI());

        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, details);
        out.flush();
    }
}
