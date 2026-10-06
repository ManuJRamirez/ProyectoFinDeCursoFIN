package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.configuracionIA;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ConfiguracionVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.reclutarTropa.ReclutamientoControlador;

// TODO: Auto-generated Javadoc
/**
 * The Class EstrategiaIANormal.
 * 
 * Clase en la que abordamos el nivel especifico de Dificultad Normal,
 * extendiendo de EstrategiaIABase. En este nivel tendremos una logica de
 * bonificaciones y penalizaciones mas elaboradas. Priorizará rematar enemigos
 * con poca vida, defender ciudades propias, perseguir a los hobbits
 * detectados,etc
 */
public class EstrategiaIANormal extends EstrategiaIABase {

	/** Atributo estatico donde definimos la importancia de la distancia. */
	private static final double PESO_DISTANCIA = 1.0;

	/**
	 * Atributo estatico donde definimos la importancia de la distancia hasta la
	 * ciudad
	 */
	private static final double PESO_DISTANCIA_CIUDAD = 1.0;

	/**
	 * Atributo estatico donde definimos la importancia del daño estimado en funcion
	 * al enemigo.
	 */
	private static final double PESO_DANIO = 0.5;

	/**
	 * Atributo estatico donde definimos la importancia del objetivo cuando tiene
	 * poca vida y puede ser rematado.
	 */
	private static final double BONUS_REMATE = 40.0;

	/**
	 * Atributo final en el que definimos el umbral al que consideraremos que un
	 * enemigo tiene poca vida(porcentaje).
	 */
	private static final int UMBRAL_VIDA_BAJA_PORCENTAJE = 30;

	/** Atributo estatico donde definimos la importancia de un hobbit detectado. */
	private static final double PRIORIDAD_HOBBIT_DETECTADO = 400.0;

	/** Atributo estatico donde definimos la importancia de rastrear hobbits . */
	private static final double PRIORIDAD_HOBBIT_A_BUSCAR = 80.0;

	/**
	 * Atributo estatico donde definimos la importancia de defender ciudades
	 * natales.
	 */
	private static final double BONUS_DEFENDER_CIUDAD_NATAL = 300.0;

	/**
	 * Atributo estatico donde definimos la importancia de defender una ciudad
	 * conquistada.
	 */
	private static final double BONUS_DEFENDER_CIUDAD_CONQUISTADA = 220.0;

	/**
	 * Atributo estatico donde definimos la importancia de reconquistar una ciudad
	 * natal perdida.
	 */
	private static final double BONUS_RECONQUISTAR_NATAL_PROPIA = 260.0;

	/**
	 * Atributo estatico donde definimos la importancia de valorar el atacar una
	 * ciudad natal enemiga.
	 */
	private static final double BONUS_CIUDAD_NATAL_HEROES = 30.0;

	/**
	 * Atributo estatico donde definimos la importancia de valorar el atacar una
	 * ciudad neutral
	 */
	private static final double BONUS_CIUDAD_NEUTRAL = 15.0;

	/**
	 * Atributo estatico donde definimos la importancia de que una ciudad este
	 * vacia, por lo que la IA podria valorar un ataque rapido con la caballeria.
	 */
	private static final double BONUS_CIUDAD_VACIA_CABALLERIA = 200.0;

	/** Atributo estatico donde definimos la importancia de reclutar. */
	private static final double PROBABILIDAD_DE_RECLUTAR = 0.75;

	/**
	 * Atributo estatico donde definimos un umbral por debajo del cual la unidad
	 * valora una huida antes de morir (0 - 1, donde 1 es el 100%) .
	 */
	private static final double UMBRAL_HUIDA_VIDA = 0.6;

