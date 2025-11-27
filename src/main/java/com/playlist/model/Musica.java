package com.playlist.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "musicas")
public class Musica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String artista;

    private int duracaoEmSegundos;
    
    @ManyToMany(mappedBy = "musicas")
    @JsonIgnoreProperties("musicas") 
    private Set<Playlist> playlists;
    public void setDuracao(String tempoFormatado) {
        if (tempoFormatado != null && tempoFormatado.contains(":")) {
            String[] partes = tempoFormatado.split(":");
            int minutos = Integer.parseInt(partes[0]);
            int segundos = Integer.parseInt(partes[1]);

            if(segundos >59){
                throw new IllegalArgumentException("Os segundos não podem ser maiores que 59 segundos");
            }
            this.duracaoEmSegundos = (minutos * 60) + segundos;

        } else {
            // Se vier só um número vai entender como minutos tipo um 5 seriam 5 minutos teria que ver se seria necessário
            // adicionar a diferença de minutos pra segundos
            try {
                this.duracaoEmSegundos = Integer.parseInt(tempoFormatado) * 60;
            } catch (NumberFormatException e) {
                this.duracaoEmSegundos = 0;
            }
        }
    }

    // retorna "duracao": "xx:xx"
    public String getDuracao() {
        int minutos = this.duracaoEmSegundos / 60;
        int segundos = this.duracaoEmSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }
}