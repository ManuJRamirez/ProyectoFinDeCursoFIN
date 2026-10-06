package learning.tokioschool.proyectoJuegoCombateEsdla.partida;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ConfiguracionVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ControladorVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ResultadoPartida;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.TipoCondicionVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;

// TODO: Auto-generated Javadoc
/**
 * The Class ControladorVictoriaTest.
 */
public class ControladorVictoriaTest {

	/**
	 * Crear controlador.
	 *
	 * @param condicionesVictoria the condiciones victoria
	 * @return the controlador victoria
	 */
	private ControladorVictoria crearControlador(TipoCondicionVictoria... condicionesVictoria) {
		EnumSet<TipoCondicionVictoria> condicionesActivas = EnumSet.noneOf(TipoCondicionVictoria.class);
		condicionesActivas.addAll(Arrays.asList(condicionesVictoria));

		ConfiguracionVictoria configuracion = new ConfiguracionVictoria(10, 3, condicionesActivas);

		return new ControladorVictoria(configuracion);
	}

	/**
	 * Crear ciudad controlada por.
	 *
	 * @param nombre the nombre
	 * @param x the x
	 * @param y the y
	 * @param faccion the faccion
	 * @return the ciudad
	 */
	private Ciudad crearCiudadControladaPor(String nombre, int x, int y, ControlCiudad faccion) {
		Ciudad ciudad = new Ciudad(nombre);
		ciudad.addCasillaCiudad(x, y);
		ciudad.setControlSobreCiudad(faccion);

		return ciudad;
	}

	/**
	 * Devuelve null si no hay condiciones de victoria activas.
	 */
	@Test
	void devuelveNullSiNoHayCondicionesDeVictoriaActivas() {
		ControladorVictoria controlador = crearControlador();
		Mapa mapa = new Mapa(5, 5);

		assertNull(controlador.comprobarCondicionesInmediatas(mapa));
	}

