package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalCategoryEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CycleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cycle")
public class CycleController {
    @Autowired
    private CycleRepository cycleRepository;
    @GetMapping
    public List<CycleEntity> list(){
        return cycleRepository.findAll();
    }

    @GetMapping("/{cycleID}") //TODO: Adicionar exception para quando não encontrar a entidade
    public Optional<CycleEntity> findById(@PathVariable Long cycleID) {
        return cycleRepository.findById(cycleID); //.orElseThrow(() -> new EntityNotFoundException("REGISTRO NÃO ENCONTRADO!"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CycleEntity save(@RequestBody @Valid CycleEntity cycleEntity) {
        return cycleRepository.save(cycleEntity);
    }
}