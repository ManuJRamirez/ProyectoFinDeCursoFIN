package learning.tokioschool.proyectoJuegoCombateEsdla.mapa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoEstadoConquista;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.humanos.Humano;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.orcos.Orco;

// TODO: Auto-generated Javadoc
/**
 * The Class CiudadYConquistaTest.
 */
public class CiudadYConquistaTest {
	
	/**
	 * Crear ciudad.
	 *
	 * @return the ciudad
	 */
	private Ciudad crearCiudad() {
		Ciudad ciudad = new Ciudad("Ciudad");
		ciudad.addCasillaCiudad(2, 2);
		return ciudad;
	}
	
	/**
	 * Hace falta tres turnos para conquistar una ciudad.
	 */
	@Test
	void haceFaltaTresTurnosParaConquistarUnaCiudad() {
		Ciudad ciudad = crearCiudad();
		Humano humano = new Humano("Humano", 100, 10, 1, 3, Faccion.HEROES);
		
		ciudad.addUnidad(humano);
		
		assertNull(ciudad.actualizarProgresoDeConquista());
		assertNull(ciudad.actualizarProgresoDeConquista());
		
		Faccion equipoQueConquista = ciudad.actualizarProgresoDeConquista();
		
		assertEquals(Faccion.HEROES, equipoQueConquista);
		assertEquals(ControlCiudad.HEROES, ciudad.getControlSobreCiudad());
	}
	
	/**
	 * Si hay unidades de ambas facciones la conquista se pausa Y entra en disputa.
	 */
	@Test
	void siHayUnidadesDeAmbasFaccionesLaConquistaSePausaYEntraEnDisputa() {
		Ciudad ciudad = crearCiudad();
		Humano humano = new Humano("Humano", 100, 10, 1, 3, Faccion.HEROES);
		Orco orco = new Orco("Orco", 100, 10, 1, 3, Faccion.BESTIAS);
		
		ciudad.addUnidad(orco);
		ciudad.addUnidad(humano);
		
		Faccion resultadoConquista = ciudad.actualizarProgresoDeConquista();
		
		assertNull(resultadoConquista);
		assertEquals(0, ciudad.getTurnosDeControlContinuo());
		assertEquals(ControlCiudad.NEUTRAL, ciudad.getControlSobreCiudad());
	}
	
	/**
	 * Nazgul no influyen en la conquista O defensa de una ciudad.
	 */
	@Test
	void nazgulNoInfluyenEnLaConquistaODefensaDeUnaCiudad() {
		Ciudad ciudad = crearCiudad();
		Nazgul nazgul = new Nazgul("Nazgul", 100, 10, 1, 3, Faccion.BESTIAS);
		
		ciudad.addUnidad(nazgul);
		
		Faccion resultadoTurno1 = ciudad.actualizarProgresoDeConquista();
		Faccion resultadoTurno2 = ciudad.actualizarProgresoDeConquista();
		Faccion resultadoTurno3 = ciudad.actualizarProgresoDeConquista();
		
		assertNull(resultadoTurno1);
		assertNull(resultadoTurno2);
		assertNull(resultadoTurno3);
		assertEquals(ControlCiudad.NEUTRAL, ciudad.getControlSobreCiudad());
	}
	
	/**
	 * Hobbit no influyen en la conquista O defensa de una ciudad.
	 */
	@Test
	void hobbitNoInfluyenEnLaConquistaODefensaDeUnaCiudad() {
		Ciudad ciudad = crearCiudad();
		Hobbit hobbit = new Hobbit("Hobbit", 100, 10, 1, 3, Faccion.HEROES);
		
		ciudad.addUnidad(hobbit);
		
		Faccion resultadoTurno1 = ciudad.actualizarProgresoDeConquista();
		Faccion resultadoTurno2 = ciudad.actualizarProgresoDeConquista();
		Faccion resultadoTurno3 = ciudad.actualizarProgresoDeConquista();
		
		assertNull(resultadoTurno1);
		assertNull(resultadoTurno2);
		assertNull(resultadoTurno3);
		assertEquals(ControlCiudad.NEUTRAL, ciudad.getControlSobreCiudad());
	}
	
