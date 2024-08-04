package br.app.iftmparacatu.baoounao.domain.util;

import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
    public static UserEntity getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof UserEntity) {
            return ((UserEntity) userDetails);
        }
        throw new IllegalStateException("Usuário autenticado é inválido");
    }
}
