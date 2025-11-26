package com.playlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.playlist.model.Musica;
@Repository
public interface MusicaRepository extends JpaRepository<Musica, Long> {
    List<Musica> findByTituloContainingIgnoreCase(String titulo);
}
