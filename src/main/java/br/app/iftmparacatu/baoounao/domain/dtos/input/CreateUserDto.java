package br.app.iftmparacatu.baoounao.domain.dtos.input;

import br.app.iftmparacatu.baoounao.domain.enums.RoleName;

public record CreateUserDto(String email, String password, RoleName role) {
}
