package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@AllArgsConstructor
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
    private boolean entregue;

@PrePersist
public void prePersist() {
    this.entregue = false;

}

    public NotificationEntity(UserEntity user, NotificationTypeEntity type) {
        this.user = user;
        this.type = type;
    }
}
