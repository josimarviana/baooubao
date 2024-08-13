package br.app.iftmparacatu.baoounao.domain.dtos.output;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RecoveryProposalDto {
    private Long id;
    private String title;
    private String description;
    private byte [] image;
    private String author;
    private int likes;
    private String category;
    private String situation;
    private String videoUrl;
    private LocalDateTime createdAt;
}
