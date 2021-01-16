package com.DIS.Practica2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.util.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

@Route
public class MainView extends VerticalLayout {

    private final CustomerRepository repo;


    final Grid<Customer> grid;

    final TextField filter;

    private final Button addNewBtn;

    private final Button Exportar;


    public MainView(AutoresBBDD aut,CustomerRepository repo) {

        this.repo = repo;
        this.grid = new Grid<>(Customer.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("Nueva Pelicula", VaadinIcon.PLUS.create());
        this.Exportar = new Button("Exportar", VaadinIcon.ARROW_CIRCLE_RIGHT.create());

        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn,Exportar);
        add(actions, grid);

        grid.setHeight("300px");

        grid.removeColumnByKey("id");
        grid.setColumns("titulo", "sinopsis","genero","imbd","numeroDeActores");



        filter.setPlaceholder("Buscar por Titulo");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> listCustomers(e.getValue()));

        // Connect selected Customer to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            modal(aut,e.getValue(),repo);
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> modalnuevapelicula(aut,repo));

        Exportar.addClickListener(e ->
                {
                    try {
                        guardamosenjson(aut,repo);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                );

        // Initialize listing
        listCustomers(null);
    }

    // tag::listCustomers[]
    void listCustomers(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findAll());
        }
        else {
            grid.setItems(repo.findByTituloStartsWithIgnoreCase(filterText));
        }
    }
    void modal(AutoresBBDD aut,Customer c,CustomerRepository repo) {
        try{
            Dialog dialog = new Dialog();
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);
            dialog.add(new HorizontalLayout(new Html("<b>Titulo: </b>"), new Text(c.getTitulo())));
            dialog.add(new HorizontalLayout(new Html("<b>Sinopsis: </b>"), new Text(c.getSinopsis())));
            dialog.add(new HorizontalLayout(new Html("<b>Genero: </b>"), new Text(c.getGenero())));
            dialog.add(new HorizontalLayout(new Html("<b>IMBD: </b>"), new Text(c.getImbd())));
            for (autores autorActual : aut.findByIdPelicula(c.getId())) {
                dialog.add(new HorizontalLayout(new Html("<b>Autor: </b>"), new Text(autorActual.getNombre())));
                dialog.add(new HorizontalLayout(new Html("<b>Enlace: </b>"), new Text(autorActual.getEnlace())));
            }
            Button confirmButton = new Button("Editar", event -> { dialog.close(); modaleditar(aut,c,repo); });
            Button cancelButton = new Button("Cancelar", event -> { dialog.close(); });
            HorizontalLayout actions2 = new HorizontalLayout(confirmButton, cancelButton);
            dialog.add(actions2);

            dialog.open();
        }catch (Exception e) {

        }
    }

    void modaleditar(AutoresBBDD aut,Customer c,CustomerRepository repo) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        TextField titulo = new TextField("Titulo");
        titulo.setValue(c.getTitulo());
        dialog.add(new HorizontalLayout(titulo));
        TextField Sinopsis = new TextField("Sinopsis");
        Sinopsis.setValue(c.getSinopsis());
        dialog.add(new HorizontalLayout(Sinopsis));
        TextField Genero = new TextField("Genero");
        Genero.setValue(c.getGenero());
        dialog.add(new HorizontalLayout(Genero));
        TextField Imbd = new TextField("Imbd");
        Imbd.setValue(c.getImbd());
        dialog.add(new HorizontalLayout(Imbd));
        int numerodeactores = c.getNumeroDeActores();
        autores todoslosatuores[]= new autores[numerodeactores];
        TextField nombreautor[]= new TextField[numerodeactores];
        TextField enlaceautor[]= new TextField[numerodeactores];
        int i =0;
        for (autores autorActual : aut.findByIdPelicula(c.getId())) {
            todoslosatuores[i]=autorActual;
            nombreautor[i] = new TextField("Nombre autor");
            nombreautor[i].setValue(autorActual.getNombre());
            dialog.add(new HorizontalLayout(nombreautor[i]));
            enlaceautor[i] = new TextField("Enlace autor");
            enlaceautor[i].setValue(autorActual.getEnlace());
            dialog.add(new HorizontalLayout(enlaceautor[i]));
            i++;
        }


        Button confirmButton = new Button("Aceptar", event -> {
            c.setTitulo(titulo.getValue());
            c.setSinopsis(Sinopsis.getValue());
            c.setGenero(Genero.getValue());
            c.setImbd(Imbd.getValue());
            for(int x=0;x<numerodeactores;x++){
                todoslosatuores[x].setNombre(nombreautor[x].getValue());
                todoslosatuores[x].setEnlace(enlaceautor[x].getValue());
                aut.save(todoslosatuores[x]);
            }
            repo.save(c);
            listCustomers("");
            dialog.close();
        });
        Button cancelButton = new Button("Cancelar", event -> { dialog.close(); });
        Button EliminarButton = new Button("Eliminar",VaadinIcon.TRASH.create(), event -> { repo.delete(c);listCustomers("");dialog.close(); });
        HorizontalLayout actions2 = new HorizontalLayout(confirmButton, cancelButton, EliminarButton);
        dialog.add(actions2);
        dialog.open();
    }

    void modalnuevapelicula(AutoresBBDD aut,CustomerRepository repo) {
        Dialog dialog = new Dialog();
        TextField titulo = new TextField("Titulo");
        dialog.add(new HorizontalLayout(titulo));
        TextField Sinopsis = new TextField("Sinopsis");
        dialog.add(new HorizontalLayout(Sinopsis));
        TextField Genero = new TextField("Genero");
        dialog.add(new HorizontalLayout(Genero));
        TextField Imbd = new TextField("Imbd");
        dialog.add(new HorizontalLayout(Imbd));
        NumberField NumeroActores = new NumberField("Numero de Actores");;
        dialog.add(new HorizontalLayout(NumeroActores));




        Button confirmButton = new Button("Aceptar", event -> {

            Double nacto=NumeroActores.getValue();
            int nmactores=0;
            if (nacto!=null) {
                nmactores = nacto.intValue();
            }
            Customer nuevo = new Customer(titulo.getValue(), Sinopsis.getValue(), Genero.getValue(), Imbd.getValue(), nmactores);
            repo.save(nuevo);
            listCustomers("");
            dialog.close();
            if (nmactores>0) {
                modalagregamosautores(aut, nmactores, nuevo.getId());
            }
        });
        Button cancelButton = new Button("Cancelar", event -> { dialog.close(); });
        HorizontalLayout actions2 = new HorizontalLayout(confirmButton, cancelButton);
        dialog.add(actions2);
        dialog.open();
    }

    void modalagregamosautores(AutoresBBDD aut,int numerodeactores,Long idpeli) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        TextField nombreautor[]= new TextField[numerodeactores];
        TextField enlaceautor[]= new TextField[numerodeactores];
        for(int i =0;i<numerodeactores;i++){
            nombreautor[i] = new TextField("Nombre autor");
            dialog.add(new HorizontalLayout(nombreautor[i]));
            enlaceautor[i] = new TextField("Enlace autor");
            dialog.add(new HorizontalLayout(enlaceautor[i]));
        }


        Button confirmButton = new Button("Aceptar", event -> {
            for(int i =0;i<numerodeactores;i++) {
                autores nuevo = new autores(nombreautor[i].getValue(),enlaceautor[i].getValue(),idpeli);
                aut.save(nuevo);
                dialog.close();
            }
        });
        Button cancelButton = new Button("Cancelar", event -> { dialog.close(); });
        HorizontalLayout actions2 = new HorizontalLayout(confirmButton, cancelButton);
        dialog.add(actions2);
        dialog.open();
    }
    void guardamosenjson(AutoresBBDD aut,CustomerRepository repo) throws IOException {
        String json2 = "{\"Success\":true,\"Message\":\"Invalid access token.\"}";
        String json = "{\"Videoteca\":{\"Nombre\":\"Marcos\",\"Peliculas\":\"sdf\"}}";

        List<Customer> custumers= repo.findAll();

        Gson gson = new Gson();
        JsonElement jelem = gson.fromJson(json, JsonElement.class);
        JsonObject jobj = jelem.getAsJsonObject();
        try (Writer writer = new FileWriter("Peliculas2.json")) {
            Gson gson1 = new GsonBuilder().create();
            gson1.toJson(jobj, writer);
        }
    }
}