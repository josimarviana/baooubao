package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.dtos.input.CreateCycleDto;
import br.app.iftmparacatu.baoounao.domain.services.CycleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cycle")
public class CycleController {
    @Autowired
    private CycleService cycleService;
    @GetMapping
    public ResponseEntity<Object> list(){
        return cycleService.findAll();
    }
    @GetMapping("/{cycleID}")
    public ResponseEntity<Object> findById(@PathVariable Long cycleID) {
        return cycleService.findById(cycleID);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> save(@RequestBody @Valid CreateCycleDto createCycleDto) {
        return cycleService.save(createCycleDto);
    }

    @PatchMapping("/{cycleID}")
    public ResponseEntity<Object> updateCycle(@PathVariable Long cycleID, @RequestBody CreateCycleDto createCycleDto ){
        return cycleService.update(cycleID,createCycleDto);
    }

    @DeleteMapping("/{cycleID}")
    public ResponseEntity<Object> deleteCycle(@PathVariable Long cycleID){
        return cycleService.delete(cycleID);
    }
}