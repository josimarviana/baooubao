package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
public class PoroposalCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CategoryEntity categoryEntity;
    @ManyToOne
    private ProposalEntity proposalEntity;
    @Column
    private LocalDateTime createdAt;

}
