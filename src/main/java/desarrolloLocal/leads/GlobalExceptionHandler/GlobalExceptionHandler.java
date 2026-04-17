package desarrolloLocal.leads.GlobalExceptionHandler;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleInternalServerError(Exception ex, HttpServletRequest request) {

        ErrorResponseDto error;
        HttpStatus status;

        if(ex instanceof IllegalArgumentException){
            status = HttpStatus.BAD_REQUEST;
            error = GetError(
                status.value(),
                "Datos Errados en la solicitud",
                "Ocurrió un error con los datos ingresados: " + ex.getMessage(),
                request.getRequestURI()
            );
        } else if (ex instanceof MethodArgumentTypeMismatchException) {
            status = HttpStatus.BAD_REQUEST;
            error = GetError(
                status.value(),
                "Tipo de parámetro incorrecto",
                String.format("El parámetro '%s' debe ser de tipo %s",
                        ((MethodArgumentTypeMismatchException) ex).getName(),
                        ((MethodArgumentTypeMismatchException) ex).getRequiredType().getSimpleName()),
                request.getRequestURI()
            );
        }
        else if (ex instanceof HttpMessageNotReadableException) {
            status = HttpStatus.BAD_REQUEST;
            error = GetError(
                status.value(),
                "Error en el formato del JSON",
                "Uno de los campos enviados tiene un tipo de dato no válido (ej. String por Integer)",
                request.getRequestURI()
            );
        }
        else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            error = GetError(
                status.value(),
                "Error interno del servidor",
                "Ocurrió un error inesperado: " + ex.getMessage(),
                request.getRequestURI());
        }

        return new ResponseEntity<>(error,status);
    }

    private ErrorResponseDto GetError(
            int codeStatus,
            String titleError,
            String messageError,
            String requestUri){

        return new ErrorResponseDto(
                LocalDateTime.now(),
                codeStatus,
                titleError,
                messageError,
                requestUri);
    }
}
