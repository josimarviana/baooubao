package br.app.iftmparacatu.baoounao.api.controller;


import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.repository.ProposalRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/proposal")
public class ProposalController {
    @Autowired
    ProposalRepository proposalRepository;

    @GetMapping
    public ResponseEntity<List<ProposalEntity>> list (){
        List<ProposalEntity> propostas =  proposalRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(propostas);
    }

    @GetMapping("/{proposalId}") //TODO: modificar a exceção quando usar o service
    public ResponseEntity<ProposalEntity> getFindById (@PathVariable Long proposalId ){
        Optional<ProposalEntity> proposta = proposalRepository.findById(proposalId);
        if(proposta.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(proposta.get());

        } else throw new RuntimeException("Proposal de id " + proposalId + "não foi encontrada");

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)//TODO: modificar a exceção quando usar o service
    public ResponseEntity<ProposalEntity> saveProposal (@RequestBody ProposalEntity proposalEntity ){
        try{
            ProposalEntity proposta = proposalRepository.save(proposalEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(proposta);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }


}