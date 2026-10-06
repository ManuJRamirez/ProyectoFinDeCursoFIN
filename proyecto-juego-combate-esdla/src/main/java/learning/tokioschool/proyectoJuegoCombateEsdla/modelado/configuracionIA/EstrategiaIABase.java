package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.configuracionIA;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoTerreno;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ConfiguracionVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ControladorVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.TipoCondicionVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Bestia;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.trasgos.Trasgo;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.reclutarTropa.ReclutamientoControlador;

/**
 * Clase abstracta en la que se basan los diferentes niveles de IA.
 * Sobreescribiremos los metodos necesarios en cada nivel diferente de
 * dificultad de la IA.
 */
public abstract class EstrategiaIABase implements EstrategiaIA {

	/** Distancia de una casilla inalcanzable que sera infinito. */
	protected static final int DISTANCIA_INALCANZABLE = 9999;

	/**
	 * Numero de unidades maximas que se asigna preferente para cada objetivo para
	 * no saturarlo.
	 */
	protected static final int MAX_UNIDADES_POR_OBJETIVO = 3;

	/**
	 * Unidades a minimas de la faccion de las bestias en la que activaremos el modo
	 * desesperado. Ignorara la saturacion por objetivo.
	 */
	protected static final int UNIDADES_PARA_MODO_DESESPERADO = 4;

	/**
	 * Distancia a la que un nazgul va a intentar alejarse de su enemigo counter que
	 * son arqueros y magos.
	 */
	protected static final int PENALIZACION_CONTRA_NAZGUL_POR_CASILLA = 4;

	/** Nombre de la ciudad importante donde hay que llevar el anillo. */
	protected static final String NOMBRE_CIUDAD_MONTE_DEL_DESTINO = "Monte del Destino";

	/**
	 * distancia minima con respecto a la ciudad "Monte del Destino",en la que un
	 * nazgul va a patrullar.
	 */
	protected static final int RADIO_PATRULLA_MONTE_MIN = 1;

	/**
	 * distancia maxima con respecto a la ciudad "Monte del Destino",en la que un
	 * nazgul va a patrullar.
	 */
	protected static final int RADIO_PATRULLA_MONTE_MAX = 5;

	/** Unidades asignadas a cada objetivo durante el turno actual. */
	private final Map<Object, Integer> asignacionesPorObjetivo = new HashMap<>();

	/**
	 * Iniciar turno. Cada vez que iniciamos un nuevo turno de IA, se reinician los
	 * objetivos.
	 */
	@Override
	public void iniciarTurno() {
		asignacionesPorObjetivo.clear();
	}

	/**
	 * Decide la accion que va a tomar la IA sobre una unidad Bestia, siguiendo el
	 * orden de prioridad: atacar a un enemigo en rango (salvo que haya una amenaza
	 * grave y el ataque no sea un remate seguro), huir de la amenaza grave, avanzar
	 * hacia el mejor objetivo enemigo o ciudad conquistable, patrullar (Nazgul o
	 * rastreadores en modo búsqueda de Hobbits) o esperar si no hay nada mejor que
	 * hacer.
	 *
	 * @param bestia                Unidad Bestia sobre la que decide la acción
	 * @param mapa                  Mapa juego
	 * @param configuracionVictoria Condiciones de victoria activas en la partida
	 * @return Acción decidida para esta unidad
	 */
	@Override
	public AccionIA decidirAccion(Personaje bestia, Mapa mapa, ConfiguracionVictoria configuracionVictoria) {
		List<Personaje> enemigosCandidatos = recopilarEnemigosCandidatos(bestia, mapa);
		boolean enModoBusquedaDeHobbits = estaEnModoBusquedaDeHobbits(bestia, mapa);

		Personaje amenazaGrave = detectarAmenazaGrave(bestia, mapa);

		Personaje mejorAtacable = null;
		double mejorPuntacionAtacable = Double.NEGATIVE_INFINITY;

		for (Personaje candidato : enemigosCandidatos) {
			int distancia = distanciaEntre(bestia, candidato);
			boolean enRango = distancia <= bestia.getRangoAtaque();

			if (enRango && esAtacableLegalmente(bestia, candidato, mapa)) {
				double puntuacion = puntuarObjetivo(bestia, candidato, mapa);
				if (puntuacion > mejorPuntacionAtacable) {
					mejorPuntacionAtacable = puntuacion;
					mejorAtacable = candidato;
				}
			}
		}
		if (mejorAtacable != null) {
			boolean esRemateSeguro = bestia.calcularDanioFinal(mejorAtacable, mapa) >= mejorAtacable.getVida();
			if (amenazaGrave == null || esRemateSeguro) {
				registrarReserva(mejorAtacable);
				return AccionIA.atacar(mejorAtacable);
			}
		}

		if (amenazaGrave != null) {
			int[] destinoHuida = mejorCasillaParaHuir(bestia, amenazaGrave, mapa);
			if (destinoHuida != null) {
				return AccionIA.mover(destinoHuida[0], destinoHuida[1]);
			}
		}

		Personaje mejorObjetivoLejano = null;
		double mejorPuntuacionLejana = Double.NEGATIVE_INFINITY;
		boolean restringidoAHobbitsADistancia = enModoBusquedaDeHobbits || bestia instanceof Nazgul;

		for (Personaje candidato : enemigosCandidatos) {
			if (restringidoAHobbitsADistancia && !(candidato instanceof Hobbit)) {
				continue;
			}
			if (candidato instanceof Hobbit && estaEnCuentaAtrasEnMonteDelDestino(candidato, mapa)
					&& !estaEnRadioDeReaccion(bestia, candidato.getX(), candidato.getY(),
							ControladorVictoria.RONDAS_NECESARIAS_EN_MONTE_DEL_DESTINO)) {
				continue;
			}
			if (!(candidato instanceof Hobbit) && objetivoSaturado(candidato, mapa)) {
				continue;
			}

			double puntuacion = puntuarObjetivo(bestia, candidato, mapa);
			if (puntuacion > mejorPuntuacionLejana) {
				mejorPuntuacionLejana = puntuacion;
				mejorObjetivoLejano = candidato;
			}
		}

		Ciudad mejorCiudad = null;
		double mejorPuntuacionCiudad = Double.NEGATIVE_INFINITY;

		if (!enModoBusquedaDeHobbits && puedeConquistarCiudades(bestia)) {
			for (Ciudad ciudad : mapa.getCiudades()) {
				if (ciudad.esCiudadControladaPorBestias()) {
					continue;
				}
				boolean esReconquistaNatalPropia = ciudad.getControlInicial() == ControlCiudad.BESTIAS;
				boolean debeEvaluarEstaCiudad = debeConsiderarConquistaDeCiudades(configuracionVictoria)
						|| (esReconquistaNatalPropia && debeReconquistarCiudadNatalPerdida());
				if (!debeEvaluarEstaCiudad) {
					continue;
				}
				if (objetivoSaturado(ciudad, mapa)) {
					continue;
				}

				double puntuacion = puntuarCiudad(bestia, ciudad, mapa, configuracionVictoria);
				if (puntuacion > mejorPuntuacionCiudad) {
					mejorPuntuacionCiudad = puntuacion;
					mejorCiudad = ciudad;
				}
			}
		}

		if (mejorCiudad != null && mejorPuntuacionCiudad > mejorPuntuacionLejana) {
			int[] destinoCiudad = mejorCasillaHaciaCiudad(bestia, mejorCiudad, mapa);

			if (destinoCiudad != null) {
				reservarObjetivo(mejorCiudad);
				return AccionIA.mover(destinoCiudad[0], destinoCiudad[1]);
			}
		}
		if (mejorObjetivoLejano == null) {
			if (enModoBusquedaDeHobbits || bestia instanceof Nazgul) {
				int[] destinoPatrulla = mejorCasillaDePatrulla(bestia, mapa);
				if (destinoPatrulla != null) {
					return AccionIA.mover(destinoPatrulla[0], destinoPatrulla[1]);
				}
			}
			return AccionIA.esperar();
		}

		int[] destino = mejorCasillaHaciaObjetivo(bestia, mejorObjetivoLejano, mapa);

		if (destino == null) {
			return AccionIA.esperar();
		}
		reservarObjetivo(mejorObjetivoLejano);

		return AccionIA.mover(destino[0], destino[1]);

	}

