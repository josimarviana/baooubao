package br.app.iftmparacatu.baoounao.domain.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
public class VotingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn
    @ManyToMany
    private ProposalEntity proposalEntity;
    //private User user;
    @Column
    private LocalDateTime createdAt;
}
