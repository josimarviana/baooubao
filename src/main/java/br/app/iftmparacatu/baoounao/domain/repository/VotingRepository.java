package br.app.iftmparacatu.baoounao.domain.repository;

import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import br.app.iftmparacatu.baoounao.domain.model.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VotingRepository extends JpaRepository<VotingEntity,Long> {
    int countByProposalEntity(ProposalEntity proposal);

    Long countByUserEntityAndProposalEntity(UserEntity userEntity, ProposalEntity proposalEntity);
    Long countByUserEntityAndProposalEntityCycleEntity(UserEntity userEntity, CycleEntity cycleEntity);

    Optional<VotingEntity> findFirstByUserEntityAndProposalEntity(UserEntity userEntity, ProposalEntity proposalEntity);
}
