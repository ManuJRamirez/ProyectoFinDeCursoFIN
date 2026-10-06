package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;

// TODO: Auto-generated Javadoc
/**
 * The Class ControladorVictoria.
 * 
 * Clase controlador encargada de comprobar, ronda a ronda, si se cumple alguna
 * condicion de victoria activa. Ademas verificará si alguna faccion se rinde.
 */
public class ControladorVictoria {

	/** Atributo estatico donde guardaremos el nombre de la ciudad. */
	private static final String NOMBRE_CIUDAD_MONTE_DEL_DESTINO = "Monte del Destino";

	/** Atributo estatico donde guardaremos el nombre de frodo. */
	private static final String NOMBRE_FRODO = "Frodo";

	/**
	 * Atributo estatico definimos las rondas que frodo debe estar dentro de la
	 * ciudad Monte del destino sin ser atacado.
	 */
	public static final int RONDAS_NECESARIAS_EN_MONTE_DEL_DESTINO = 3;

	/** The configuracion. */
	private final ConfiguracionVictoria configuracion;

	/** Contador de puntos del bando Heroes. */
	private int puntosHeroes = 0;

	/** Contador de puntos del bando Bestias. */
	private int puntosBestias = 0;

	/**
	 * Contador de rondas que esta frodo sin ser atacado dentro del monte del
	 * destino.
	 */
	private int rondasFrodoEnMonteSinAtaque = 0;

	/** Atributo para saber si la partida ha concluido. */
	private boolean partidaFinalizada = false;

	/**
	 * Constructor de clase en el que instanciamos un nuevo controlador con las
	 * condiciones de victoria indicadas.
	 *
	 * @param configuracion the configuracion
	 */
	public ControladorVictoria(ConfiguracionVictoria configuracion) {
		this.configuracion = configuracion;
	}

	/**
	 * Metodo para saber si la partida ya ha concluido.
	 *
	 * @return true, if successful
	 */
	public boolean estaPartidaFinalizada() {
		return partidaFinalizada;
	}

	/**
	 * Metodo para actualizar los puntos de conquista despues de cada ronda.
	 *
	 * @param mapa the mapa
	 */
	public void actualizarPuntosPorRonda(Mapa mapa) {
		if (partidaFinalizada || mapa == null) {
			return;
		}
		for (Ciudad ciudad : mapa.getCiudades()) {
			Faccion faccionQueGanaPunto = ciudad.getFaccionQueGeneraPunto();
			if (faccionQueGanaPunto == Faccion.HEROES) {
				puntosHeroes++;
			} else if (faccionQueGanaPunto == Faccion.BESTIAS) {
				puntosBestias++;
			}
		}
	}

	/**
	 * Metodo con el que comprobamos si alguna faccion ha alcanzado el limite de
	 * puntos definido para producirse una Victoria.
	 *
	 * @return the resultado partida Nos devolvera un resultado de partida con el
	 *         ganador/empate o null si aun no se ha cumplido.
	 */
	public ResultadoPartida comprobarVictoriaPorLimiteDePuntos() {
		if (partidaFinalizada || !configuracion.estaActiva(TipoCondicionVictoria.LIMITE_DE_PUNTOS)) {
			return null;
		}
		int limite = configuracion.getPuntosMaximos();
		if (limite <= 0) {
			return null;
		}
		boolean heroesLleganLimitePuntos = puntosHeroes >= limite;
		boolean bestiasLleganLimitePuntos = puntosBestias >= limite;

		if (!heroesLleganLimitePuntos && !bestiasLleganLimitePuntos) {
			return null;
		}

		if (heroesLleganLimitePuntos && bestiasLleganLimitePuntos) {
			if (puntosHeroes == puntosBestias) {
				return finalizar(new ResultadoPartida(null, "ambos bandos han alcanzado el límite de " + limite
						+ " puntos a la vez (" + puntosHeroes + " - " + puntosBestias + ")"));
			}
			Faccion ganador = puntosHeroes > puntosBestias ? Faccion.HEROES : Faccion.BESTIAS;
			return finalizar(new ResultadoPartida(ganador, nombreFaccion(ganador) + " han alcanzado antes el límite de "
					+ limite + " puntos (" + puntosHeroes + " - " + puntosBestias + ")"));
		}
		Faccion ganador = heroesLleganLimitePuntos ? Faccion.HEROES : Faccion.BESTIAS;
		return finalizar(new ResultadoPartida(ganador,
				nombreFaccion(ganador) + " han alcanzado el límite de " + limite + " puntos"));
	}

