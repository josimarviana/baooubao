package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CategoryRepository;
import br.app.iftmparacatu.baoounao.domain.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;
    @GetMapping
    public ResponseEntity<Object> list(){
        return categoryService.findAll();
    }

    @GetMapping("/active")
    public ResponseEntity<Object> listActive(){
        return categoryService.findAllActive();
    }

    @GetMapping("/{categoryID}")
    public ResponseEntity<Object> findById(@PathVariable Long categoryID) {
        return categoryService.findById(categoryID);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid CategoryEntity categoryEntity) {
        return categoryService.save(categoryEntity);
    }

    @PatchMapping("/{categoryID}")
    public ResponseEntity<Object> updateCategory(@PathVariable Long categoryID, @RequestBody CategoryEntity categoryEntity ){
        return categoryService.update(categoryID,categoryEntity);
    }
}
