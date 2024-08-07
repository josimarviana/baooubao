package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class NotificationTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String  type;
    @Column
    private boolean active;

}
