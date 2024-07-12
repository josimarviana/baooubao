package br.app.iftmparacatu.baoounao.domain.repository;

import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalRepository extends JpaRepository<ProposalEntity,Long> {
    List<ProposalEntity> findByDescriptionContaining(String text);
    List<ProposalEntity> findByTitleContainingOrDescriptionContaining(String text1,String text2);

    List<ProposalEntity> findTop3ByLikesGreaterThanOrderByLikesDesc(int likes);

}