	/**
	 * Una unidad eliminada en una ciudad no cuenta para su conquista.
	 */
	@Test
	void unaUnidadEliminadaEnUnaCiudadNoCuentaParaSuConquista() {
		Ciudad ciudad = crearCiudad();
		Humano humanoEliminado = new Humano("HumanoCaido", 100, 10, 1,3,Faccion.HEROES);
		
		ciudad.addUnidad(humanoEliminado);
		humanoEliminado.recibirDanio(999);
		
		Faccion resultadoTurno1 = ciudad.actualizarProgresoDeConquista();
		Faccion resultadoTurno2 = ciudad.actualizarProgresoDeConquista();
		Faccion resultadoTurno3 = ciudad.actualizarProgresoDeConquista();
		
		assertNull(resultadoTurno1);
		assertNull(resultadoTurno2);
		assertNull(resultadoTurno3);
		assertEquals(ControlCiudad.NEUTRAL, ciudad.getControlSobreCiudad());
	}
	
	/**
	 * Si una unidad heroe O bestia esta conquistando Y sale de la ciudad reinicia el contador.
	 */
	@Test
	void siUnaUnidadHeroeOBestiaEstaConquistandoYSaleDeLaCiudadReiniciaElContador() {
		Ciudad ciudad = crearCiudad();
		Humano humano = new Humano("Aragon", 100, 10, 1, 3, Faccion.HEROES);
		Orco orco = new Orco("Orco", 100, 10, 1, 3, Faccion.BESTIAS);
		
		ciudad.addUnidad(humano);
		ciudad.actualizarProgresoDeConquista();
		ciudad.actualizarProgresoDeConquista();
		ciudad.salirCiudad(humano);
		ciudad.addUnidad(orco);
		ciudad.actualizarProgresoDeConquista();
		
		assertEquals(1, ciudad.getTurnosDeControlContinuo());
		assertEquals(ControlCiudad.NEUTRAL, ciudad.getControlSobreCiudad());
	}
	
	/**
	 * Estado visible de conquista en disputa.
	 */
	@Test
	void estadoVisibleDeConquistaEnDisputa() {
		Ciudad ciudad = crearCiudad();
		Humano humano = new Humano("Humano", 100, 10, 1, 3, Faccion.HEROES);
		Orco orco = new Orco("Orco", 100, 10, 1, 3, Faccion.BESTIAS);
		
		ciudad.addUnidad(orco);
		ciudad.addUnidad(humano);
		
		assertEquals(TipoEstadoConquista.DISPUTA, ciudad.getEstadoConquistaVisible().getTipo());
	}
	
	/**
	 * Estado visible de conquista en reposo.
	 */
	@Test
	void estadoVisibleDeConquistaEnReposo() {
		Ciudad ciudad = crearCiudad();
		Humano humano = new Humano("Humano", 100, 10, 1, 3, Faccion.HEROES);
		
		ciudad.setControlSobreCiudad(ControlCiudad.HEROES);
		ciudad.addUnidad(humano);
		
		assertEquals(TipoEstadoConquista.REPOSO, ciudad.getEstadoConquistaVisible().getTipo());
	}
	
	/**
	 * Caballeria no puede atacar desde fuera de ciudad hacia dentro.
	 */
	@Test
	void caballeriaNoPuedeAtacarDesdeFueraDeCiudadHaciaDentro() {
		Mapa mapa = new Mapa(5,5);
		Ciudad ciudad = crearCiudad();
		Humano caballeria = new Humano("Caballeria", 100, 10, 1,3, Faccion.HEROES);
		Orco orcoDefensor = new Orco("Orco", 100, 10, 1, 3,Faccion.BESTIAS);
		
		mapa.addCiudad(ciudad);
		ciudad.addCasillaCiudad(2, 2);
		orcoDefensor.setPosicion(2, 2);
		caballeria.setPosicion(2, 3);
		caballeria.setTipoCombate("CABALLERIA");
		
		boolean puedeAtacar = caballeria.puedeAtacarObjetivo(orcoDefensor, mapa);		
		
		assertFalse(puedeAtacar);
	}
	
