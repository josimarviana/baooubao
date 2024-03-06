package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Entity
@Data
public class SituationEntity {
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    public enum Status {

        OPEN_FOR_VOTING,
        FORWARDED_TO_BOARD,
        APPROVED,
        DENIED,
        PENDING_MODERATION
    }
}
