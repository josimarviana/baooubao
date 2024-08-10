package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.domain.dtos.input.CreateCycleDto;
import br.app.iftmparacatu.baoounao.domain.model.*;
import br.app.iftmparacatu.baoounao.domain.repository.CycleRepository;
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
        CycleEntity newCycle = CycleEntity.builder()
                .title(createCycleDto.title())
                .startDate(createCycleDto.startDate())
                .finishDate(createCycleDto.finishDate())
                .build();
        cycleRepository.save(newCycle);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    public ResponseEntity<Object> findById(Long cycleID){
        Optional<CycleEntity> cycleEntity = Optional.ofNullable(cycleRepository.findById(cycleID).orElseThrow(() -> new EntityNotFoundException("REGISTRO NÃO ENCONTRADO!")));
        return ResponseEntity.status(HttpStatus.CREATED).body(cycleEntity.get());
    }
    public ResponseEntity<Object> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(cycleRepository.findAll());
    }
}
