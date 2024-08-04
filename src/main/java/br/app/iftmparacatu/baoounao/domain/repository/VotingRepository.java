package br.app.iftmparacatu.baoounao.domain.repository;

import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import br.app.iftmparacatu.baoounao.domain.model.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotingRepository extends JpaRepository<VotingEntity,Long> {
    int countByProposalEntity(ProposalEntity proposal);

    Long countByUserEntityAndProposalEntityCycleEntity(UserEntity userEntity, CycleEntity cycleEntity);
}
