package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.api.exception.NotAllowedOperation;
import br.app.iftmparacatu.baoounao.domain.dtos.input.CreateCategoryDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.PaginatedCategoryResponse;
import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CategoryRepository;
import br.app.iftmparacatu.baoounao.domain.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Comparator;
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

        Optional<CategoryEntity> checkCateogry = categoryRepository.findByTitleAndActiveTrue(updatedCategory.getTitle());

        if(checkCateogry.isPresent() && categoryID != checkCateogry.get().getId()){
            throw new NotAllowedOperation(String.format("Categoria %s já foi cadastrada !!",updatedCategory.getTitle()));
        }

        Optional.ofNullable(updatedCategory.getTitle())
                .ifPresent(existingCategory::setTitle);
        Optional.ofNullable(updatedCategory.getActive())
                .ifPresent(existingCategory::setActive);
        Optional.ofNullable(updatedCategory.getIcon())
                .ifPresent(existingCategory::setIcon);
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

    public ResponseEntity<Object> findAll(int page,int size,String text,String sort){
        List<CategoryEntity> categoryEntityList = categoryRepository.findByTitleContaining(text);
        sortCategory(categoryEntityList, sort);
        int countReg = categoryEntityList.size();
        int start = Math.min(page * size, countReg);
        int end = Math.min((page + 1) * size, countReg);
        List<CategoryEntity> paginatedList = categoryEntityList.subList(start, end);

        PaginatedCategoryResponse response = PaginatedCategoryResponse.builder()
                .categoryEntityList(paginatedList)
                .totalElements(countReg)
                .totalPages((int) Math.ceil((double) countReg / size))
                .currentPage(page)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private void sortCategory(List<CategoryEntity> cycleEntityList, String sort) {
        switch (sort.toLowerCase()) {
            case "recent_createdat":
                cycleEntityList.sort(Comparator.comparing(CategoryEntity::getCreatedAt).reversed());
                break;
            case "oldset_createdat":
                cycleEntityList.sort(Comparator.comparing(CategoryEntity::getCreatedAt));
                break;
            default:
                break;
        }
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
