package br.app.iftmparacatu.baoounao.api.controller;
import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.model.NotificationEntity;
import br.app.iftmparacatu.baoounao.domain.repository.NotificationRepository;
import br.app.iftmparacatu.baoounao.domain.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<NotificationEntity> list(){return notificationRepository.findAll();}


//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public NotificationEntity save() {
//        return notificationService.cadastrar());
//    }
}
