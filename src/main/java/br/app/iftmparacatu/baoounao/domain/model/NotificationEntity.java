package br.app.iftmparacatu.baoounao.domain.model;

import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "user_entity_id")
    @ManyToOne
    private UserEntity user;
    @Column
    private boolean entregue;
    @Column(length = 100)
    @Enumerated(value = EnumType.STRING)
    private Situation status;
    @Column
    private String message;

    @PrePersist
    public void prePersist() {
        this.entregue = false;
    }
}
