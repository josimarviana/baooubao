package br.app.iftmparacatu.baoounao.domain.model;

import br.app.iftmparacatu.baoounao.domain.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @EqualsAndHashCode.Include
    private RoleName name;

    public RoleEntity(RoleName name){
        this.name = name;
    }
    @Override
    public String toString() {
        return this.name.toString();
    }

}
