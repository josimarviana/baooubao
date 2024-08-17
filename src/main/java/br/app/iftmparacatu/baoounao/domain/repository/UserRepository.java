package br.app.iftmparacatu.baoounao.domain.repository;

import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    UserDetails findByEmail(String email);
    Page<UserEntity> findByNameContainingOrEmailContaining(String text1,String text2,Pageable pageable);
}
