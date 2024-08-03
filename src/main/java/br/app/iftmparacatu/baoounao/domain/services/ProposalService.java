package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.ProposalException;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryProposalDto;
import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CategoryRepository;
import br.app.iftmparacatu.baoounao.domain.repository.ProposalRepository;
import br.app.iftmparacatu.baoounao.domain.util.SecurityUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProposalService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private VotingService votingService;

    public RecoveryProposalDto mapToDto(ProposalEntity proposalEntity) {
        return modelMapper.map(proposalEntity, RecoveryProposalDto.class);
    }

    public RecoveryProposalDto mapToDto(ProposalEntity proposalEntity, int voteCount) {
        RecoveryProposalDto dto = modelMapper.map(proposalEntity, RecoveryProposalDto.class);
        dto.setLikes(voteCount);
        return dto;
    }

    public ResponseEntity<Object> save(String tittle,String description,String url,MultipartFile image,String category){
        try{
            ProposalEntity proposalEntity = new ProposalEntity();
            proposalEntity.setDescription(description);
            proposalEntity.setTitle(tittle);
            proposalEntity.setVideoUrl(url);
            proposalEntity.setImage(image.getBytes());
            CategoryEntity categoryEntity = categoryRepository.findByTitle(category);
            proposalEntity.setCategoryEntity(categoryEntity);
            proposalEntity.setUserEntity(SecurityUtil.getAuthenticatedUser());
            //TO-DO Add categories, user and to add initial situation on model

            proposalRepository.save(proposalEntity);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    };

    public List<RecoveryProposalDto> findAll(){
        List<ProposalEntity> proposals = proposalRepository.findAll();
        return proposals.stream()
                .map(proposal -> mapToDto(proposal,votingService.countByProposalEntity(proposal)))
                .collect(Collectors.toList());
    }
    public RecoveryProposalDto update(Long proposalID, ProposalEntity proposalEntity) {
        if (proposalEntity.getSituation().equals(Situation.OPEN_FOR_VOTING)){
            throw new ProposalException("Não é possível atualizar propostas em votação");
        } else {
            ProposalEntity existingProposal = proposalRepository.findById(proposalID)
                    .orElseThrow(() -> new ProposalException("Proposta não encontrada com o ID: " + proposalID));

            BeanUtils.copyProperties(proposalEntity, existingProposal, "id");
            proposalRepository.save(existingProposal); // Certifique-se de salvar a entidade atualizada

            return this.mapToDto(existingProposal);
        }
    }
}
