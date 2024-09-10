package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.api.exception.NotAllowedOperation;
import br.app.iftmparacatu.baoounao.domain.dtos.input.CreateCycleDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.PaginatedCycleResponse;
import br.app.iftmparacatu.baoounao.domain.dtos.output.PaginatedProposalsResponse;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryProposalFilterDto;
import br.app.iftmparacatu.baoounao.domain.model.*;
import br.app.iftmparacatu.baoounao.domain.repository.CycleRepository;
import br.app.iftmparacatu.baoounao.domain.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CycleService {
    @Autowired
    private CycleRepository cycleRepository;

    @Autowired
    private ProposalService proposalService;

    public Optional<CycleEntity> findProgressCycle(){
        LocalDate date = LocalDate.now();
        return cycleRepository.findByStartDateLessThanEqualAndFinishDateGreaterThanEqualAndActiveTrue(date,date);
    }

    private void checkUpdateOrCreateCycle(boolean update, CreateCycleDto createCycleDto){
        String operation = update ? "Atualização" : "Cadastro";
        List<Optional<CycleEntity>> overlappingCycleList = findOverlappingCycle(createCycleDto.startDate(),createCycleDto.finishDate());
        Optional<CycleEntity> checkCycle = cycleRepository.findByTitleAndActiveTrue(createCycleDto.title());

        if (checkCycle.isPresent()){
            throw new NotAllowedOperation(String.format("Ciclo %s já cadastrado !!",createCycleDto.title()));
        }


        if (!overlappingCycleList.isEmpty()) {
            List<String> cycleTitles = overlappingCycleList.stream()
                    .filter(Optional::isPresent) // Filtra apenas os Optionals que contêm valores
                    .map(Optional::get) // Obtém o valor do Optional
                    .map(CycleEntity::toString) // Converte para String usando o método toString da classe CycleEntity
                    .toList();
            throw new NotAllowedOperation(String.format(
                    "%s do ciclo não permitido: já existe um ou mais ciclos em andamento para a data atual. Ciclos: %s",
                    operation,
                    cycleTitles));
        }

        if (createCycleDto.startDate().isAfter(createCycleDto.finishDate())) {
            throw new NotAllowedOperation(String.format(
                    "%s do ciclo não permitido: a data de início (%s) é posterior à data de término (%s).",
                    operation,
                    createCycleDto.startDate(),
                    createCycleDto.finishDate()
            ));
        }
    }

    private void checkUpdateOrCreateCycle(Long cycleID,boolean update, CreateCycleDto createCycleDto){
        String operation = update ? "Atualização" : "Cadastro";
        List<Optional<CycleEntity>> overlappingCycleList = new ArrayList<>();
        LocalDate startDate = createCycleDto.startDate();
        LocalDate finishDate = createCycleDto.finishDate();

        if (startDate != null || finishDate != null) {
            LocalDate effectiveStartDate = (startDate != null) ? startDate : finishDate;
            LocalDate effectiveFinishDate = (finishDate != null) ? finishDate : startDate;
            overlappingCycleList = findOverlappingCycle(effectiveStartDate, effectiveFinishDate);
        }


        Optional<CycleEntity> checkCycle = cycleRepository.findByTitleAndActiveTrue(createCycleDto.title());

        if (checkCycle.isPresent() && cycleID != checkCycle.get().getId()){
            throw new NotAllowedOperation(String.format("Ciclo %s já cadastrado !!",createCycleDto.title()));
        }
        Optional<CycleEntity> cycleVerification = cycleRepository.findByIdAndActiveTrue(cycleID);
        if (!overlappingCycleList.isEmpty() || ( update && cycleVerification.isPresent() && cycleVerification.get().getId() != cycleID)) {
            List<String> cycleTitles = overlappingCycleList.stream()
                    .filter(Optional::isPresent) // Filtra apenas os Optionals que contêm valores
                    .map(Optional::get) // Obtém o valor do Optional
                    .map(CycleEntity::toString) // Converte para String usando o método toString da classe CycleEntity
                    .toList();
            throw new NotAllowedOperation(String.format(
                    "%s do ciclo não permitido: já existe um ou mais ciclos em andamento para a data atual. Ciclos: %s",
                    operation,
                    cycleTitles));
        }

        if (Optional.ofNullable(createCycleDto.startDate()).isPresent() &&
            Optional.ofNullable(createCycleDto.finishDate()).isPresent() &&
                createCycleDto.startDate().isAfter(createCycleDto.finishDate())) {
            throw new NotAllowedOperation(String.format(
                    "%s do ciclo não permitido: a data de início (%s) é posterior à data de término (%s).",
                    operation,
                    createCycleDto.startDate(),
                    createCycleDto.finishDate()
            ));
        }
    }

    public ResponseEntity<Object> save(CreateCycleDto createCycleDto){
        checkUpdateOrCreateCycle(false,createCycleDto);
        CycleEntity newCycle = CycleEntity.builder()
                .title(createCycleDto.title())
                .startDate(createCycleDto.startDate())
                .finishDate(createCycleDto.finishDate())
                .build();
        cycleRepository.save(newCycle);
        return ResponseUtil.createSuccessResponse("Ciclo cadastrado com sucesso !!",HttpStatus.CREATED);
    }

    public List<Optional<CycleEntity>> findOverlappingCycle(LocalDate dateStart, LocalDate dateEnd) {
        return cycleRepository.findByStartDateLessThanEqualAndActiveTrueAndFinishDateGreaterThanEqualAndActiveTrueOrStartDateBetweenAndActiveTrueOrFinishDateBetweenAndActiveTrue(
                dateEnd,
                dateStart,
                dateStart, dateEnd,
                dateStart, dateEnd
        );
    }
    public ResponseEntity<Object> findById(Long cycleID){
        Optional<CycleEntity> cycleEntity = Optional.ofNullable(cycleRepository.findById(cycleID).orElseThrow(() -> new EntityNotFoundException("REGISTRO NÃO ENCONTRADO!")));
        return ResponseEntity.status(HttpStatus.CREATED).body(cycleEntity.get());
    }
    public ResponseEntity<PaginatedCycleResponse> findAll(int page,int size,String text,String sort ){
        List<CycleEntity> cycleEntityList = cycleRepository.findByTitleContaining(text);

        sortCycles(cycleEntityList, sort);
        int countReg = cycleEntityList.size();
        int start = Math.min(page * size, countReg);
        int end = Math.min((page + 1) * size, countReg);
        List<CycleEntity> paginatedList = cycleEntityList.subList(start, end);

        PaginatedCycleResponse response = PaginatedCycleResponse.builder()
                .cycleEntityList(paginatedList)
                .totalElements(countReg)
                .totalPages((int) Math.ceil((double) countReg / size))
                .currentPage(page)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private void sortCycles(List<CycleEntity> cycleEntityList, String sort) {
        switch (sort.toLowerCase()) {
            case "recent_createdat":
                cycleEntityList.sort(Comparator.comparing(CycleEntity::getCreatedAt).reversed());
                break;
            case "oldset_createdat":
                cycleEntityList.sort(Comparator.comparing(CycleEntity::getCreatedAt));
                break;
            case "recent_startdate":
                cycleEntityList.sort(Comparator.comparing(CycleEntity::getStartDate).reversed());
                break;
            case "oldest_startdate":
                cycleEntityList.sort(Comparator.comparing(CycleEntity::getStartDate));
                break;
            case "recent_finishdate":
                cycleEntityList.sort(Comparator.comparing(CycleEntity::getFinishDate).reversed());
                break;
            case "oldest_finishdate":
                cycleEntityList.sort(Comparator.comparing(CycleEntity::getFinishDate));
                break;
            default:
                break;
        }
    }


    public ResponseEntity<Object> update(Long cycleID, CreateCycleDto updatedCycleDto) {
        checkUpdateOrCreateCycle(cycleID,true,updatedCycleDto);
        CycleEntity existingCycle = cycleRepository.findById(cycleID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ciclo de id %d não encontrada!", cycleID)));
        Optional.ofNullable(updatedCycleDto.title())
                .ifPresent(existingCycle::setTitle);
        Optional.ofNullable(updatedCycleDto.startDate())
                .ifPresent(existingCycle::setStartDate);
        Optional.ofNullable(updatedCycleDto.finishDate())
                .ifPresent(existingCycle::setFinishDate);
        Optional.ofNullable(updatedCycleDto.active())
                .ifPresent(existingCycle::setActive);
        cycleRepository.save(existingCycle);
        return ResponseUtil.createSuccessResponse("Ciclo atualizada com sucesso !!",HttpStatus.OK);
    }

    public ResponseEntity<Object> delete(Long cycleID) {
        CycleEntity existingCycle = cycleRepository.findById(cycleID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ciclo de id %d não encontrada!", cycleID)));

        if(proposalService.cycleHasProposals(existingCycle)){
            throw new NotAllowedOperation("Não é possível desativar este ciclo porque há propostas vinculadas a ele !!");
        }

        existingCycle.setActive(false);
        cycleRepository.save(existingCycle);
        return ResponseUtil.createSuccessResponse("Ciclo desativado com sucesso !!",HttpStatus.OK);
    }
}
