package com.playlist.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleValidationErro(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("erro", e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNotFound(NoSuchElementException e) {
        String mensagem = (e.getMessage() != null) ? e.getMessage() : "Registro não encontrado.";
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", mensagem));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDatabaseError(DataIntegrityViolationException e) {
        String mensagem = "Erro de integridade de dados.";
        
        String causaRaiz = e.getMostSpecificCause().getMessage();

        if (causaRaiz != null) {
            if (causaRaiz.contains("Duplicate entry")) {
                mensagem = "Já existe um registro com este dado (ex: login ou e-mail duplicado).";
            } else if (causaRaiz.contains("foreign key")) {
                mensagem = "Não é possível excluir este registro pois ele está sendo usado em outra parte do sistema (ex: Playlist com músicas).";
            }
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT) 
                .body(Map.of("erro", mensagem));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneral(Exception e) {
        e.printStackTrace(); // Imprime o erro no console do Java
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", "Ocorreu um erro interno no servidor. Tente novamente mais tarde."));
    }
}