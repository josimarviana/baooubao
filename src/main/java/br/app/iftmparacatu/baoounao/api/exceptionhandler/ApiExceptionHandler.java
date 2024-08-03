package br.app.iftmparacatu.baoounao.api.exceptionhandler;

import br.app.iftmparacatu.baoounao.api.response.Problem;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
//    @ExceptionHandler(EntidadeNaoEncontradaException.class)
//    public ResponseEntity<?> tratarEntidadeNaoEncontrada(EntidadeNaoEncontradaException e, WebRequest request){
//        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND,request);
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Problem> handleGenericException(Exception ex) {
        Problem problem = Problem.builder()
                .dataHora(OffsetDateTime.now())
                .mensagem(ex.getMessage())
                .detalhes(new HashMap<>()) // Adicione detalhes adicionais, se necessário
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (e instanceof MethodArgumentNotValidException) {
            // Tratamento personalizado para a exceção MethodArgumentNotValidException

            Map<String, String> errors = new HashMap<>();
            ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });

            // Crie uma instância de Problem com os detalhes desejados
            body = Problem.builder()
                    .dataHora(OffsetDateTime.now())
                    .mensagem("Erro de validação")
                    .detalhes(errors)
                    .build();

            status = HttpStatus.BAD_REQUEST;
        } else {
            // Tratamento para outras exceções
            if (body == null) {
                body = Problem.builder()
                        .dataHora(OffsetDateTime.now())
                        .mensagem(status.toString())
                        .build();
            } else if (body instanceof String) {
                body = Problem.builder()
                        .dataHora(OffsetDateTime.now())
                        .mensagem((String) body)
                        .build();
            }
        }

        return super.handleExceptionInternal(e, body, headers, status, request);
    }

}