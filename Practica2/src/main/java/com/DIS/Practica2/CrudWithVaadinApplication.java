package com.DIS.Practica2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CrudWithVaadinApplication {

    private static final Logger log = LoggerFactory.getLogger(CrudWithVaadinApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CrudWithVaadinApplication.class);
    }

    @Bean
    public CommandLineRunner loadData(CustomerRepository repository,AutoresBBDD aut) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        Object object = parser.parse(new FileReader("Peliculas.json"));
        JsonObject gsonObj = (JsonObject) object;
        gsonObj = gsonObj.getAsJsonObject("Videoteca");
        gsonObj = gsonObj.getAsJsonObject("Peliculas");
        JsonArray demarcation = gsonObj.get("Pelicula").getAsJsonArray();

        return (args) -> {
            // save customers
            for (JsonElement demarc : demarcation) {
                String titulo = ((JsonObject) demarc).get("Titulo").getAsString();
                String sinopsis = ((JsonObject) demarc).get("Sinopsis").getAsString();
                String imbd = ((JsonObject) demarc).get("IMBD").getAsString();
                String genero;
                try{
                    genero = ((JsonObject) demarc).get("Genero").getAsString();
                }catch (Exception e) {
                    genero="None";
                }
                JsonObject autors = demarc.getAsJsonObject();
                autors = autors.getAsJsonObject("Reparto");

                int numeroActores=0;
                String[] nombreA =new String[10];
                String[] enlaceP =new String[10];
                try{// si hay mas de un actor
                    JsonArray autores = autors.get("Actor").getAsJsonArray();
                    for (JsonElement demarc1 : autores) {
                        String nmb=((JsonObject) demarc1).get("Nombre").getAsString();
                        String EnlaceWiki=((JsonObject) demarc1).get("EnlaceWikipedia").getAsString();
                        nombreA[numeroActores]=nmb;
                        enlaceP[numeroActores]=EnlaceWiki;
                        numeroActores+=1;
                    }
                }catch (Exception e1) {// si solo hay un actor, dara una excepcion y se metera aqui
                    autors = autors.getAsJsonObject("Actor");
                    numeroActores=1;
                    String nmb=autors.get("Nombre").getAsString();
                    String EnlaceWiki=autors.get("EnlaceWikipedia").getAsString();
                    nombreA[0]=nmb;
                    enlaceP[0]=EnlaceWiki;

                }


                Customer nuevo = new Customer(titulo, sinopsis, genero, imbd, numeroActores);
                repository.save(nuevo);
                Long idpeli=nuevo.getId();
                for(int i=0;i<numeroActores;i++){
                    autores nuevoAutor= new autores(nombreA[i],enlaceP[i],idpeli);
                    aut.save(nuevoAutor);
                }

            }


        };
    }

}