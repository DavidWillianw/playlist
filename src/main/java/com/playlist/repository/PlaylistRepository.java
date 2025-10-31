package com.playlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.playlist.model.*;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}