package br.app.iftmparacatu.baoounao.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

@Entity
@Data
public class CycleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    @NonNull
    private String title;
    @Column(nullable = false)
    @NonNull
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime finishedAt;

}
