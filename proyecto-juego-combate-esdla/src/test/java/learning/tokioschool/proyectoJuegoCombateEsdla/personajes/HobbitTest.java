package learning.tokioschool.proyectoJuegoCombateEsdla.personajes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.trasgos.Trasgo;

// TODO: Auto-generated Javadoc
/**
 * The Class HobbitTest.
 */
public class HobbitTest {

	/**
	 * Crear hobbit.
	 *
	 * @param nombre the nombre
	 * @return the hobbit
	 */
	private Hobbit crearHobbit(String nombre) {
		Hobbit hobbit = new Hobbit(nombre, 100, 10, 3, 3, Faccion.HEROES);
		
		return hobbit;
	}
	
	/**
	 * Frodo sin sam cerca hace 100 por ciento mas danio A exploradores.
	 */
	@Test
	void frodoSinSamCercaHace100PorCientoMasDanioAExploradores() {
		Mapa mapa = new Mapa(10, 10);
		Hobbit frodo = crearHobbit("Frodo");
		Trasgo explorador = new Trasgo("Trasgo", 100, 10, 1, 3, Faccion.BESTIAS);
		
		mapa.colocarUnidad(1, 1, frodo);
		explorador.setTipoCombate("EXPLORADOR");
		
		double multiplicador = frodo.getMultiplicadorAtaque(explorador, mapa);
		
		assertEquals(2.0, multiplicador, 0.0001);
	}
	
	/**
	 * Frodo con sam A menos de cuadro casilla hace 200 por ciento mas danio A exploradores.
	 */
	@Test
	void frodoConSamAMenosDeCuadroCasillaHace200PorCientoMasDanioAExploradores(){
		Mapa mapa = new Mapa(10, 10);
		Hobbit frodo = crearHobbit("Frodo"); 
		Hobbit sam = crearHobbit("Sam");
		Trasgo explorador = new Trasgo("Trasgo", 100, 10, 1, 3, Faccion.BESTIAS);
		
		mapa.colocarUnidad(2, 2, frodo);
		mapa.colocarUnidad(2, 3, sam);
		explorador.setTipoCombate("EXPLORADOR");
		
		double multiplicador = frodo.getMultiplicadorAtaque(explorador, mapa);
		
		assertEquals(3.0, multiplicador, 0.0001);
	}
	
	/**
	 * Hobbits no tienen bonus contra enemigos que no son exploradores.
	 */
	@Test
	void hobbitsNoTienenBonusContraEnemigosQueNoSonExploradores() {
		Mapa mapa = new Mapa(5, 5);
		Hobbit frodo = crearHobbit("Frodo");
		Trasgo guerrero = new Trasgo("trasgo", 100, 10, 1, 3, Faccion.BESTIAS);
		
		mapa.colocarUnidad(2, 2, frodo);
		guerrero.setTipoCombate("GUERRERO");
		
		double multiplicador = frodo.getMultiplicadorAtaque(guerrero, mapa);
		
		assertEquals(1.0, multiplicador, 0.0001);
	}
	
	/**
	 * Companiero en rango devuelve false si mapa es nulo.
	 */
	@Test
	void companieroEnRangoDevuelveFalseSiMapaEsNulo() {
		Hobbit frodo = crearHobbit("Frodo");
		assertFalse(frodo.companieroEnRango(null));
	}
	
	/**
	 * Gets the nombre companiero para cada hobbit.
	 *
	 * @return the nombre companiero para cada hobbit
	 */
	@Test
	void getNombreCompanieroParaCadaHobbit() {
		Hobbit frodo = crearHobbit("Frodo");
		Hobbit sam = crearHobbit("Sam");
		
		assertEquals("Sam", frodo.getNombreCompaniero());
		assertEquals("Frodo", sam.getNombreCompaniero());
	}
	
	/**
	 * Companiero muerto no cuenta como que esta en rango.
	 */
	@Test
	void companieroMuertoNoCuentaComoQueEstaEnRango() {
		Mapa mapa = new Mapa(5,5);
		Hobbit frodo = crearHobbit("Frodo");
		Hobbit sam = crearHobbit("Sam");
		
		mapa.colocarUnidad(1, 1, sam);
		mapa.colocarUnidad(1, 2, frodo);
		sam.recibirDanio(1000);
		
		assertTrue(!sam.estaVivo());
		assertFalse(frodo.companieroEnRango(mapa));
	}
}
