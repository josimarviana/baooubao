package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

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

}
