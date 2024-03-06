package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
public class PoroposalCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    //private CategoryEntity categoryEntity;
    @JoinColumn
    private ProposalEntity proposalEntity;
    @Column
    private LocalDateTime createdAt;

}
