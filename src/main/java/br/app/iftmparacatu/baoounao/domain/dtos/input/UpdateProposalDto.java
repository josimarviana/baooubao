package br.app.iftmparacatu.baoounao.domain.dtos.input;

import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import lombok.Builder;

@Builder
public record UpdateProposalDto(String title, String description, String image, String url, CategoryEntity categoryEntity) {}