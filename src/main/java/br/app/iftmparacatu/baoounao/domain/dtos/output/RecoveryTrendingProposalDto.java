package br.app.iftmparacatu.baoounao.domain.dtos.output;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RecoveryTrendingProposalDto {
    private Long id;
    private String title;
    private String description;
    private String category;
}
