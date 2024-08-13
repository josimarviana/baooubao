package br.app.iftmparacatu.baoounao.domain.repository;

import br.app.iftmparacatu.baoounao.domain.model.ConfirmationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfimationTokenRepository extends JpaRepository<ConfirmationTokenEntity, Long> {
   ConfirmationTokenEntity findByToken(String token);

}
