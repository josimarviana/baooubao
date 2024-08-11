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

    public ResponseEntity<Object> save(CreateCycleDto createCycleDto){
        Optional<CycleEntity> overlappingCycle = findOverlappingCycle(createCycleDto.startDate(),createCycleDto.finishDate());
        if (overlappingCycle.isPresent()) {
            CycleEntity foundedCycle = overlappingCycle.get();
            throw new NotAllowedOperation(String.format(
                    "Cadastro do ciclo não permitido: já existe um ciclo em andamento para a data atual. O ciclo '%s' ocorrerá de %s a %s.",
                    foundedCycle.getTitle(),
                    foundedCycle.getStartDate(),
                    foundedCycle.getFinishDate()
            ));
        }

        if (createCycleDto.startDate().isAfter(createCycleDto.finishDate())) {
            throw new NotAllowedOperation(String.format(
                    "Cadastro do ciclo não permitido: a data de início (%s) é posterior à data de término (%s).",
                    createCycleDto.startDate(),
                    createCycleDto.finishDate()
            ));
        }

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
}
