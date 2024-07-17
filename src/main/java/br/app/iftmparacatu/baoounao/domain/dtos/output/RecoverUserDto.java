package br.app.iftmparacatu.baoounao.domain.dtos.output;

import br.app.iftmparacatu.baoounao.domain.enums.RoleName;

import java.util.List;

public record RecoverUserDto(Long id, String email, List<RoleName> roles) {
}
