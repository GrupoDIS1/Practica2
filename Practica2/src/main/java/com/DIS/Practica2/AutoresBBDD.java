package com.DIS.Practica2;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutoresBBDD extends JpaRepository<autores, Long> {
    List<autores> findByIdPelicula(Long i);
}
