package br.app.iftmparacatu.baoounao.domain.dtos.output;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RecoveryProposalWhithoutImageDto {
    private Long id;
    private String title;
    private String description;
    private String author;
    private String category;
    private String situation;
    private String videoUrl;
    private LocalDateTime createdAt;
}
