package br.app.iftmparacatu.baoounao.domain.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Comparator;

import java.util.List;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedProposalsResponse {
    private List<RecoveryProposalFilterDto> proposals;
    private long totalElements;
    private int totalPages;
    private int currentPage;

    public void sortProposals(String sort) {
        switch (sort.toLowerCase()) {
            case "recent":
                proposals.sort(Comparator.comparing(RecoveryProposalFilterDto::getCreatedAt).reversed());
                break;
            case "oldest":
                proposals.sort(Comparator.comparing(RecoveryProposalFilterDto::getCreatedAt));
                break;
            case "most_votes":
                proposals.sort(Comparator.comparing(RecoveryProposalFilterDto::getVotes).reversed());
                break;
            case "least_votes":
                proposals.sort(Comparator.comparing(RecoveryProposalFilterDto::getVotes));
                break;
            default:
                proposals.sort(Comparator.comparing(RecoveryProposalFilterDto::getCreatedAt).reversed());
                break;
        }
    }
}