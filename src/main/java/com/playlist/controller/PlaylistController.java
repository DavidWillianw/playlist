package com.playlist.controller;

import com.playlist.model.Playlist;
import com.playlist.repository.MusicaRepository;
import com.playlist.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistRepository playlistRepository;
    
    @Autowired
    private MusicaRepository musicaRepository;

    // SELECT ALL - GET
    @GetMapping
    public List<Playlist> listarTodas() {
        return playlistRepository.findAll();
    }
    
    // SELECT BY ID - GET /{id}
    @GetMapping("/{id}")
    public ResponseEntity<Playlist> buscarPorId(@PathVariable Long id) {
        return playlistRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // INSERT - POST
    @PostMapping
    public Playlist criarPlaylist(@RequestBody Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    // UPDATE - PUT /{id}
    @PutMapping("/{id}")
    public ResponseEntity<Playlist> atualizarPlaylist(@PathVariable Long id, @RequestBody Playlist playlistAtualizada) {
        return playlistRepository.findById(id)
                .map(playlistExistente -> {
                    playlistExistente.setNome(playlistAtualizada.getNome());
                    playlistExistente.setFoto(playlistAtualizada.getFoto());
                    // Se quiser atualizar a ordem das músicas via JSON completo
                    if(playlistAtualizada.getMusicas() != null) {
                        playlistExistente.setMusicas(playlistAtualizada.getMusicas());
                    }
                    Playlist salvo = playlistRepository.save(playlistExistente);
                    return ResponseEntity.ok(salvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // DELETE - DELETE /{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPlaylist(@PathVariable Long id) {
        if (playlistRepository.existsById(id)) {
            playlistRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    // Remover música específica da playlist
    @DeleteMapping("/{playlistId}/musicas/{musicaId}")
    public ResponseEntity<Playlist> removerMusica(@PathVariable Long playlistId, @PathVariable Long musicaId) {
        return playlistRepository.findById(playlistId)
                .map(playlist -> {
                    playlist.getMusicas().removeIf(musica -> musica.getId().equals(musicaId));
                    playlistRepository.save(playlist);
                    return ResponseEntity.ok(playlist);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Adicionar música existente na playlist
    @PostMapping("/{playlistId}/musicas/{musicaId}")
    public ResponseEntity<Playlist> adicionarMusica(@PathVariable Long playlistId, @PathVariable Long musicaId) {
        return playlistRepository.findById(playlistId)
                .map(playlist -> {
                    return musicaRepository.findById(musicaId)
                            .map(musica -> {
                                playlist.getMusicas().add(musica);
                                return ResponseEntity.ok(playlistRepository.save(playlist));
                            })
                            .orElse(ResponseEntity.notFound().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }
}