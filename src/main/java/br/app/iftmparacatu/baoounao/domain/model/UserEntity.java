package br.app.iftmparacatu.baoounao.domain.model;

import br.app.iftmparacatu.baoounao.domain.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true) // inserido unique
    private String email;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserType type;
    @Column(nullable = false)
    private String password;
    @Column
    private Boolean active;
    @Column(nullable = false)
    @NotNull
    private LocalDate createdAt;

    //private Boolean admin;

    // inserido
    @ManyToMany(fetch =  FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name="user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns =  @JoinColumn(name = "role_id"))
    private List<RoleEntity> roles;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = createdAt.now();
        }
    }
}
