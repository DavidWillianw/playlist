package com.playlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.playlist.model.*;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUsuarioId(Long usuarioId);
}