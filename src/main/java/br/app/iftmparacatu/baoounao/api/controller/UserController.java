package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.dtos.input.*;
import br.app.iftmparacatu.baoounao.domain.dtos.output.PaginatedUsersResponse;
import br.app.iftmparacatu.baoounao.domain.dtos.output.RecoveryJwtTokenDto;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import br.app.iftmparacatu.baoounao.domain.repository.UserRepository;
import br.app.iftmparacatu.baoounao.domain.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/filter")
    public ResponseEntity<PaginatedUsersResponse> list(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "9") int size, @RequestParam(value = "contain", required = false) String text, @RequestParam(value = "sort", defaultValue = "recent") String sort ){
        return userService.findAll(text,page,size,sort);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        UserEntity user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody LoginUserDto loginUserDto) {
        RecoveryJwtTokenDto token = userService.authenticateUser(loginUserDto);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid CreateUserDto createUserDto) {
        userService.createUser(createUserDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<Object> validationUser(@PathVariable String token) {
        return userService.validateUser(token);
    }

    @PatchMapping("/{userID}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userID, @RequestBody SimpleUpdateUserDto simpleUpdateUserDto) {
        return userService.updateUser(userID,simpleUpdateUserDto);

    }

    @GetMapping("/email/{email}")
    public ResponseEntity<String> receberEmail(@PathVariable String email) {
        return  userService.validateEmail(email);

    }

    @GetMapping("/validation/{token}")
    public ResponseEntity<Object> validarTokenTrocaSenha(@PathVariable String token) {
        return  userService.validateToken(token);

    }

    @PatchMapping("/email/senha/{token}")
    public ResponseEntity<Object> receberSenha(@PathVariable String token, @RequestBody  @Valid UpdateUserDto updateUserDto) {
        return  userService.trocarSenha(token,updateUserDto);
    }

    @PatchMapping("/role/revoke-adm/{userID}")
    public ResponseEntity<Object> revokeAdministrator(@PathVariable Long userID,@RequestBody @Valid RevokeRoleDto revokeRoleDto) {
        return userService.revokeAdministrator(userID,revokeRoleDto);
    }

    @DeleteMapping("/{userID}")
    public ResponseEntity<Object> delete(@PathVariable Long userID) {
        return userService.delete(userID);
    }
}