	/**
	 * Metodo con el que comoprobamos si la partida ha alcanzado el limite de turnos
	 * definidos. En el caso de agotar los turnos maximos, valoraremos otra
	 * condicion de victoria activa. La faccion que tenga mas puntos, o la que tenga
	 * mas ciudades, etc.
	 *
	 * @param rondaQueAcabaDeTerminar Le pasamos el numero de ronda que acaba de
	 *                                terminar.
	 * @param mapa                    the mapa
	 * @return the resultado partida Nos devolvera un resultado de partida con el
	 *         ganador/empate o null si aun no se ha cumplido.
	 */
	public ResultadoPartida comprobarVictoriaPorTurnosAgotados(int rondaQueAcabaDeTerminar, Mapa mapa) {
		if (partidaFinalizada || !configuracion.estaActiva(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS)) {
			return null;
		}

		int maximo = configuracion.getTurnosMaximos();
		if (maximo <= 0 || rondaQueAcabaDeTerminar < maximo) {
			return null;
		}

		if (configuracion.estaActiva(TipoCondicionVictoria.LIMITE_DE_PUNTOS) && mapa != null
				&& puntosHeroes != puntosBestias) {
			Faccion ganador = puntosHeroes > puntosBestias ? Faccion.HEROES : Faccion.BESTIAS;
			int puntosGanador = Math.max(puntosHeroes, puntosBestias);
			int puntosPerdedor = Math.min(puntosHeroes, puntosBestias);

			return finalizar(
					new ResultadoPartida(ganador, nombreFaccion(ganador) + " terminan la partida con más puntos ("
							+ puntosGanador + " - " + puntosPerdedor + ") al agotarse los " + maximo + " turnos"));
		}

		if (configuracion.estaActiva(TipoCondicionVictoria.DOMINIO) && mapa != null) {
			int ciudadesFaccionHeroes = contarCiudadesControladasPorFaccion(mapa, ControlCiudad.HEROES);
			int ciudadesFaccionBestia = contarCiudadesControladasPorFaccion(mapa, ControlCiudad.BESTIAS);

			if (ciudadesFaccionHeroes != ciudadesFaccionBestia) {
				Faccion ganador = ciudadesFaccionHeroes > ciudadesFaccionBestia ? Faccion.HEROES : Faccion.BESTIAS;
				int ciudadesDelGanador = Math.max(ciudadesFaccionHeroes, ciudadesFaccionBestia);
				int ciudadesDelPerdedor = Math.min(ciudadesFaccionHeroes, ciudadesFaccionBestia);

				return finalizar(new ResultadoPartida(ganador,
						nombreFaccion(ganador) + " terminan la partida controlando más ciudades (" + ciudadesDelGanador
								+ " - " + ciudadesDelPerdedor + ") al agotarse los " + maximo + " turnos"));
			}
		}

		boolean frodoMonteDelDestinoEstaActiva = configuracion
				.estaActiva(TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO);
		boolean frodoEleminadoEstaActiva = configuracion.estaActiva(TipoCondicionVictoria.FRODO_ELIMINADO);

		if (frodoMonteDelDestinoEstaActiva && !frodoEleminadoEstaActiva) {
			return finalizar(new ResultadoPartida(Faccion.BESTIAS,
					"Frodo no ha llegado al Monte del Destino al agotarse los " + maximo + " turnos"));
		}
		if (frodoEleminadoEstaActiva && !frodoMonteDelDestinoEstaActiva) {
			return finalizar(
					new ResultadoPartida(Faccion.HEROES, "Frodo sigue con vida al agotarse los " + maximo + " turnos"));
		}

		return finalizar(new ResultadoPartida(null, "empate al agotarse los " + maximo + " turnos"));
	}

	/**
	 * Metodo para contar cuantas ciudades posee cada faccion.
	 *
	 * @param mapa                     the mapa
	 * @param faccionQueControlaCiudad the faccion que controla ciudad
	 * @return the int Numero de ciudades controlada por esa faccion
	 */
	private int contarCiudadesControladasPorFaccion(Mapa mapa, ControlCiudad faccionQueControlaCiudad) {
		int contador = 0;
		for (Ciudad ciudad : mapa.getCiudades()) {
			if (ciudad.getControlSobreCiudad() == faccionQueControlaCiudad) {
				contador++;
			}
		}
		return contador;
	}

