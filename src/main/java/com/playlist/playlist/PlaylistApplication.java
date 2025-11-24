package com.playlist.playlist;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.playlist")
@EnableJpaRepositories("com.playlist.repository") 
@EntityScan("com.playlist.model")                 
public class PlaylistApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaylistApplication.class, args);
    }
}