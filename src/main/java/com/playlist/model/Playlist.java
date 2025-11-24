package com.playlist.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "foto_url")
    private String foto;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) 
    @JsonIgnoreProperties("playlists")
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
        name = "playlist_musicas", 
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "musica_id") 
    )
    @JsonIgnoreProperties("playlists") 
    private List<Musica> musicas = new ArrayList<>();

    public String getDuracaoTotal() {
        if (musicas == null || musicas.isEmpty()) {
            return "00:00";
        }
        int totalSegundos = 0;
        for (Musica musica : musicas) {
            totalSegundos += musica.getDuracaoEmSegundos();
        }
        int minutos = totalSegundos / 60;
        int segundosRestantes = totalSegundos % 60;
        return String.format("%02d:%02d", minutos, segundosRestantes);
    }
}