	/**
	 * Metodo para actualizar el progreso de Frodo en el Monte del Destino. Una vez
	 * que Frodo entra en la ciudad, empezara a contar cada ronda que esta dentro y
	 * NO es atacado. Si recibe ataque se reiniciara este contador y si llega al
	 * maximo numero de rondas sin ser atacado, se producira una victoria para los
	 * heroes
	 *
	 * @param mapa the mapa
	 * @return the resultado partida Nos devolvera un resultado de partida con el
	 *         ganador o null si aun no se ha cumplido.
	 */
	public ResultadoPartida actualizarProgresoFrodoMonteDelDestino(Mapa mapa) {
		if (partidaFinalizada || mapa == null
				|| !configuracion.estaActiva(TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO)) {
			return null;
		}
		Personaje frodo = mapa.getTodasLasUnidades().stream()
				.filter(pj -> pj != null && pj.estaVivo() && NOMBRE_FRODO.equalsIgnoreCase(pj.getNombre())).findFirst()
				.orElse(null);
		if (frodo == null) {
			rondasFrodoEnMonteSinAtaque = 0;
			return null;
		}
		Ciudad ciudadDeFrodo = mapa.buscarCiudadesPorCasilla(frodo.getX(), frodo.getY());
		boolean estaEnMonteDelDestino = ciudadDeFrodo != null
				&& NOMBRE_CIUDAD_MONTE_DEL_DESTINO.equalsIgnoreCase(ciudadDeFrodo.getNombreCiudad());

		if (!estaEnMonteDelDestino) {
			rondasFrodoEnMonteSinAtaque = 0;
			return null;
		}
		if (frodo.isHasidoAtacadoEstaRonda()) {
			rondasFrodoEnMonteSinAtaque = 0;
			return null;
		}
		rondasFrodoEnMonteSinAtaque++;

		if (rondasFrodoEnMonteSinAtaque >= RONDAS_NECESARIAS_EN_MONTE_DEL_DESTINO) {
			return finalizar(
					new ResultadoPartida(Faccion.HEROES, "Frodo ha resistido " + RONDAS_NECESARIAS_EN_MONTE_DEL_DESTINO
							+ " rondas en el Monte del Destino sin ser atacado. ¡El Anillo ha sido destruido!"));
		}
		return null;
	}

	/**
	 * Metodo para comprobar si Frodo ha sido eliminado. En caso afirmativo y que la
	 * condicion de victoria "FRODO_ELIMINADO" estuviera activa, se produciria el
	 * fin inmediato de la partida y una victoria para las Bestias.
	 *
	 * @param mapa the mapa
	 * @return the resultado partida Nos devolvera un resultado de partida con el
	 *         ganador o null si aun no se ha cumplido.
	 */
	public ResultadoPartida comprobarVictoriaPorFrodoEliminado(Mapa mapa) {
		if (partidaFinalizada || mapa == null || !configuracion.estaActiva(TipoCondicionVictoria.FRODO_ELIMINADO)) {
			return null;
		}
		boolean frodoSigueVivo = mapa.getTodasLasUnidades().stream()
				.anyMatch(pj -> pj != null && pj.estaVivo() && NOMBRE_FRODO.equalsIgnoreCase(pj.getNombre()));

		if (!frodoSigueVivo) {
			return finalizar(new ResultadoPartida(Faccion.BESTIAS, "Frodo ha sido eliminado"));
		}
		return null;
	}

