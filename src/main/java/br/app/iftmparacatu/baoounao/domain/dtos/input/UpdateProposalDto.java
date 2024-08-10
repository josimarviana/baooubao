package br.app.iftmparacatu.baoounao.domain.dtos.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateProposalDto(String title, String description, byte[] image) {}