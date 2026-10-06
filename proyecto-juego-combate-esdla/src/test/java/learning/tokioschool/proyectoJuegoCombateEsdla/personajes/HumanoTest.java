package learning.tokioschool.proyectoJuegoCombateEsdla.personajes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoTerreno;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.humanos.Humano;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.orcos.Orco;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.trasgos.Trasgo;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.urukHai.UrukHai;

// TODO: Auto-generated Javadoc
/**
 * The Class HumanoTest.
 */
public class HumanoTest {
	
	/**
	 * Metodo para crear un personaje humano
	 *
	 * @param tipoCombate the tipo combate
	 * @return the humano
	 */
	private Humano crearHumano(String tipoCombate) {
		Humano humano = new Humano("Humano", 100, 10, 1, 3, Faccion.HEROES);
		humano.setTipoCombate(tipoCombate);
		
		return humano;
	}
	
	/**
	 * Guerrero hace 15 por ciento danio A trasgos.
	 */
	@Test
	void guerreroHace15PorCientoDanioATrasgos() {
		Humano guerrero = crearHumano("GUERRERO");
		Trasgo trago = new Trasgo("Trasgo", 100, 10, 1, 3, Faccion.BESTIAS);
		double multiplicador = guerrero.getMultiplicadorAtaque(trago, null);
		
		assertEquals(1.15, multiplicador, 0.0001);
	}
	
	/**
	 * Caballeria hace 25 por ciento mas danio A bestias en llano.
	 */
	@Test
	void caballeriaHace25PorCientoMasDanioABestiasEnLlano() {
		Mapa mapa = new Mapa(5,5);
		Humano caballeria = crearHumano("CABALLERIA");
		UrukHai enemigo = new UrukHai("Enemigo", 100, 10, 1, 3, Faccion.BESTIAS);
		enemigo.setPosicion(1, 1);
		
		double multiplicador = caballeria.getMultiplicadorAtaque(enemigo, mapa);
		
		assertEquals(1.25, multiplicador, 0.0001);
		
	}

	/**
	 * Caballeria tiene 35 por ciento mas danio contra arqueros en llano.
	 */
	@Test
	void caballeriaTiene35PorCientoMasDanioContraArquerosEnLlano() {
		Mapa mapa = new Mapa(5, 5);
		Humano caballeria = crearHumano("CABALLERIA");
		UrukHai arqueroUrukHai = new UrukHai("ArqueroUruk", 100, 10, 1, 3, Faccion.BESTIAS);
		Orco arqueroOrco = new Orco("ArqueroOrco", 100, 10, 1, 3, Faccion.BESTIAS);
		
		arqueroUrukHai.setTipoCombate("ARQUERO");
		arqueroUrukHai.setPosicion(1, 1);
		mapa.setTerreno(1, 1, TipoTerreno.LLANO);
		arqueroOrco.setTipoCombate("ARQUERO");
		arqueroOrco.setPosicion(1, 2);
		mapa.setTerreno(1, 2, TipoTerreno.LLANO);
		
		double multiplicadorVsOrco = caballeria.getMultiplicadorAtaque(arqueroOrco, mapa);
		double multiplicadorVsUruk = caballeria.getMultiplicadorAtaque(arqueroUrukHai, mapa);
		
		assertEquals(1.60, multiplicadorVsOrco, 0.0001);
		assertEquals(1.60, multiplicadorVsUruk, 0.0001);
		
	}
	
	/**
	 * Caballeria sin bonus fuera de terreno llano.
	 */
	@Test
	void caballeriaSinBonusFueraDeTerrenoLlano() {
		Mapa mapa = new Mapa(5, 5);
		Humano caballeria = crearHumano("CABALLERIA");
		Orco enemigo = new Orco("Enemigo", 100, 10, 1, 3, Faccion.BESTIAS);
		
		mapa.setTerreno(1, 1, TipoTerreno.BOSQUE);
		enemigo.setPosicion(1, 1);
		
		double multiplicador = caballeria.getMultiplicadorAtaque(enemigo, mapa);
		
		assertEquals(1.0, multiplicador, 0.0001);
	}
	
	/**
	 * Caballeria tiene penalizacion 35 por ciento danio si lucha dentro de ciudad.
	 */
	@Test
	void caballeriaTienePenalizacion35PorCientoDanioSiLuchaDentroDeCiudad() {
		Mapa mapa = new Mapa(5, 5);
		Ciudad ciudad = new Ciudad("Ciudad");
		Humano caballeria = crearHumano("CABALLERIA");
		Orco enemigo = new Orco("Enemigo", 100, 10, 1, 3, Faccion.BESTIAS);
		
		ciudad.addCasillaCiudad(2, 2);
		mapa.setTerreno(2, 2, TipoTerreno.CIUDAD);
		ciudad.addCasillaCiudad(2, 3);
		mapa.setTerreno(2, 3, TipoTerreno.CIUDAD);
		mapa.addCiudad(ciudad);
		caballeria.setPosicion(2, 2);
		enemigo.setPosicion(2, 3);
		
		double multiplicador = caballeria.getMultiplicadorAtaque(enemigo, mapa);
		
		assertEquals(0.65, multiplicador, 0.0001);
	}
	
	/**
	 * Guerrero tiene 20 por ciento mas armadura defendiendo su ciudad.
	 */
	@Test
	void guerreroTiene20PorCientoMasArmaduraDefendiendoSuCiudad() {
		Mapa mapa = new Mapa(5,5);
		Ciudad ciudad = new Ciudad("Prueba");
		Humano guerrero = crearHumano("GUERRERO");
		Orco atacante = new Orco("Orco", 100, 10, 1, 3, Faccion.BESTIAS);
		
		ciudad.addCasillaCiudad(2, 2);
		ciudad.setControlSobreCiudad(ControlCiudad.HEROES);
		mapa.addCiudad(ciudad);
		guerrero.setPosicion(2, 2);
		
		double modificadorDefensa = guerrero.getModificadorDefensa(atacante, mapa);
		
		assertEquals(1.20, modificadorDefensa, 0.0001);
	}
}
