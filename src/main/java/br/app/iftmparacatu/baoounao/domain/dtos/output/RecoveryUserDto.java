package br.app.iftmparacatu.baoounao.domain.dtos.output;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
@Builder
public record RecoveryUserDto(Long id, String name, String email, List<String> roles, String type, boolean active, LocalDateTime createdAt) { }
