package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.api.exception.NotAllowedOperation;
import br.app.iftmparacatu.baoounao.domain.dtos.input.UpdateProposalDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryProposalDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryTrendingProposalDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryVoteProposalDto;
import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CategoryRepository;
import br.app.iftmparacatu.baoounao.domain.repository.ProposalRepository;
import br.app.iftmparacatu.baoounao.domain.util.SecurityUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
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
        CycleEntity currentCycle = getCurrentCycleOrThrow("Não foram encontrados ciclos em andamento. Para cadastrar uma proposta, é necessário primeiro cadastrar um ciclo.");

        if(proposalRepository.countByUserEntityAndCycleEntity(SecurityUtil.getAuthenticatedUser(),currentCycle) == 3)
            throw new NotAllowedOperation("O limite de 3 propostas foi atingido. Não é possível cadastrar mais propostas.");

        try{
            ProposalEntity proposalEntity = new ProposalEntity();
            proposalEntity.setDescription(description);
            proposalEntity.setTitle(tittle);
            proposalEntity.setVideoUrl(url);
            proposalEntity.setImage(image.getBytes());
            proposalEntity.setCycleEntity(currentCycle);
            CategoryEntity categoryEntity = categoryRepository.findByTitle(category);
            proposalEntity.setCategoryEntity(categoryEntity);
            proposalRepository.save(proposalEntity);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    };

    public List<RecoveryProposalDto> findAll(){
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        List<ProposalEntity> proposals = proposalRepository.findAllByCycleEntityOrderByCreatedAtDesc(currentCycle);
        return proposals.stream()
                .map(proposal -> mapToDto(proposal,votingService.countByProposalEntity(proposal),RecoveryProposalDto.class))
                .collect(Collectors.toList());
    }

    public ResponseEntity<Object> myProposals(){
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        List<ProposalEntity> proposalEntityList = proposalRepository.findAllByUserEntityAndCycleEntityOrderByCreatedAtDesc(SecurityUtil.getAuthenticatedUser(),currentCycle);
        List <RecoveryProposalDto> recoveryProposalDtoList = proposalEntityList.stream()
                                                             .map(proposal -> mapToDto(proposal,votingService.countByProposalEntity(proposal),RecoveryProposalDto.class))
                                                              .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(recoveryProposalDtoList);
    }

    public ResponseEntity<Object> trendingProposals(){
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<ProposalEntity> proposalEntityList = proposalRepository.findAllByCycleEntityOrderByVotesDesc(pageRequest,currentCycle).getContent();
        List<RecoveryTrendingProposalDto> recoveryProposalDtoList = proposalEntityList.stream()
                                                            .map(proposal -> mapToDto(proposal,votingService.countByProposalEntity(proposal),RecoveryTrendingProposalDto.class))
                                                            .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(recoveryProposalDtoList);
    }
    public ResponseEntity<Object> update(Long proposalID, UpdateProposalDto updateProposalDto) {
        ProposalEntity existingProposal = checkDeleteOrUpdateProposal(true,proposalID);
        Optional.ofNullable(updateProposalDto.title())
                .ifPresent(existingProposal::setTitle);
        Optional.ofNullable(updateProposalDto.description())
                .ifPresent(existingProposal::setDescription);
        Optional.ofNullable(updateProposalDto.image())
                .ifPresent(existingProposal::setImage);
        proposalRepository.save(existingProposal);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public ResponseEntity<Object> moderate(Long proposalID, Situation situation) {
        ProposalEntity existingProposal = proposalRepository.findById(proposalID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Proposta de id %d não encontrada!", proposalID)));
        Optional.ofNullable(situation)
                .ifPresent(existingProposal::setSituation);
        proposalRepository.save(existingProposal);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private ProposalEntity checkDeleteOrUpdateProposal(boolean update, Long proposalID){
        String operation = update ? "atualizar" : "apagar";
        ProposalEntity existingProposal = proposalRepository.findById(proposalID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Proposta de id %d não encontrada!", proposalID)));
        boolean openForVoting = existingProposal.getSituation().equals(Situation.OPEN_FOR_VOTING);
        boolean inModeration = existingProposal.getSituation().equals(Situation.IN_MODERATION);
        boolean fowardedToBoard = existingProposal.getSituation().equals(Situation.FORWARDED_TO_BOARD);

        if (openForVoting){
            throw new NotAllowedOperation(String.format("Não é possível %s propostas em votação",operation));
        }else if (inModeration){
            throw new NotAllowedOperation(String.format("Não é possível %s propostas em moderação",operation));
        }else if (fowardedToBoard){
            throw new NotAllowedOperation(String.format("Não é possível %s propostas enviada para o conselho",operation));
        }

        return existingProposal;
    }

    public ResponseEntity<Object> delete(Long proposalID) {
        ProposalEntity existingProposal = checkDeleteOrUpdateProposal(false,proposalID);
        proposalRepository.delete(existingProposal);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<Object> hasVoted(Long proposalId){
        Optional<ProposalEntity> proposal = Optional.ofNullable(proposalRepository.findById(proposalId).orElseThrow(() -> new EntityNotFoundException(String.format("Proposta de id %d não foi encontrada",proposalId))));
        return ResponseEntity.status(HttpStatus.OK).body(new RecoveryVoteProposalDto(votingService.hasVoted(SecurityUtil.getAuthenticatedUser(),proposal.get())));
    }
    public List<ProposalEntity> filterByDescriptionOrTitle(String text){
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        return proposalRepository.findByCycleEntityAndTitleContainingOrCycleEntityAndDescriptionContaining(currentCycle,text,currentCycle,text);
    }

    private CycleEntity getCurrentCycleOrThrow(){
        return cycleService.findProgressCycle().orElseThrow(() -> new EntityNotFoundException(String.format("Não foram localizados ciclos em andamento")));
    }

    private CycleEntity getCurrentCycleOrThrow(String message){
        return cycleService.findProgressCycle().orElseThrow(() -> new EntityNotFoundException(String.format("%s",message)));
    }
}