	/**
	 * El bando que alcanza el limite de puntos primerogana.
	 */
	@Test
	void elBandoQueAlcanzaElLimiteDePuntosPrimerogana() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.LIMITE_DE_PUNTOS);
		Mapa mapa = new Mapa(5, 5);
		Ciudad ciudad = crearCiudadControladaPor("ciudad", 2, 2, ControlCiudad.HEROES);

		mapa.addCiudad(ciudad);
		controlador.actualizarPuntosPorRonda(mapa);
		controlador.actualizarPuntosPorRonda(mapa);
		controlador.actualizarPuntosPorRonda(mapa);

		ResultadoPartida resultado = controlador.comprobarVictoriaPorLimiteDePuntos();

		assertEquals(Faccion.HEROES, resultado.getGanador());
		assertTrue(controlador.estaPartidaFinalizada());
	}

	/**
	 * Cuando se agotan los turnos gana quien tenga mas puntos.
	 */
	@Test
	void cuandoSeAgotanLosTurnosGanaQuienTengaMasPuntos() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.LIMITE_DE_PUNTOS);
		Mapa mapa = new Mapa(5, 5);
		Ciudad ciudad = crearCiudadControladaPor("ciudad", 0, 0, ControlCiudad.HEROES);

		mapa.addCiudad(ciudad);
		controlador.actualizarPuntosPorRonda(mapa);

		ResultadoPartida resultado = controlador.comprobarVictoriaPorTurnosAgotados(10, mapa);

		assertEquals(Faccion.HEROES, resultado.getGanador());
	}

	/**
	 * Si hay empate A puntos al finalizar turnos Y no hay otra condicion activa el resultado de la partida es empate.
	 */
	@Test
	void siHayEmpateAPuntosAlFinalizarTurnosYNoHayOtraCondicionActivaElResultadoDeLaPartidaEsEmpate() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.LIMITE_DE_PUNTOS,
				TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS);
		Mapa mapa = new Mapa(5, 5);
		ResultadoPartida resultado = controlador.comprobarVictoriaPorTurnosAgotados(10, mapa);

		assertTrue(resultado.esEmpate());
		assertNull(resultado.getGanador());
	}

	/**
	 * La partida no acaba si no se cumple una de las condiciones.
	 */
	@Test
	void laPartidaNoAcabaSiNoSeCumpleUnaDeLasCondiciones() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.LIMITE_DE_PUNTOS);
		Mapa mapa = new Mapa(5, 5);

		assertNull(controlador.comprobarVictoriaPorLimiteDePuntos());
		assertNull(controlador.comprobarVictoriaPorTurnosAgotados(5, mapa));
		assertFalse(controlador.estaPartidaFinalizada());
	}

	/**
	 * Partida termina si se alcanza el limite de puntos aunque no se agoten los turnos maximos.
	 */
	@Test
	void partidaTerminaSiSeAlcanzaElLimiteDePuntosAunqueNoSeAgotenLosTurnosMaximos() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.LIMITE_DE_PUNTOS);
		Mapa mapa = new Mapa(5, 5);
		Ciudad ciudad = crearCiudadControladaPor("ciudad", 2, 2, ControlCiudad.HEROES);

		mapa.addCiudad(ciudad);
		controlador.actualizarPuntosPorRonda(mapa);
		controlador.actualizarPuntosPorRonda(mapa);
		controlador.actualizarPuntosPorRonda(mapa);

		ResultadoPartida resultadoPorPuntos = controlador.comprobarVictoriaPorLimiteDePuntos();

		assertEquals(Faccion.HEROES, resultadoPorPuntos.getGanador());
		assertTrue(controlador.estaPartidaFinalizada());
		assertNull(controlador.comprobarVictoriaPorTurnosAgotados(10, mapa));
	}

	/**
	 * Si solo frodo en monte destino esta activa Y se agotan los turnos sin llegar ganan las bestias.
	 */
	@Test
	void siSoloFrodoEnMonteDestinoEstaActivaYSeAgotanLosTurnosSinLlegarGananLasBestias() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO);
		Mapa mapa = new Mapa(5, 5);

		ResultadoPartida resultado = controlador.comprobarVictoriaPorTurnosAgotados(10, mapa);

		assertEquals(Faccion.BESTIAS, resultado.getGanador());
	}

	/**
	 * Si solo frodo eliminado esta activa Y se agotan los turnos sin ser eliminado ganan los heroes.
	 */
	@Test
	void siSoloFrodoEliminadoEstaActivaYSeAgotanLosTurnosSinSerEliminadoGananLosHeroes() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.FRODO_ELIMINADO);
		Mapa mapa = new Mapa(5, 5);

		ResultadoPartida resultado = controlador.comprobarVictoriaPorTurnosAgotados(10, mapa);

		assertEquals(Faccion.HEROES, resultado.getGanador());
	}

	/**
	 * Si frodo eliminado Y frodo monte destino estan activas Y se agotan los turnos sin que se cumplan es empate.
	 */
	@Test
	void siFrodoEliminadoYFrodoMonteDestinoEstanActivasYSeAgotanLosTurnosSinQueSeCumplanEsEmpate() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.FRODO_ELIMINADO, TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO);
		Mapa mapa = new Mapa(5, 5);

		ResultadoPartida resultado = controlador.comprobarVictoriaPorTurnosAgotados(10, mapa);

		assertTrue(resultado.esEmpate());
		assertNull(resultado.getGanador());
	}

	/**
	 * Si solo dominio esta activo Y se agotan los turnos gana quien tenga mas ciudades.
	 */
	@Test
	void siSoloDominioEstaActivoYSeAgotanLosTurnosGanaQuienTengaMasCiudades() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.DOMINIO);
		Mapa mapa = new Mapa(5, 5);
		Ciudad ciudad1 = crearCiudadControladaPor("ciudad1", 2, 2, ControlCiudad.HEROES);
		Ciudad ciudad2 = crearCiudadControladaPor("ciudad2", 2, 3, ControlCiudad.HEROES);
		Ciudad ciudad3 = crearCiudadControladaPor("ciudad3", 3, 2, ControlCiudad.BESTIAS);

		mapa.addCiudad(ciudad1);
		mapa.addCiudad(ciudad2);
		mapa.addCiudad(ciudad3);

		ResultadoPartida resultado = controlador.comprobarVictoriaPorTurnosAgotados(10, mapa);

		assertEquals(Faccion.HEROES, resultado.getGanador());
	}

	/**
	 * Si domino Y las dos condiciones de frodo estan activas Y frodo no esta en el monte Y esta vivo dominio decide el ganador al finalizar turnos.
	 */
	@Test
	void siDominoYLasDosCondicionesDeFrodoEstanActivasYFrodoNoEstaEnElMonteYEstaVivoDominioDecideElGanadorAlFinalizarTurnos() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.DOMINIO, TipoCondicionVictoria.FRODO_ELIMINADO,
				TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO);
		Mapa mapa = new Mapa(5, 5);

		Ciudad ciudad1 = crearCiudadControladaPor("ciudad1", 2, 2, ControlCiudad.HEROES);
		Ciudad ciudad2 = crearCiudadControladaPor("ciudad2", 2, 3, ControlCiudad.BESTIAS);
		Ciudad ciudad3 = crearCiudadControladaPor("ciudad3", 3, 2, ControlCiudad.BESTIAS);

		mapa.addCiudad(ciudad1);
		mapa.addCiudad(ciudad2);
		mapa.addCiudad(ciudad3);

		ResultadoPartida resultado = controlador.comprobarVictoriaPorTurnosAgotados(10, mapa);

		assertEquals(Faccion.BESTIAS, resultado.getGanador());
	}

	/**
	 * Si todas las condiciones estan activas Y se agotan los turnos se prioriza la cantidad de puntos.
	 */
	@Test
	void siTodasLasCondicionesEstanActivasYSeAgotanLosTurnosSePriorizaLaCantidadDePuntos() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.LIMITE_DE_PUNTOS, TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO,
				TipoCondicionVictoria.FRODO_ELIMINADO, TipoCondicionVictoria.DOMINIO);
		Mapa mapa = new Mapa(5, 5);
		Ciudad ciudad1 = crearCiudadControladaPor("ciudad1", 2, 2, ControlCiudad.HEROES);
		Ciudad ciudad2 = crearCiudadControladaPor("ciudad2", 2, 3, ControlCiudad.BESTIAS);
		Ciudad ciudad3 = crearCiudadControladaPor("ciudad3", 3, 2, ControlCiudad.BESTIAS);

		mapa.addCiudad(ciudad1);
		controlador.actualizarPuntosPorRonda(mapa);

		mapa.addCiudad(ciudad2);
		mapa.addCiudad(ciudad3);

		ResultadoPartida resultado = controlador.comprobarVictoriaPorTurnosAgotados(10, mapa);

		assertEquals(Faccion.HEROES, resultado.getGanador());
	}

	/**
	 * Si todas las condiciones estan activas Y estan empatados A puntos Y se agotan los turnos decide el dominio.
	 */
	@Test
	void siTodasLasCondicionesEstanActivasYEstanEmpatadosAPuntosYSeAgotanLosTurnosDecideElDominio() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.LIMITE_DE_PUNTOS, TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO,
				TipoCondicionVictoria.FRODO_ELIMINADO, TipoCondicionVictoria.DOMINIO);
		Mapa mapa = new Mapa(5, 5);
		Ciudad ciudad1 = crearCiudadControladaPor("ciudad1", 2, 2, ControlCiudad.HEROES);
		Ciudad ciudad2 = crearCiudadControladaPor("ciudad2", 2, 3, ControlCiudad.BESTIAS);
		Ciudad ciudad3 = crearCiudadControladaPor("ciudad3", 3, 2, ControlCiudad.BESTIAS);

		mapa.addCiudad(ciudad1);
		mapa.addCiudad(ciudad2);
		mapa.addCiudad(ciudad3);

		ResultadoPartida resultado = controlador.comprobarVictoriaPorTurnosAgotados(10, mapa);

		assertEquals(Faccion.BESTIAS, resultado.getGanador());
	}

	/**
	 * Si todas las condiciones estan activas Y estan todas igualadas entonces sera empate definitivo al acabar los turnos.
	 */
	@Test
	void siTodasLasCondicionesEstanActivasYEstanTodasIgualadasEntoncesSeraEmpateDefinitivoAlAcabarLosTurnos() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.LIMITE_DE_PUNTOS, TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO,
				TipoCondicionVictoria.FRODO_ELIMINADO, TipoCondicionVictoria.DOMINIO);
		Mapa mapa = new Mapa(5, 5);
		Ciudad ciudad1 = crearCiudadControladaPor("ciudad1", 2, 2, ControlCiudad.HEROES);
		Ciudad ciudad2 = crearCiudadControladaPor("ciudad2", 2, 3, ControlCiudad.BESTIAS);

		mapa.addCiudad(ciudad1);
		mapa.addCiudad(ciudad2);

		ResultadoPartida resultado = controlador.comprobarVictoriaPorTurnosAgotados(10, mapa);

		assertTrue(resultado.esEmpate());
		assertNull(resultado.getGanador());
	}

	/**
	 * Frodo gana la partida resistiendo tres turnos sin ser atacado en el monte del destino.
	 */
	@Test
	void frodoGanaLaPartidaResistiendoTresTurnosSinSerAtacadoEnElMonteDelDestino() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO);
		Mapa mapa = new Mapa(5, 5);
		Ciudad MonteDestino = new Ciudad("Monte del Destino");
		Hobbit frodo = new Hobbit("Frodo", 100, 10, 1, 3, Faccion.HEROES);

		MonteDestino.addCasillaCiudad(2, 2);
		mapa.addCiudad(MonteDestino);
		mapa.colocarUnidad(2, 2, frodo);

		assertNull(controlador.actualizarProgresoFrodoMonteDelDestino(mapa));
		assertNull(controlador.actualizarProgresoFrodoMonteDelDestino(mapa));

		ResultadoPartida resultado = controlador.actualizarProgresoFrodoMonteDelDestino(mapa);

		assertEquals(Faccion.HEROES, resultado.getGanador());
	}

	/**
	 * Si frodo es atacado en el monte del destino el contador se reinicia.
	 */
	@Test
	void siFrodoEsAtacadoEnElMonteDelDestinoElContadorSeReinicia() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO);
		Mapa mapa = new Mapa(5, 5);
		Ciudad MonteDestino = new Ciudad("Monte del Destino");
		Hobbit frodo = new Hobbit("Frodo", 100, 10, 1, 3, Faccion.HEROES);

		MonteDestino.addCasillaCiudad(2, 2);
		mapa.addCiudad(MonteDestino);
		mapa.colocarUnidad(2, 2, frodo);

		controlador.actualizarProgresoFrodoMonteDelDestino(mapa);
		frodo.recibirDanio(1);
		controlador.actualizarProgresoFrodoMonteDelDestino(mapa);

		assertEquals(0, controlador.getRondasFrodoEnMonteSinAtaque());
	}

	/**
	 * Bestias ganan si condicion frodo eliminado esta activa Y eliminan A frodo.
	 */
	@Test
	void bestiasGananSiCondicionFrodoEliminadoEstaActivaYEliminanAFrodo() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS,
				TipoCondicionVictoria.FRODO_ELIMINADO);
		Mapa mapa = new Mapa(5, 5);
		Hobbit frodo = new Hobbit("Frodo", 100, 10, 1, 3, Faccion.HEROES);

		mapa.colocarUnidad(2, 2, frodo);
		frodo.recibirDanio(999);

		ResultadoPartida resultado = controlador.comprobarVictoriaPorFrodoEliminado(mapa);

		assertEquals(Faccion.BESTIAS, resultado.getGanador());
	}

	/**
	 * Partida no acaba si condicion frodo eliminado esta activa Y no consiguen eliminar A frodo.
	 */
	@Test
	void partidaNoAcabaSiCondicionFrodoEliminadoEstaActivaYNoConsiguenEliminarAFrodo() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO,
				TipoCondicionVictoria.FRODO_ELIMINADO);
		Mapa mapa = new Mapa(5, 5);
		Hobbit frodo = new Hobbit("Frodo", 100, 10, 1, 3, Faccion.HEROES);

		mapa.colocarUnidad(2, 2, frodo);

		assertNull(controlador.comprobarVictoriaPorFrodoEliminado(mapa));
	}
	
	/**
	 * Los heroes ganan por dominio si conquistan todas las ciudades de las bestias.
	 */
	@Test
	void losHeroesGananPorDominioSiConquistanTodasLasCiudadesDeLasBestias() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.DOMINIO);
		Mapa mapa = new Mapa(5,5);
		Ciudad ciudadNatalBestias = crearCiudadControladaPor("ciudadBestias", 2, 2, ControlCiudad.BESTIAS);
		
		mapa.addCiudad(ciudadNatalBestias);
		ciudadNatalBestias.fijarControlInicial();
		ciudadNatalBestias.setControlSobreCiudad(ControlCiudad.HEROES);
	
		ResultadoPartida resultado = controlador.comprobarVictoriaPorDominio(mapa);
		
		assertEquals(Faccion.HEROES, resultado.getGanador());
	}
	
	/**
	 * No hay victoria por dominio si queda almenos una ciudad enemiga sin conquistar.
	 */
	@Test
	void noHayVictoriaPorDominioSiQuedaAlmenosUnaCiudadEnemigaSinConquistar() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.DOMINIO);
		Mapa mapa = new Mapa(5,5);
		Ciudad ciudadConquistada = crearCiudadControladaPor("ciudadConquistada", 2, 2, ControlCiudad.BESTIAS);
		Ciudad ciudadNatal = crearCiudadControladaPor("ciudadNatal", 2, 3, ControlCiudad.BESTIAS);
		
		ciudadNatal.fijarControlInicial();
		mapa.addCiudad(ciudadNatal);
		mapa.addCiudad(ciudadConquistada);
		
		ciudadNatal.setControlSobreCiudad(ControlCiudad.HEROES);
		
		assertNull(controlador.comprobarVictoriaPorDominio(mapa));
	}
	
	/**
	 * Ganan por dominio los heroes si conquistan todas las ciudades de las bestias.
	 */
	@Test
	void gananPorDominioLosHeroesSiConquistanTodasLasCiudadesDeLasBestias() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.DOMINIO);
		Mapa mapa = new Mapa(5,5);
		Ciudad ciudadConquistada = crearCiudadControladaPor("ciudadConquistada", 2, 2, ControlCiudad.BESTIAS);
		Ciudad ciudadNatal = crearCiudadControladaPor("ciudadNatal", 2, 3, ControlCiudad.BESTIAS);
		
		ciudadNatal.fijarControlInicial();
		mapa.addCiudad(ciudadNatal);
		mapa.addCiudad(ciudadConquistada);
		
		ciudadNatal.setControlSobreCiudad(ControlCiudad.HEROES);
		ciudadConquistada.setControlSobreCiudad(ControlCiudad.HEROES);
		
		ResultadoPartida resultado = controlador.comprobarVictoriaPorDominio(mapa);
		
		assertEquals(Faccion.HEROES, resultado.getGanador());
	}
	
	/**
	 * Ganan por dominio las bestias si conquistan todas las ciudades de los heroes.
	 */
	@Test
	void gananPorDominioLasBestiasSiConquistanTodasLasCiudadesDeLosHeroes() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.DOMINIO);
		Mapa mapa = new Mapa(5,5);
		Ciudad ciudadConquistada = crearCiudadControladaPor("ciudadConquistada", 2, 2, ControlCiudad.HEROES);
		Ciudad ciudadNatal = crearCiudadControladaPor("ciudadNatal", 2, 3, ControlCiudad.HEROES);
		
		ciudadNatal.fijarControlInicial();
		mapa.addCiudad(ciudadNatal);
		mapa.addCiudad(ciudadConquistada);
		
		ciudadNatal.setControlSobreCiudad(ControlCiudad.BESTIAS);
		ciudadConquistada.setControlSobreCiudad(ControlCiudad.BESTIAS);
		
		ResultadoPartida resultado = controlador.comprobarVictoriaPorDominio(mapa);
		
		assertEquals(Faccion.BESTIAS, resultado.getGanador());
	}
	
	/**
	 * Si un bando se rinde gana el contrario.
	 */
	@Test
	void siUnBandoSeRindeGanaElContrario() {
		ControladorVictoria controlador = crearControlador(TipoCondicionVictoria.DOMINIO);
		
		ResultadoPartida resultado = controlador.rendirse(Faccion.HEROES);
		
		assertEquals(Faccion.BESTIAS, resultado.getGanador());
		assertTrue(controlador.estaPartidaFinalizada());
	}
}
