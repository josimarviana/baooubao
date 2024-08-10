package br.app.iftmparacatu.baoounao.api.controller;


import br.app.iftmparacatu.baoounao.domain.dtos.input.UpdateProposalDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryProposalDto;
import br.app.iftmparacatu.baoounao.domain.enums.Situation;
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

    @GetMapping("/{proposalId}")
    public ResponseEntity<Object> getFindById (@PathVariable Long proposalId ){
        return proposalService.findById(proposalId);
    }


    @PostMapping
    public ResponseEntity<Object> saveProposal (@RequestParam("title") String tittle,
                                                @RequestParam("description") String description,
                                                @RequestParam("url") String url,
                                                @RequestParam("image") MultipartFile image,
                                                @RequestParam("category") String category) throws IOException{
        return proposalService.save(tittle, description, url, image, category);
    }

    @GetMapping("/filter")
    public ResponseEntity<Object> filterByDescriptionOrTitle (@RequestParam(value = "contain", required = false) String text ){
        return proposalService.filterByDescriptionOrTitle(text);
    }
    @GetMapping("/trending")
    public ResponseEntity<Object> trendingProposals (){
        return proposalService.trendingProposals();
    }

    @GetMapping("/my-proposals")
    public ResponseEntity<Object> myProposals (){
        return proposalService.myProposals();
    }


    @PatchMapping("/{proposalID}")
    public ResponseEntity<Object> updateProposal(@PathVariable Long proposalID, @RequestBody UpdateProposalDto updateProposalDto){
        return proposalService.update(proposalID,updateProposalDto);
    }

    @PatchMapping("/moderate/approve/{proposalID}")
    public ResponseEntity<Object> approveProposal(@PathVariable Long proposalID){
        return proposalService.moderate(proposalID, Situation.OPEN_FOR_VOTING);
    }

    @PatchMapping("/moderate/deny/{proposalID}")
    public ResponseEntity<Object> denyProposal(@PathVariable Long proposalID){
        return proposalService.moderate(proposalID,Situation.DENIED);
    }

    @PatchMapping("/moderate/board/{proposalID}")
    public ResponseEntity<Object> fowardedToBoard(@PathVariable Long proposalID){
        return proposalService.moderate(proposalID,Situation.FORWARDED_TO_BOARD);
    }

    @GetMapping("/has-voted/{proposalId}")
    public ResponseEntity<Object> hasVoted (@PathVariable Long proposalId ){
        return proposalService.hasVoted(proposalId);
    }

    @DeleteMapping("/{proposalID}")
    public ResponseEntity<Object> deleteProposal(@PathVariable Long proposalID) {
        return proposalService.delete(proposalID);
    }
}