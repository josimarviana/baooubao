package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Entity
@Data
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
    @jakarta.validation.constraints.NotNull
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}