	/**
	 * Recopila una lista de los Heroes candidatos a ser elegidos por la unidad
	 * Bestia que se pasa por parametros.
	 *
	 * @param bestia Unidad Bestia que busca candidatos
	 * @param mapa   Mapa del juego
	 * @return Lista de enemigos candidatos
	 */
	protected List<Personaje> recopilarEnemigosCandidatos(Personaje bestia, Mapa mapa) {
		List<Personaje> enemigosCandidatos = new ArrayList<>();

		for (Personaje pj : mapa.getTodasLasUnidades()) {
			if (pj.getFaccion() != Faccion.HEROES || !pj.estaVivo()) {
				continue;
			}
			if (pj instanceof Hobbit && !pj.esDetectadoPorEnemigo()) {
				continue;
			}
			if (bestia instanceof Nazgul && pj instanceof Hobbit) {
				continue;
			}
			enemigosCandidatos.add(pj);
		}
		return enemigosCandidatos;
	}

	/**
	 * Método para saber si una bestia un rastreador o no. Para que lo sea debe
	 * tener un tipo de combate "EXPLORADOR" o directamente un "Nazgul".
	 *
	 * @param bestia que vamos a verificar
	 * @return true, if successful
	 */
	protected boolean esRastreadorDeHobbits(Personaje bestia) {
		return bestia instanceof Nazgul
				|| (bestia instanceof Trasgo && "EXPLORADOR".equalsIgnoreCase(bestia.getTipoCombate()));
	}

	/**
	 * Si seleccionamos una unidad Bestia que es rastreadora, entonces añadimos a
	 * candidatos los hobbits vivos y que esten en el rango de rastreo.
	 *
	 * @param bestia     Unidad rastreadora
	 * @param mapa       Mapa del juego
	 * @param candidatos Lista de candidatos
	 * @return La misma lista de candidatos, ampliada con los hobbits, si es que
	 *         corresponde
	 */
	protected List<Personaje> anadirHobbitsParaRastreador(Personaje bestia, Mapa mapa, List<Personaje> candidatos) {
		if (!esRastreadorDeHobbits(bestia)) {
			return candidatos;
		}
		int radioDeRastreo = calcularRadioDeRastreo(bestia, mapa);
		for (Personaje pj : mapa.getTodasLasUnidades()) {
			if (pj instanceof Hobbit && pj.estaVivo() && !candidatos.contains(pj)
					&& distanciaEntre(bestia, pj) <= radioDeRastreo) {
				candidatos.add(pj);
			}
		}
		return candidatos;
	}

	/**
	 * Metodo con el que calculamos el rango de rastreo de una unidad rastreadora.
	 * Es su radio de deteccion mas su rango de movimiento para ese turno.
	 *
	 * @param bestia Unidad rastreadora
	 * @param mapa   Mapa del juego
	 * @return Radio de rastreo
	 */
	protected int calcularRadioDeRastreo(Personaje bestia, Mapa mapa) {
		return mapa.calcularRangoDeteccion(bestia) + bestia.getMovimiento();
	}

