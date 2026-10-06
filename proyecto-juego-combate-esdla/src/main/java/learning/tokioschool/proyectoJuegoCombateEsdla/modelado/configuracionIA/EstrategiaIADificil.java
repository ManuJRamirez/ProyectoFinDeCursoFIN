package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.configuracionIA;

import java.util.HashSet;
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
 * The Class EstrategiaIADificil.
 * 
 * Clase en la que definimos la estrategia que va a seguir la IA cuando
 * seleccionamos la dificultad Dificil. Concentrara los ataques en un objetivo
 * elegido, evitara casillas en la que haya multiples amenazas, intentara evitar
 * que los nazguls esten cerca de las unidades que tienes bonus contra ellos,
 * defendera fuertemente sus ciudades y reclutara en funcion de qué unidades son
 * las que mas escasean.
 */
public class EstrategiaIADificil extends EstrategiaIABase {

	/** Atributo estatico donde definimos la importancia de la distancia. */
	private static final double PESO_DISTANCIA = 1.0;

	/**
	 * Atributo estatico en el que tendremos en cuenta el daño estimado sobre el
	 * objetivo a la hora de seleccionarlo..
	 */
	private static final double PESO_DANIO = 0.5;

	/**
	 * Atributo estatico donde definimos la importancia de la distancia de una
	 * ciudad.
	 */
	private static final double PESO_DISTANCIA_CIUDAD = 1.0;

	/**
	 * Atributo estatico donde definimos la importancia de la posibilidad de rematar
	 * a un objetivo.
	 */
	private static final double BONUS_REMATE_SEGURO = 60.0;

	/**
	 * Atributo estatico donde definimos la importancia de hacer focus a un objetivo
	 * ya marcado durante ese turno para concentrar el daño.
	 */
	private static final double BONUS_FOCUS_ATAQUE = 25.0;

	/**
	 * Atributo estatico donde definimos la importancia de priorizar un objetivo
	 * importante como es un hobbit.
	 */
	private static final double PRIORIDAD_HOBBIT_DETECTADO = 650.0;

	/**
	 * Atributo estatico donde definimos la importancia de rastrear a los hobbits si
	 * aun no han sido detectados
	 */
	private static final double PRIORIDAD_HOBBIT_A_BUSCAR = 150.0;

	/**
	 * Atributo estatico donde definimos una penalizacion a la hora de elegir una
	 * casilla si detecta que hay amenazas sobre ella.
	 */
	private static final double PENALIZACION_POR_AMENAZA = 2.0;

	/**
	 * Atributo estatico donde definimos la penalizacion a la caballeria para
	 * intentar que evite estar dentro de una ciudad.
	 */
	private static final double PENALIZACION_CIUDAD_CABALLERIA = 5.0;

	/**
	 * Atributo estatico donde definimos la importancia de defender una ciudad
	 * natal.
	 */
	private static final double BONUS_DEFENDER_CIUDAD_NATAL = 600.0;

	/**
	 * Atributo estatico donde definimos la importancia de defender una ciudad
	 * conquistada.
	 */
	private static final double BONUS_DEFENDER_CIUDAD_CONQUISTADA = 520.0;

	/**
	 * Atributo estatico donde definimos la importancia de reconquistar una ciudad
	 * natal perdida.
	 */
	private static final double BONUS_RECONQUISTAR_NATAL_PROPIA = 500.0;

	/**
	 * Atributo estatico donde definimos la importancia de atacar una ciudad natal
	 * de los heroes.
	 */
	private static final double BONUS_CIUDAD_NATAL_HEROES = 40.0;

	/**
	 * Atributo estatico donde definimos la importancia de atacar una ciudad
	 * neutral.
	 */
	private static final double BONUS_CIUDAD_NEUTRAL = 20.0;

	/**
	 * Atributo estatico donde definimos la importancia de atacar una ciudad con
	 * unidades de caballeria, si la ciudad se encuentra vacia, para poder realizar
	 * conquistas rapidas.
	 */
	private static final double BONUS_CIUDAD_VACIA_CABALLERIA = 520.0;

	/**
	 * Atributo estatico donde definimos la probabilidad en la que se iniciara un
	 * reclutamiento. (0 - 1, donde 1 = 100%)
	 */
	private static final double PROBABILIDAD_DE_RECLUTAR = 0.9;

	/**
	 * Atributo donde definimos el umbral de vida en el que una unidad bestia
	 * valorara una huida. (0 - 1 donde 1 = 100%).
	 */
	private static final double UMBRAL_HUIDA_VIDA = 0.4;

	/**
	 * Conjunto de personajes atacados por alguna unidad bestia, durante el turno
	 * actual.
	 */
	private final Set<Personaje> objetivosAtacados = new HashSet<>();

