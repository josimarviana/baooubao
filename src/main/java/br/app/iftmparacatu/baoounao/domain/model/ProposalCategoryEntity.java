package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
@Entity
public class ProposalCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "category_entity_id")
    private CategoryEntity categoryEntity;
    @ManyToOne
    @JoinColumn(name = "proposal_entity_id")
    private ProposalEntity proposalEntity;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

}
