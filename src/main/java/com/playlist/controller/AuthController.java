package com.playlist.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playlist.dto.LoginRequest;
import com.playlist.model.Usuario;
import com.playlist.security.JwtUtil;
import com.playlist.service.UsuarioService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {

        Usuario novo = usuarioService.cadastrar(usuario);
        return ResponseEntity.ok(novo);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        Usuario usuario = usuarioService.buscarPorLogin(loginRequest.getLogin());
        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário não encontrado"));
        }

        boolean senhaValida = usuarioService.validarSenha(loginRequest.getSenha(), usuario.getSenha());
        if (!senhaValida) {
            return ResponseEntity.status(401).body(Map.of("erro", "Senha inválida"));
        }

        String token = jwtUtil.gerarToken(usuario.getLogin());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "login", usuario.getLogin(),
                "nome", usuario.getNome()));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validarToken() {
        return ResponseEntity.ok("Token válido");
    }

}
