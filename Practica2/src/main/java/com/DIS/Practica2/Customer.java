package com.DIS.Practica2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    private String titulo;

    @Column(length=1000)
    private String sinopsis;

    private String genero;

    private String imbd;

    private int numeroDeActores;


    protected Customer() {
    }

    public Customer(String Titulo, String Sinopsis,String Genero,String IMBD,Integer numerodeactores) {
        this.titulo = Titulo;
        this.sinopsis = Sinopsis;
        this.genero = Genero;
        this.imbd=IMBD;
        this.numeroDeActores=numerodeactores;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String Titulo) {
        this.titulo = Titulo;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String Sinopsis) {
        this.sinopsis = Sinopsis;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String Genero) {
        this.genero = Genero;
    }

    public String getImbd() {
        return imbd;
    }

    public void setImbd(String imbd) {
        this.imbd = imbd;
    }

    public int getNumeroDeActores() {
        return numeroDeActores;
    }

    public void setNumeroDeActores(int numerodeactores) {
        this.numeroDeActores = numerodeactores;
    }
    @Override
    public String toString() {
        return String.format("Customer[id=%d, Titulo='%s', Sinopsis='%s']", id, titulo, sinopsis);
    }

}