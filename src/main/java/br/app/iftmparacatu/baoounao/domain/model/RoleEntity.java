package br.app.iftmparacatu.baoounao.domain.model;

import br.app.iftmparacatu.baoounao.domain.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor // gera construtor sem parametros
@AllArgsConstructor // gera construtor com todos os parametros
@Getter

public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

}
