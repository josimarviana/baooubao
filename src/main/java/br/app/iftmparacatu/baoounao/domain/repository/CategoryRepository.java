package br.app.iftmparacatu.baoounao.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    CategoryEntity findByTitle(String title);
    List<CategoryEntity> findAllByActiveTrue();
}
