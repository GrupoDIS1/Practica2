package com.DIS.Practica2;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class Practica2ApplicationTests {

	@Test
	void testGetEnlace() {

		Actores obj = new Actores();
		assertNull(obj.getEnlace());

	}
	@Test

	void testGetNombre(){
		Actores obj = new Actores();
		assertNull(obj.getNombre());

	}

	@Test

	void testGetIdPelicula(){
		Actores obj = new Actores();
		assertNull(obj.getIdPelicula());

	}

	/*@Test

	void TestVaadin(){
		CrudWithVaadinApplication obj = new CrudWithVaadinApplication();
		String [] cadena;
		cadena=obj.loadData();
		assertNull(cadena);

	}*/


	@Test
	void testGetId(){
		Peliculas cus = new Peliculas();
		assertNull(cus.getId());

	}
	@Test
	void getTitulo(){
		Peliculas cus = new Peliculas();
		assertNull(cus.getTitulo());

	}
	@Test
	void getSinopsis(){
		Peliculas cus = new Peliculas();
		assertNull(cus.getSinopsis());

	}
	@Test
	void getGenero(){
		Peliculas cus = new Peliculas();
		assertNull(cus.getGenero());

	}
	@Test
	void getImbd(){
		Peliculas cus = new Peliculas();
		assertNull(cus.getImbd());

	}
	@Test
	void getNumeroDeActores(){
		Peliculas cus = new Peliculas();
		assertNotNull(cus.getNumeroDeActores());
		

	}

}