	/**
	 * Metodo para puntuar al candidato Heroe. Se basara en las puntuaciones
	 * guardada en los atributos definidos anteriormente y devolvera el resultado
	 *
	 * @param bestia    the bestia
	 * @param candidato the candidato
	 * @param mapa      the mapa
	 * @return the double la puntuacion del candidato. (Mayor = mejor)
	 */
	@Override
	protected double puntuarObjetivo(Personaje bestia, Personaje candidato, Mapa mapa) {

		if (candidato instanceof Hobbit) {
			double prioridad = candidato.esDetectadoPorEnemigo() ? PRIORIDAD_HOBBIT_DETECTADO
					: PRIORIDAD_HOBBIT_A_BUSCAR;

			return prioridad - distanciaEntre(bestia, candidato);
		}

		int distancia = distanciaEntre(bestia, candidato);
		int danioEstimado = bestia.calcularDanioFinal(candidato, mapa);
		double puntuacion = -distancia * PESO_DISTANCIA + danioEstimado * PESO_DANIO;
		int vidaPorcentaje = (int) (100.0 * candidato.getVida() / candidato.getVidaMaxima());
		boolean esEliminadoEnProximoAtaque = danioEstimado >= candidato.getVida();

		if (esEliminadoEnProximoAtaque || vidaPorcentaje <= UMBRAL_VIDA_BAJA_PORCENTAJE) {
			puntuacion += BONUS_REMATE;
		}
		puntuacion += bonusDefensaDeCiudad(bestia, candidato, mapa, BONUS_DEFENDER_CIUDAD_NATAL,
				BONUS_DEFENDER_CIUDAD_CONQUISTADA);

		return puntuacion;
	}

	/**
	 * Recopila los enemigos candidatos que pueden ser elegidos.
	 *
	 * @param bestia the bestia
	 * @param mapa   the mapa
	 * @return the list de personajes enemigos que son candidatos.
	 */
	@Override
	protected List<Personaje> recopilarEnemigosCandidatos(Personaje bestia, Mapa mapa) {
		List<Personaje> candidatos = super.recopilarEnemigosCandidatos(bestia, mapa);
		return anadirHobbitsParaRastreador(bestia, mapa, candidatos);
	}

	/**
	 * Metodo para calcular la distancia deseada a la que el personaje quiere
	 * quedarse de su objetivo, valorando si es rango o melee.
	 *
	 * @param bestia the bestia
	 * @return the int Distancia deseada hasta el objetivo.
	 */
	@Override
	protected int distanciaDeseadaHastaObjetivo(Personaje bestia) {
		if (bestia.getRangoAtaque() > 1) {
			return bestia.getRangoAtaque();
		}
		return 0;
	}

	/**
	 * Metodo para calcular la mejor casilla a la que debe moverse la unidad para
	 * acercarse al enemigo. Ademas si es caballeria va elegir no meterse dentro de
	 * la ciudad ya que tiene una penalizacion de ataque, si hay una casilla fuera
	 * elegible, sino pues irá a la ciudad.
	 *
	 * @param bestia   the bestia
	 * @param objetivo the objetivo
	 * @param mapa     the mapa
	 * @return the int[] coordenada de la casilla.
	 */
	@Override
	protected int[] mejorCasillaHaciaObjetivo(Personaje bestia, Personaje objetivo, Mapa mapa) {
		int[] casilla = super.mejorCasillaHaciaObjetivo(bestia, objetivo, mapa);

		if (casilla == null || !"CABALLERIA".equalsIgnoreCase(bestia.getTipoCombate())) {
			return casilla;
		}

		Ciudad ciudadDestino = mapa.buscarCiudadesPorCasilla(casilla[0], casilla[1]);

		if (ciudadDestino == null) {
			return casilla;
		}

		Set<String> alcanzables = mapa.calcularCasillasAlcanzables(bestia);
		Map<String, Integer> distanciasReales = calcularDistanciaRealHastaObjetivo(bestia, mapa, objetivo.getX(),
				objetivo.getY());
		int distanciaRealOriginal = distanciasReales.getOrDefault(casilla[0] + "," + casilla[1],
				DISTANCIA_INALCANZABLE);

		for (String casillaCoordenada : alcanzables) {
			int[] coordenadas = parsearCoordenadas(casillaCoordenada);

			if (mapa.buscarCiudadesPorCasilla(coordenadas[0], coordenadas[1]) != null) {
				continue;
			}
			int distanciaReal = distanciasReales.getOrDefault(casillaCoordenada, DISTANCIA_INALCANZABLE);
			if (distanciaReal <= distanciaRealOriginal) {
				return coordenadas;
			}
		}

		return casilla;
	}

	/**
	 * Metodo para detectar si existe una amenaza grave pasandole como parametro el
	 * humbral de vida para valorar la huida.
	 *
	 * @param bestia the bestia
	 * @param mapa   the mapa
	 * @return the personaje El personaje que supone una amenaza grave, o null si no
	 *         existiera ninguno.
	 */
	@Override
	protected Personaje detectarAmenazaGrave(Personaje bestia, Mapa mapa) {
		return detectarAmenazaGravePorUmbral(bestia, mapa, UMBRAL_HUIDA_VIDA);
	}

