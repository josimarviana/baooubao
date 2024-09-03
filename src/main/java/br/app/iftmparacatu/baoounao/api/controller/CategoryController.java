package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.dtos.input.CreateCategoryDto;
import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CategoryRepository;
import br.app.iftmparacatu.baoounao.domain.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/filter")
    public ResponseEntity<Object> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "9") int size,
                                       @RequestParam(value = "contain", required = false) String text,
                                       @RequestParam(value = "sort", defaultValue = "recent_createdAt") String sort){
        return categoryService.findAll(page,size,text,sort);
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
    public ResponseEntity<Object> save(@RequestBody @Valid CreateCategoryDto createCategoryDto) {
        return categoryService.save(createCategoryDto);
    }

    @DeleteMapping("/{categoryID}")
    public ResponseEntity<Object> delete(@PathVariable Long categoryID) {
        return categoryService.delete(categoryID);
    }

    @PatchMapping("/{categoryID}")
    public ResponseEntity<Object> updateCategory(@PathVariable Long categoryID, @RequestBody CategoryEntity categoryEntity ){
        return categoryService.update(categoryID,categoryEntity);
    }
}
