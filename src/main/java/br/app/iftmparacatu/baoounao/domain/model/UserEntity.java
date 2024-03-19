package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
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
    @Column
    private Boolean admin;
}
