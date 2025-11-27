package com.playlist.controller;

import com.playlist.model.Musica;
import com.playlist.model.Playlist;
import com.playlist.repository.MusicaRepository;
import com.playlist.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/musicas")
public class MusicaController {

    @Autowired
    private MusicaRepository musicaRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    // Listar todas
    @GetMapping
    public List<Musica> listar() {
        return musicaRepository.findAll();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Musica> buscarPorId(@PathVariable Long id) {
        return musicaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar por Nome
    @GetMapping("/buscar")
    public List<Musica> buscarPorNome(@RequestParam String nome) {
        return musicaRepository.findByTituloContainingIgnoreCase(nome);
    }

    // Atualizar 
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> dados) {
        return musicaRepository.findById(id)
                .map(musica -> {
                    if (dados.containsKey("titulo")) musica.setTitulo((String) dados.get("titulo"));
                    if (dados.containsKey("artista")) musica.setArtista((String) dados.get("artista"));
                    
                    if (dados.containsKey("duracao")) {
                        try {
                            String novaDuracao = (String) dados.get("duracao");
                            musica.setDuracao(novaDuracao); 
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException(e.getMessage());
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Formato de duração inválido.");
                        }
                    }

                    return ResponseEntity.ok(musicaRepository.save(musica));
                })
                .orElse(ResponseEntity.notFound().build());                
    }

    // Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return musicaRepository.findById(id).map(musica -> {
            for (Playlist playlist : musica.getPlaylists()) {
                playlist.getMusicas().remove(musica);
                playlistRepository.save(playlist);
            }
            musicaRepository.delete(musica);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}