package proyecto.dh.exceptions;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import proyecto.dh.exceptions.dto.ExceptionDetails;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;

import java.util.Date;

@ControllerAdvice
public class GlobalException {

    private ResponseEntity<ExceptionDetails> buildResponseEntity(Exception ex, HttpStatus status, String message, WebRequest request) {
        ExceptionDetails details = new ExceptionDetails();
        details.setTimestamp(new Date());
        details.setMessage(message);
        details.setError(status.getReasonPhrase());
        details.setPath(request.getDescription(false).substring(4)); // Remove 'uri=' prefix
        return new ResponseEntity<>(details, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDetails> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.CONFLICT, "Vulneración de la integridad de los datos, póngase en contacto con el administrador.", request);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExceptionDetails> handleDataAccessException(DataAccessException ex, WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Error de base de datos", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDetails> handleGlobalException(Exception ex, WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDetails> handleNotFoundException(NotFoundException ex, WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDetails> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }
}