package com.playlist.controller;

import com.playlist.model.Musica;
import com.playlist.model.Playlist;
import com.playlist.model.Usuario;
import com.playlist.repository.MusicaRepository;
import com.playlist.repository.PlaylistRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    // Listar playlists
    @GetMapping
    public List<Playlist> listarMinhasPlaylists() {
        Usuario usuario = getUsuarioLogado();
        return playlistRepository.findByUsuarioId(usuario.getId());
    }

    // Buscar playlist por ID 
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Usuario usuario = getUsuarioLogado();
        return playlistRepository.findById(id)
                .map(p -> {
                    if (!p.getUsuario().getId().equals(usuario.getId())) {
                        return ResponseEntity.status(403).body(Map.of("erro", "Acesso negado"));
                    }
                    return ResponseEntity.ok(p);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Criar nova Playlist
    @PostMapping
    public ResponseEntity<?> criarPlaylist(@RequestBody Playlist playlist) {
        Usuario usuario = getUsuarioLogado();
        playlist.setUsuario(usuario);
        return ResponseEntity.ok(playlistRepository.save(playlist));
    }

    // Atualizar Playlist
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPlaylist(@PathVariable Long id, @RequestBody Playlist playlistAtualizada) {
        Usuario usuario = getUsuarioLogado();
        return playlistRepository.findById(id)
                .map(playlistExistente -> {
                    if (!playlistExistente.getUsuario().getId().equals(usuario.getId())) {
                        return ResponseEntity.status(403).body(Map.of("erro", "Acesso negado"));
                    }
                    playlistExistente.setNome(playlistAtualizada.getNome());
                    playlistExistente.setFoto(playlistAtualizada.getFoto());
                    return ResponseEntity.ok(playlistRepository.save(playlistExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Deletar Playlist
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPlaylist(@PathVariable Long id) {
        Usuario usuario = getUsuarioLogado();
        return playlistRepository.findById(id)
                .map(playlist -> {
                    if (!playlist.getUsuario().getId().equals(usuario.getId())) {
                        return ResponseEntity.status(403).body(Map.of("erro", "Acesso negado"));
                    }
                    playlistRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Remover música da playlist
    @DeleteMapping("/{playlistId}/musicas/{musicaId}")
    public ResponseEntity<?> removerMusica(@PathVariable Long playlistId, @PathVariable Long musicaId) {
        Usuario usuario = getUsuarioLogado();
        return playlistRepository.findById(playlistId).map(playlist -> {
            if (!playlist.getUsuario().getId().equals(usuario.getId())) {
                return ResponseEntity.status(403).body(Map.of("erro", "Acesso negado"));
            }
            return musicaRepository.findById(musicaId).map(musica -> {
                playlist.getMusicas().remove(musica);
                playlistRepository.save(playlist);
                return ResponseEntity.ok(playlist);
            }).orElse(ResponseEntity.notFound().build());
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{playlistId}/musicas")
    public ResponseEntity<?> adicionarMusicaNaPlaylist(
            @PathVariable Long playlistId, 
            @RequestBody Map<String, String> dados) {
        
        Usuario usuario = getUsuarioLogado();

        return playlistRepository.findById(playlistId).map(playlist -> {
            if (!playlist.getUsuario().getId().equals(usuario.getId())) {
                return ResponseEntity.status(403).body(Map.of("erro", "Acesso negado"));
            }

            String titulo = dados.get("titulo");
            String artista = dados.get("artista");
            String duracaoTexto = dados.get("duracao");

            if (titulo == null || titulo.trim().isEmpty() || 
                artista == null || artista.trim().isEmpty() ||
                duracaoTexto == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Título, artista e duração são obrigatórios."));
            }

            Musica musica = new Musica();
            musica.setTitulo(titulo);
            musica.setArtista(artista);

            try {
                musica.setDuracao(duracaoTexto); 
                
                if (musica.getDuracaoEmSegundos() <= 0) {
                    throw new IllegalArgumentException("Duração deve ser maior que zero.");
                }
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Formato inválido. Use MM:SS."));
            }

            Musica novaMusica = musicaRepository.save(musica);
            playlist.getMusicas().add(novaMusica);
            playlistRepository.save(playlist);

            return ResponseEntity.ok(novaMusica);

        }).orElse(ResponseEntity.notFound().build());
    }
}