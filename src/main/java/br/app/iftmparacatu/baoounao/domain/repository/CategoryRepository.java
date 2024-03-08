package br.app.iftmparacatu.baoounao.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

}
