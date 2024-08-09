package br.app.iftmparacatu.baoounao.domain.repository;

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
    List<ProposalEntity> findByDescriptionContaining(String text);
    List<ProposalEntity> findByCycleEntityAndTitleContainingOrCycleEntityAndDescriptionContaining(CycleEntity cycleEntity, String text1, CycleEntity cycleEntity2, String text2);
    Page<ProposalEntity> findAllByCycleEntityOrderByVotesDesc(Pageable pageable,CycleEntity cycleEntity);
    List<ProposalEntity> findAllByCycleEntityOrderByCreatedAtDesc(CycleEntity cycleEntity);
    List<ProposalEntity> findAllByUserEntityAndCycleEntityOrderByCreatedAtDesc(UserEntity userEntity,CycleEntity cycleEntity);

}
