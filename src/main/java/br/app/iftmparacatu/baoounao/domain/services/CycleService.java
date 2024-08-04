package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.api.exception.ProgressCycleException;
import br.app.iftmparacatu.baoounao.api.exception.ProposalException;
import br.app.iftmparacatu.baoounao.domain.dtos.input.CreateCycleDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryProposalDto;
import br.app.iftmparacatu.baoounao.domain.enums.RoleName;
import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import br.app.iftmparacatu.baoounao.domain.model.*;
import br.app.iftmparacatu.baoounao.domain.repository.CategoryRepository;
import br.app.iftmparacatu.baoounao.domain.repository.CycleRepository;
import br.app.iftmparacatu.baoounao.domain.repository.ProposalRepository;
import br.app.iftmparacatu.baoounao.domain.util.SecurityUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CycleService {
    @Autowired
    private CycleRepository cycleRepository;

    public Optional<CycleEntity> findProgressCycle(){
        return Optional.ofNullable(cycleRepository.findByFinishedAtIsNull().orElseThrow(() -> new EntityNotFoundException("NÃO FOI ENCONTRADO CICLO EM ANDAMENTO")));
    }

    public ResponseEntity<Object> save(CreateCycleDto createCycleDto){
        Optional<CycleEntity> openCycle = cycleRepository.findByFinishedAtIsNull();

        if(openCycle.isPresent()){
            throw new ProgressCycleException("Não é possível cadastrar um novo ciclo pois já existe um ciclo em andamento");
        }

        CycleEntity newCycle = CycleEntity.builder()
                .title(createCycleDto.title())
                .finishedAt(createCycleDto.finishedAt())
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
