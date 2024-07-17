package br.app.iftmparacatu.baoounao.domain.dtos.input;

import br.app.iftmparacatu.baoounao.domain.enums.RoleName;

public record LoginUserDto(String email, String password) {
}
