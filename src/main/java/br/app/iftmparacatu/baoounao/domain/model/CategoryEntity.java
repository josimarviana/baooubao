package br.app.iftmparacatu.baoounao.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column
    private boolean active;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime finishedAt;

}
