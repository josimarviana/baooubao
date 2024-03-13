package br.app.iftmparacatu.baoounao.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;

@Repository
public interface CycleRepository extends JpaRepository<CycleEntity, Long> {

}