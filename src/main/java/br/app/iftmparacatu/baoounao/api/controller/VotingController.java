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
    @PostMapping
    public ResponseEntity<Object> vote(@RequestBody @Valid VotingDto votingDto) {
        return votingService.save(votingDto);
    }

    @DeleteMapping("/unvote")
    public ResponseEntity<Object> unvote(@RequestBody @Valid VotingDto votingDto) {
        return votingService.remove(votingDto);
    }

    @GetMapping("/proposal/by-user")
    public ResponseEntity<Object> findAllVotedUserProposals(){
        return votingService.findAllVotedUserProposals();
    }

    @GetMapping("/limit")
    public ResponseEntity<Object> limit(){
        return votingService.limit();
    }
}
