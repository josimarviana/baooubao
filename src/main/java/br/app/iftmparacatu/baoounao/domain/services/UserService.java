package br.app.iftmparacatu.baoounao.domain.services;


import br.app.iftmparacatu.baoounao.api.exception.EmailSendingException;
import br.app.iftmparacatu.baoounao.api.exception.InactiveUserException;
import br.app.iftmparacatu.baoounao.api.exception.InvalidDomainException;
import br.app.iftmparacatu.baoounao.config.SecurityConfig;
import br.app.iftmparacatu.baoounao.domain.dtos.input.CreateUserDto;
import br.app.iftmparacatu.baoounao.domain.dtos.input.LoginUserDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryJwtTokenDto;
import br.app.iftmparacatu.baoounao.domain.enums.RoleName;
import br.app.iftmparacatu.baoounao.domain.enums.UserType;
import br.app.iftmparacatu.baoounao.domain.model.RoleEntity;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import br.app.iftmparacatu.baoounao.domain.repository.RoleRepository;
import br.app.iftmparacatu.baoounao.domain.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Value("${url.email.redirect}")
    private String url_redirect;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SecurityConfig securityConfiguration;

    @Autowired
    EmailService emailService;

    @Autowired
    ConfirmTokenService confirmationTokenService;

    // Método responsável por autenticar um usuário e retornar um token JWT
    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto) {
        // Cria um objeto de autenticação com o email e a senha do usuári
        // Autentica o usuário com as credenciais fornecidas
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUserDto.email(),loginUserDto.password()));

        // Obtém o objeto UserDetails do usuário autenticado
        UserEntity userDetails = (UserEntity) authentication.getPrincipal();

        if(!userDetails.isActive()){
            throw new InactiveUserException("Não foi possível realizar o login, pois este usuário está inativo");
        }

        // Gera um token JWT para o usuário autenticado
        return new RecoveryJwtTokenDto(jwtTokenService.generateToken(userDetails));
    }

    public void createUser(CreateUserDto createUserDto) {

        if (isValidDomainAndType(createUserDto)) {
            UserEntity newUser = UserEntity.builder()
                .email(createUserDto.email())
                .name(createUserDto.name())
                .type(createUserDto.type())
                .password(securityConfiguration.passwordEncoder().encode(createUserDto.password()))
                .roles(List.of(roleRepository.findByName(RoleName.ROLE_USER)))
                .build();
                   userRepository.save(newUser);
                try {
                    emailService.enviarEmailDeConfirmacao(createUserDto.email(), createUserDto.name(),confirmationTokenService.salvar(newUser));
                } catch (MessagingException e) {
                    throw  new EmailSendingException("Erro ao enviar e-mail de confirmação");
                }
        }else  throw new InvalidDomainException("Domínio não é válido");

    }

    public ResponseEntity<Object> validateUser(String token) {
        return confirmationTokenService.validation(token)
                .map(t -> {
                    UserEntity user = t.getUser();
                    user.setActive(true);
                    userRepository.save(user);
                    confirmationTokenService.delete(t);
                    URI redirectUri = URI.create(url_redirect);
                    return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Token inválido ou expirado"));
    }

    private boolean isValidDomainAndType(CreateUserDto createUserDto) {
        String email = createUserDto.email();
        String domain = email.substring(email.lastIndexOf("@") + 1);
        UserType userType = createUserDto.type();

        Map<String, List<UserType>> domainToUserTypes = Map.of(
                "iftm.edu.br", List.of(UserType.DOCENTE, UserType.TAE),
                "estudante.iftm.edu.br", List.of(UserType.ESTUDANTE)
        );
        return domainToUserTypes.getOrDefault(domain, List.of()).contains(userType);
    }


}