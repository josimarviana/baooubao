package br.app.iftmparacatu.baoounao.domain.services;


import br.app.iftmparacatu.baoounao.api.exception.*;
import br.app.iftmparacatu.baoounao.config.SecurityConfig;
import br.app.iftmparacatu.baoounao.domain.dtos.input.CreateUserDto;
import br.app.iftmparacatu.baoounao.domain.dtos.input.LoginUserDto;
import br.app.iftmparacatu.baoounao.domain.dtos.input.UpdateUserDto;
import br.app.iftmparacatu.baoounao.domain.dtos.output.*;
import br.app.iftmparacatu.baoounao.domain.enums.RoleName;
import br.app.iftmparacatu.baoounao.domain.enums.UserType;
import br.app.iftmparacatu.baoounao.domain.model.ConfirmationTokenEntity;
import br.app.iftmparacatu.baoounao.domain.model.RoleEntity;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import br.app.iftmparacatu.baoounao.domain.repository.RoleRepository;
import br.app.iftmparacatu.baoounao.domain.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Value("${url.email.redirect.authenticated}")
    private String urlAuthenticated;
    @Value("${url.email.redirect.expired}")
    private String urlExpired;
    @Value("${url.valid.token.trocar.senha}")
    private String urlValidTokenForTrocaSenha;

    @Value("${url.email}")
    private String urlConfirmationEmail;
    @Value("${url.email.senha}")
    private String urlTrocarSenha;


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

    @Autowired
    private ModelMapper modelMapper;

    // Método responsável por autenticar um usuário e retornar um token JWT
    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto) {
        // Cria um objeto de autenticação com o email e a senha do usuário
        // Autentica o usuário com as credenciais fornecidas
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUserDto.email(), loginUserDto.password()));

            // Obtém o objeto UserDetails do usuário autenticado
            UserEntity userDetails = (UserEntity) authentication.getPrincipal();

            if (!userDetails.isActive()) {
                throw new InactiveUserException("Não foi possível realizar o login, pois este usuário está inativo");
            }
            // Gera um token JWT para o usuário autenticado
            return new RecoveryJwtTokenDto(jwtTokenService.generateToken(userDetails));

        } catch (BadCredentialsException e) {
            throw new InvalidLoginException("Email ou senha incorretos");

        } catch (InternalAuthenticationServiceException e) {
            throw new EntityNotFoundException("Email ou senha incorretos");
        }
    }

    public void createUser(CreateUserDto createUserDto) {
        try {
        if (isValidDomainAndType(createUserDto)) {
            UserEntity newUser = UserEntity.builder()
                    .email(createUserDto.email())
                    .name(createUserDto.name())
                    .type(createUserDto.type())
                    .password(securityConfiguration.passwordEncoder().encode(createUserDto.password()))
                    .roles(List.of(roleRepository.findByName(RoleName.ROLE_USER)))
                    .build();
            userRepository.save(newUser);

                emailService.enviarEmailDeConfirmacao(createUserDto.email(), createUserDto.name(), confirmationTokenService.salvar(newUser,urlConfirmationEmail));

        } else throw new InvalidDomainException("Domínio não é válido");
        } catch (MessagingException e) {
            throw new EmailSendingException("Erro ao enviar e-mail de confirmação");
        }catch (DataIntegrityViolationException e) {
            throw new NotAllowedOperation("Email ja cadastrado!");
        }
    }

    public ResponseEntity<Object> validateUser(String token) {
        return confirmationTokenService.validation(token)
                .map(t -> {
                    UserEntity user = t.getUser();
                    user.setActive(true);
                    userRepository.save(user);
                    confirmationTokenService.delete(t);
                    URI redirectUri = URI.create(urlAuthenticated);
                    return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
                }).orElseGet(() -> {
                    confirmationTokenService.findByToken(token)
                            .ifPresent(confirmationTokenService::delete);
                    URI redirectUriExpired = URI.create(urlExpired);
                    return ResponseEntity.status(HttpStatus.FOUND).location(redirectUriExpired).build();

                });
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

    private RecoveryUserDto mapToDto(UserEntity userEntity) {
        RecoveryUserDto recoveryUserDto = RecoveryUserDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .type(userEntity.getType().toString())
                .active(userEntity.isActive())
                .roles(userEntity.getRoles().stream()
                        .map(RoleEntity::toString)
                        .collect(Collectors.toList()))
                .createdAt(userEntity.getCreatedAt())
                //.roles(userEntity.getRoles())
                .createdAt(userEntity.getCreatedAt())
                .build();
        return recoveryUserDto;
    }

    public ResponseEntity<PaginatedUsersResponse> findAll(String text, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> userEntityPageable = userRepository.findByNameContainingOrEmailContaining(text, text, pageable);

        List<RecoveryUserDto> recoveryUsersDtoList = userEntityPageable.stream()
                .map(user -> mapToDto(user))
                .collect(Collectors.toList());

        PaginatedUsersResponse response = PaginatedUsersResponse.builder()
                .users(recoveryUsersDtoList)
                .totalElements(userEntityPageable.getTotalElements())
                .totalPages(userEntityPageable.getTotalPages())
                .currentPage(userEntityPageable.getNumber())
                .build();
        response.sortUsers(sort);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<Object> updateUser(Long userId, UpdateUserDto updateUserDto) {
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado!"));

        Optional.ofNullable(updateUserDto.name())
                .ifPresent(existingUser::setName);
        Optional.ofNullable(updateUserDto.email())
                .ifPresent(existingUser::setEmail);
        Optional.ofNullable(updateUserDto.password())
                .ifPresent(password -> existingUser.setPassword(securityConfiguration.passwordEncoder().encode(password)));
        Optional.ofNullable(updateUserDto.type())
                .ifPresent(existingUser::setType);
        try {

            Optional.ofNullable(updateUserDto.roles()).ifPresent(roles -> {
                List<RoleEntity> roleEntities = roles.stream()
                        .map(roleName -> roleRepository.findByName(roleName.getName()))
                        .collect(Collectors.toList());
                existingUser.setRoles(roleEntities);
            });
        }catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Role não encontrada");
        }
        userRepository.save(existingUser);
        return ResponseEntity.ok("Usuário atualizado com sucesso!");
    }

    public ResponseEntity<String> validateEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .map(user -> {
                    if (user.isEnabled()) {
                        try {
                            emailService.enviarEmailTrocaDeSenha(
                                    ((UserEntity) user).getEmail(),
                                    ((UserEntity) user).getName(),
                                    confirmationTokenService.salvar((UserEntity) user, urlValidTokenForTrocaSenha)
                            );
                            return ResponseEntity.status(HttpStatus.OK).body("Email para trocar de senha enviado!");
                        } catch (MessagingException e) {
                            throw new EmailSendingException("Erro ao enviar e-mail de troca de senha");
                        }
                    } else {
                        throw new EntityNotFoundException("Usuário não está habilitado");
                    }
                })
                .orElseThrow(() -> new EntityNotFoundException("Email não encontrado"));
    }


    public ResponseEntity<Object> validateToken(String token) {
        return confirmationTokenService.validation(token)
                .map(t -> {
                    String url = urlTrocarSenha.replace("{token}", token);
                    URI redirectUri = URI.create(url);
                    return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
                })
                .orElseGet(() -> {
                    confirmationTokenService.findByToken(token).ifPresent(confirmationTokenService::delete);
                    URI redirectUriExpired = URI.create(urlExpired);
                    return ResponseEntity.status(HttpStatus.FOUND).location(redirectUriExpired).build();
                });
    }

    public ResponseEntity<Object> trocarSenha(String token,UpdateUserDto dto) {
        Optional<ConfirmationTokenEntity> optionalToken = confirmationTokenService.validation(token);
        if (optionalToken.isEmpty()) {
            throw new EntityNotFoundException("Token inválido ou expirado");
        }
        if (dto.password().equals(dto.confirmPassword())) {
            optionalToken.get().getUser().setPassword(securityConfiguration.passwordEncoder().encode(dto.password()));
            confirmationTokenService.delete(optionalToken.get());
            return ResponseEntity.ok("As senhas foram trocadas");
        }else throw new RuntimeException("As senhas não batem!");
    }



}