	/**
	 * Caballeria si puede atacar desde dentro de la ciudad hacia dentro.
	 */
	@Test
	void caballeriaSiPuedeAtacarDesdeDentroDeLaCiudadHaciaDentro() {
		Mapa mapa = new Mapa(5,5);
		Ciudad ciudad = crearCiudad();
		Humano caballeria = new Humano("Caballeria", 100, 10, 1,3, Faccion.HEROES);
		Orco orcoDefensor = new Orco("Orco", 100, 10, 1, 3,Faccion.BESTIAS);
		
		mapa.addCiudad(ciudad);
		ciudad.addCasillaCiudad(2, 2);
		ciudad.addCasillaCiudad(2, 3);
		orcoDefensor.setPosicion(2, 2);
		caballeria.setPosicion(2, 3);
		caballeria.setTipoCombate("CABALLERIA");
		
		boolean puedeAtacar = caballeria.puedeAtacarObjetivo(orcoDefensor, mapa);
		
		assertTrue(puedeAtacar);
	}
	
	/**
	 * Guerrero si puede atacar desde fuera hacia dentro.
	 */
	@Test
	void guerreroSiPuedeAtacarDesdeFueraHaciaDentro() {
		Mapa mapa = new Mapa(5,5);
		Ciudad ciudad = crearCiudad();
		Humano guerrero = new Humano("Guerrero", 100, 10, 1,3, Faccion.HEROES);
		Orco orcoDefensor = new Orco("Orco", 100, 10, 1, 3,Faccion.BESTIAS);
		
		mapa.addCiudad(ciudad);
		ciudad.addCasillaCiudad(2, 2);
		orcoDefensor.setPosicion(2, 2);
		guerrero.setPosicion(2, 3);
		guerrero.setTipoCombate("GUERRERO");
		
		boolean puedeAtacar = guerrero.puedeAtacarObjetivo(orcoDefensor, mapa);
		
		assertTrue(puedeAtacar);
	}
	
	/**
	 * Faccion que controla una ciudad inicialmente neutral genera puntos.
	 */
	@Test
	void faccionQueControlaUnaCiudadInicialmenteNeutralGeneraPuntos() {
		Ciudad ciudad = crearCiudad();
		
		ciudad.setControlSobreCiudad(ControlCiudad.HEROES);
		
		assertEquals(Faccion.HEROES, ciudad.getFaccionQueGeneraPunto());
	}
	
	/**
	 * Ciudad inicialmente natal de una faccion solo genera puntos si es conquistada por la otra faccion.
	 */
	@Test
	void ciudadInicialmenteNatalDeUnaFaccionSoloGeneraPuntosSiEsConquistadaPorLaOtraFaccion() {
		Ciudad ciudad = crearCiudad();
		
		ciudad.setControlSobreCiudad(ControlCiudad.HEROES);
		ciudad.fijarControlInicial();
		ciudad.setControlSobreCiudad(ControlCiudad.BESTIAS);
		
		assertEquals(Faccion.BESTIAS, ciudad.getFaccionQueGeneraPunto());
	}
	
	/**
	 * Ciudad natal no genera puntos si la conserva la misma faccion.
	 */
	@Test
	void ciudadNatalNoGeneraPuntosSiLaConservaLaMismaFaccion() {
		Ciudad ciudad = crearCiudad();
		
		ciudad.setControlSobreCiudad(ControlCiudad.HEROES);
		ciudad.fijarControlInicial();
		
		assertNull(ciudad.getFaccionQueGeneraPunto());
	}
}
