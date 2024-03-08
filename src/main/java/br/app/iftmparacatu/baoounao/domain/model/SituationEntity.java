package br.app.iftmparacatu.baoounao.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SituationEntity {
    OPEN_FOR_VOTING,
    FORWARDED_TO_BOARD,
    APPROVED,
    DENIED,
    PENDING_MODERATION
}
