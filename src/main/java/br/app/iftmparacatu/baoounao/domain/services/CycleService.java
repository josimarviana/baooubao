package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.api.exception.NotAllowedOperation;
import br.app.iftmparacatu.baoounao.domain.dtos.input.CreateCycleDto;
import br.app.iftmparacatu.baoounao.domain.model.*;
import br.app.iftmparacatu.baoounao.domain.repository.CycleRepository;
import br.app.iftmparacatu.baoounao.domain.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CycleService {
    @Autowired
    private CycleRepository cycleRepository;

    public Optional<CycleEntity> findProgressCycle(){
        LocalDate date = LocalDate.now();
        return cycleRepository.findByStartDateLessThanEqualAndFinishDateGreaterThanEqual(date,date);
    }

    private void checkUpdateOrCreateCycle(boolean update, CreateCycleDto createCycleDto){
        String operation = update ? "Atualização" : "Cadastro";
        Optional<CycleEntity> overlappingCycle = findOverlappingCycle(createCycleDto.startDate(),createCycleDto.finishDate()); //TODO: pode ser que sobreponha mais de um ciclo, talvez seja necessário ajustar e alterar a mensagem de erro
        if (overlappingCycle.isPresent()) {
            CycleEntity foundedCycle = overlappingCycle.get();
            throw new NotAllowedOperation(String.format(
                    "%s do ciclo não permitido: já existe um ciclo em andamento para a data atual. O ciclo '%s' ocorrerá de %s a %s.",
                    operation,
                    foundedCycle.getTitle(),
                    foundedCycle.getStartDate(),
                    foundedCycle.getFinishDate()
            ));
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

    public Optional<CycleEntity> findOverlappingCycle(LocalDate dateStart, LocalDate dateEnd) {
        return cycleRepository.findByStartDateLessThanEqualAndFinishDateGreaterThanEqualOrStartDateBetweenOrFinishDateBetween(
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
    public ResponseEntity<Object> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(cycleRepository.findAll());
    }

    public ResponseEntity<Object> update(Long cycleID, CreateCycleDto updatedCycleDto) {
        checkUpdateOrCreateCycle(true,updatedCycleDto);
        CycleEntity existingCycle = cycleRepository.findById(cycleID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ciclo de id %d não encontrada!", cycleID)));
        Optional.ofNullable(updatedCycleDto.title())
                .ifPresent(existingCycle::setTitle);
        Optional.ofNullable(updatedCycleDto.startDate())
                .ifPresent(existingCycle::setStartDate);
        Optional.ofNullable(updatedCycleDto.finishDate())
                .ifPresent(existingCycle::setFinishDate);
        cycleRepository.save(existingCycle);
        return ResponseUtil.createSuccessResponse("Ciclo atualizada com sucesso !!",HttpStatus.OK);
    }

    public ResponseEntity<Object> delete(Long cycleID) {
        CycleEntity existingCycle = cycleRepository.findById(cycleID)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ciclo de id %d não encontrada!", cycleID)));
        existingCycle.setActive(false);
        cycleRepository.save(existingCycle);
        return ResponseUtil.createSuccessResponse("Ciclo desativado com sucesso !!",HttpStatus.OK);
    }
}
