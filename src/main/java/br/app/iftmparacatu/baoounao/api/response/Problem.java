package br.app.iftmparacatu.baoounao.api.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
public class Problem {
    private OffsetDateTime dataHora;
    private String mensagem;
    private Map<String,String> detalhes;
}