package com.DIS.Practica2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class autores {
    @Id
    @GeneratedValue
    private Long idAutor;

    private String nombre;

    private String enlace;
    private Long idPelicula;

    protected autores() {
    }
    public autores(String nombre, String enlace,Long idpelicula) {
        this.nombre = nombre;
        this.enlace = enlace;
        this.idPelicula = idpelicula;
    }

    public Long getIdPelicula() {
        return this.idPelicula;
    }
    public String getNombre() {
        return this.nombre;
    }
    public String getEnlace() {
        return this.enlace;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }
}
