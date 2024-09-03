package br.app.iftmparacatu.baoounao.domain.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RevokeRoleDto(@NotNull Boolean revoke) { }
