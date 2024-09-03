package br.app.iftmparacatu.baoounao.domain.dtos.output;
import lombok.Builder;

@Builder
public record RevokeRoleDto(boolean revokeAdm) { }
