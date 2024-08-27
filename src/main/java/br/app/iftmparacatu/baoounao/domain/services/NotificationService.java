package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import br.app.iftmparacatu.baoounao.domain.model.NotificationEntity;
import br.app.iftmparacatu.baoounao.domain.repository.NotificationRepository;
import br.app.iftmparacatu.baoounao.domain.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationWebSocketHandler webSocketHandler;


    public void save(String message, Situation status) {
        NotificationEntity notificationEntity = NotificationEntity.builder()
                .user(SecurityUtil.getAuthenticatedUser())
                .message(message)
                .status(status)
                .entregue(true)
                .build();
        notificationRepository.save(notificationEntity);
        sendNotification(notificationEntity);
    }

    public void sendNotification(NotificationEntity notification) {
        try {
            String notificationMessage = notification.getMessage();
            webSocketHandler.sendNotificationToUser(notification.getUser().getEmail(),notificationMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
