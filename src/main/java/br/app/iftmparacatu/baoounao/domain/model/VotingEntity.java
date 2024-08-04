package br.app.iftmparacatu.baoounao.domain.model;


import br.app.iftmparacatu.baoounao.domain.util.SecurityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
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

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (userEntity == null) {
            userEntity = SecurityUtil.getAuthenticatedUser();
        }
    }
}
