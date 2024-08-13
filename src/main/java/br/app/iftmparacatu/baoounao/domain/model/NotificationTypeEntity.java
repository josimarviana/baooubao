package br.app.iftmparacatu.baoounao.domain.model;

import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class NotificationTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Situation situation;
    @Column
    private String mensagem;
    @Column
    private boolean active;


}