	/**
	 * Metodo para comprobar si una faccion ha conquistado todas las ciudades de la
	 * faccion contraria. En caso afirmativo se acabaria la partida y se produciria
	 * una victoria por domino
	 *
	 * @param mapa the mapa
	 * @return the resultado partida Nos devolvera un resultado de partida con el
	 *         ganador o null si aun no se ha cumplido.
	 */
	public ResultadoPartida comprobarVictoriaPorDominio(Mapa mapa) {
		if (partidaFinalizada || mapa == null || !configuracion.estaActiva(TipoCondicionVictoria.DOMINIO)) {
			return null;
		}
		boolean heroesDominan = dominioTotalSobre(mapa, ControlCiudad.BESTIAS);
		boolean bestiasDominan = dominioTotalSobre(mapa, ControlCiudad.HEROES);

		if (heroesDominan) {
			return finalizar(new ResultadoPartida(Faccion.HEROES,
					"Los Heroes han conquistado todas las ciudades de las Bestias"));
		}
		if (bestiasDominan) {
			return finalizar(new ResultadoPartida(Faccion.BESTIAS,
					"Las Bestias han conquistado todas las ciudades de los Heroes"));
		}
		return null;
	}

	/**
	 * Metodo por el cual verificamos si una faccion tiene control sobre alguna
	 * ciudad en el mapa. Si dispone de alguna ciudad devolvera falso.
	 *
	 * @param mapa           the mapa
	 * @param controlEnemigo the control enemigo
	 * @return true, Si el enemigo no dispone de ninguna ciudad y se produce un
	 *         dominio total sobre el
	 */
	private boolean dominioTotalSobre(Mapa mapa, ControlCiudad controlEnemigo) {
		boolean quedaAlgunaCiudadEnemiga = false;

		for (Ciudad ciudad : mapa.getCiudades()) {
			if (ciudad.getControlInicial() == controlEnemigo) {
				quedaAlgunaCiudadEnemiga = true;
			}
			if (ciudad.getControlSobreCiudad() == controlEnemigo) {
				return false;
			}
		}
		return quedaAlgunaCiudadEnemiga;
	}

	/**
	 * Metodo para gestionar la rendicion de alguna faccion. Dara una victoria
	 * instantanea al otro equipo.
	 *
	 * @param faccionQueSeRinde the faccion que se rinde
	 * @return the resultado partida
	 */
	public ResultadoPartida rendirse(Faccion faccionQueSeRinde) {
		if (partidaFinalizada || faccionQueSeRinde == null) {
			return null;
		}
		Faccion ganador = (faccionQueSeRinde == Faccion.HEROES) ? Faccion.BESTIAS : Faccion.HEROES;
		String motivo = nombreFaccion(faccionQueSeRinde) + " se han rendido";
		return finalizar(new ResultadoPartida(ganador, motivo));
	}

	/**
	 * Metodo para comprobar si se ha cumplido alguna condicion inmediata, como por
	 * ejemplo la eliminacion de frodo.
	 *
	 * @param mapa the mapa
	 * @return the resultado partida Nos devolvera un resultado de partida con el
	 *         ganador o null si aun no se ha cumplido.
	 */
	public ResultadoPartida comprobarCondicionesInmediatas(Mapa mapa) {
		ResultadoPartida resultado = comprobarVictoriaPorFrodoEliminado(mapa);
		if (resultado == null) {
			resultado = comprobarVictoriaPorDominio(mapa);
		}
		return resultado;
	}

	/**
	 * Metodo para marcar la partida como finalizada y devuelve el resultado
	 * definitivo.
	 *
	 * @param resultado the resultado
	 * @return the resultado partida
	 */
	private ResultadoPartida finalizar(ResultadoPartida resultado) {
		this.partidaFinalizada = true;
		return resultado;
	}

	/**
	 * Nombre faccion.
	 *
	 * @param faccion the faccion
	 * @return the string
	 */
	private String nombreFaccion(Faccion faccion) {
		return faccion == Faccion.HEROES ? "Los Heroes" : "Las Bestias";
	}

	/**
	 * Gets the configuracion.
	 *
	 * @return the configuracion
	 */
	public ConfiguracionVictoria getConfiguracion() {
		return configuracion;
	}

	/**
	 * Gets the puntos heroes.
	 *
	 * @return the puntos heroes
	 */
	public int getPuntosHeroes() {
		return puntosHeroes;
	}

	/**
	 * Gets the puntos bestias.
	 *
	 * @return the puntos bestias
	 */
	public int getPuntosBestias() {
		return puntosBestias;
	}

	/**
	 * Metodo para saber las rondas que lleva frodo sin ser atacado en el monte del
	 * destino.
	 *
	 * @return the rondas frodo en monte sin ataque
	 */
	public int getRondasFrodoEnMonteSinAtaque() {
		return rondasFrodoEnMonteSinAtaque;
	}

}
