package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.api.exception.ProposalException;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryProposalDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryTrendingProposalDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryVoteProposalDto;
import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.model.VotingEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CategoryRepository;
import br.app.iftmparacatu.baoounao.domain.repository.ProposalRepository;
import br.app.iftmparacatu.baoounao.domain.util.SecurityUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Autowired
    private CycleService cycleService;

    public RecoveryProposalDto mapToDto(ProposalEntity proposalEntity) {
        return modelMapper.map(proposalEntity, RecoveryProposalDto.class);
    }

    public <T> T mapToDto(ProposalEntity proposalEntity, int voteCount, Class<T> dtoClass) {
        T dto = modelMapper.map(proposalEntity, dtoClass);
        if (dto instanceof RecoveryProposalDto) {
            ((RecoveryProposalDto) dto).setLikes(voteCount);
        }
        return dto;
    }

    public ResponseEntity<Object> findById(Long proposalId){
        Optional<ProposalEntity> proposal = Optional.ofNullable(proposalRepository.findById(proposalId).orElseThrow(() -> new EntityNotFoundException(String.format("Proposta de id %d não foi encontrada",proposalId))));
        proposal.get().setLikes(votingService.countByProposalEntity(proposal.get()));
        return ResponseEntity.status(HttpStatus.OK).body(mapToDto(proposal.get()));
    }

    public ResponseEntity<Object> save(String tittle,String description,String url,MultipartFile image,String category){
        Optional<CycleEntity> currentCycle = cycleService.findProgressCycle();

        if(!currentCycle.isPresent()){
            throw new EntityNotFoundException("Não foram encontrados ciclos em andamento. Para cadastrar uma proposta, é necessário primeiro cadastrar um ciclo.");
        }

        try{
            ProposalEntity proposalEntity = new ProposalEntity();
            proposalEntity.setDescription(description);
            proposalEntity.setTitle(tittle);
            proposalEntity.setVideoUrl(url);
            proposalEntity.setImage(image.getBytes());
            proposalEntity.setCycleEntity(currentCycle.get());
            CategoryEntity categoryEntity = categoryRepository.findByTitle(category);
            proposalEntity.setCategoryEntity(categoryEntity);
            //TO-DO Add categories, user and to add initial situation on model

            proposalRepository.save(proposalEntity);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    };

    public List<RecoveryProposalDto> findAll(){
        CycleEntity currentCycle = cycleService.findProgressCycle().orElseThrow(() -> new EntityNotFoundException(String.format("Não foram localizados ciclos em andamento")));
        List<ProposalEntity> proposals = proposalRepository.findAllByCycleEntityOrderByCreatedAtDesc(currentCycle);
        return proposals.stream()
                .map(proposal -> mapToDto(proposal,votingService.countByProposalEntity(proposal),RecoveryProposalDto.class))
                .collect(Collectors.toList());
    }

    public ResponseEntity<Object> myProposals(){
        CycleEntity currentCycle = cycleService.findProgressCycle().orElseThrow(() -> new EntityNotFoundException(String.format("Não foram localizados ciclos em andamento")));
        List<ProposalEntity> proposalEntityList = proposalRepository.findAllByUserEntityAndCycleEntityOrderByCreatedAtDesc(SecurityUtil.getAuthenticatedUser(),currentCycle);
        List <RecoveryProposalDto> recoveryProposalDtoList = proposalEntityList.stream()
                                                             .map(proposal -> mapToDto(proposal,votingService.countByProposalEntity(proposal),RecoveryProposalDto.class))
                                                              .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(recoveryProposalDtoList);
    }

    public ResponseEntity<Object> trendingProposals(){
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<ProposalEntity> proposalEntityList = proposalRepository.findAllByOrderByVotesDesc(pageRequest).getContent();
        List<RecoveryTrendingProposalDto> recoveryProposalDtoList = proposalEntityList.stream()
                                                            .map(proposal -> mapToDto(proposal,votingService.countByProposalEntity(proposal),RecoveryTrendingProposalDto.class))
                                                            .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(recoveryProposalDtoList);
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

    public ResponseEntity<Object> hasVoted(Long proposalId){
        Optional<ProposalEntity> proposal = Optional.ofNullable(proposalRepository.findById(proposalId).orElseThrow(() -> new EntityNotFoundException(String.format("Proposta de id %d não foi encontrada",proposalId))));
        return ResponseEntity.status(HttpStatus.OK).body(new RecoveryVoteProposalDto(votingService.hasVoted(SecurityUtil.getAuthenticatedUser(),proposal.get())));
    }
}
