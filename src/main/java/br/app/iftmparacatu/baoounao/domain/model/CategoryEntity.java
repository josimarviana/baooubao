package br.app.iftmparacatu.baoounao.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String title;
    @Column
    private boolean active;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime finishedAt;

}
