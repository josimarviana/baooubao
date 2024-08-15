package br.app.iftmparacatu.baoounao.domain.dtos.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryDto(@NotNull @NotBlank String title, @NotNull @NotBlank String icon) {}