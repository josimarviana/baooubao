package br.app.iftmparacatu.baoounao.config;

import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryProposalDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryBasicProposalDto;
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

        modelMapper.addMappings(new PropertyMap<ProposalEntity, RecoveryProposalDto>() {
            @Override
            protected void configure() {
                map().setAuthor(source.getUserEntity().getName());
            }
        });

        modelMapper.addMappings(new PropertyMap<ProposalEntity, RecoveryProposalDto>() {
            @Override
            protected void configure() {
                map().setImage(source.getImage());
            }
        });

        modelMapper.addMappings(new PropertyMap<ProposalEntity, RecoveryBasicProposalDto>() {
            @Override
            protected void configure() {
                map().setIcon(source.getCategoryEntity().getIcon());
            }
        });


        return modelMapper;
    }
}
