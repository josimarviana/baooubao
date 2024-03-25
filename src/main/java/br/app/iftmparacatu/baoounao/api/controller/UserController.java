package br.app.iftmparacatu.baoounao.api.controller;

import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import br.app.iftmparacatu.baoounao.domain.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping
    public List<UserEntity> list(){
        return userRepository.findAll();
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserEntity save(@RequestBody @Valid UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

}