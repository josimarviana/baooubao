package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.model.ProposalCategoryEntity;
import br.app.iftmparacatu.baoounao.domain.repository.ProposalCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/proposal-category")
public class ProposalCategoryController {
    @Autowired
    private ProposalCategoryRepository proposalCategoryRepository;
    @GetMapping
    public List<ProposalCategoryEntity> list(){
        return proposalCategoryRepository.findAll();
    }

}
