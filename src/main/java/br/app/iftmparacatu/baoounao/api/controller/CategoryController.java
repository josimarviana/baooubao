package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;
    @GetMapping
    public List<CategoryEntity> list(){
        return categoryRepository.findAll();
    }

    @GetMapping("/{categoryID}") //TODO: Adicionar exception para quando não encontrar a entidade
    public Optional<CategoryEntity> findById(@PathVariable Long categoryID) {
        return categoryRepository.findById(categoryID); //.orElseThrow(() -> new EntityNotFoundException("REGISTRO NÃO ENCONTRADO!"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryEntity save(@RequestBody @Valid CategoryEntity categoryEntity) {
        return categoryRepository.save(categoryEntity);
    }

}
