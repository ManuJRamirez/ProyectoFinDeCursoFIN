package learning.tokioschool.proyectoJuegoCombateEsdla.personajes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.elfos.Elfo;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.orcos.Orco;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.trasgos.Trasgo;

// TODO: Auto-generated Javadoc
/**
 * The Class ElfoTest.
 */
public class ElfoTest {
	
	/**
	 * Crear elfo.
	 *
	 * @param tipoCombate the tipo combate
	 * @return the elfo
	 */
	private Elfo crearElfo(String tipoCombate) {
		Elfo elfo = new Elfo("ElfoPruebas", 100, 10, 3, 3, Faccion.HEROES);
		elfo.setTipoCombate(tipoCombate);
		
		return elfo;
	}
	
	/**
	 * Arquero hace 25 por ciento mas danio A nazgul.
	 */
	@Test
	void arqueroHace25PorCientoMasDanioANazgul() {
		Elfo arquero = crearElfo("ARQUERO");
		Nazgul nazgul = new Nazgul("Nazgul", 100, 10, 1, 3, Faccion.BESTIAS);
		double multiplicador = arquero.getMultiplicadorAtaque(nazgul, null);
		
		assertEquals(1.25, multiplicador, 0.0001);
	}
	
	/**
	 * Mago hace 25 por ciento mas danio A nazgul.
	 */
	@Test
	void magoHace25PorCientoMasDanioANazgul() {
		Elfo mago = crearElfo("MAGO");
		Nazgul nazgul = new Nazgul("Nazgul", 100, 10, 1, 3, Faccion.BESTIAS);
		double multiplicador = mago.getMultiplicadorAtaque(nazgul, null);
		
		assertEquals(1.25, multiplicador, 0.0001);
	}
	
	/**
	 * Guerrero hace diez por ciento mas daño A orcos.
	 */
	@Test
	void guerreroHaceDiezPorCientoMasDañoAOrcos() {
		Elfo guerrero = crearElfo("GUERRERO");
		Orco orco = new Orco("Orco", 100, 10, 1, 3, Faccion.BESTIAS);
		double multiplicador = guerrero.getMultiplicadorAtaque(orco, null);
		
		assertEquals(1.10, multiplicador, 0.0001);
	}
	
	/**
	 * Danio sin bonificadores es mulplicado por uno.
	 */
	@Test
	void danioSinBonificadoresEsMulplicadoPorUno() {
		Elfo guerrero = crearElfo("GUERRERO");
		Trasgo trasgo = new Trasgo("Trasgo", 100, 10, 1, 3, Faccion.BESTIAS);
		double multiplicador = guerrero.getMultiplicadorAtaque(trasgo, null);
		
		assertEquals(1.0, multiplicador, 0.001);
	}
	
	/**
	 * Arquero double ataque al disparar desde ciudad hacia fuera.
	 */
	@Test
	void arqueroDoubleAtaqueAlDispararDesdeCiudadHaciaFuera() {
		Mapa mapa =  new Mapa(5,5);
		Ciudad ciudad = new Ciudad("Prueba");
		Elfo arquero = crearElfo("ARQUERO");
		Orco orcoFueraDeCiudad = new Orco("Orco", 100, 10, 1, 3, Faccion.BESTIAS);
		
		ciudad.addCasillaCiudad(2, 2);
		ciudad.setControlSobreCiudad(ControlCiudad.HEROES);
		mapa.addCiudad(ciudad);
		arquero.setPosicion(2, 2);
		orcoFueraDeCiudad.setPosicion(0, 0);
		
		double multiplicador = arquero.getMultiplicadorAtaque(orcoFueraDeCiudad, mapa);
		
		assertEquals(2.0, multiplicador, 0.0001);		
	}
	
	/**
	 * Mago suma 50 por ciento al atacar desde ciudad hacia fuera.
	 */
	@Test
	void magoSuma50PorCientoAlAtacarDesdeCiudadHaciaFuera() {
		Mapa mapa = new Mapa(5,5);
		Ciudad ciudad = new Ciudad("Prueba");
		Elfo mago = crearElfo("MAGO");
		Orco orcoFueraDeCiudad = new Orco("Orco", 100, 10, 1, 3, Faccion.BESTIAS);
		
		ciudad.addCasillaCiudad(2, 2);
		ciudad.setControlSobreCiudad(ControlCiudad.HEROES);
		mapa.addCiudad(ciudad);
		mago.setPosicion(2, 2);
		orcoFueraDeCiudad.setPosicion(0, 0);
		
		double multiplicador = mago.getMultiplicadorAtaque(orcoFueraDeCiudad, mapa);
		
		assertEquals(1.5, multiplicador, 0.0001);
	}
	
	/**
	 * No hay bonus de muralla si el enemigo seleccionado esta dentro de la ciudad.
	 */
	@Test
	void noHayBonusDeMurallaSiElEnemigoSeleccionadoEstaDentroDeLaCiudad() {
		Mapa mapa = new Mapa(5, 5);
		Ciudad ciudad = new Ciudad("Prueba");
		Elfo arquero = crearElfo("ARQUERO");
		Elfo mago = crearElfo("MAGO");
		Orco OrcoDentroCiudad = new Orco("Orco", 100, 10, 1, 3, Faccion.BESTIAS);
		
		ciudad.addCasillaCiudad(2, 2);
		ciudad.addCasillaCiudad(2, 1);
		ciudad.addCasillaCiudad(0, 0);
		ciudad.setControlSobreCiudad(ControlCiudad.HEROES);
		mapa.addCiudad(ciudad);
		arquero.setPosicion(2, 2);
		mago.setPosicion(2, 1);
		OrcoDentroCiudad.setPosicion(0, 0);
		
		double multiplicadorArquero = arquero.getMultiplicadorAtaque(OrcoDentroCiudad, mapa);
		double multiplicadorMago = mago.getMultiplicadorAtaque(OrcoDentroCiudad, mapa);
		
		assertEquals(1.0, multiplicadorArquero, 0.0001);
		assertEquals(1.0, multiplicadorMago, 0.0001);
	}
	
	/**
	 * Guerrero tiene veinte por ciento mas de armadura defendiendo su ciudad.
	 */
	@Test
	void guerreroTieneVeintePorCientoMasDeArmaduraDefendiendoSuCiudad() {
		Mapa mapa = new Mapa(5,5);
		Ciudad ciudad = new Ciudad("Prueba");
		Elfo guerrero = crearElfo("GUERRERO");
		Orco orcoAtacante = new Orco("Orco", 100, 10, 1, 3, Faccion.BESTIAS);
		
		ciudad.addCasillaCiudad(2, 2);
		ciudad.addCasillaCiudad(2, 1);
		ciudad.setControlSobreCiudad(ControlCiudad.HEROES);
		mapa.addCiudad(ciudad);
		guerrero.setPosicion(2, 2);
		orcoAtacante.setPosicion(2, 1);
		
		double modificadorDefensa = guerrero.getModificadorDefensa(orcoAtacante, mapa);
		
		assertEquals(1.20, modificadorDefensa, 0.0001);
	}
	
}
