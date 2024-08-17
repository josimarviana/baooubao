package br.app.iftmparacatu.baoounao.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Senha fraca. A senha deve ter no mínimo 8 caracteres, incluindo uma letra maiúscula, uma letra minúscula, um número e um caractere especial.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
