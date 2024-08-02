package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.domain.dtos.input.VotingDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryProposalDto;
import br.app.iftmparacatu.baoounao.domain.enums.RoleName;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.model.RoleEntity;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import br.app.iftmparacatu.baoounao.domain.model.VotingEntity;
import br.app.iftmparacatu.baoounao.domain.repository.VotingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VotingService {
    @Autowired
    private VotingRepository votingRepository;

    public int countByProposalEntity(ProposalEntity proposalEntity){
        return votingRepository.countByProposalEntity(proposalEntity);
    }

    public ResponseEntity<Object> save(VotingDto votingDto){
        VotingEntity newVoting = VotingEntity.builder()
                .proposalEntity(votingDto.proposalEntity())
                .userEntity(votingDto.userEntity())
                .build();
        votingRepository.save(newVoting);
       return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
