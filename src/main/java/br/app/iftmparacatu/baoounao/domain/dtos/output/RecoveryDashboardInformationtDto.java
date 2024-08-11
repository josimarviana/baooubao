package br.app.iftmparacatu.baoounao.domain.dtos.output;

import lombok.Builder;

@Builder
public record RecoveryDashboardInformationtDto(Long openProposals, Long votes, Long deniedProposals, Long acceptedProposals) { }
