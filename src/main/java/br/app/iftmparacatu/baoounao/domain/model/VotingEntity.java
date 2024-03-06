package br.app.iftmparacatu.baoounao.domain.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
public class VotingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn
    private ProposalEntity proposalEntity;
    //private User user;
    @Column
    private LocalDateTime createdAt;
}
