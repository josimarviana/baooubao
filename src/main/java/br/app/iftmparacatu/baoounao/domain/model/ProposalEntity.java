package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;

import java.sql.Blob;
import java.time.LocalDateTime;

@Entity
public class ProposalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(length = 100)
    String title;
    @Column
    String description;
    @Column
    Integer likes;
    @Column(length = 100)
    String situation;
    @Column
    Boolean active;
    @Column
    LocalDateTime createdAt;
    @Column(length = 100)
    String url;
    @Column
    Blob photograpy;

    //Usuario usuario;
    //Ciclo ciclo;
}
