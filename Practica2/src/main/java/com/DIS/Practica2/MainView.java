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
import com.vaadin.flow.component.notification.Notification;
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
    //declaramos las variables
    private final PeliculasBBDD repo;

    //el grid
    final Grid<Peliculas> grid;

    final TextField filter;

    private final Button addNewBtn;

    private final Button Exportar;


    public MainView(ActoresBBDD aut,PeliculasBBDD repo) {
        //inicializamos las variables
        this.repo = repo;
        this.grid = new Grid<>(Peliculas.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("Nueva Pelicula", VaadinIcon.PLUS.create());
        this.Exportar = new Button("Exportar", VaadinIcon.ARROW_CIRCLE_RIGHT.create());

        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn,Exportar);
        add(actions, grid);
        //le ponemos un height de 300 px
        grid.setHeight("300px");

        //elimnamos la comuna id del grid, ya que no la queremos mostrar
        grid.removeColumnByKey("id");
        //indicamos que coumnas tendra el grid
        grid.setColumns("titulo", "sinopsis","genero","imbd","numeroDeActores");


        // le ponemos un placeholder al filter
        filter.setPlaceholder("Buscar por Titulo");


        //cuendo el usuario busca una pelicula se actualiza
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> listCustomers(e.getValue()));

        // Cuando se sleeciona un elemneto de el grid
        grid.asSingleSelect().addValueChangeListener(e -> {
            //nos vamos a la funcion modal, y l e pasamos la pelicula que ha clicado el usuario
            modal(aut,e.getValue(),repo);
        });

        // Si quiere añadir una nueva pelicula, se abrira el modal correspondiente
        // al pinchar el boton
        addNewBtn.addClickListener(e -> modalnuevapelicula(aut,repo));

        // si queremos guardar la lista de peliculas actuales
        Exportar.addClickListener(e ->
                {
                    try {
                        //guardamos en el json
                        guardamosenjson(aut,repo);
                        // notificacion, para avisar al usuario que se ha exportado correctamente
                        Notification.show("Datos exportados correctamente a Peliculas.json");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                );

        // inicalizamos la lista
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
    // el modal, aqui se abrir en detalle la pelicula clicada
    void modal(ActoresBBDD aut,Peliculas c,PeliculasBBDD repo) {
        try{
            // declaramos el modal
            Dialog dialog = new Dialog();
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);
            // añadimos al modal los elemntos de la pelicula correspondiente
            dialog.add(new HorizontalLayout(new Html("<b>Titulo: </b>"), new Text(c.getTitulo())));
            dialog.add(new HorizontalLayout(new Html("<b>Sinopsis: </b>"), new Text(c.getSinopsis())));
            dialog.add(new HorizontalLayout(new Html("<b>Genero: </b>"), new Text(c.getGenero())));
            dialog.add(new HorizontalLayout(new Html("<b>IMBD: </b>"), new Text(c.getImbd())));
            // añadimos los autores
            for (Actores autorActual : aut.findByIdPelicula(c.getId())) {
                dialog.add(new HorizontalLayout(new Html("<b>Actor: </b>"), new Text(autorActual.getNombre())));
                dialog.add(new HorizontalLayout(new Html("<b>Enlace: </b>"), new Text(autorActual.getEnlace())));
            }
            // ponemos los botones de editar, donde se abrbira otro modal para asi poder editar la pelicula y otro
            // para salir
            Button confirmButton = new Button("Editar", event -> { dialog.close(); modaleditar(aut,c,repo); });
            Button cancelButton = new Button("Cancelar", event -> { dialog.close(); });
            HorizontalLayout actions2 = new HorizontalLayout(confirmButton, cancelButton);
            dialog.add(actions2);
            //abrimos el modal
            dialog.open();
        }catch (Exception e) {

        }
    }
    // este modal es para poder editar
    void modaleditar(ActoresBBDD aut,Peliculas c,PeliculasBBDD repo) {
        //declaramos el modal
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        //declaramos los textedit y les ponemos los datos correspondientes
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
        Actores todoslosatuores[]= new Actores[numerodeactores];
        TextField nombreautor[]= new TextField[numerodeactores];
        TextField enlaceautor[]= new TextField[numerodeactores];
        int i =0;
        //ponemos los actores
        for (Actores autorActual : aut.findByIdPelicula(c.getId())) {
            todoslosatuores[i]=autorActual;
            nombreautor[i] = new TextField("Nombre actor");
            nombreautor[i].setValue(autorActual.getNombre());
            dialog.add(new HorizontalLayout(nombreautor[i]));
            enlaceautor[i] = new TextField("Enlace actor");
            enlaceautor[i].setValue(autorActual.getEnlace());
            dialog.add(new HorizontalLayout(enlaceautor[i]));
            i++;
        }

        //si le da aceptar se editara los datos de la base de datos
        Button confirmButton = new Button("Aceptar", event -> {
            c.setTitulo(titulo.getValue());
            c.setSinopsis(Sinopsis.getValue());
            c.setGenero(Genero.getValue());
            c.setImbd(Imbd.getValue());
            // por si ha modificado los autores
            for(int x=0;x<numerodeactores;x++){
                todoslosatuores[x].setNombre(nombreautor[x].getValue());
                todoslosatuores[x].setEnlace(enlaceautor[x].getValue());
                aut.save(todoslosatuores[x]);
            }
            //guardamos los cambios en la base de datos
            repo.save(c);
            // actualizamos
            listCustomers("");
            dialog.close();
        });
        Button cancelButton = new Button("Cancelar", event -> { dialog.close(); });
        // si le da a elimnar elimnaremos esa pelicula de la base de datos
        Button EliminarButton = new Button("Eliminar",VaadinIcon.TRASH.create(), event -> { repo.delete(c);listCustomers("");dialog.close(); });
        HorizontalLayout actions2 = new HorizontalLayout(confirmButton, cancelButton, EliminarButton);
        dialog.add(actions2);
        dialog.open();
    }

    //para añadir una nueva pelicula
    void modalnuevapelicula(ActoresBBDD aut,PeliculasBBDD repo) {
        // declaramos el modal
        Dialog dialog = new Dialog();
        //ponemos los textView
        TextField titulo = new TextField("Titulo");
        dialog.add(new HorizontalLayout(titulo));
        TextField Sinopsis = new TextField("Sinopsis");
        dialog.add(new HorizontalLayout(Sinopsis));
        TextField Genero = new TextField("Genero");
        dialog.add(new HorizontalLayout(Genero));
        TextField Imbd = new TextField("Imbd");
        dialog.add(new HorizontalLayout(Imbd));
        // le preguntamos al usuario cuantos actores tiene la pelicula, dependiendo del nuemro de actores,
        //se pondran los textview

        //declaramos el NumberField
        NumberField NumeroActores = new NumberField("Numero de Actores");;
        dialog.add(new HorizontalLayout(NumeroActores));

        Button confirmButton = new Button("Aceptar", event -> {

            Double nacto=NumeroActores.getValue();
            int nmactores=0;
            if (nacto!=null) { // si ha metido un numero valido
                nmactores = nacto.intValue();
            }
            //creamos y guardamos la nueva pelicula
            Peliculas nuevo = new Peliculas(titulo.getValue(), Sinopsis.getValue(), Genero.getValue(), Imbd.getValue(), nmactores);
            repo.save(nuevo);
            //actualizamos
            listCustomers("");
            dialog.close();
            // si hay algun actor abrbiremos un modal, para que el usuario los escriba
            if (nmactores>0) {
                // abrimos un modal, para escribir los actores
                modalagregamosautores(aut, nmactores, nuevo.getId());
            }
        });
        Button cancelButton = new Button("Cancelar", event -> { dialog.close(); });
        HorizontalLayout actions2 = new HorizontalLayout(confirmButton, cancelButton);
        dialog.add(actions2);
        //abrimos el modal
        dialog.open();
    }
    // segun el nuemro de actores que haya introducido el usaruio para la nueva pelicula, se pondran
    // los correspondientes TextView
    void modalagregamosautores(ActoresBBDD aut,int numerodeactores,Long idpeli) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        //creamos los arrays
        TextField nombreautor[]= new TextField[numerodeactores];
        TextField enlaceautor[]= new TextField[numerodeactores];
        // ponemos los correspondientes textfiel para que el usario introduzaca los datos
        for(int i =0;i<numerodeactores;i++){
            nombreautor[i] = new TextField("Nombre actor");
            dialog.add(new HorizontalLayout(nombreautor[i]));
            enlaceautor[i] = new TextField("Enlace actor");
            dialog.add(new HorizontalLayout(enlaceautor[i]));
        }
        Button confirmButton = new Button("Aceptar", event -> {
            for(int i =0;i<numerodeactores;i++) {
                Actores nuevo = new Actores(nombreautor[i].getValue(),enlaceautor[i].getValue(),idpeli);
                aut.save(nuevo);
                dialog.close();
            }
        });
        Button cancelButton = new Button("Cancelar", event -> { dialog.close(); });
        HorizontalLayout actions2 = new HorizontalLayout(confirmButton, cancelButton);
        dialog.add(actions2);
        dialog.open();
    }
    // guardamos los dtaaos de la BBDD en el json
    void guardamosenjson(ActoresBBDD aut,PeliculasBBDD repo) throws IOException {
        String json2 = "{\"Success\":true,\"Message\":\"Invalid access token.\"}";
        String json = "{\"Videoteca\":{\"Nombre\":\"Marcos\",\"Ubicacion\":\"Madrid\",\"Fecha\":2020,\"Peliculas\":{\"Pelicula\":[";
        //extraemos todas las peliculas
        List<Peliculas> peliculas= repo.findAll();
        //vamos recorriendo todas las peliculas
        for(int i = 0; i < peliculas.size(); i++)
        {
            String titulo= peliculas.get(i).getTitulo();
            String sinopsis= peliculas.get(i).getSinopsis();
            String imbd= peliculas.get(i).getImbd();
            String genero= peliculas.get(i).getGenero();
            Long idpelicula= peliculas.get(i).getId();
            int numActores=peliculas.get(i).getNumeroDeActores();
            json+="{\"Titulo\":\""+titulo+ "\",\"Sinopsis\":\""+sinopsis+ "\",\"Genero\":\""+genero+"\",\"IMBD\":\""+imbd+"\",";
            if (numActores==0){ // si no hay actores
                // para el minar la ultima: ,
                json = json.substring(0, json.length()-1);
            }else{// si hay actores
                List<Actores> autorespeli=aut.findByIdPelicula(idpelicula);
                json+="\"Reparto\":{\"Actor\":[";
                // bucle para recorrer todos los actores
                for(int x = 0; x < autorespeli.size(); x++)
                {
                    String NombreAutor=autorespeli.get(x).getNombre();
                    String EnlaceAutor=autorespeli.get(x).getEnlace();
                    json+="{\"Nombre\":\""+NombreAutor+ "\",\"EnlaceWikipedia\":\""+EnlaceAutor+ "\"},";
                }
                // para el minar la ultima: ,
                json = json.substring(0, json.length()-1);
                json+="]}";
            }

            json+="},";
        }
        // para el minar la ultima: ,
        json = json.substring(0, json.length()-1);

        json+="]}}}";
        //convertimos el string en un objeto de json
        Gson gson = new Gson();
        JsonElement jelem = gson.fromJson(json, JsonElement.class);
        JsonObject jobj = jelem.getAsJsonObject();
        // lo guardamos todo en un archvivo
        try (Writer writer = new FileWriter("Peliculas.json")) {
            Gson gson1 = new GsonBuilder().create();
            gson1.toJson(jobj, writer);
        }
    }
}