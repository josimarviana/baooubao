package br.app.iftmparacatu.baoounao.domain.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecoveryProposalFilterDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String icon;
    private int votes;
    private LocalDateTime createdAt;
}
