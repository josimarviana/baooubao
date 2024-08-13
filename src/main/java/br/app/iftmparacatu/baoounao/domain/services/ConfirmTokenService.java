package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.model.ConfirmationTokenEntity;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CategoryRepository;
import br.app.iftmparacatu.baoounao.domain.repository.ConfimationTokenRepository;
import br.app.iftmparacatu.baoounao.domain.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConfirmTokenService {
    @Autowired
    private ConfimationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenEntity salvar(UserEntity user) {

       ConfirmationTokenEntity token = ConfirmationTokenEntity.builder()
               .token(UUID.randomUUID().toString())
               .createdDate(LocalDateTime.now())
               .expiryDate(LocalDateTime.now().plusHours(24))
               .user(user)
               .build();

        return token;
    }
    public Optional<ConfirmationTokenEntity> validation(String token) {
        return Optional.ofNullable(confirmationTokenRepository.findByToken(token))
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    public void delete(ConfirmationTokenEntity t) {
        confirmationTokenRepository.delete(t);
    }

}