package com.playlist.controller;

import com.playlist.model.Musica;
import com.playlist.model.Playlist;
import com.playlist.repository.MusicaRepository;
import com.playlist.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/musicas")
public class MusicaController {

    @Autowired
    private MusicaRepository musicaRepository;
    
    @Autowired
    private PlaylistRepository playlistRepository;

    // INSERT - POST
    @PostMapping
    public Musica criar(@RequestBody Musica musica) {
        return musicaRepository.save(musica);
    }

    // SELECT ALL - GET
    @GetMapping
    public List<Musica> listar() {
        return musicaRepository.findAll();
    }

    // SELECT BY ID - GET /{id} <-- NOVO
    @GetMapping("/{id}")
    public ResponseEntity<Musica> buscarPorId(@PathVariable Long id) {
        return musicaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE - PUT /{id} <-- NOVO
    @PutMapping("/{id}")
    public ResponseEntity<Musica> atualizar(@PathVariable Long id, @RequestBody Musica musicaNova) {
        return musicaRepository.findById(id)
                .map(musica -> {
                    musica.setTitulo(musicaNova.getTitulo());
                    musica.setArtista(musicaNova.getArtista());
                    // Atualiza a duração se ela vier via JSON
                    if (musicaNova.getDuracaoEmSegundos() > 0) {
                        musica.setDuracaoEmSegundos(musicaNova.getDuracaoEmSegundos());
                    }
                    return ResponseEntity.ok(musicaRepository.save(musica));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - DELETE /{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return musicaRepository.findById(id).map(musica -> {
            // Remove a música das playlists antes de apagar
            for (Playlist playlist : musica.getPlaylists()) {
                playlist.getMusicas().remove(musica);
                playlistRepository.save(playlist);
            }
            musicaRepository.delete(musica);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}