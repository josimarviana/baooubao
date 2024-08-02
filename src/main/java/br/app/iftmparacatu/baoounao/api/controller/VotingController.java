package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.dtos.input.VotingDto;
import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.model.VotingEntity;
import br.app.iftmparacatu.baoounao.domain.repository.VotingRepository;
import br.app.iftmparacatu.baoounao.domain.services.VotingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/voting")
public class VotingController {
    @Autowired
    private VotingService votingService;
//    @GetMapping
//    public List<VotingEntity> list(){
//        return votingRepository.findAll();
//    }

//    @GetMapping("/{votingID}") //TODO: Adicionar exception para quando não encontrar a entidade
//    public Optional<VotingEntity> findById(@PathVariable Long votingID) {
//        return votingRepository.findById(votingID); //.orElseThrow(() -> new EntityNotFoundException("REGISTRO NÃO ENCONTRADO!"));
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> save(@RequestBody @Valid VotingDto votingDto) {
        return votingService.save(votingDto);
    }
}
