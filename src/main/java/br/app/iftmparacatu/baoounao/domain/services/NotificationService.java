package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import br.app.iftmparacatu.baoounao.domain.model.NotificationEntity;
import br.app.iftmparacatu.baoounao.domain.repository.NotificationRepository;
import br.app.iftmparacatu.baoounao.domain.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    NotificationRepository notificationRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


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
            messagingTemplate.convertAndSendToUser(notification.getUser().getEmail(), "/queue/greetings", notificationMessage);
            System.out.println("Sent message to /user/queue/notifications for user: " + notification.getUser().getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
