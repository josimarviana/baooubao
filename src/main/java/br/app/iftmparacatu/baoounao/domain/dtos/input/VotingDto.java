package br.app.iftmparacatu.baoounao.domain.dtos.input;

import br.app.iftmparacatu.baoounao.domain.enums.UserType;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VotingDto(@NotNull ProposalEntity proposalEntity) {
}
