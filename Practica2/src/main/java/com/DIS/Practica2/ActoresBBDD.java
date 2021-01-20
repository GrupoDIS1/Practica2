package com.DIS.Practica2;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActoresBBDD extends JpaRepository<Actores, Long> {
    List<Actores> findByIdPelicula(Long i);
}
