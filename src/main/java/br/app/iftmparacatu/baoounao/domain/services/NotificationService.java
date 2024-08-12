package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.domain.model.NotificationEntity;
import br.app.iftmparacatu.baoounao.domain.model.NotificationTypeEntity;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import br.app.iftmparacatu.baoounao.domain.repository.NotificationRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    NotificationRepository notificationRepository;


    public NotificationEntity cadastrar(UserEntity user, NotificationTypeEntity notificationType) {
        return Optional.of(notificationType)
                .filter(NotificationTypeEntity::isActive)
                .map(type -> NotificationEntity.builder()
                        .user(user)
                        .type(type)
                        .build())
                .map(notificationRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de notificação inativo. Não é possível cadastrar a notificação."));
    }

}
