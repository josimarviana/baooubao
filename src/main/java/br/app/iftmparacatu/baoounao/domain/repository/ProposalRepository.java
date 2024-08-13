package br.app.iftmparacatu.baoounao.domain.repository;

import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalRepository extends JpaRepository<ProposalEntity,Long> {
    Long countByUserEntityAndCycleEntity(UserEntity userEntity, CycleEntity cycleEntity);
    List<ProposalEntity> findByCycleEntityAndTitleContainingAndSituationOrCycleEntityAndDescriptionContainingAndSituation(
            CycleEntity cycleEntity1, String text1, Situation situation1,
            CycleEntity cycleEntity2, String text2, Situation situation2);
    Page<ProposalEntity> findAllByCycleEntityOrderByVotesDesc(Pageable pageable,CycleEntity cycleEntity);
    List<ProposalEntity> findByCycleEntityAndSituationAndActiveTrueOrderByCreatedAtDesc(CycleEntity cycleEntity, Situation situation);

    List<ProposalEntity> findByCycleEntity(CycleEntity cycleEntity);
    List<ProposalEntity> findAllByUserEntityAndCycleEntityAndActiveTrueOrderByCreatedAtDesc(UserEntity userEntity,CycleEntity cycleEntity);
    Long countBySituationAndCycleEntity(Situation situation, CycleEntity cycleEntity);

}
