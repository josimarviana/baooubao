package br.app.iftmparacatu.baoounao.domain.util;

import br.app.iftmparacatu.baoounao.api.response.Success;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

public class ResponseUtil {
    public static <T> ResponseEntity<Object> createSuccessResponse(String message, T data, HttpStatus statusCode) {
        Success<T> response = Success.<T>builder()
                .dataHora(OffsetDateTime.now())
                .mensagem(message)
                .dados(data)
                .build();

        return new ResponseEntity<>(response, statusCode);
    }

    public static <T> ResponseEntity<Object> createSuccessResponse(String message, HttpStatus statusCode) {
        Success<T> response = Success.<T>builder()
                .dataHora(OffsetDateTime.now())
                .mensagem(message)
                .build();

        return new ResponseEntity<>(response, statusCode);
    }
}
