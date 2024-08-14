package br.app.iftmparacatu.baoounao.domain.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    @EqualsAndHashCode.Include
    private String title;
    @Column
    private Boolean active;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;
    @Column
    String icon;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (active == null) {
            active = true;
        }
    }

    @Override
    public String toString() {
        return title;
    }
}
