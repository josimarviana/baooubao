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
public class RecoveryProposalDto {
    private Long id;
    private String title;
    private String description;
    private String image;
    private String author;
    private int likes;
    private String category;
    private String situation;
    private String videoUrl;
    private String icon;
    private LocalDateTime createdAt;
}
