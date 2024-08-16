package br.app.iftmparacatu.baoounao.domain.model;

import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import br.app.iftmparacatu.baoounao.domain.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ProposalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(length = 100)
    private String title;
    @Column
    private String description;
    @OneToMany(mappedBy = "proposalEntity")
    @JsonIgnore
    private List<VotingEntity> votes;
    @Column(length = 100)
    @Enumerated(value = EnumType.STRING)
    private Situation situation;
    @Column
    private Boolean active;
    @Column
    private LocalDateTime createdAt;
    @Column(length = 100)
    private String videoUrl;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte [] image;
    @JoinColumn(name = "user_entity_id")
    @ManyToOne
    private UserEntity userEntity;
    @JoinColumn(name = "cycle_entity_id")
    @ManyToOne
    private CycleEntity cycleEntity;
    @JoinColumn(name = "category_entity_id")
    @ManyToOne
    @NotNull
    private CategoryEntity categoryEntity;
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (active == null) {
            active = true;
        }
        if (situation == null) {
            situation = Situation.PENDING_MODERATION;
        }

        if(userEntity == null){
            userEntity = SecurityUtil.getAuthenticatedUser();
        }
    }
}
