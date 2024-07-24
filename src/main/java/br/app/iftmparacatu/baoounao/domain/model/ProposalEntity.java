package br.app.iftmparacatu.baoounao.domain.model;

import br.app.iftmparacatu.baoounao.domain.enums.Situation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@EqualsAndHashCode
public class ProposalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(length = 100)
    private String title;
    @Column
    private String description;
    @Column
    private Integer likes;
    @Column(length = 100)
    @Enumerated(value = EnumType.STRING)
    private Situation situation;
    @Column
    private Boolean active;
    @Column
    private LocalDateTime createdAt;
    @Column(length = 100)
    private String url;
    @Column
    @Lob
    private Blob image;
    @JoinColumn(name = "user_entity_id")
    @ManyToOne
    private UserEntity userEntity;
    @JoinColumn(name = "cycle_entity_id")
    @ManyToOne
    private CycleEntity cycleEntity;
    @OneToMany(mappedBy = "proposalEntity")
    @JsonIgnore
    private List<ProposalCategoryEntity> proposalCategoryEntityList = new ArrayList<>();
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
