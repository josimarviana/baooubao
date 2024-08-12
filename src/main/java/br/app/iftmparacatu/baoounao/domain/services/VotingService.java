package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.api.exception.NotAllowedOperation;
import br.app.iftmparacatu.baoounao.domain.dtos.input.VotingDto;
import br.app.iftmparacatu.baoounao.domain.model.*;
import br.app.iftmparacatu.baoounao.domain.repository.VotingRepository;
import br.app.iftmparacatu.baoounao.domain.util.ResponseUtil;
import br.app.iftmparacatu.baoounao.domain.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VotingService {
    @Autowired
    private VotingRepository votingRepository;

    @Autowired
    private CycleService cycleService;

    public int countByProposalEntity(ProposalEntity proposalEntity){
        return votingRepository.countByProposalEntity(proposalEntity);
    }

    public boolean hasVoted(UserEntity userEntity, ProposalEntity proposalEntity){
        return votingRepository.countByUserEntityAndProposalEntity(userEntity,proposalEntity) > 0;
    }

    public ResponseEntity<Object> save(VotingDto votingDto){
        UserEntity currentUser = SecurityUtil.getAuthenticatedUser();
        CycleEntity currentCicle = cycleService.findProgressCycle().get();
        Long userVotesCurrentCycle = votingRepository.countByUserEntityAndProposalEntityCycleEntity(currentUser,currentCicle);
        Long userVotesCurrentProposal = votingRepository.countByUserEntityAndProposalEntity(currentUser,votingDto.proposalEntity());

        if(userVotesCurrentProposal != 0){
            throw new NotAllowedOperation("Você já votou nesta proposta. Não é permitido votar mais de uma vez na mesma proposta.");
        }
        if (userVotesCurrentCycle == 3){ //TODO: tavlez fique interessante parametrizar a quantidade de votos por ciclo em uma configuração do sistema
            throw new NotAllowedOperation("Você atingiu o limite máximo de 3 votos para o ciclo atual. Não é permitido votar em mais do que 3 propostas.");
        }


        VotingEntity newVoting = VotingEntity.builder()
                .proposalEntity(votingDto.proposalEntity())
                .build();
        votingRepository.save(newVoting);
        return ResponseUtil.createSuccessResponse("Voto registrado com sucesso!",HttpStatus.CREATED);
    }

    public ResponseEntity<Object> remove(VotingDto votingDto){
        Optional<VotingEntity> vote = Optional.ofNullable(votingRepository.findFirstByUserEntityAndProposalEntity(SecurityUtil.getAuthenticatedUser(), votingDto.proposalEntity()).orElseThrow(() -> new EntityNotFoundException("Não foram encontrado votos para esta proposta")));
        votingRepository.delete(vote.get());
        return ResponseUtil.createSuccessResponse("Voto cancelado com sucesso!",HttpStatus.NO_CONTENT);
    }

    public Long countByCycleEntity(CycleEntity cycleEntity){
        return  votingRepository.countByProposalEntityCycleEntity(cycleEntity);
    }

}
