package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.api.exception.NotAllowedOperation;
import br.app.iftmparacatu.baoounao.domain.dtos.input.CreateCategoryDto;
import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
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

    @Autowired
    private ProposalService proposalService;

    public ResponseEntity<Object> update(Long categoryID, CategoryEntity updatedCategory) {
        CategoryEntity existingCategory = categoryRepository.findById(categoryID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Categoria de id %d não encontrada!", categoryID)));
        Optional.ofNullable(updatedCategory.getTitle())
                .ifPresent(existingCategory::setTitle);
        Optional.of(updatedCategory.getActive())
                .ifPresent(existingCategory::setActive);
        categoryRepository.save(existingCategory);
        return ResponseUtil.createSuccessResponse("Categoria atualizada com sucesso !!",HttpStatus.OK);
    }

    public ResponseEntity<Object> save(CreateCategoryDto createCategoryDto){
        Optional<CategoryEntity> existingCategory = categoryRepository.findByTitleAndActiveTrue(createCategoryDto.title());

        if(existingCategory.isPresent()){
            throw new NotAllowedOperation(String.format("Categoria %s já foi cadastrada !!",createCategoryDto.title()));
        }

        CategoryEntity saveCategory = CategoryEntity.builder()
                .title(createCategoryDto.title())
                .icon(createCategoryDto.icon())
                .build();

        categoryRepository.save(saveCategory);
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
        if(proposalService.categoryHasProposals(existingCategory)){
            throw new NotAllowedOperation("Não é possível desativar esta categoria porque há propostas vinculadas a ela !!");
        }
        existingCategory.setActive(false);
        categoryRepository.save(existingCategory);
        return ResponseUtil.createSuccessResponse("Categoria desativada com sucesso !!",HttpStatus.NO_CONTENT);
    }
}
