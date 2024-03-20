package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalCategoryEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}