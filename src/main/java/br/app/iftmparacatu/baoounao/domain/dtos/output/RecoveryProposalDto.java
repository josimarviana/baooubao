package br.app.iftmparacatu.baoounao.domain.dtos.output;

import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalCategoryEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class RecoveryProposalDto {
    private Long id;
    private String title;
    private String description;
    private Blob photography;
    private String name;
    private List<ProposalCategoryEntity> proposalCategoryEntityList;
    private LocalDateTime createdAt;
}