	/**
	 * Metodo para iniciar el turno de la IA. Se va a sobreescribir porque queremos
	 * vaciar los objetivos atacados durante el turno anterior.
	 */
	@Override
	public void iniciarTurno() {
		super.iniciarTurno();
		objetivosAtacados.clear();
	}

	/**
	 * Metodo que invoca registrarReserva de la clase padre EstrategaIABase, por el
	 * que dejamos señalado un objetivo para llevar un control de saturacion. Ademas
	 * lo añadiremos al conjunto de objetivosAtacados para favorecer que otras
	 * unidades lo ataquen tambien.
	 *
	 * @param objetivoElegido the objetivo elegido
	 */
	@Override
	protected void registrarReserva(Personaje objetivoElegido) {
		super.registrarReserva(objetivoElegido);
		objetivosAtacados.add(objetivoElegido);
	}

	/**
	 * Metodo para recopilar una lista de enemigos candidatos, añadiendo tambien a
	 * los hobbits para las unidades rastreadoras.
	 *
	 * @param bestia the bestia
	 * @param mapa   the mapa
	 * @return the list de candidatos
	 */
	@Override
	protected List<Personaje> recopilarEnemigosCandidatos(Personaje bestia, Mapa mapa) {
		List<Personaje> candidatos = super.recopilarEnemigosCandidatos(bestia, mapa);

		return anadirHobbitsParaRastreador(bestia, mapa, candidatos);
	}

	/**
	 * Metodo para puntuar a un candidato. Esta puntuacion se basara en los
	 * atributos estaticos definidos al principio de la clase.
	 *
	 * @param bestia    the bestia
	 * @param candidato the candidato
	 * @param mapa      the mapa
	 * @return the double puntuacion del candidato.
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

		if (danioEstimado >= candidato.getVida()) {
			puntuacion += BONUS_REMATE_SEGURO;
		}
		if (objetivosAtacados.contains(candidato)) {
			puntuacion += BONUS_FOCUS_ATAQUE;
		}
		puntuacion += bonusDefensaDeCiudad(bestia, candidato, mapa, BONUS_DEFENDER_CIUDAD_NATAL,
				BONUS_DEFENDER_CIUDAD_CONQUISTADA);

		return puntuacion;
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
	 * Metodo para calcular la mejor casilla hacia la que moverse para acercarse al
	 * objetivo, penalizando las casillas amenazadas por varios Héroes, teniendo
	 * encuenta las amenazas a los Nazguls y las penalizaciones a la Caballeria
	 * dentro de la ciudad.
	 *
	 * @param bestia   the bestia
	 * @param objetivo the objetivo
	 * @param mapa     the mapa
	 * @return the int[] coordenada de la mejor casilla.
	 */
	@Override
	protected int[] mejorCasillaHaciaObjetivo(Personaje bestia, Personaje objetivo, Mapa mapa) {
		Set<String> alcanzables = mapa.calcularCasillasAlcanzables(bestia);

		if (alcanzables.isEmpty()) {
			return null;
		}

		int distanciaDeseada = distanciaDeseadaHastaObjetivo(bestia);
		boolean esCaballeria = "CABALLERIA".equalsIgnoreCase(bestia.getTipoCombate());
		int[] mejorCasilla = null;
		double mejorPuntuacion = Double.NEGATIVE_INFINITY;
		Map<String, Integer> distanciasReales = calcularDistanciaRealHastaObjetivo(bestia, mapa, objetivo.getX(),
				objetivo.getY());

		for (String casilla : alcanzables) {
			int[] coordenadas = parsearCoordenadas(casilla);
			int x = coordenadas[0];
			int y = coordenadas[1];
			int distanciaReal = distanciasReales.getOrDefault(casilla, DISTANCIA_INALCANZABLE);
			double puntuacion = -Math.abs(distanciaReal - distanciaDeseada);

			puntuacion -= PENALIZACION_POR_AMENAZA * contarAmenazasSobreCasilla(objetivo, x, y, mapa);
			puntuacion -= penalizacionContraNazgul(bestia, x, y, mapa);

			if (esCaballeria && mapa.buscarCiudadesPorCasilla(x, y) != null) {
				puntuacion -= PENALIZACION_CIUDAD_CABALLERIA;
			}
			if (puntuacion > mejorPuntuacion) {
				mejorPuntuacion = puntuacion;
				mejorCasilla = new int[] { x, y };
			}
		}
		return mejorCasilla;
	}

