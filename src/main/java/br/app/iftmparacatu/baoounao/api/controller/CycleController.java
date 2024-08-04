package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.dtos.input.CreateCycleDto;
import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CycleRepository;
import br.app.iftmparacatu.baoounao.domain.services.CycleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cycle")
public class CycleController {
    @Autowired
    private CycleService cycleService;
    @GetMapping
    public ResponseEntity<Object> list(){
        return cycleService.findAll();
    }
    @GetMapping("/{cycleID}") //TODO: Adicionar exception para quando não encontrar a entidade
    public ResponseEntity<Object> findById(@PathVariable Long cycleID) {
        return cycleService.findById(cycleID);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> save(@RequestBody @Valid CreateCycleDto createCycleDto) {
        return cycleService.save(createCycleDto);
    }
}