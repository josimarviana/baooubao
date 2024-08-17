package br.app.iftmparacatu.baoounao.domain.dtos.output;

import br.app.iftmparacatu.baoounao.domain.enums.RoleName;
import br.app.iftmparacatu.baoounao.domain.model.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Comparator;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedUsersResponse {
    private List<RecoveryUserDto> users;
    private long totalElements;
    private int totalPages;
    private int currentPage;

//    public void sortUsers(String sort) {
//        switch (sort.toLowerCase()) {
//            case "docente":
//            case "tae":
//            case "estudante":
//                users.sort(Comparator.comparing(RecoveryUserDto::type));
//                break;
//            default:
//                users.sort(Comparator.comparing(RecoveryUserDto::createdAt).reversed());
//                break;
//        }
//    }

    public void sortUsers(String sort) {
        if (users == null || users.isEmpty()) {
            return;
        }

        users.sort((u1, u2) -> {
            int comparison;

            // Ordenação pelo papel especificado na URL
            if (sort.toLowerCase().startsWith("role_")) {
                boolean u1HasRole = u1.roles().contains(sort.toUpperCase());
                boolean u2HasRole = u2.roles().contains(sort.toUpperCase());
                comparison = Boolean.compare(u2HasRole, u1HasRole); // Papéis que contêm o role especificado vêm primeiro
            } else {
                // Ordenação por tipo, se não for um papel específico
                if (sort.equalsIgnoreCase(u1.type()) && !sort.equalsIgnoreCase(u2.type())) {
                    comparison = -1;
                } else if (!sort.equalsIgnoreCase(u1.type()) && sort.equalsIgnoreCase(u2.type())) {
                    comparison = 1;
                } else {
                    comparison = u1.type().compareTo(u2.type());
                }
            }

            // Se a comparação for igual, ordena por data de criação como fallback
            if (comparison == 0) {
                comparison = u1.createdAt().compareTo(u2.createdAt());
            }

            return comparison;
        });
    }
}