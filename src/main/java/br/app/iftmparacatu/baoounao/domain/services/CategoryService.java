package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CategoryRepository;
import br.app.iftmparacatu.baoounao.domain.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public ResponseEntity<Object> update(Long categoryID, CategoryEntity updatedCategory) {
        CategoryEntity existingCategory = categoryRepository.findById(categoryID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Categoria de id %d não encontrada!", categoryID)));
        Optional.ofNullable(updatedCategory.getTitle())
                .ifPresent(existingCategory::setTitle);
        Optional.of(updatedCategory.isActive())
                .ifPresent(existingCategory::setActive);
        categoryRepository.save(existingCategory);
        return ResponseUtil.createSuccessResponse("Categoria atualizada com sucesso !!",HttpStatus.OK);
    }

    public ResponseEntity<Object> save(CategoryEntity categoryEntity){
        categoryRepository.save(categoryEntity);
        return ResponseUtil.createSuccessResponse("Categoria salva com sucesso !!",HttpStatus.CREATED);
    }

    public ResponseEntity<Object> findById(Long categoryID){
        CategoryEntity existingCategory = categoryRepository.findById(categoryID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Categoria de id %d não encontrada!", categoryID)));
        return ResponseEntity.status(HttpStatus.OK).body(existingCategory);
    }

    public ResponseEntity<Object> findAll(){
        List<CategoryEntity> categoryEntityList = categoryRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(categoryEntityList);
    }

    public ResponseEntity<Object> findAllActive(){
        List<CategoryEntity> categoryEntityList = categoryRepository.findAllByActiveTrue();
        return ResponseEntity.status(HttpStatus.OK).body(categoryEntityList);
    }

    public ResponseEntity<Object> delete(Long categoryID) {
        CategoryEntity existingCategory = categoryRepository.findById(categoryID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Categoria de id %d não encontrada!", categoryID)));
        existingCategory.setActive(false);
        categoryRepository.save(existingCategory);
        return ResponseUtil.createSuccessResponse("Categoria desativada com sucesso !!",HttpStatus.NO_CONTENT);
    }
}
