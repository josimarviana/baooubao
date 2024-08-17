package br.app.iftmparacatu.baoounao.domain.dtos.output;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecoveryBasicProposalDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String icon;
}
