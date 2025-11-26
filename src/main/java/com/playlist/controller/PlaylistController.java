package com.playlist.controller;

import com.playlist.model.Playlist;
import com.playlist.model.Usuario;
import com.playlist.repository.MusicaRepository;
import com.playlist.repository.PlaylistRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistRepository playlistRepository;
    private final MusicaRepository musicaRepository;

    public PlaylistController(PlaylistRepository playlistRepository, MusicaRepository musicaRepository) {
        this.playlistRepository = playlistRepository;
        this.musicaRepository = musicaRepository;
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // SELECT ALL - GET
    @GetMapping
    public List<Playlist> listarMinhasPlaylists() {
        Usuario usuario = getUsuarioLogado();
        return playlistRepository.findByUsuarioId(usuario.getId());
    }

    // SELECT BY ID - GET /{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Usuario usuario = getUsuarioLogado();
        return playlistRepository.findById(id)
                .map(p -> {
                    if (!p.getUsuario().getId().equals(usuario.getId())) {
                        return ResponseEntity.status(403).body("Acesso negado");
                    }
                    return ResponseEntity.ok(p);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // INSERT - POST
    @PostMapping
    public ResponseEntity<?> criarPlaylist(@RequestBody Playlist playlist) {

        Usuario usuario = getUsuarioLogado();
        playlist.setUsuario(usuario);
        playlist.setId(null);

        Playlist salva = playlistRepository.save(playlist);

        return ResponseEntity.ok(salva);
    }

    // UPDATE - PUT /{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPlaylist(@PathVariable Long id, @RequestBody Playlist playlistAtualizada) {
        Usuario usuario = getUsuarioLogado();
        return playlistRepository.findById(id)
                .map(playlistExistente -> {
                    if (!playlistExistente.getUsuario().getId().equals(usuario.getId())) {
                        return ResponseEntity.status(403).body("Acesso negado");
                    }
                    playlistExistente.setNome(playlistAtualizada.getNome());
                    playlistExistente.setFoto(playlistAtualizada.getFoto());
                    if (playlistAtualizada.getMusicas() != null) {
                        playlistExistente.setMusicas(playlistAtualizada.getMusicas());
                    }
                    return ResponseEntity.ok(playlistRepository.save(playlistExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - DELETE /{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPlaylist(@PathVariable Long id) {
        Usuario usuario = getUsuarioLogado();
        return playlistRepository.findById(id)
                .map(playlist -> {
                    if (!playlist.getUsuario().getId().equals(usuario.getId())) {
                        return ResponseEntity.status(403).body("Acesso negado");
                    }
                    playlistRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Remover música específica da playlist
    @DeleteMapping("/{playlistId}/musicas/{musicaId}")
    public ResponseEntity<?> removerMusica(@PathVariable Long playlistId, @PathVariable Long musicaId) {
    Usuario usuario = getUsuarioLogado();
    return playlistRepository.findById(playlistId).map(playlist -> {
        if (!playlist.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).body("Acesso negado");
        }

        return musicaRepository.findById(musicaId).map(musica -> {
            playlist.getMusicas().remove(musica);
            playlistRepository.save(playlist);
            return ResponseEntity.ok(playlist);
        }).orElse(ResponseEntity.notFound().build());

    }).orElse(ResponseEntity.notFound().build());
}

    // Adicionar música existente na playlist
    @PostMapping("/{playlistId}/musicas/{musicaId}")
    public ResponseEntity<?> adicionarMusica(@PathVariable Long playlistId, @PathVariable Long musicaId) {
        Usuario usuario = getUsuarioLogado();
        return playlistRepository.findById(playlistId)
                .map(playlist -> {
                    if (!playlist.getUsuario().getId().equals(usuario.getId())) {
                        return ResponseEntity.status(403).body("Acesso negado");
                    }
                    return musicaRepository.findById(musicaId)
                            .map(musica -> {
                                playlist.getMusicas().add(musica);
                                playlistRepository.save(playlist);
                                return ResponseEntity.ok(playlist);
                            })
                            .orElse(ResponseEntity.notFound().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }
}