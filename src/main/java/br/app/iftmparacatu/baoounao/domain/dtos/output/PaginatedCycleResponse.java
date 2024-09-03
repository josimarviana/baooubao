package br.app.iftmparacatu.baoounao.domain.dtos.output;

import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedCycleResponse {
    private List<CycleEntity> cycleEntityList;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}