	/**
	 * Metodo por el cual comprobamos si un rastreador esta en modo rastreo/busqueda
	 * de hobbits o no.
	 *
	 * @param bestia Unidad a comprobar
	 * @param mapa   Mapa lógico del juego
	 * @return true, if successful
	 */
	protected boolean estaEnModoBusquedaDeHobbits(Personaje bestia, Mapa mapa) {
		if (!esRastreadorDeHobbits(bestia)) {
			return false;
		}
		for (Personaje pj : mapa.getTodasLasUnidades()) {
			if (pj instanceof Hobbit && pj.estaVivo() && pj.esDetectadoPorEnemigo()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Metodo para saber si el personaje/unidad heroe es counter de los Nazgul. Este
	 * metodo lo usamos para que el nazgul mantenga distancia con ellos.
	 *
	 * @param pj personaje heroe a comprobar
	 * @return true, if successful
	 */
	protected boolean esCounterDeNazgul(Personaje pj) {
		if (pj == null) {
			return false;
		}
		String tipoCombate = pj.getTipoCombate();

		return "ARQUERO".equalsIgnoreCase(tipoCombate) || "MAGO".equalsIgnoreCase(tipoCombate);
	}

	/**
	 * Metodo para calcular cuantas unidades Arqueros o Magos (counter de nazgul)
	 * podrian llegar a amenazar una determinada casilla, en el siguiente turno
	 * teniendo en cuenta el rango de movimiento y ataque de la unidad Heroe.
	 *
	 * @param x    Coordenada X de la casilla
	 * @param y    Coordenada Y de la casilla
	 * @param mapa Mapa del juego.
	 * @return Número de amenazas contra el Nazgul sobre esa casilla en cuestion.
	 */
	protected int contarContrasDeNazgulSobreCasilla(int x, int y, Mapa mapa) {
		int contador = 0;
		for (Personaje pj : mapa.getTodasLasUnidades()) {
			if (pj.getFaccion() != Faccion.HEROES || !pj.estaVivo() || !esCounterDeNazgul(pj)) {
				continue;
			}
			int distancia = Math.max(Math.abs(pj.getX() - x), Math.abs(pj.getY() - y));
			if (distancia <= pj.getRangoAtaque() + pj.getMovimiento()) {
				contador++;
			}
		}
		return contador;
	}

	/**
	 * Metodo por el cual aplicamos la distancia de seguridad (penalizacion contra
	 * nazgul) contra una unidad counter, sobre una casilla pasada por parametros.
	 *
	 * @param bestia Unidad que se mueve
	 * @param x      Coordenada X de la casilla
	 * @param y      Coordenada Y de la casilla
	 * @param mapa   Mapa del juego.
	 * @return Penalización a sumar al coste/diferencia de esa casilla (0 si no es
	 *         un Nazgul)
	 */
	protected int penalizacionContraNazgul(Personaje bestia, int x, int y, Mapa mapa) {
		if (!(bestia instanceof Nazgul)) {
			return 0;
		}
		return PENALIZACION_CONTRA_NAZGUL_POR_CASILLA * contarContrasDeNazgulSobreCasilla(x, y, mapa);
	}

	/**
	 * Callcula las casillas candidatas para que un nazgul use su movimiento extra
	 * despues de atacar. Tiene en cuenta tambien si hay o no cerca las unidades
	 * Heroes que son counter de él.
	 *
	 * @param bestia Nazgul
	 * @param mapa   Mapa del juego
	 * @return Lista de coordenadas candidatas, ordenada de más a menos segura
	 *         (puede estar vacia si no hay ninguna casilla valida).
	 */
	protected List<int[]> candidatasRetiradaPostAtaqueNazgul(Personaje bestia, Mapa mapa) {
		List<int[]> candidatas = new ArrayList<>();

		for (int dx = -2; dx <= 2; dx++) {
			for (int dy = -2; dy <= 2; dy++) {
				if (dx == 0 && dy == 0) {
					continue;
				}
				int x = bestia.getX() + dx;
				int y = bestia.getY() + dy;

				if (mapa.estaDentroDelMapa(x, y) && mapa.caminoValido(bestia, bestia.getX(), bestia.getY(), x, y)) {
					candidatas.add(new int[] { x, y });
				}
			}
		}
		candidatas.sort((a, b) -> Integer.compare(contarContrasDeNazgulSobreCasilla(a[0], a[1], mapa),
				contarContrasDeNazgulSobreCasilla(b[0], b[1], mapa)));

		return candidatas;
	}

	/**
	 * Metodo con el cual verificamos si una unidad bestia cumple con las reglas del
	 * juego a la hora de atacar a una unidad Heroe.
	 *
	 * @param bestia    Unidad atacante
	 * @param candidato Unidad Heroe candidata
	 * @param mapa      Mapa del juego
	 * @return true, si puede atacar
	 */
	protected boolean esAtacableLegalmente(Personaje bestia, Personaje candidato, Mapa mapa) {
		if (bestia instanceof Nazgul && candidato instanceof Hobbit) {
			return false;
		}
		return bestia.puedeAtacarObjetivo(candidato, mapa);
	}

	/**
	 * Metodo que vamos a usar para darle una puntuacion de prioridad a los
	 * candidatos Heroes. Esta puntuacion variará en funcion de cada nivel de
	 * dificultad.
	 *
	 * @param bestia    Unidad Bestia
	 * @param candidato Candidato Heroe.
	 * @param mapa      Mapa del juego
	 * @return the double Puntuacion del candidato
	 */
	protected abstract double puntuarObjetivo(Personaje bestia, Personaje candidato, Mapa mapa);

	/**
	 * Metodo para dejar señalado/marcado a un Heroe candidato ya elegido, durante
	 * este turno. Lo usaremos para llevar un control de saturacion sobre los
	 * objetivos y asi evitar que todas las unidades controladas por la IA se muevan
	 * a un unico objetivo.
	 *
	 * @param objetivoElegido the objetivo elegido
	 */
	protected void registrarReserva(Personaje objetivoElegido) {
		reservarObjetivo(objetivoElegido);
	}

	/**
	 * Comprueba si el numero de unidades Bestias restantes cumplen la condicion
	 * para activar el modo desesperado
	 *
	 * @param mapa Mapa del juego
	 * @return true, if successful
	 */
	protected boolean modoDesesperado(Mapa mapa) {
		long bestiasVivas = mapa.getTodasLasUnidades().stream()
				.filter(pj -> pj.getFaccion() == Faccion.BESTIAS && pj.estaVivo()).count();
		return bestiasVivas <= UNIDADES_PARA_MODO_DESESPERADO;
	}

	/**
	 * Metodo por el cual comprobamos si el objetivo seleccionado ya tiene asignadas
	 * las unidades maximas para no sobrepasar la saturacion durante este turno.
	 * Usamos Object porque el objetivo puede ser una Ciudad o un Personaje.
	 *
	 * @param objetivo Objetivo (ciudad o personaje) a verificar.
	 * @param mapa     Mapa del juego.
	 * @return true, if successful
	 */
	protected boolean objetivoSaturado(Object objetivo, Mapa mapa) {
		if (modoDesesperado(mapa)) {
			return false;
		}
		return asignacionesPorObjetivo.getOrDefault(objetivo, 0) >= MAX_UNIDADES_POR_OBJETIVO;
	}

	/**
	 * Metodo con el cual aumentaremos las unidades asignada a un objetivo en
	 * concreto.
	 *
	 * @param objetivo the objetivo
	 */
	protected void reservarObjetivo(Object objetivo) {
		asignacionesPorObjetivo.merge(objetivo, 1, Integer::sum);
	}

	/**
	 * Metodo para calcular la mejor casilla cuando esta en modo patrulla.
	 *
	 * @param bestia Unidad Bestia en patrulla
	 * @param mapa   Mapa del juego
	 * @return Coordenadas de la mejor casilla alcanzable para la patrulla, o null
	 *         si no hay ninguna
	 */
	protected int[] mejorCasillaDePatrulla(Personaje bestia, Mapa mapa) {
		if (!(bestia instanceof Bestia)) {
			return null;
		}
		Bestia bestiaConcreta = (Bestia) bestia;
		boolean necesitaNuevoDestino = !bestiaConcreta.tienePatrullaDestinoAsignado()
				|| Math.max(Math.abs(bestiaConcreta.getPatrullaDestinoX() - bestia.getX()),
						Math.abs(bestiaConcreta.getPatrullaDestinoY() - bestia.getY())) <= 1;

		if (necesitaNuevoDestino) {
			int[] nuevoDestino = elegirNuevoPuntoDePatrulla(bestia, mapa);
			if (nuevoDestino == null) {
				return null;
			}
			bestiaConcreta.setPatrullaDestino(nuevoDestino[0], nuevoDestino[1]);
		}
		return mejorCasillaHaciaPunto(bestia, mapa, bestiaConcreta.getPatrullaDestinoX(),
				bestiaConcreta.getPatrullaDestinoY());
	}

	/**
	 * Metodo para elegir nuevo punto de patrulla, de forma aleatoria. Hace 15
	 * intentos maximos de encontrar una casilla ideal para patrullar.
	 *
	 * @param bestia Unidad Bestia que patrulla
	 * @param mapa   Mapa del juego.
	 * @return Coordenadas del nuevo punto de patrulla, o null si no se encontro
	 *         ninguno valido
	 */
	private int[] elegirNuevoPuntoDePatrulla(Personaje bestia, Mapa mapa) {
		if (bestia instanceof Nazgul) {
			int[] puntoCercaDelMonte = puntoAlrededorDeMonteDelDestino(bestia, mapa);
			if (puntoCercaDelMonte != null) {
				return puntoCercaDelMonte;
			}
		}
		int intentosMaximos = 15;

		for (int intento = 0; intento < intentosMaximos; intento++) {
			int x = (int) (Math.random() * mapa.getAncho());
			int y = (int) (Math.random() * mapa.getAlto());

			if (!mapa.estaDentroDelMapa(x, y)) {
				continue;
			}
			TipoTerreno terreno = mapa.getCasilla(x, y).getTerreno();
			if (mapa.puedeAtravesarTerreno(bestia, terreno)) {
				return new int[] { x, y };
			}
		}
		return null;
	}

	/**
	 * Metodo usado para encontrar una casilla aleatoria, exclusivamente dentro del
	 * rango de patrulla de la ciudad del monte del destino.
	 *
	 * @param bestia the bestia
	 * @param mapa   the mapa
	 * @return the int[]
	 */
	private int[] puntoAlrededorDeMonteDelDestino(Personaje bestia, Mapa mapa) {
		Ciudad monteDelDestino = buscarMonteDelDestino(mapa);
		if (monteDelDestino == null) {
			return null;
		}
		Map<String, int[]> casillasValidas = new HashMap<>();

		for (Point casillaCiudad : monteDelDestino.getCasillasDeCiudad()) {
			for (int dx = -RADIO_PATRULLA_MONTE_MAX; dx <= RADIO_PATRULLA_MONTE_MAX; dx++) {
				for (int dy = -RADIO_PATRULLA_MONTE_MAX; dy <= RADIO_PATRULLA_MONTE_MAX; dy++) {
					int distanciaAlBorde = Math.max(Math.abs(dx), Math.abs(dy));
					if (distanciaAlBorde < RADIO_PATRULLA_MONTE_MIN) {
						continue;
					}
					int x = casillaCiudad.x + dx;
					int y = casillaCiudad.y + dy;
					if (!mapa.estaDentroDelMapa(x, y) || mapa.buscarCiudadesPorCasilla(x, y) == monteDelDestino) {
						continue;
					}
					TipoTerreno terreno = mapa.getCasilla(x, y).getTerreno();
					if (mapa.puedeAtravesarTerreno(bestia, terreno)) {
						casillasValidas.put(x + "," + y, new int[] { x, y });
					}
				}
			}
		}
		if (casillasValidas.isEmpty()) {
			return null;
		}
		List<int[]> candidatas = new ArrayList<>(casillasValidas.values());
		return candidatas.get((int) (Math.random() * candidatas.size()));
	}

	/**
	 * Metodo para buscar la cuidad "Monte del Destino", dentro del mapa.
	 *
	 * @param mapa the mapa
	 * @return the ciudad
	 */
	private Ciudad buscarMonteDelDestino(Mapa mapa) {
		for (Ciudad ciudad : mapa.getCiudades()) {
			if (NOMBRE_CIUDAD_MONTE_DEL_DESTINO.equalsIgnoreCase(ciudad.getNombreCiudad())) {
				return ciudad;
			}
		}
		return null;
	}

	/**
	 * Metodo para calcular la mejor ruta hacia una casilla alcanzable por la unidad
	 * Bestia. Tiene en cuenta tambien las penalizaciones por terreno.
	 *
	 * @param bestia    Unidad Bestia
	 * @param mapa      Mapa del juego.
	 * @param objetivoX Coordenada X del punto de destino
	 * @param objetivoY Coordenada Y del punto de destino
	 * @return Coordenadas de la mejor casilla, o null si no hay ninguna alcanzable
	 */
	protected int[] mejorCasillaHaciaPunto(Personaje bestia, Mapa mapa, int objetivoX, int objetivoY) {
		Set<String> alcanzables = mapa.calcularCasillasAlcanzables(bestia);

		if (alcanzables.isEmpty()) {
			return null;
		}
		Map<String, Integer> distanciaReal = calcularDistanciaRealHastaObjetivo(bestia, mapa, objetivoX, objetivoY);
		int[] mejorCasilla = null;
		int mejorDistancia = Integer.MAX_VALUE;

		for (String casilla : alcanzables) {
			Integer distancia = distanciaReal.get(casilla);
			if (distancia == null) {
				continue;
			}
			int[] coordenadas = parsearCoordenadas(casilla);
			int distanciaConPenalizacion = distancia
					+ penalizacionContraNazgul(bestia, coordenadas[0], coordenadas[1], mapa);

			if (distanciaConPenalizacion < mejorDistancia) {
				mejorDistancia = distanciaConPenalizacion;
				mejorCasilla = coordenadas;
			}
		}
		return mejorCasilla;
	}

	/**
	 * Metodo para calcular la mejor ruta hacia un objetivo. Tendremos en cuenta la
	 * distancia deseada en funcion del rango de ataque, invocando al metodo
	 * "distanciaDeseadaHastaObjetivo" que se sobreescribira en funcion de cada
	 * nivel de dificultad IA.
	 *
	 * @param bestia   Unidad Bestia
	 * @param objetivo Personaje objetivo al que se quiere aproximar.
	 * @param mapa     Mapa del juego.
	 * @return Coordenadas de la mejor casilla, o null si no hay ninguna alcanzable.
	 */
	protected int[] mejorCasillaHaciaObjetivo(Personaje bestia, Personaje objetivo, Mapa mapa) {
		Set<String> alcanzables = mapa.calcularCasillasAlcanzables(bestia);

		if (alcanzables.isEmpty()) {
			return null;
		}
		int distanciaDeseada = distanciaDeseadaHastaObjetivo(bestia);
		Map<String, Integer> distanciasReales = calcularDistanciaRealHastaObjetivo(bestia, mapa, objetivo.getX(),
				objetivo.getY());
		int[] mejorCasilla = null;
		int mejorDiferencia = Integer.MAX_VALUE;

		for (String casilla : alcanzables) {
			Integer distanciaReal = distanciasReales.get(casilla);
			if (distanciaReal == null) {
				continue;
			}
			int[] coordenadas = parsearCoordenadas(casilla);
			int diferencia = Math.abs(distanciaReal - distanciaDeseada);
			diferencia += penalizacionContraNazgul(bestia, coordenadas[0], coordenadas[1], mapa);
			if (diferencia < mejorDiferencia) {
				mejorDiferencia = diferencia;
				mejorCasilla = coordenadas;
			}
		}
		if (mejorCasilla != null) {
			return mejorCasilla;
		}

		return mejorCasillaPorLineaRecta(objetivo, alcanzables, distanciaDeseada);
	}

	/**
	 * Invocaremos al metodo calcularDistanciaRealHastaObjetivos, para hacerlos
	 * solamente sobre un objetivo.
	 *
	 * @param bestia    Unidad bestia para la que se calcula el coste de movimiento.
	 * @param mapa      Mapa del juego.
	 * @param objetivoX Coordenada X del destino.
	 * @param objetivoY Coordenada Y del destino.
	 * @return Mapa de distancias, compuesto por coordenada "x,y".
	 */
	protected Map<String, Integer> calcularDistanciaRealHastaObjetivo(Personaje bestia, Mapa mapa, int objetivoX,
			int objetivoY) {
		List<Point> unicoObjetivo = new ArrayList<>();
		unicoObjetivo.add(new Point(objetivoX, objetivoY));

		return calcularDistanciaRealHastaObjetivos(bestia, mapa, unicoObjetivo);
	}

	/**
	 * Usando el algoritmo de Dijktra calcula el coste real de movimiento para las
	 * casillas del mapa, teniendo en cuenta tipo de terreno y limitaciones.
	 *
	 * @param bestia    Unidad bestia para la que se calcula el coste de movimiento.
	 * @param mapa      Mapa del juego.
	 * @param objetivos Lista de puntos de origen de la búsqueda (por ejemplo, las
	 *                  casillas de una ciudad).
	 * @return Mapa de distancias, compuesto por cordenadas x,y.
	 */
	protected Map<String, Integer> calcularDistanciaRealHastaObjetivos(Personaje bestia, Mapa mapa,
			List<Point> objetivos) {
		Map<String, Integer> distancias = new HashMap<>();
		PriorityQueue<int[]> pendientes = new PriorityQueue<>((a, b) -> Integer.compare(a[2], b[2]));

		for (Point objetivo : objetivos) {
			String coordenadaObjetivo = objetivo.x + "," + objetivo.y;
			if (!distancias.containsKey(coordenadaObjetivo)) {
				distancias.put(coordenadaObjetivo, 0);
				pendientes.add(new int[] { objetivo.x, objetivo.y, 0 });
			}
		}

		while (!pendientes.isEmpty()) {
			int[] actual = pendientes.poll();
			int x = actual[0];
			int y = actual[1];
			int coste = actual[2];
			String coordenada = x + "," + y;

			if (coste > distancias.getOrDefault(coordenada, Integer.MAX_VALUE)) {
				continue;
			}
			TipoTerreno terrenoActual = mapa.getCasilla(x, y).getTerreno();

			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					if (dx == 0 && dy == 0) {
						continue;
					}
					int vecinoX = x + dx;
					int vecinoY = y + dy;

					if (!mapa.estaDentroDelMapa(vecinoX, vecinoY)) {
						continue;
					}
					TipoTerreno terrenoVecino = mapa.getCasilla(vecinoX, vecinoY).getTerreno();
					if (!mapa.puedeAtravesarTerreno(bestia, terrenoVecino)) {
						continue;
					}
					int costeDelPaso = Math.max(mapa.costePaso(bestia, terrenoActual),
							mapa.costePaso(bestia, terrenoVecino));
					int nuevoCoste = coste + costeDelPaso;
					String coordenadaVecino = vecinoX + "," + vecinoY;

					if (nuevoCoste < distancias.getOrDefault(coordenadaVecino, Integer.MAX_VALUE)) {
						distancias.put(coordenadaVecino, nuevoCoste);
						pendientes.add(new int[] { vecinoX, vecinoY, nuevoCoste });
					}
				}
			}
		}
		return distancias;
	}

	/**
	 * Usaremos este metodo, como alternativa a
	 * "calcularDistanciaRealHastaObjetivos". Algoritmo Chebyshev.
	 *
	 * @param objetivo         Personaje Heroe objetivo.
	 * @param alcanzables      Conjunto de casillas alcanzables.
	 * @param distanciaDeseada Distancia deseada hasta el objetivo.
	 * @return Coordenadas de la mejor casilla, o null si alcanzables está vacío.
	 */
	private int[] mejorCasillaPorLineaRecta(Personaje objetivo, Set<String> alcanzables, int distanciaDeseada) {
		int[] mejorCasilla = null;
		int mejorDiferencia = Integer.MAX_VALUE;

		for (String casilla : alcanzables) {
			int[] coordenadas = parsearCoordenadas(casilla);
			int distancia = Math.max(Math.abs(objetivo.getX() - coordenadas[0]),
					Math.abs(objetivo.getY() - coordenadas[1]));
			int diferencia = Math.abs(distancia - distanciaDeseada);

			if (diferencia < mejorDiferencia) {
				mejorDiferencia = diferencia;
				mejorCasilla = coordenadas;
			}
		}
		return mejorCasilla;
	}

	/**
	 * Metodo auxiliar para darle formato a las coordenadas de las casillas de
	 * string y las devuelve como un array de int.
	 *
	 * @param casilla the casilla
	 * @return Array de int con las coordenadas x,y;
	 */
	protected int[] parsearCoordenadas(String casilla) {
		String[] coordenadas = casilla.split(",");
		return new int[] { Integer.parseInt(coordenadas[0]), Integer.parseInt(coordenadas[1]) };
	}

	/**
	 * Distancia deseada a la que un personaje prefiere situarse respecto a su
	 * enemigo. Por defecto sera 0, que es el rango de cuerpo cuerpo.
	 *
	 * @param bestia Unidad para la que se calcula.
	 * @return the int distancia (en casillas) a la que esta unidad prefiere
	 *         situarse de su objetivo.
	 */
	protected int distanciaDeseadaHastaObjetivo(Personaje bestia) {
		return 0;
	}

	/**
	 * Calcula la distancia maxima entre dos personajes. Algoritmo Chebyshev
	 *
	 * @param pjA the pj A
	 * @param pjB the pj B
	 * @return the int
	 */
	protected int distanciaEntre(Personaje pjA, Personaje pjB) {
		int dX = Math.abs(pjA.getX() - pjB.getX());
		int dY = Math.abs(pjA.getY() - pjB.getY());

		return Math.max(dX, dY);
	}

	/**
	 * Metodo que cada dificultad dificultad usara a la hora de decidir si
	 * conquistar una ciudad enemiga o no.
	 *
	 * @param configuracionVictoria condiciones de victorias activas.
	 * @return true, if successful
	 */
	protected boolean debeConsiderarConquistaDeCiudades(ConfiguracionVictoria configuracionVictoria) {
		return false;
	}

	/**
	 * Metodo que cada nivel de dificultad usara a la hora de decidir si
	 * reconquistar una ciudad natal perdida o no.
	 *
	 * @return true, if successful
	 */
	protected boolean debeReconquistarCiudadNatalPerdida() {
		return false;
	}

	/**
	 * Metodo en el que se puntuara la importancia de conquistar una ciudad
	 * concreta, basado en los criterios de cada nivel de dificultad.
	 *
	 * @param bestia                the bestia
	 * @param ciudad                the ciudad
	 * @param mapa                  the mapa
	 * @param configuracionVictoria the configuracion victoria
	 * @return the double
	 */
	protected double puntuarCiudad(Personaje bestia, Ciudad ciudad, Mapa mapa,
			ConfiguracionVictoria configuracionVictoria) {
		return Double.NEGATIVE_INFINITY;
	}

	/**
	 * Metodo por el cual comprobaremos si una unidad Bestia puede conquistar una
	 * ciudad.
	 *
	 * @param bestia the bestia
	 * @return true, if successful
	 */
	protected boolean puedeConquistarCiudades(Personaje bestia) {
		return !(bestia instanceof Nazgul);
	}

	/**
	 * Metodo por el que vamos a detectar si existe una amenaza grabe para la unidad
	 * Bestia, la cual hara decidir si deberia huir o luchar.
	 *
	 * @param bestia the bestia
	 * @param mapa   the mapa
	 * @return the personaje
	 */
	protected Personaje detectarAmenazaGrave(Personaje bestia, Mapa mapa) {
		return null;
	}

	/**
	 * Metodo para calcular cual de las unidades Heroes podria alcanzar al personaje
	 * Bestia en cuestion y cuño daño estimado superaria el umbral de vida indicado.
	 *
	 * @param bestia               the bestia
	 * @param mapa                 the mapa
	 * @param umbralVidaPorcentaje umbral basado en porcentaje de la vida a partir
	 *                             el cual se considera amenaza grave.
	 * @return the personaje La unidad Heroe que forma la amenaza mas grave
	 *         superando el umbral, o null sino hay ninguno.
	 */
	protected Personaje detectarAmenazaGravePorUmbral(Personaje bestia, Mapa mapa, double umbralVidaPorcentaje) {
		Personaje peorAmenaza = null;
		int peorDanio = -1;

		for (Personaje pj : mapa.getTodasLasUnidades()) {
			if (pj.getFaccion() != Faccion.HEROES || !pj.estaVivo()) {
				continue;
			}
			int distancia = distanciaEntre(bestia, pj);

			if (distancia > pj.getRangoAtaque() + pj.getMovimiento()) {
				continue;
			}
			int danioQueRecibiria = pj.calcularDanioFinal(bestia, mapa);

			if (danioQueRecibiria < bestia.getVida() * umbralVidaPorcentaje) {
				continue;
			}
			if (danioQueRecibiria > peorDanio) {
				peorDanio = danioQueRecibiria;
				peorAmenaza = pj;
			}
		}
		return peorAmenaza;
	}

	/**
	 * Metodo por el cual calcula entre las casillas alcanzables, la mas alejada de
	 * la amenaza grave indicada, para que la unidad Bestia pueda huir.
	 *
	 * @param bestia  the bestia
	 * @param amenaza the amenaza
	 * @param mapa    the mapa
	 * @return the int[] Array de int con las coordenadas de la mejor casilla de
	 *         huida.
	 */
	protected int[] mejorCasillaParaHuir(Personaje bestia, Personaje amenaza, Mapa mapa) {
		Set<String> alcanzables = mapa.calcularCasillasAlcanzables(bestia);

		if (alcanzables.isEmpty()) {
			return null;
		}
		Map<String, Integer> distanciaReal = calcularDistanciaRealHastaObjetivo(bestia, mapa, amenaza.getX(),
				amenaza.getY());
		int[] mejorCasilla = null;
		int mejorDistancia = -1;

		for (String casilla : alcanzables) {
			int distancia = distanciaReal.getOrDefault(casilla, DISTANCIA_INALCANZABLE);
			if (distancia > mejorDistancia) {
				mejorDistancia = distancia;
				mejorCasilla = parsearCoordenadas(casilla);
			}
		}
		return mejorCasilla;
	}

	/**
	 * Metodo para comprobar si la configuracion activa depende de puntos por
	 * conquista o dominio total de ciudades, para que la estrategia de la IA
	 * priorice o no conquistar ciudades.
	 *
	 * @param configuracionVictoria Condiciones de victoria activas
	 * @return true, if successful
	 */
	protected boolean esVictoriaPorPuntosODominio(ConfiguracionVictoria configuracionVictoria) {
		if (configuracionVictoria == null) {
			return false;
		}

		return configuracionVictoria.estaActiva(TipoCondicionVictoria.LIMITE_DE_PUNTOS)
				|| configuracionVictoria.estaActiva(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS)
				|| configuracionVictoria.estaActiva(TipoCondicionVictoria.DOMINIO);
	}

	/**
	 * Metodo para puntuar la prioridad a la hora de conquistar ciudades combinando
	 * varios parametros.
	 *
	 * @param bestia                       Unidad que evalúa la ciudad
	 * @param ciudad                       Ciudad candidata
	 * @param mapa                         Mapa del juego
	 * @param configuracionVictoria        Condiciones de victoria activas
	 * @param pesoDistancia                Peso aplicado a la distancia hasta la
	 *                                     ciudad
	 * @param bonusReconquistarNatalPropia Bonus si es la ciudad natal propia
	 *                                     perdida
	 * @param bonusNatalHeroes             Bonus si es la ciudad natal de los Héroes
	 * @param bonusNeutral                 Bonus si la ciudad era neutral en origen
	 * @param bonusCiudadVaciaCaballeria   Bonus adicional si la unidad es de
	 *                                     Caballería y la ciudad está vacía de
	 *                                     Héroes
	 * @return the double Puntuación calculada para esa ciudad.
	 */
	protected double puntuarCiudadPorCondicionVictoria(Personaje bestia, Ciudad ciudad, Mapa mapa,
			ConfiguracionVictoria configuracionVictoria, double pesoDistancia, double bonusReconquistarNatalPropia,
			double bonusNatalHeroes, double bonusNeutral, double bonusCiudadVaciaCaballeria) {

		boolean esNatalPropiaPerdida = ciudad.getControlInicial() == ControlCiudad.BESTIAS;
		boolean esNatalHeroes = ciudad.getControlInicial() == ControlCiudad.HEROES;
		boolean esNeutralOrigen = ciudad.getControlInicial() == ControlCiudad.NEUTRAL;
		int distancia = distanciaEntreCiudad(bestia, ciudad);

		if (esNatalPropiaPerdida) {
			return bonusReconquistarNatalPropia - distancia * pesoDistancia;
		}
		double puntuacion = -distancia * pesoDistancia;
		if (esNatalHeroes) {
			puntuacion += bonusNatalHeroes;
		}
		if (esNeutralOrigen) {
			puntuacion += bonusNeutral;
		}
		if (esUnidadDeCaballeria(bestia) && ciudadEstaVacia(ciudad, mapa)) {
			puntuacion += bonusCiudadVaciaCaballeria;
		}

		return puntuacion;
	}

	/**
	 * Metodo para calcular si una unidad podria alcanzar unas coordenadas y en una
	 * cantidad de turnos indicados segun su movimiento por turno
	 *
	 * @param bestia            the bestia
	 * @param objetivoX         the objetivo X
	 * @param objetivoY         the objetivo Y
	 * @param rondasDisponibles the rondas disponibles
	 * @return true, if successful
	 */
	protected boolean estaEnRadioDeReaccion(Personaje bestia, int objetivoX, int objetivoY, int rondasDisponibles) {
		int distancia = Math.max(Math.abs(bestia.getX() - objetivoX), Math.abs(bestia.getY() - objetivoY));
		int alcanceEstimado = Math.max(1, bestia.getMovimiento()) * Math.max(1, rondasDisponibles);

		return distancia <= alcanceEstimado;
	}

	/**
	 * Metodo para comprobar si hay un hobbit en el monte del destino.
	 *
	 * @param hobbit the hobbit
	 * @param mapa   the mapa
	 * @return true, if successful
	 */
	protected boolean estaEnCuentaAtrasEnMonteDelDestino(Personaje hobbit, Mapa mapa) {
		Ciudad ciudad = mapa.buscarCiudadesPorCasilla(hobbit.getX(), hobbit.getY());

		return ciudad != null && NOMBRE_CIUDAD_MONTE_DEL_DESTINO.equalsIgnoreCase(ciudad.getNombreCiudad());
	}

	/**
	 * Metodo para calcular el bonus en el que evalua cuando atacar a un Heroe ayuda
	 * a defender una ciudad Bestia amenazada por el, diferenciando entre ciudad
	 * natal Bestia y ciudad neutral conquistada por las bestias. Tambien analiza el
	 * si hay tiempo de reaccion.
	 *
	 * @param bestia           the bestia
	 * @param heroe            the heroe
	 * @param mapa             the mapa
	 * @param bonusNatal       the bonus natal
	 * @param bonusConquistada the bonus conquistada
	 * @return the double Bonus a sumar a la puntuación del personaje Heroe. Por
	 *         defecto sera 0.0
	 */
	protected double bonusDefensaDeCiudad(Personaje bestia, Personaje heroe, Mapa mapa, double bonusNatal,
			double bonusConquistada) {

		if (bestia instanceof Nazgul) {
			return 0.0;
		}

		Ciudad ciudadAmenazada = ciudadBestiaAmenazadaPor(heroe, mapa);

		if (ciudadAmenazada == null) {
			return 0.0;
		}

		if (!estaEnRadioDeReaccion(bestia, heroe.getX(), heroe.getY(),
				ciudadAmenazada.getTurnosNecesariosParaConquistar())) {
			return 0.0;
		}

		boolean esNatal = ciudadAmenazada.getControlInicial() == ControlCiudad.BESTIAS;

		return esNatal ? bonusNatal : bonusConquistada;
	}

	/**
	 * Metodo que analiza si hay alguna unidad Heroe amenazando una ciudad Bestia.
	 *
	 * @param heroe the heroe
	 * @param mapa  the mapa
	 * @return the ciudad
	 */
	protected Ciudad ciudadBestiaAmenazadaPor(Personaje heroe, Mapa mapa) {
		Ciudad ciudadPropia = mapa.buscarCiudadesPorCasilla(heroe.getX(), heroe.getY());

		if (ciudadPropia != null && ciudadPropia.esCiudadControladaPorBestias()) {
			return ciudadPropia;
		}
		for (Ciudad ciudad : mapa.getCiudades()) {
			if (!ciudad.esCiudadControladaPorBestias()) {
				continue;
			}
			if (distanciaEntreCiudad(heroe, ciudad) <= heroe.getRangoAtaque()) {
				return ciudad;
			}
		}
		return null;
	}

	/**
	 * Metodo para calcular la distancia minima entre una unidad y cualquier casilla
	 * de una ciudad
	 *
	 * @param bestia the bestia
	 * @param ciudad the ciudad
	 * @return the int Distancia.
	 */
	protected int distanciaEntreCiudad(Personaje bestia, Ciudad ciudad) {
		int mejorDistancia = Integer.MAX_VALUE;

		for (Point casilla : ciudad.getCasillasDeCiudad()) {
			int distancia = Math.max(Math.abs(bestia.getX() - casilla.x), Math.abs(bestia.getY() - casilla.y));

			if (distancia < mejorDistancia) {
				mejorDistancia = distancia;
			}
		}
		return mejorDistancia;
	}

	/**
	 * Metodo para saber si una unidad bestia tiene el tipo de combate "CABALLERIA"
	 * o no.
	 *
	 * @param bestia the bestia
	 * @return true, if successful
	 */
	protected boolean esUnidadDeCaballeria(Personaje bestia) {
		return "CABALLERIA".equalsIgnoreCase(bestia.getTipoCombate());
	}

	/**
	 * Metodo para saber si la guarnicion de una ciudad esta vacia.
	 *
	 * @param ciudad the ciudad
	 * @param mapa   the mapa
	 * @return true, if successful
	 */
	protected boolean ciudadEstaVacia(Ciudad ciudad, Mapa mapa) {
		for (Point casilla : ciudad.getCasillasDeCiudad()) {
			Personaje ocupante = mapa.getCasilla(casilla.x, casilla.y).getPersonaje();
			if (ocupante != null && ocupante.estaVivo() && ocupante.getFaccion() == Faccion.HEROES) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Metodo para calcular, usando casillas alcanzables por la unidad Bestia, la
	 * distancia real mas corta hasta cualquier casilla de una ciudad.
	 *
	 * @param bestia the bestia
	 * @param ciudad the ciudad
	 * @param mapa   the mapa
	 * @return the int[] coordenada de la mejor casilla, o null si no hay ninguna
	 *         alcanzable.
	 */
	protected int[] mejorCasillaHaciaCiudad(Personaje bestia, Ciudad ciudad, Mapa mapa) {
		Set<String> alcanzables = mapa.calcularCasillasAlcanzables(bestia);

		if (alcanzables.isEmpty()) {
			return null;
		}
		Map<String, Integer> distanciasReales = calcularDistanciaRealHastaObjetivos(bestia, mapa,
				ciudad.getCasillasDeCiudad());
		int[] mejorCasilla = null;
		int mejorDistancia = Integer.MAX_VALUE;

		for (String casilla : alcanzables) {
			Integer distanciaReal = distanciasReales.get(casilla);
			if (distanciaReal == null) {
				continue;
			}
			if (distanciaReal < mejorDistancia) {
				mejorDistancia = distanciaReal;
				mejorCasilla = parsearCoordenadas(casilla);
			}
		}
		return mejorCasilla;
	}

	/**
	 * Recorre todas las ciudades controladas por la Faccion y decide iniciar el
	 * reclutamiento de una unidad Tropa, elegida con el "selector" pasado por
	 * parametro, en las ciudades que haya reclutamiento disponible.
	 *
	 * @param faccion       the faccion
	 * @param mapa          the mapa
	 * @param rosterTropas  lista de tropas disponibles para reclutar.
	 * @param reclutamiento Controlador de reclutamiento.
	 * @param tropasMaximas limnite de tropas maximas
	 * @param selector      parametro que elige en el caso que haya mas de una
	 *                      unidad, cual de ellas reclutar.
	 */
	protected void reclutarEnCiudadesControladas(Faccion faccion, Mapa mapa, List<Personaje> rosterTropas,
			ReclutamientoControlador reclutamiento, int tropasMaximas, Function<List<Personaje>, Personaje> selector) {

		if (tropasMaximas <= 0 || reclutamiento == null || rosterTropas == null || mapa == null) {
			return;
		}

		for (Ciudad ciudad : mapa.getCiudades()) {
			if (!reclutamiento.puedeIniciarReclutamiento(ciudad, faccion, tropasMaximas)) {
				continue;
			}
			List<Personaje> disponibles = reclutamiento.obtenerTropasReclutablesEnCiudad(rosterTropas, ciudad, faccion);

			if (disponibles.isEmpty()) {
				continue;
			}

			Personaje elegido = selector.apply(disponibles);
			if (elegido != null) {
				reclutamiento.iniciarReclutamiento(ciudad, elegido, tropasMaximas);
			}
		}
	}
}
