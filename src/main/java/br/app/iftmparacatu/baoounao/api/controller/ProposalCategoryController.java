package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.model.ProposalCategoryEntity;
import br.app.iftmparacatu.baoounao.domain.model.VotingEntity;
import br.app.iftmparacatu.baoounao.domain.repository.ProposalCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/proposal-category")
public class ProposalCategoryController {
    @Autowired
    private ProposalCategoryRepository proposalCategoryRepository;
    @GetMapping
    public List<ProposalCategoryEntity> list(){
        return proposalCategoryRepository.findAll();
    }

    @GetMapping("/{proposalCategoryID}") //TODO: Adicionar exception para quando não encontrar a entidade
    public Optional<ProposalCategoryEntity> findById(@PathVariable Long proposalCategoryID) {
        return proposalCategoryRepository.findById(proposalCategoryID); //.orElseThrow(() -> new EntityNotFoundException("REGISTRO NÃO ENCONTRADO!"));
    }
}
