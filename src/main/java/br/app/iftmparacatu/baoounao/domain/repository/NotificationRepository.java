package br.app.iftmparacatu.baoounao.domain.repository;
import br.app.iftmparacatu.baoounao.domain.model.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
