package br.app.iftmparacatu.baoounao.api.controller;


import br.app.iftmparacatu.baoounao.domain.dtos.input.UpdateProposalDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.PaginatedProposalsResponse;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryBasicProposalDto;
import br.app.iftmparacatu.baoounao.domain.enums.Situation;
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
    public ResponseEntity<List<RecoveryBasicProposalDto>> list (){
        List<RecoveryBasicProposalDto> propostas =  proposalService.findAll();
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
    public ResponseEntity<PaginatedProposalsResponse> filterByDescriptionOrTitle (@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "9") int size, @RequestParam(value = "contain", required = false) String text, @RequestParam(value = "sort", defaultValue = "recent") String sort ){
        return proposalService.filterByDescriptionOrTitle(text,page,size,sort);
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
    public ResponseEntity<Object> updateProposal(@PathVariable Long proposalID,
                                                 @RequestParam(value = "title",required = false) String tittle,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam(value = "url", required = false) String url,
                                                 @RequestParam(value = "image", required = false) MultipartFile image,
                                                 @RequestParam(value = "category", required = false) String category) throws IOException {
        return proposalService.update(proposalID,tittle, description, url, image, category);
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

    @GetMapping("/dashboard")
    public ResponseEntity<Object> count(){
        return proposalService.dashboardCount();
    }

    @GetMapping("/limit")
    public ResponseEntity<Object> limit(){
        return proposalService.limit();
    }
}