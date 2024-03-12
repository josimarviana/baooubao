package br.app.iftmparacatu.baoounao.domain.model;


import jakarta.persistence.*;

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
    @Column
    private LocalDateTime createdAt;
}
