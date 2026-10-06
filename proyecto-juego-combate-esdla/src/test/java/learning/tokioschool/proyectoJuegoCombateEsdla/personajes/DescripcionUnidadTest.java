package learning.tokioschool.proyectoJuegoCombateEsdla.personajes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.DescripcionUnidad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;

public class DescripcionUnidadTest {
	
	@Test
	void generarConPersonajeNuloDevuelveNull() {
		
		assertEquals("", DescripcionUnidad.generar(null));
	}
	
	@Test
	void laDescripcionDeUnNazgulIndicaQueEsVoladorYNoPuedeAtacarHobbits() {
		Nazgul nazgul = new Nazgul("Nazgul", 150, 20, 1, 4, Faccion.BESTIAS);
		nazgul.setRaza("Nazgul");
		
		String descripcion = DescripcionUnidad.generar(nazgul);
				
		assertTrue(descripcion.contains("Unidad voladora"));
		assertTrue(descripcion.contains("No puede atacar a los Hobbits"));
		assertTrue(descripcion.contains("No puede conquistar ciudades"));		
	}
	
	@Test
	void descripcionDeFrodoDiceQueElLlevaElAnillo() {
		Hobbit frodo = new Hobbit("Frodo", 100, 10, 1, 3, Faccion.HEROES);
		frodo.setRaza("Hobbit");
		
		String descripcion = DescripcionUnidad.generar(frodo);
		
		assertTrue(descripcion.contains("portador del Anillo"));
		assertTrue(descripcion.contains("Unidad invisible"));		
	}

	@Test
	void descripcionDeSamNoDiceQueElLLevaElAnillo() {
		Hobbit sam = new Hobbit("Sam", 100, 10, 1, 3, Faccion.HEROES);
		sam.setRaza("Hobbit");
		
		String descripcion = DescripcionUnidad.generar(sam);
		
		assertFalse(descripcion.contains("portador del Anillo"));
	}
}