	/**
	 * Metodo que vamos a usar para aprovechar el movimiento extra del Nazgul tras
	 * atacar para retirarlo a una zona segura.
	 *
	 * @param bestia the bestia
	 * @param mapa   the mapa
	 * @return the list lista de coordenadas de casillas candidatas.
	 */
	@Override
	public List<int[]> casillasCandidatasParaMovimientoPostAtaqueNazgul(Personaje bestia, Mapa mapa) {
		return candidatasRetiradaPostAtaqueNazgul(bestia, mapa);
	}

	/**
	 * Metodo para definir si en funcion a la configuracion de victoria
	 * seleccionada, la IA debe añadir conquistar ciudades en su estrategia.
	 *
	 * @param configuracionVictoria the configuracion victoria
	 * @return true, if successful
	 */
	@Override
	protected boolean debeConsiderarConquistaDeCiudades(ConfiguracionVictoria configuracionVictoria) {
		return esVictoriaPorPuntosODominio(configuracionVictoria);
	}

	/**
	 * Metodo para sabes si la faccion bestia debe intentar reconquistar sus
	 * ciudades natales o no. Para la dificultad Normal, siempre lo debe intentar.
	 *
	 * @return true, if successful
	 */
	@Override
	protected boolean debeReconquistarCiudadNatalPerdida() {
		return true;
	}

	/**
	 * Metodo para puntuar la eleccion de una ciudad para ser atacada y conquistada
	 * en funcion a los pesos definidos en los atributos estaticos anteriormente.
	 *
	 * @param bestia                the bestia
	 * @param ciudad                the ciudad
	 * @param mapa                  the mapa
	 * @param configuracionVictoria the configuracion victoria
	 * @return the double puntuacion de la ciudad.
	 */
	@Override
	protected double puntuarCiudad(Personaje bestia, Ciudad ciudad, Mapa mapa,
			ConfiguracionVictoria configuracionVictoria) {
		return puntuarCiudadPorCondicionVictoria(bestia, ciudad, mapa, configuracionVictoria, PESO_DISTANCIA_CIUDAD,
				BONUS_RECONQUISTAR_NATAL_PROPIA, BONUS_CIUDAD_NATAL_HEROES, BONUS_CIUDAD_NEUTRAL,
				BONUS_CIUDAD_VACIA_CABALLERIA);
	}

	/**
	 * Metodo para decidir si se inicia un proceso de reclutamiento en una ciudad
	 * natal controlada por la faccion. En esta dificultad (Normal), usaremos el
	 * atributo de probabilidad de reclutar para influir.
	 *
	 * @param faccion       the faccion
	 * @param mapa          the mapa
	 * @param rosterTropas  the roster tropas
	 * @param reclutamiento the reclutamiento
	 * @param tropasMaximas the tropas maximas
	 */
	@Override
	public void decidirReclutamiento(Faccion faccion, Mapa mapa, List<Personaje> rosterTropas,
			ReclutamientoControlador reclutamiento, int tropasMaximas) {

		reclutarEnCiudadesControladas(faccion, mapa, rosterTropas, reclutamiento, tropasMaximas, disponibles -> {
			if (Math.random() >= PROBABILIDAD_DE_RECLUTAR) {
				return null;
			}
			return elegirTropaMasRapidaRandom(disponibles);
		});
	}

	/**
	 * Metodo para elegir que tropa se va a reclutar. En dificultad Normal, se
	 * eligira la unidad que tarda menos en ser creada. Si hay 2 unidades que tarden
	 * lo mismo, ambas entraran en las "masRapidas" y se seleccionara de forma
	 * aleatoria.
	 *
	 * @param disponibles the disponibles
	 * @return the personaje
	 */
	private Personaje elegirTropaMasRapidaRandom(List<Personaje> disponibles) {
		int menorTurnosCreaccion = disponibles.stream().mapToInt(Personaje::getTurnosCreacion).min()
				.orElse(Integer.MAX_VALUE);
		List<Personaje> masRapidas = disponibles.stream().filter(pj -> pj.getTurnosCreacion() == menorTurnosCreaccion)
				.collect(Collectors.toList());

		return masRapidas.get((int) (Math.random() * masRapidas.size()));
	}
}
