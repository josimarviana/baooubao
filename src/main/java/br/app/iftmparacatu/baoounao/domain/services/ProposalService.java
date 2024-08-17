package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.api.exception.NotAllowedOperation;
import br.app.iftmparacatu.baoounao.domain.dtos.input.UpdateProposalDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.*;
import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import br.app.iftmparacatu.baoounao.domain.model.CategoryEntity;
import br.app.iftmparacatu.baoounao.domain.model.CycleEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.repository.CategoryRepository;
import br.app.iftmparacatu.baoounao.domain.repository.ProposalRepository;
import br.app.iftmparacatu.baoounao.domain.util.ResponseUtil;
import br.app.iftmparacatu.baoounao.domain.util.SecurityUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    @Value("${config.proposals.limit}")
    private int PROPOSALS_LIMIT;

    @Autowired
    private CycleService cycleService;

    public <T>  T mapToDto(ProposalEntity proposalEntity , Class<T> dtoClass) {
        T dto = modelMapper.map(proposalEntity, dtoClass);
        return dto;
    }

    public RecoveryProposalFilterDto mapToDto(ProposalEntity proposalEntity, int votes){
        RecoveryProposalFilterDto recoveryProposalDto = RecoveryProposalFilterDto.builder()
                .id(proposalEntity.getId())
                .title(proposalEntity.getTitle())
                .description(proposalEntity.getDescription())
                .category(proposalEntity.getCategoryEntity().getTitle())
                .icon(proposalEntity.getCategoryEntity().getIcon())
                .votes(votes)
                .createdAt(proposalEntity.getCreatedAt().toString())
                .build();
        return recoveryProposalDto;
    }

    public ResponseEntity<Object> findById(Long proposalId){
        Optional<ProposalEntity> proposal = Optional.ofNullable(proposalRepository.findById(proposalId).orElseThrow(() -> new EntityNotFoundException(String.format("Proposta de id %d não foi encontrada",proposalId))));
        ProposalEntity recoveredProposal = proposal.get();
        RecoveryProposalDto recoveryProposalDto = RecoveryProposalDto.builder()
                .id(proposalId)
                .title(recoveredProposal.getTitle())
                .description(recoveredProposal.getDescription())
                .situation(recoveredProposal.getSituation().toString())
                .image(recoveredProposal.getImage())
                .author(recoveredProposal.getUserEntity().getName())
                .likes(votingService.countByProposalEntity(recoveredProposal))
                .category(recoveredProposal.getCategoryEntity().getTitle())
                .videoUrl(recoveredProposal.getVideoUrl())
                .icon(recoveredProposal.getCategoryEntity().getIcon())
                .createdAt(recoveredProposal.getCreatedAt())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(recoveryProposalDto);
    }

    public ResponseEntity<Object> save(String tittle,String description,String url,MultipartFile image,String category){
        CycleEntity currentCycle = getCurrentCycleOrThrow("Não foram encontrados ciclos em andamento. Para cadastrar uma proposta, é necessário primeiro cadastrar um ciclo.");

        if(proposalRepository.countByUserEntityAndCycleEntityAndActiveTrue(SecurityUtil.getAuthenticatedUser(),currentCycle) == PROPOSALS_LIMIT)
            throw new NotAllowedOperation(String.format("O limite de %d propostas foi atingido. Não é possível cadastrar mais propostas.",PROPOSALS_LIMIT));

        CategoryEntity categoryEntity = categoryRepository.findByTitleAndActiveTrue(category).orElseThrow(() -> new EntityNotFoundException(String.format("Categoria de nome %s não encontrada!", category)));
        try{
            ProposalEntity proposalEntity = ProposalEntity.builder()
                    .description(description)
                    .title(tittle)
                    .videoUrl(url)
                    .image(image.getBytes())
                    .cycleEntity(currentCycle)
                    .categoryEntity(categoryEntity)
                    .build();
            proposalRepository.save(proposalEntity);

            return ResponseUtil.createSuccessResponse("Proposta salva com sucesso !!",HttpStatus.CREATED);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    };

    public List<RecoveryBasicProposalDto> findAll(){ //Este endpoint lista todas as propostas pendentes de moderação
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        List<ProposalEntity> proposals = proposalRepository.findByCycleEntityAndSituationAndActiveTrueOrderByCreatedAtDesc(currentCycle,Situation.PENDING_MODERATION);
        return proposals.stream()
                .map(proposal -> mapToDto(proposal, RecoveryBasicProposalDto.class))
                .collect(Collectors.toList());
    }

    public ResponseEntity<Object> myProposals(){
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        List<ProposalEntity> proposalEntityList = proposalRepository.findAllByUserEntityAndCycleEntityAndActiveTrueOrderByCreatedAtDesc(SecurityUtil.getAuthenticatedUser(),currentCycle);
        List <RecoveryBasicProposalDto> recoveryProposalDtoList = proposalEntityList.stream()
                                                             .map(proposal -> mapToDto(proposal, RecoveryBasicProposalDto.class))
                                                             .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(recoveryProposalDtoList);
    }

    public ResponseEntity<Object> trendingProposals(){
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        List<ProposalEntity> proposalEntityList = proposalRepository.findAllByCycleEntityAndActiveTrueAndSituation(currentCycle, Situation.OPEN_FOR_VOTING);
        List<RecoveryProposalFilterDto> recoveryProposalDtoList = proposalEntityList.stream()
                                                            .map(proposal -> mapToDto(proposal,votingService.countByProposalEntity(proposal)))
                                                            .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(recoveryProposalDtoList.stream()
                                                        .sorted(Comparator.comparingInt(RecoveryProposalFilterDto::getVotes).reversed())
                                                        .limit(3)
                                                        .collect(Collectors.toList()));
    }
    public ResponseEntity<Object> update(Long proposalID, UpdateProposalDto updateProposalDto) {
        ProposalEntity existingProposal = checkDeleteOrUpdateProposal(true,proposalID);
        Optional.ofNullable(updateProposalDto.title())
                .ifPresent(existingProposal::setTitle);
        Optional.ofNullable(updateProposalDto.description())
                .ifPresent(existingProposal::setDescription);
        Optional.ofNullable(updateProposalDto.image())
                .ifPresent(existingProposal::setImage);
        existingProposal.setSituation(Situation.PENDING_MODERATION);
        proposalRepository.save(existingProposal);

        return ResponseUtil.createSuccessResponse("Proposta atualizada com sucesso !!",HttpStatus.OK);
    }

    public ResponseEntity<Object> moderate(Long proposalID, Situation situation) {
        ProposalEntity existingProposal = proposalRepository.findById(proposalID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Proposta de id %d não encontrada!", proposalID)));
        Optional.ofNullable(situation)
                .ifPresent(existingProposal::setSituation);
        proposalRepository.save(existingProposal);

        Map<Situation, String> responseMessages = Map.of(
                Situation.OPEN_FOR_VOTING, "Aprovada",
                Situation.DENIED, "Negada",
                Situation.FORWARDED_TO_BOARD, "Encaminhada ao Conselho"
        );

        String responseText = responseMessages.getOrDefault(situation, "");
        return ResponseUtil.createSuccessResponse(String.format("Proposta %s com sucesso !!",responseText),HttpStatus.OK);
    }

    private ProposalEntity checkDeleteOrUpdateProposal(boolean update, Long proposalID){
        String operation = update ? "atualizar" : "apagar";
        ProposalEntity existingProposal = proposalRepository.findById(proposalID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Proposta de id %d não encontrada!", proposalID)));
        boolean openForVoting = existingProposal.getSituation().equals(Situation.OPEN_FOR_VOTING);
        boolean fowardedToBoard = existingProposal.getSituation().equals(Situation.FORWARDED_TO_BOARD);

        if (openForVoting){
            throw new NotAllowedOperation(String.format("Não é possível %s propostas em votação",operation));
        }else if (fowardedToBoard){
            throw new NotAllowedOperation(String.format("Não é possível %s propostas enviada para o conselho",operation));
        }

        return existingProposal;
    }

    public ResponseEntity<Object> delete(Long proposalID) {
        ProposalEntity existingProposal = checkDeleteOrUpdateProposal(false,proposalID);
        existingProposal.setActive(false);
        proposalRepository.save(existingProposal);
        return ResponseUtil.createSuccessResponse("Proposta desativada com sucesso !!",HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<Object> hasVoted(Long proposalId){
        Optional<ProposalEntity> proposal = Optional.ofNullable(proposalRepository.findById(proposalId).orElseThrow(() -> new EntityNotFoundException(String.format("Proposta de id %d não foi encontrada",proposalId))));
        return ResponseEntity.status(HttpStatus.OK).body(new RecoveryVoteProposalDto(votingService.hasVoted(SecurityUtil.getAuthenticatedUser(),proposal.get())));
    }
    public ResponseEntity<PaginatedProposalsResponse> filterByDescriptionOrTitle(String text,int page, int size, String sort){
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        Situation situation = Situation.OPEN_FOR_VOTING;
        Pageable pageable = PageRequest.of(page, size);
        Page<ProposalEntity> proposalEntityList = proposalRepository.findByCycleEntityAndTitleContainingAndSituationOrCycleEntityAndDescriptionContainingAndSituation(currentCycle,text,situation,currentCycle,text,situation,pageable);


        List <RecoveryProposalFilterDto> recoveryProposalDtoList = proposalEntityList.stream()
                .map(proposal -> mapToDto(proposal,votingService.countByProposalEntity(proposal)))
                .collect(Collectors.toList());

        PaginatedProposalsResponse response = PaginatedProposalsResponse.builder()
                .proposals(recoveryProposalDtoList)
                .totalElements(proposalEntityList.getTotalElements())
                .totalPages(proposalEntityList.getTotalPages())
                .currentPage(proposalEntityList.getNumber())
                .build();
        response.sortProposals(sort);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private CycleEntity getCurrentCycleOrThrow(){
        return cycleService.findProgressCycle().orElseThrow(() -> new EntityNotFoundException(String.format("Não foram localizados ciclos em andamento")));
    }

    private CycleEntity getCurrentCycleOrThrow(String message){
        return cycleService.findProgressCycle().orElseThrow(() -> new EntityNotFoundException(String.format("%s",message)));
    }

    public ResponseEntity<Object> dashboardCount(){
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        Long openProposals = proposalRepository.countBySituationAndCycleEntity(Situation.PENDING_MODERATION,currentCycle);
        Long totalVotes = votingService.countByCycleEntity(currentCycle);
        Long deniedProposals = proposalRepository.countBySituationAndCycleEntity(Situation.DENIED,currentCycle);
        Long acceptedProposals = proposalRepository.countBySituationAndCycleEntity(Situation.OPEN_FOR_VOTING,currentCycle);

        RecoveryDashboardInformationtDto recoveryDashboardInformationtDto = RecoveryDashboardInformationtDto.builder()
                .openProposals(openProposals)
                .votes(totalVotes)
                .deniedProposals(deniedProposals)
                .acceptedProposals(acceptedProposals)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(recoveryDashboardInformationtDto);
    }

    public boolean cycleHasProposals(CycleEntity cycleEntity){ //TODO: notificar quando houver propostas anexadas aquele ciclo quando o front tentar desativar um ciclo
        return !proposalRepository.findByCycleEntity(cycleEntity).isEmpty();
    }

    public boolean categoryHasProposals(CategoryEntity categoryEntity){
        return !proposalRepository.findAllByCategoryEntity(categoryEntity).isEmpty();
    }
}
