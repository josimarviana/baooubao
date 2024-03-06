package br.app.iftmparacatu.baoounao.domain.model;


import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class CycleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(length = 100)
    String title;
    @Column
    LocalDateTime createdAt;
    @Column
    LocalDateTime finishedAt;

    

}
