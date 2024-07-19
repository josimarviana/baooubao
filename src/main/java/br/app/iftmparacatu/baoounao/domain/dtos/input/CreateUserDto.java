package br.app.iftmparacatu.baoounao.domain.dtos.input;

import br.app.iftmparacatu.baoounao.domain.enums.RoleName;
import br.app.iftmparacatu.baoounao.domain.enums.UserType;

public record CreateUserDto(String name, UserType type, String email, String password, RoleName role, Boolean active) {
}