	/**
	 * Metodo para contar cuantos Heroes vivos y detactados amenazan (rango de
	 * movimiento / rango de ataque) una casilla pasada por parametros, excluyendo
	 * la unidad objetivo seleccionado.
	 *
	 * @param objetivoPerseguido the objetivo perseguido
	 * @param x                  the x
	 * @param y                  the y
	 * @param mapa               the mapa
	 * @return the int Numero de amanezas sobre esa casilla.
	 */
	private int contarAmenazasSobreCasilla(Personaje objetivoPerseguido, int x, int y, Mapa mapa) {
		int amenazas = 0;

		for (Personaje pj : mapa.getTodasLasUnidades()) {
			if (pj.getFaccion() != Faccion.HEROES || !pj.estaVivo() || pj == objetivoPerseguido) {
				continue;
			}
			if (pj instanceof Hobbit && !pj.esDetectadoPorEnemigo()) {
				continue;
			}
			int distancia = Math.max(Math.abs(pj.getX() - x), Math.abs(pj.getY() - y));

			if (distancia <= pj.getRangoAtaque() + pj.getMovimiento()) {
				amenazas++;
			}
		}
		return amenazas;
	}

	/**
	 * Metodo para detectar si existe una amenaza grave sobre una unidad Bestia.
	 * Tendremos en cuenta el umbral de vida para valorar una huida.
	 *
	 * @param bestia the bestia
	 * @param mapa   the mapa
	 * @return the personaje
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
	 * ciudades natales o no. Para la dificultad Dificil, siempre lo debe intentar.
	 *
	 * @return true, if successful
	 */
	@Override
	protected boolean debeReconquistarCiudadNatalPerdida() {
		return true;
	}

	/**
	 * Metodo para puntuar una ciudad candidata a ser conquistada. Usaremos los
	 * atributos estaticos definidos al principio de la clase para realizar esa
	 * puntuacion.
	 *
	 * @param bestia                the bestia
	 * @param ciudad                the ciudad
	 * @param mapa                  the mapa
	 * @param configuracionVictoria the configuracion victoria
	 * @return the double Puntuacion de la ciudad.
	 */
	@Override
	protected double puntuarCiudad(Personaje bestia, Ciudad ciudad, Mapa mapa,
			ConfiguracionVictoria configuracionVictoria) {
		return puntuarCiudadPorCondicionVictoria(bestia, ciudad, mapa, configuracionVictoria, PESO_DISTANCIA_CIUDAD,
				BONUS_RECONQUISTAR_NATAL_PROPIA, BONUS_CIUDAD_NATAL_HEROES, BONUS_CIUDAD_NEUTRAL,
				BONUS_CIUDAD_VACIA_CABALLERIA);
	}

	/**
	 * Metodo encargado de decidir el reclutamiento para cada ciudad controlada por
	 * la facción indicada. Usaremos la probabilidad de reclutamiento para
	 * influenciar en el reclutamiento. En esta dificultad, en caso de que haya mas
	 * de una tropa disponible para reclutar, se tendra en cuenta la que menos
	 * cantidad haya actualmente en el ejercito Bestia.
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
			return elegirTropaQueMasFalta(disponibles, faccion, mapa);
		});
	}

	/**
	 * Metodo auxiliar que ayuda a decidir el reclutamiento. En concreto lo que
	 * hacemos en este metodo es, en caso de que haya mas de una opcion a reclutar
	 * en la ciudad, revisamos que unidad Tropa hay menos en el ejercito actual, y
	 * reclutaremos la que mas escasee.
	 *
	 * @param disponibles the disponibles
	 * @param faccion     the faccion
	 * @param mapa        the mapa
	 * @return the personaje tropa elegida.
	 */
	private Personaje elegirTropaQueMasFalta(List<Personaje> disponibles, Faccion faccion, Mapa mapa) {

		Map<String, Long> conteoPorTipo = mapa.getTodasLasUnidades().stream().filter(
				pj -> pj.getFaccion() == faccion && pj.estaVivo() && "TROPA".equalsIgnoreCase(pj.getCategoria()))
				.collect(Collectors.groupingBy(this::normalizarTipoCombate, Collectors.counting()));

		long menorConteo = disponibles.stream()
				.mapToLong(pj -> conteoPorTipo.getOrDefault(normalizarTipoCombate(pj), 0L)).min().orElse(0L);

		List<Personaje> masEscasas = disponibles.stream()
				.filter(pj -> conteoPorTipo.getOrDefault(normalizarTipoCombate(pj), 0L) == menorConteo)
				.collect(Collectors.toList());

		return masEscasas.get((int) (Math.random() * masEscasas.size()));
	}

	/**
	 * Normalizar el tipo de combate a mayuscula, devolviendo null si no tiene tipo
	 * de combate.
	 *
	 * @param pj the pj
	 * @return the string
	 */
	private String normalizarTipoCombate(Personaje pj) {
		String tipoCombate = pj.getTipoCombate();

		return tipoCombate == null ? "" : tipoCombate.toUpperCase();
	}
}
