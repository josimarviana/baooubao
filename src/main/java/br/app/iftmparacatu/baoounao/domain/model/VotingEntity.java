package br.app.iftmparacatu.baoounao.domain.model;


import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
@Entity
public class VotingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ProposalEntity proposalEntity;
    @ManyToOne
    private UserEntity userEntity;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;
}
