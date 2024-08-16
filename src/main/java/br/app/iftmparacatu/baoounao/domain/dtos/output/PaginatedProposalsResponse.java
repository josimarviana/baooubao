package br.app.iftmparacatu.baoounao.domain.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedProposalsResponse {
    private List<RecoveryTrendingProposalDto> proposals;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}