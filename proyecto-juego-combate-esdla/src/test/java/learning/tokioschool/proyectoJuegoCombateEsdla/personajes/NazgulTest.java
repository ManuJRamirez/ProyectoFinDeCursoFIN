package learning.tokioschool.proyectoJuegoCombateEsdla.personajes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoTerreno;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.humanos.Humano;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;

// TODO: Auto-generated Javadoc
/**
 * The Class NazgulTest.
 */
public class NazgulTest {
	
	/**
	 * Crear nazgul.
	 *
	 * @return the nazgul
	 */
	private Nazgul crearNazgul() {
		return new Nazgul("Nazgul", 150, 20, 1, 4, Faccion.BESTIAS);
	}
	
	/**
	 * Hace 50 por ciento mas danio A heroes en llano.
	 */
	@Test
	void hace50PorCientoMasDanioAHeroesEnLlano() {
		Mapa mapa = new Mapa(5,5);
		Nazgul nazgul = crearNazgul();
		Humano humano = new Humano("Humano", 100, 10, 1, 3, Faccion.HEROES);
		mapa.setTerreno(1, 1, TipoTerreno.LLANO);
		humano.setPosicion(1, 1);
		
		double multiplicador = nazgul.getMultiplicadorAtaque(humano, mapa);
		
		assertEquals(1.5, multiplicador, 0.0001);
	}
	
	/**
	 * No tiene bonus contra heroes fuera de llano.
	 */
	@Test
	void noTieneBonusContraHeroesFueraDeLlano() {
		Mapa mapa = new Mapa(5,5);
		Nazgul nazgul = crearNazgul();
		Humano humano = new Humano("Humano",100, 10, 1, 3, Faccion.HEROES);
	
		mapa.setTerreno(1, 1, TipoTerreno.BOSQUE);
		humano.setPosicion(1, 1);
		
		double multiplicador = nazgul.getMultiplicadorAtaque(humano, mapa);
		
		assertEquals(1.0, multiplicador, 0.0001);
	}
	
	
	/**
	 * Recibe un 80 por ciento menos danio si el atacante no es arquero O mago.
	 */
	@Test
	void recibeUn80PorCientoMenosDanioSiElAtacanteNoEsArqueroOMago() {
		Nazgul nazgul = crearNazgul();
		Humano guerrero = new Humano("Humano", 100, 10, 1, 3, Faccion.HEROES);
		guerrero.setTipoCombate("GUERRERO");
		
		double modificadorArmadura = nazgul.getModificadorDefensa(guerrero, null);
		
		assertEquals(1.80, modificadorArmadura, 0.0001);
	}
	
	/**
	 * No tiene bonus defensa contra magos Y arqueros.
	 */
	@Test
	void noTieneBonusDefensaContraMagosYArqueros() {
		Nazgul nazgul = crearNazgul();
		Humano arquero = new Humano("Humano", 100, 10, 3, 3, Faccion.HEROES);
		
		arquero.setTipoCombate("ARQUERO");
		
		double modificadorArmadura = nazgul.getModificadorDefensa(arquero, null);
		
		assertEquals(1.0, modificadorArmadura, 0.0001);
	}
	
	/**
	 * El nazgul tiene un radio de deteccion de tres casillas en llano Y uno en montania.
	 */
	@Test
	void elNazgulTieneUnRadioDeDeteccionDeTresCasillasEnLlanoYUnoEnMontania() {
		Mapa mapa = new Mapa(5,5);
		Nazgul nazgul = crearNazgul();
		
		mapa.colocarUnidad(5, 5, nazgul);
		
		assertEquals(3, mapa.calcularRangoDeteccion(nazgul, false));
		assertEquals(1, mapa.calcularRangoDeteccion(nazgul, true));
	}
	
	/**
	 * Un hobbit dentro del rango de deteccion del nazgul pasa A estar detectado.
	 */
	@Test
	void unHobbitDentroDelRangoDeDeteccionDelNazgulPasaAEstarDetectado() {
		Mapa mapa = new Mapa(5,5);
		Nazgul nazgul = crearNazgul(); 
		Hobbit frodo = new Hobbit("Frodo", 50, 5, 1, 3, Faccion.HEROES);
		
		mapa.colocarUnidad(2, 2, nazgul);
		mapa.colocarUnidad(2, 3, frodo);
		
		assertFalse(frodo.esDetectadoPorEnemigo());
		
		mapa.actualizarDeteccionHobbits();
		
		assertTrue(frodo.esDetectadoPorEnemigo());
	}
	
	/**
	 * Un hobbit fuera del radio de deteccion del nazgul permanece invisible.
	 */
	@Test
	void unHobbitFueraDelRadioDeDeteccionDelNazgulPermaneceInvisible() {
		Mapa mapa = new Mapa(5,5);
		Nazgul nazgul = crearNazgul();
		Hobbit frodo = new Hobbit("Frodo", 100, 10, 1, 3, Faccion.HEROES);
		
		mapa.colocarUnidad(0, 0, nazgul);
		mapa.colocarUnidad(5, 5, frodo);
		
		mapa.actualizarDeteccionHobbits();
		
		assertFalse(frodo.esDetectadoPorEnemigo());
	}
}
