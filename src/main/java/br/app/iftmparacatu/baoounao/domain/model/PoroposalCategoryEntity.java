package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
public class PoroposalCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JoinColumn
    @ManyToMany
    private CategoryEntity categoryEntity;
    @JoinColumn
    @ManyToMany
    private ProposalEntity proposalEntity;
    @Column
    private LocalDateTime createdAt;

}
