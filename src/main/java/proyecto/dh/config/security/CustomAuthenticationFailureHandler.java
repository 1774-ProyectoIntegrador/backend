package proyecto.dh.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import proyecto.dh.exceptions.dto.ExceptionDetails;

import java.io.IOException;
import java.util.Date;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ExceptionDetails details = new ExceptionDetails();
        details.setTimestamp(new Date());
        details.setMessage("Token invalido o expirado");
        details.setError("Unauthorized " + HttpServletResponse.SC_UNAUTHORIZED); // "Unauthorized"
        details.setPath(request.getRequestURI());

        // Convert ExceptionDetails to JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(details);

        // Write JSON to response
        response.getWriter().write(json);
    }
}
