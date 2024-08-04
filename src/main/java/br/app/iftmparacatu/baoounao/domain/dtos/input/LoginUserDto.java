package br.app.iftmparacatu.baoounao.domain.dtos.input;

import br.app.iftmparacatu.baoounao.domain.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginUserDto(@NotNull @NotBlank @Email String email,@NotNull @NotBlank String password) {
}
