package com.playlist.controller;

import com.playlist.model.Usuario;
import com.playlist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // INSERT - POST
    @PostMapping
    public Usuario criarUsuario(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // SELECT ALL - GET
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // SELECT BY ID - GET /{id}  <-- NOVO
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE - PUT /{id} <-- NOVO
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNome(usuarioAtualizado.getNome());
                    usuarioExistente.setLogin(usuarioAtualizado.getLogin());
                    // Se a senha vier preenchida, atualiza (futuramente criptografar aqui)
                    if(usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()){
                        usuarioExistente.setSenha(usuarioAtualizado.getSenha());
                    }
                    Usuario salvo = usuarioRepository.save(usuarioExistente);
                    return ResponseEntity.ok(salvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

@DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new java.util.NoSuchElementException("Usuário não encontrado para exclusão.");
        }

        usuarioRepository.deleteById(id);

        return ResponseEntity.ok()
                .body(java.util.Map.of("mensagem", "Usuário deletado com sucesso"));
    }
}