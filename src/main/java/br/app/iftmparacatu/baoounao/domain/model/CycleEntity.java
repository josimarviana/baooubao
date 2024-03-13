package br.app.iftmparacatu.baoounao.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CycleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime finishedAt;

}
