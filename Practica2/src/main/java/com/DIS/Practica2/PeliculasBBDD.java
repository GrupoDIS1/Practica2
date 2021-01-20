package com.DIS.Practica2;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeliculasBBDD extends JpaRepository<Peliculas, Long> {
    List<Peliculas> findByTituloStartsWithIgnoreCase(String lastName);
}
