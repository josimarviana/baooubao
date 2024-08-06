package br.app.iftmparacatu.baoounao.domain.dtos.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateCycleDto(@NotNull @NotBlank String title, @NotNull LocalDate startDate, @NotNull LocalDate finishDate) {}