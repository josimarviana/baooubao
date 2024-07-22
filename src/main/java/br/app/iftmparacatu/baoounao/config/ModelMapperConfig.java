package br.app.iftmparacatu.baoounao.config;

import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryProposalDto;
import br.app.iftmparacatu.baoounao.domain.model.ProposalCategoryEntity;
import br.app.iftmparacatu.baoounao.domain.model.ProposalEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        // Configuração para mapear 'especie' de Operacao para 'especie' em LancamentoDTO
//        modelMapper.addMappings(new PropertyMap<ProposalEntity, RecoveryProposalDto>() {
//            @Override
//            protected void configure() {
//                map().setCategory(source.getCategoryEntityList());//.setEspecie(source.getMovimentoOperacaoTipoOperacao().getMovimentoOperacao().getOperacao().getEspecie()); //Necessario devido ambiguidade de toStrings ??
//            }
//        });
//
//        modelMapper.addMappings(new PropertyMap<MovimentoOperacaoTipoOperacao, MovimentoOperacaoTipoOperacaoDTO>() {
//            @Override
//            protected void configure() {
//                map().setEspecie(source.getMovimentoOperacao().getOperacao().getEspecie()); //Necessario devido ambiguidade de toStrings ??
//            }
//        });
//
//        modelMapper.addMappings(new PropertyMap<MovimentoOperacao, MovimentoOperacaoDTO>() {
//            @Override
//            protected void configure() {
//                map().setEspecie(source.getOperacao().getEspecie()); //Necessario devido ambiguidade de toStrings ??
//            }
//        });

        return modelMapper;
    }
}
