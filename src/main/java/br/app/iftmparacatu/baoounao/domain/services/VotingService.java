package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.EntityNotFoundException;
import br.app.iftmparacatu.baoounao.api.exception.VoteNotAllowedException;
import br.app.iftmparacatu.baoounao.domain.dtos.input.VotingDto;
import br.app.iftmparacatu.baoounao.domain.model.*;
import br.app.iftmparacatu.baoounao.domain.repository.VotingRepository;
import br.app.iftmparacatu.baoounao.domain.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public ResponseEntity<Object> save(VotingDto votingDto){
        UserEntity currentUser = SecurityUtil.getAuthenticatedUser();
        CycleEntity currentCicle = cycleService.findProgressCycle().get();
        Long userVotesCurrentCycle = votingRepository.countByUserEntityAndProposalEntityCycleEntity(currentUser,currentCicle);
        Long userVotesCurrentProposal = votingRepository.countByUserEntityAndProposalEntity(currentUser,votingDto.proposalEntity());

        if(userVotesCurrentProposal != 0){
            throw new VoteNotAllowedException("Você já votou nesta proposta. Não é permitido votar mais de uma vez na mesma proposta.");
        }
        if (userVotesCurrentCycle == 3){ //TODO: tavlez fique interessante parametrizar a quantidade de votos por ciclo em uma configuração do sistema
            throw new VoteNotAllowedException("Você atingiu o limite máximo de 3 votos para o ciclo atual. Não é permitido votar em mais do que 3 propostas.");
        }


        VotingEntity newVoting = VotingEntity.builder()
                .proposalEntity(votingDto.proposalEntity())
                .build();
        votingRepository.save(newVoting);
       return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<Object> remove(VotingDto votingDto){
        Optional<VotingEntity> vote = Optional.ofNullable(votingRepository.findFirstByUserEntityAndProposalEntity(SecurityUtil.getAuthenticatedUser(), votingDto.proposalEntity()).orElseThrow(() -> new EntityNotFoundException("Não foram encontrado votos para esta proposta")));
        votingRepository.delete(vote.get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
