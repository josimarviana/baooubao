package br.app.iftmparacatu.baoounao.api.controller;


import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryProposalDto;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.repository.ProposalRepository;
import br.app.iftmparacatu.baoounao.domain.services.ProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/proposal")
public class ProposalController {
    @Autowired
    ProposalRepository proposalRepository;

    @Autowired
    ProposalService proposalService;

    @GetMapping
    public ResponseEntity<List<RecoveryProposalDto>> list (){
        List<RecoveryProposalDto> propostas =  proposalService.findAll();
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
    public ResponseEntity<ProposalEntity> saveProposal (@RequestParam("title") String tittle,
                                                        @RequestParam("description") String description,
                                                        @RequestParam("url") String url,
                                                        @RequestParam("image") MultipartFile image) throws IOException{
        try{
            ProposalEntity proposalEntity = new ProposalEntity();
            proposalEntity.setDescription(description);
            proposalEntity.setTitle(tittle);
            proposalEntity.setVideoUrl(url);
            proposalEntity.setImage(image.getBytes());
            //TO-DO Add categories, user and to add initial situation on model

            proposalRepository.save(proposalEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(proposalEntity);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    @GetMapping("/filter")
    public List<ProposalEntity> filterByDescriptionOrTitle (@RequestParam(value = "contain", required = false) String text ){
        return proposalRepository.findByTitleContainingOrDescriptionContaining(text,text);

    }
    @GetMapping("/trending")
    public List<ProposalEntity> trendingProposals (){
        return proposalRepository.findTop3ByLikesGreaterThanOrderByLikesDesc(0);

    }


    @PatchMapping("/{proposalID}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RecoveryProposalDto> updateProposal(@PathVariable Long proposalId, @RequestBody ProposalEntity proposalEntity ){
        RecoveryProposalDto proposalDto = proposalService.update(proposalId,proposalEntity);
        return ResponseEntity.status(HttpStatus.OK).body(proposalDto);
    }

}