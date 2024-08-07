package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "user_entity_id")
    @ManyToOne
    private UserEntity user;
    @JoinColumn(name = "type_entity_id")
    @ManyToOne
    private NotificationTypeEntity type;
    @Column
    private String mensagem;
    @Column
    private boolean entregue;


}
