package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class ProposalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column
    private String description;
    @Column
    private Integer likes;
    @Column(length = 100)
    private String situation;
    @Column
    private Boolean active;
    @Column
    private LocalDateTime createdAt;
    @Column(length = 100)
    private String url;
    @Column
    private  Blob photograpy;

    //Usuario usuario; Ciclo ciclo;



}
