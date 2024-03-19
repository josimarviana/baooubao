package br.app.iftmparacatu.baoounao.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class CycleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    @NotNull
    private String title;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime finishedAt;

}
