package br.app.iftmparacatu.baoounao.api.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
public class Success<T> {
    private OffsetDateTime dataHora;
    private String mensagem;
    private T dados;
}