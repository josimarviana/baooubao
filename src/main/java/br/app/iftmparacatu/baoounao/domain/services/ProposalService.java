package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.api.exception.ProposalException;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryProposalDto;
import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import br.app.iftmparacatu.baoounao.domain.repository.ProposalRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProposalService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProposalRepository proposalRepository;

    public RecoveryProposalDto mapToDto(ProposalEntity proposalEntity) {
        return modelMapper.map(proposalEntity, RecoveryProposalDto.class);
    }

    public List<RecoveryProposalDto> findAll(){
        List<ProposalEntity> proposals = proposalRepository.findAll();
        return proposals.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    public RecoveryProposalDto update(Long proposalID, ProposalEntity proposalEntity) {
        if (proposalEntity.getSituation().equals(Situation.OPEN_FOR_VOTING)){
            throw new ProposalException("Não é possível atualizar propostas em votação");
        } else {
            ProposalEntity existingProposal = proposalRepository.findById(proposalID)
                    .orElseThrow(() -> new ProposalException("Proposta não encontrada com o ID: " + proposalID));

            BeanUtils.copyProperties(proposalEntity, existingProposal, "id");
            proposalRepository.save(existingProposal); // Certifique-se de salvar a entidade atualizada

            return this.mapToDto(existingProposal);
        }
    }
}
