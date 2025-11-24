package com.playlist.model;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import jakarta.persistence.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Entity 
@Table(name = "usuarios") 
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome; 

    @Column(nullable = false, unique = true)
    private String login; 

    @Column(nullable = false)
    private String senha; 

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("usuario")
    private Set<Playlist> playlists;
}