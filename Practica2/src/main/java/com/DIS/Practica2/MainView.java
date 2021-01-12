package com.DIS.Practica2;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

@Route
public class MainView extends VerticalLayout {

    private final CustomerRepository repo;

    private final CustomerEditor editor;

    final Grid<Customer> grid;

    final TextField filter;

    private final Button addNewBtn;

    public MainView(CustomerRepository repo, CustomerEditor editor) {

        this.repo = repo;
        this.editor = editor;
        this.grid = new Grid<>(Customer.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New customer", VaadinIcon.PLUS.create());

        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        add(actions, grid, editor);

        grid.setHeight("300px");
        //grid.removeColumnByKey("age");
        grid.setColumns("id", "titulo", "sinopsis","genero","imbd","numerodeactores");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

        filter.setPlaceholder("Filter by last name");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> listCustomers(e.getValue()));

        // Connect selected Customer to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editCustomer(e.getValue());
            modal(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", "","","",0)));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listCustomers(filter.getValue());
        });

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
    void modal(Customer c) {
        Dialog dialog = new Dialog();

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.add(new HorizontalLayout(new Html("<b>Titulo: </b>"), new Text(c.getTitulo())));
        dialog.add(new HorizontalLayout(new Html("<b>Sinopsis: </b>"), new Text(c.getSinopsis())));
        dialog.add(new HorizontalLayout(new Html("<b>Genero: </b>"), new Text(c.getGenero())));
        dialog.add(new HorizontalLayout(new Html("<b>IMBD: </b>"), new Text(c.getImbd())));
        //dialog.add(new Text(c.getTitulo()));
        Button confirmButton = new Button("Editar", event -> { dialog.close(); });
        Button cancelButton = new Button("Cancelar", event -> { dialog.close(); });
        HorizontalLayout actions2 = new HorizontalLayout(confirmButton, cancelButton);
        dialog.add(actions2);

        dialog.open();
    }

}