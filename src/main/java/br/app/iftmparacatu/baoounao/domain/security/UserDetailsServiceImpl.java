package br.app.iftmparacatu.baoounao.domain.security;

import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import br.app.iftmparacatu.baoounao.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl {
    @Autowired
    private UserRepository userRepository;
    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        UserEntity user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
              return new UserDetailsImpl(user);
    }
}
