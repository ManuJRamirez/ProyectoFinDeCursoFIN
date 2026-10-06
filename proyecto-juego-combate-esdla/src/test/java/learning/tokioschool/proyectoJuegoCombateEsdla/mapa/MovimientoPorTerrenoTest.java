package learning.tokioschool.proyectoJuegoCombateEsdla.mapa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoTerreno;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.humanos.Humano;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.trasgos.Trasgo;

// TODO: Auto-generated Javadoc
/**
 * The Class MovimientoPorTerrenoTest.
 */
public class MovimientoPorTerrenoTest {
	
	/**
	 * Nazgul puede cruzar cualquier terreno.
	 */
	@Test
	void nazgulPuedeCruzarCualquierTerreno() {
		Mapa mapa = new Mapa(3,3);
		Nazgul nazgul = new Nazgul("Nazgul", 100, 10, 1, 4, Faccion.BESTIAS);
		
		assertTrue(mapa.puedeAtravesarTerreno(nazgul, TipoTerreno.MONTANIA));
		assertTrue(mapa.puedeAtravesarTerreno(nazgul, TipoTerreno.RIO));
		assertTrue(mapa.puedeAtravesarTerreno(nazgul, TipoTerreno.LLANO));
		assertTrue(mapa.puedeAtravesarTerreno(nazgul, TipoTerreno.CIUDAD));
	}
	
	/**
	 * Los hobbits pueden cruzar rios.
	 */
	@Test
	void losHobbitsPuedenCruzarRios() {
		Mapa mapa = new Mapa(3,3);
		Hobbit hobbit = new Hobbit("Hobbit", 100, 10, 1, 3, Faccion.HEROES);
		
		assertTrue(mapa.puedeAtravesarTerreno(hobbit, TipoTerreno.RIO));
	}
	
	/**
	 * Hobbits Y trasgos exploradores pueden caminar por montanias.
	 */
	@Test
	void hobbitsYTrasgosExploradoresPuedenCaminarPorMontanias() {
		Mapa mapa = new Mapa(5,5);
		Hobbit hobbit = new Hobbit("Hobbit", 100, 10, 1,3,Faccion.HEROES);
		Trasgo explorador = new Trasgo("Explorador",100, 10, 1, 3, Faccion.BESTIAS);
		Trasgo guerrero = new Trasgo("Guerrero", 100, 10, 1,3, Faccion.BESTIAS);
		
		explorador.setTipoCombate("EXPLORADOR");
		guerrero.setTipoCombate("GUERRERO");
		
		assertTrue(mapa.puedeAtravesarTerreno(hobbit, TipoTerreno.MONTANIA));
		assertTrue(mapa.puedeAtravesarTerreno(explorador, TipoTerreno.MONTANIA));
		assertFalse(mapa.puedeAtravesarTerreno(guerrero, TipoTerreno.MONTANIA));
	}
	
	/**
	 * Hobbit tiene penalizacion de movimiento en rios Y montania.
	 */
	@Test
	void hobbitTienePenalizacionDeMovimientoEnRiosYMontania() {
		Mapa mapa = new Mapa(5,5);
		Hobbit hobbit = new Hobbit("Hobbit", 100, 10, 1, 3, Faccion.HEROES);
		
		assertEquals(1, mapa.costePaso(hobbit, TipoTerreno.LLANO));
		assertEquals(3, mapa.costePaso(hobbit, TipoTerreno.MONTANIA));
		assertEquals(4, mapa.costePaso(hobbit, TipoTerreno.RIO));
	}
	
	/**
	 * Trasgo explorador tiene penalizacion de movimiento en montania.
	 */
	@Test
	void trasgoExploradorTienePenalizacionDeMovimientoEnMontania() {
		Mapa mapa = new Mapa(5,5);
		Trasgo explorador = new Trasgo("Trasgo", 100, 10, 1,3,Faccion.BESTIAS);
		
		explorador.setTipoCombate("EXPLORADOR");
		
		assertEquals(1, mapa.costePaso(explorador, TipoTerreno.LLANO));
		assertEquals(4, mapa.costePaso(explorador, TipoTerreno.MONTANIA));
	}
	
	/**
	 * Nazgul no tiene penalizacion de movimiento por ningun terreno.
	 */
	@Test
	void nazgulNoTienePenalizacionDeMovimientoPorNingunTerreno() {
		Mapa mapa = new Mapa(5,5);
		Nazgul nazgul = new Nazgul("Nazgul", 100, 10, 1,4,Faccion.BESTIAS);
		
		assertEquals(1, mapa.costePaso(nazgul, TipoTerreno.LLANO));
		assertEquals(1, mapa.costePaso(nazgul, TipoTerreno.RIO));
		assertEquals(1, mapa.costePaso(nazgul, TipoTerreno.MONTANIA));
	}

	/**
	 * Prueba de movimiento de una unidad en terreno llano.
	 */
	@Test
	void pruebaDeMovimientoDeUnaUnidadEnTerrenoLlano() {
		Mapa mapa = new Mapa(5,5);
		Humano humano = new Humano("Humano", 100, 10, 1, 2, Faccion.HEROES);
		
		mapa.colocarUnidad(1, 1, humano);
		
		Set<String> alcanzables = mapa.calcularCasillasAlcanzables(humano);
		
		assertTrue(alcanzables.contains("2,1"));
		assertTrue(alcanzables.contains("3,1"));
		assertFalse(alcanzables.contains("4,1"));
	}
	
	/**
	 * No se puede mover una unidad A una casilla ocupada.
	 */
	@Test
	void noSePuedeMoverUnaUnidadAUnaCasillaOcupada() {
		Mapa mapa = new Mapa(5,5);
		Humano humano = new Humano("Humano",100, 10, 1, 3, Faccion.HEROES);
		Hobbit hobbit = new Hobbit("Hobbit", 100, 10, 1, 3, Faccion.HEROES);
		
		mapa.colocarUnidad(2, 2, hobbit);
		mapa.colocarUnidad(2, 3, humano);
		
		Set<String> alcanzables = mapa.calcularCasillasAlcanzables(humano);
		
		assertFalse(alcanzables.contains("2,2"));
	}
	
	/**
	 * No se puede volver A mover una unidad que ya se ha movido este turno.
	 */
	@Test
	void noSePuedeVolverAMoverUnaUnidadQueYaSeHaMovidoEsteTurno() {
		Mapa mapa = new Mapa(5,5);
		Humano humano = new Humano("Humano", 100, 10, 1,3,Faccion.HEROES);
		
		mapa.colocarUnidad(2, 2, humano);
		humano.setHaMovidoEsteTurno(true);
		
		boolean movimientoConExito = mapa.moverUnidad(humano, 2, 1);
		
		assertFalse(movimientoConExito);
	}
	
	/**
	 * No podemos mover una unidad mas lejos de su rango maximo de movimiento.
	 */
	@Test
	void noPodemosMoverUnaUnidadMasLejosDeSuRangoMaximoDeMovimiento() {
		Mapa mapa = new Mapa(5,5);
		Humano humano = new Humano("Humano", 100, 10, 1, 2,Faccion.HEROES);
		
		mapa.colocarUnidad(0, 0, humano);
		
		boolean exito = mapa.moverUnidad(humano, 5, 5);
		
		assertFalse(exito);
	}
}
