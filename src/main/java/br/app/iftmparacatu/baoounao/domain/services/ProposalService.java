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
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    @Value("${cloud.aws.s3.bucket}")
    private String S3_BUCKET;

    @Autowired
    private CycleService cycleService;

    @Autowired
    private AmazonS3 amazonS3Client;

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
                .createdAt(proposalEntity.getCreatedAt())
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
    @Transactional
    public ResponseEntity<Object> save(String tittle,String description,String url,MultipartFile image,String category){
        CycleEntity currentCycle = getCurrentCycleOrThrow("Não foram encontrados ciclos em andamento. Para cadastrar uma proposta, é necessário primeiro cadastrar um ciclo.");

        if(proposalRepository.countByUserEntityAndCycleEntityAndActiveTrue(SecurityUtil.getAuthenticatedUser(),currentCycle) == PROPOSALS_LIMIT)
            throw new NotAllowedOperation(String.format("O limite de %d propostas foi atingido. Não é possível cadastrar mais propostas.",PROPOSALS_LIMIT));

        CategoryEntity categoryEntity = categoryRepository.findByTitleAndActiveTrue(category).orElseThrow(() -> new EntityNotFoundException(String.format("Categoria de nome %s não encontrada!", category)));
        try{
            String imageUrl = Optional.ofNullable(image)
                    .filter(img -> !img.isEmpty())
                    .map(img -> {
                        try {
                            return uploadImageToS3(img);
                        } catch (IOException e) {
                            throw new RuntimeException("Erro ao fazer upload da imagem", e);
                        }
                    })
                    .orElse(null);
            ProposalEntity proposalEntity = ProposalEntity.builder()
                    .description(description)
                    .title(tittle)
                    .videoUrl(url)
                    .image(imageUrl)
                    .cycleEntity(currentCycle)
                    .categoryEntity(categoryEntity)
                    .build();
            proposalRepository.save(proposalEntity);

            return ResponseUtil.createSuccessResponse("Proposta salva com sucesso !!",HttpStatus.CREATED);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    private String generateUniqueKey(String originalFilename) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = now.format(formatter);
        String invertedDateTime = new StringBuilder(formattedDateTime).reverse().toString();
        String uniqueId = UUID.randomUUID().toString();
        return invertedDateTime + "_" + uniqueId + "_" + originalFilename;
    }

    private String uploadImageToS3(MultipartFile file) throws IOException {
        try{
            String key = generateUniqueKey(file.getOriginalFilename());

            amazonS3Client.putObject(S3_BUCKET, key, file.getInputStream(), null);

            return amazonS3Client.getUrl(S3_BUCKET, key).toString();
        }catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload da imagem para o S3", e);
        }

    }

    public ResponseEntity<PaginatedProposalsResponse> findAllPendingModeration(int page, int size, String text,String sort ){ //Este endpoint lista todas as propostas pendentes de moderação
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        //List<ProposalEntity> proposals = proposalRepository.findByCycleEntityAndSituationAndActiveTrueOrderByCreatedAtDesc(currentCycle,Situation.PENDING_MODERATION);

        List<ProposalEntity> proposalEntityList = proposalRepository.findByCycleEntityAndTitleContainingAndSituationOrCycleEntityAndDescriptionContainingAndSituation(currentCycle,text,Situation.PENDING_MODERATION,currentCycle,text,Situation.PENDING_MODERATION);


        List <RecoveryProposalFilterDto> recoveryProposalDtoList = proposalEntityList.stream()
                .map(proposal -> mapToDto(proposal,votingService.countByProposalEntity(proposal)))
                .collect(Collectors.toList());

        // Ordena a lista completa com base no critério fornecido
        sortProposals(recoveryProposalDtoList, sort);
        int countReg = recoveryProposalDtoList.size();
        int start = Math.min(page * size, countReg);
        int end = Math.min((page + 1) * size, countReg);
        List<RecoveryProposalFilterDto> paginatedList = recoveryProposalDtoList.subList(start, end);

        PaginatedProposalsResponse response = PaginatedProposalsResponse.builder()
                .proposals(paginatedList)
                .totalElements(countReg)
                .totalPages((int) Math.ceil((double) countReg / size))
                .currentPage(page)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<Object> myProposals(){
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        List<ProposalEntity> proposalEntityList = proposalRepository.findAllByUserEntityAndCycleEntityAndActiveTrueOrderByCreatedAtDesc(SecurityUtil.getAuthenticatedUser(),currentCycle);
        List <RecoveryBasicProposalDto> recoveryProposalDtoList = proposalEntityList.stream()
                .map(proposal -> mapToDto(proposal, RecoveryBasicProposalDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(recoveryProposalDtoList);
    }

    private CategoryEntity findActiveCategory(String category) {
        return categoryRepository.findByTitleAndActiveTrue(category)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Categoria de nome %s não encontrada!", category)));
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
    @Transactional
    public ResponseEntity<Object> update(Long proposalID, String tittle,String description,String url,MultipartFile image,String category) throws IOException {
        ProposalEntity existingProposal = checkDeleteOrUpdateProposal(true,proposalID);

        CategoryEntity categoryEntity = Optional.ofNullable(category)
                .map(this::findActiveCategory)
                .orElse(null);

        String imageUrl = Optional.ofNullable(image)
                .filter(img -> !img.isEmpty())
                .map(img -> {
                    try {
                        return uploadImageToS3(img);
                    } catch (IOException e) {
                        throw new RuntimeException("Erro ao fazer upload da imagem", e);
                    }
                })
                .orElse(null);

        UpdateProposalDto updateProposalDto = UpdateProposalDto.builder()
                .title(tittle)
                .description(description)
                .categoryEntity(categoryEntity)
                .url(url)
                .image(imageUrl)
                .build();
        Optional.ofNullable(updateProposalDto.title())
                .ifPresent(existingProposal::setTitle);
        Optional.ofNullable(updateProposalDto.description())
                .ifPresent(existingProposal::setDescription);
        Optional.ofNullable(updateProposalDto.image())
                .ifPresent(existingProposal::setImage);
        Optional.ofNullable(updateProposalDto.categoryEntity())
                .ifPresent(existingProposal::setCategoryEntity);
        Optional.ofNullable(updateProposalDto.url())
                .ifPresent(existingProposal::setVideoUrl);
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
    public ResponseEntity<PaginatedProposalsResponse> filterByDescriptionOrTitle(String text,int page, int size, String sort, String voted){
        boolean showVoted = voted != null ? (voted.equalsIgnoreCase("show")) : false;
        System.out.println(showVoted);
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        Situation situation = Situation.OPEN_FOR_VOTING;
        Pageable pageable = PageRequest.of(page, size);

        List <RecoveryProposalFilterDto> recoveryProposalDtoList;

        if (showVoted){
            recoveryProposalDtoList = votingService.findAllVotedUserProposals();
        }else{
            List<ProposalEntity> proposalEntityList = proposalRepository.findByCycleEntityAndTitleContainingAndSituationOrCycleEntityAndDescriptionContainingAndSituation(currentCycle,text,situation,currentCycle,text,situation);
            recoveryProposalDtoList = proposalEntityList.stream()
                    .map(proposal -> mapToDto(proposal,votingService.countByProposalEntity(proposal)))
                    .collect(Collectors.toList());
        }

        // Ordena a lista completa com base no critério fornecido
        sortProposals(recoveryProposalDtoList, sort);
        int countReg = recoveryProposalDtoList.size();
        int start = Math.min(page * size, countReg);
        int end = Math.min((page + 1) * size, countReg);
        List<RecoveryProposalFilterDto> paginatedList = recoveryProposalDtoList.subList(start, end);

        PaginatedProposalsResponse response = PaginatedProposalsResponse.builder()
                .proposals(paginatedList)
                .totalElements(countReg)
                .totalPages((int) Math.ceil((double) countReg / size))
                .currentPage(page)
                .build();
        //response.sortProposals(sort);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private CycleEntity getCurrentCycleOrThrow(){
        return cycleService.findProgressCycle().orElseThrow(() -> new EntityNotFoundException(String.format("Não foram localizados ciclos em andamento")));
    }

    private void sortProposals(List<RecoveryProposalFilterDto> proposals, String sort) {
        switch (sort.toLowerCase()) {
            case "recent":
                proposals.sort(Comparator.comparing(RecoveryProposalFilterDto::getCreatedAt).reversed());
                break;
            case "oldest":
                proposals.sort(Comparator.comparing(RecoveryProposalFilterDto::getCreatedAt));
                break;
            case "most_votes":
                proposals.sort(Comparator.comparing(RecoveryProposalFilterDto::getVotes).reversed());
                break;
            case "least_votes":
                proposals.sort(Comparator.comparing(RecoveryProposalFilterDto::getVotes));
                break;
            default:
                break;
        }
    }

    private CycleEntity getCurrentCycleOrThrow(String message){
        return cycleService.findProgressCycle().orElseThrow(() -> new EntityNotFoundException(String.format("%s",message)));
    }

    public ResponseEntity<Object> dashboardCount(){
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        Long openProposals = proposalRepository.countBySituationAndCycleEntityAndActiveTrue(Situation.PENDING_MODERATION,currentCycle);
        Long totalVotes = votingService.countByCycleEntity(currentCycle);
        Long deniedProposals = proposalRepository.countBySituationAndCycleEntityAndActiveTrue(Situation.DENIED,currentCycle);
        Long acceptedProposals = proposalRepository.countBySituationAndCycleEntityAndActiveTrue(Situation.OPEN_FOR_VOTING,currentCycle);

        RecoveryDashboardInformationtDto recoveryDashboardInformationtDto = RecoveryDashboardInformationtDto.builder()
                .openProposals(openProposals)
                .votes(totalVotes)
                .deniedProposals(deniedProposals)
                .acceptedProposals(acceptedProposals)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(recoveryDashboardInformationtDto);
    }

    public boolean cycleHasProposals(CycleEntity cycleEntity){
        return !proposalRepository.findByCycleEntityAndActiveTrue(cycleEntity).isEmpty();
    }

    public boolean categoryHasProposals(CategoryEntity categoryEntity){
        return !proposalRepository.findAllByCategoryEntity(categoryEntity).isEmpty();
    }

    public ResponseEntity<Object> limit(){
        CycleEntity currentCycle = getCurrentCycleOrThrow();
        Long createdProposals = proposalRepository.countByUserEntityAndCycleEntityAndActiveTrue(SecurityUtil.getAuthenticatedUser(),currentCycle);
        RecoveryLimitDto recoveryLimitDto = RecoveryLimitDto.builder()
                .available(PROPOSALS_LIMIT - createdProposals)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(recoveryLimitDto);
    }
}
