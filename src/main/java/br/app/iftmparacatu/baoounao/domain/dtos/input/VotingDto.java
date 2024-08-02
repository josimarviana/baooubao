package br.app.iftmparacatu.baoounao.domain.dtos.input;

import br.app.iftmparacatu.baoounao.domain.enums.UserType;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;

public record VotingDto(ProposalEntity proposalEntity, UserEntity userEntity) {
}
