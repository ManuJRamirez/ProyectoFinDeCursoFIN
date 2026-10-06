/*
 * 
 */
package learning.tokioschool.proyectoJuegoCombateEsdla.personajes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoTerreno;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.elfos.Elfo;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.orcos.Orco;

// TODO: Auto-generated Javadoc
/**
 * The Class OrcoTest.
 */
public class OrcoTest {	
	
	/**
	 * Crear orco.
	 *
	 * @param tipoCombate the tipo combate
	 * @return the orco
	 */
	private Orco crearOrco(String tipoCombate) {
		Orco orco = new Orco("Orco", 100, 15, 1, 3, Faccion.BESTIAS);
		orco.setTipoCombate(tipoCombate);
		
		return orco;
	}
	
	/**
	 * Reduce la armadura del enemigo 10 porciento siempre.
	 */
	@Test
	void reduceLaArmaduraDelEnemigo10PorcientoSiempre() {
		Orco orco = crearOrco("GUERRERO");
		
		assertEquals(0.9, orco.getModificadorArmaduraEnemigo(), 0.0001);
	}
	
	/**
	 * Caballeria hace 20 por ciento mas danio A arqueros elfos en llano.
	 */
	@Test
	void caballeriaHace20PorCientoMasDanioAArquerosElfosEnLlano() {
		Mapa mapa = new Mapa(5,5);
		Orco caballeria = crearOrco("CABALLERIA");
		Elfo arqueroElfo = new Elfo("Elfo", 100, 10, 3, 3, Faccion.BESTIAS);
		
		mapa.setTerreno(1, 1, TipoTerreno.LLANO);
		arqueroElfo.setTipoCombate("ARQUERO");
		arqueroElfo.setPosicion(1, 1);
		
		double multiplicador = caballeria.getMultiplicadorAtaque(arqueroElfo, mapa);
		
		assertEquals(1.20, multiplicador, 0.0001);
	}
	
	/**
	 * Caballeria pierde 35 por ciento de ataque si combate dentro de una ciudad.
	 */
	@Test
	void caballeriaPierde35PorCientoDeAtaqueSiCombateDentroDeUnaCiudad() {
		Mapa mapa = new Mapa(5,5);
		Ciudad ciudad = new Ciudad("Ciudad");
		Orco caballeria = crearOrco("CABALLERIA");
		Elfo elfoEnemigo = new Elfo("elfo", 100, 10, 1, 3, Faccion.HEROES);
		
		mapa.addCiudad(ciudad);
		ciudad.addCasillaCiudad(2, 2);
		ciudad.addCasillaCiudad(1, 0);
		caballeria.setPosicion(2, 2);
		elfoEnemigo.setPosicion(1, 1);
		
		double multiplicador = caballeria.getMultiplicadorAtaque(elfoEnemigo, mapa);
		
		assertEquals(0.65, multiplicador, 0.0001);
	}

}
