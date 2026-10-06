package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Heroe;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.reclutarTropa.OrdenReclutamiento;

/**
 * The Class Ciudad.
 * 
 * Representa una ciudad dentro del mapa del juego.
 * 
 * Una ciudad está formada por:
 * <ul>
 * <li>- Un conjunto de casillas que definen su área urbana.</li>
 * <li>- Un territorio alrededor que puede ser controlado.</li>
 * <li>- Un conjunto de unidades que se encuentran dentro de ella.</li>
 * </ul>
 * Esta clase gestiona la captura de la ciudad, la salida de unidades, y
 * determina si está dentro del rango de ataque de un personaje.
 */
public class Ciudad {

	/**
	 * Numero de turnos consecutivos de control sobre una ciudad para completar una
	 * conquista
	 */
	private static final int TURNOS_NECESARIOS_PARA_CONQUISTAR = 3;

	/** Nombre de la ciudad. */
	private String nombreCiudad;

	/** Casillas que forman una ciudad. */
	private List<Point> casillasDeCiudad = new ArrayList<>();

	/** Casillas que forman el territorio controlado por esa ciudad. */
	private List<Point> casillasFormanTerritorio = new ArrayList<>();

	/** Unidades actualmente dentro ciudad. */
	private List<Personaje> unidadesDentroCiudad = new ArrayList<>();

	/** Faccion que controla la ciudad. */
	private ControlCiudad controlSobreCiudad = ControlCiudad.NEUTRAL;

	/** Control que hay sobre la ciudad justo cuando la partida comienza. */
	private ControlCiudad controlInicial = ControlCiudad.NEUTRAL;

	/**
	 * Indica si se ha establecido el control inicial de las ciudades al iniciar la
	 * partida.
	 */
	private boolean controlInicialFijado = false;

	/** Faccion que está en progreso de conquista. */
	private Faccion faccionEnProgresoDeConquista = null;

	/**
	 * Turnos de control consecutivo que una Faccion lleva en la ciudad durante el
	 * proceso de conquista.
	 */
	private int turnosDeControlContinuo = 0;

	/** Estado de reclutamiento activo en la ciudad. */
	private OrdenReclutamiento reclutamientoActivo;

	/**
	 * Constructor de clase con el que creamos una Ciudad con el nombre indicado.
	 *
	 * @param nombre Nombre de la ciudad.
	 */
	public Ciudad(String nombre) {
		this.nombreCiudad = nombre;
	}

	/**
	 * Método por el cual añadimos una casilla a una ciudad.
	 *
	 * @param x Coordenada X.
	 * @param y Coordenada Y.
	 */
	public void addCasillaCiudad(int x, int y) {
		casillasDeCiudad.add(new Point(x, y));
	}

	/**
	 * Añade una casilla al territorio de controlado por una ciudad.
	 *
	 * @param x Coordenada X.
	 * @param y Coordenada Y.
	 */
	public void addTerritorio(int x, int y) {
		casillasFormanTerritorio.add(new Point(x, y));
	}

	/**
	 * Método para saber si hay unidades dentro de una ciudad.
	 *
	 * @return true, Si hay unidades dentro.
	 */
	public boolean tieneUnidadesDentro() {
		return !unidadesDentroCiudad.isEmpty();
	}

	/**
	 * Metodo con el cual fijamos las ciudades natales de cada faccion al iniciar la
	 * partida.
	 */
	public void fijarControlInicial() {
		if (!controlInicialFijado) {
			this.controlInicial = this.controlSobreCiudad;
			this.controlInicialFijado = true;
		}
	}

	/**
	 * Verificamos si una ciudad ha sido fijada como ciudad natal.
	 *
	 * @return true, if successful
	 */
	public boolean esCiudadNatal() {
		return controlInicial != ControlCiudad.NEUTRAL;
	}

	/**
	 * Metodo para saber si una faccion gana un punto de control de ciudad. Ganan
	 * puntos aquella faccion que controla una ciudad inicialmente neutral que ha
	 * sido conquistada por ellos y tambien ganan un punto por cada ciudad natal
	 * enemiga capturada. No dan puntos si una faccion controla su propia ciudad
	 * natal.
	 *
	 * @return the faccion que genera punto
	 */
	public Faccion getFaccionQueGeneraPunto() {
		if (controlSobreCiudad == ControlCiudad.NEUTRAL) {
			return null;
		}
		if (controlInicial == ControlCiudad.NEUTRAL) {
			return (controlSobreCiudad == ControlCiudad.HEROES) ? Faccion.HEROES : Faccion.BESTIAS;
		}

		if (controlInicial == ControlCiudad.HEROES) {
			return (controlSobreCiudad == ControlCiudad.BESTIAS) ? Faccion.BESTIAS : null;
		}
		if (controlInicial == ControlCiudad.BESTIAS) {
			return (controlSobreCiudad == ControlCiudad.HEROES) ? Faccion.HEROES : null;
		}
		return null;
	}

	/**
	 * Método en el que un Personaje captura una ciudad para su faccion.
	 *
	 * @param pj Se le pasa el Personaje que captura la ciudad.
	 */
	public void capturarCiudad(Personaje pj) {
		if (pj instanceof Heroe) {
			controlSobreCiudad = ControlCiudad.HEROES;
		} else {
			controlSobreCiudad = ControlCiudad.BESTIAS;
		}

		if (!unidadesDentroCiudad.contains(pj)) {
			unidadesDentroCiudad.add(pj);
		}

	}

	/**
	 * Metodo para saber si una unidad puede conquistar o interferir en el proceso
	 * de conquista de una ciudad. Los nazguls y los hobbits no influyen en estos
	 * casos.
	 *
	 * @param pj El personaje que vamos a examinar.
	 * @return true, if successful
	 */
	private boolean puedeConquistar(Personaje pj) {
		return !(pj instanceof Nazgul) && !(pj instanceof Hobbit);
	}

	/**
	 * Metodo que usaremos para llevar un control o seguimiento del proceso de
	 * conquista de una ciudad. Para esto ignoraremos la presencia de hobbits y
	 * nazguls. Si hay unidades de ambas facciones, el proceso estara reiniciado y
	 * en disputa. Si solo hay unidades de una faccion, entonces el contador avanza.
	 * Cuando este contador llega al turnos maximos para conquistar una ciudad, la
	 * ciudad cambia a la faccion de esa unidad que hay dentro de la ciudad.
	 *
	 * @return the faccion
	 */
	public Faccion actualizarProgresoDeConquista() {
		Set<Faccion> faccionesPresentes = new HashSet<>();

		for (Personaje pj : unidadesDentroCiudad) {
			if (pj == null || !pj.estaVivo() || !puedeConquistar(pj)) {
				continue;
			}
			faccionesPresentes.add(pj.getFaccion());
		}
		if (faccionesPresentes.size() != 1) {
			faccionEnProgresoDeConquista = null;
			turnosDeControlContinuo = 0;
			return null;
		}

		Faccion unicaFaccionPresente = faccionesPresentes.iterator().next();

		if (unicaFaccionPresente != faccionEnProgresoDeConquista) {
			faccionEnProgresoDeConquista = unicaFaccionPresente;
			turnosDeControlContinuo = 1;

		} else {
			turnosDeControlContinuo++;
		}
		if (turnosDeControlContinuo >= TURNOS_NECESARIOS_PARA_CONQUISTAR) {
			ControlCiudad nuevoControl = (unicaFaccionPresente == Faccion.HEROES) ? ControlCiudad.HEROES
					: ControlCiudad.BESTIAS;
			if (controlSobreCiudad != nuevoControl) {
				controlSobreCiudad = nuevoControl;
				reclutamientoActivo = null;
				return unicaFaccionPresente;
			}
		}
		return null;
	}

	/**
	 * Metodo que gestiona el estado visible, es decir el HUD del proceso de
	 * conquista de una ciudad. Dara la informacion para que luego sea representada.
	 *
	 * @return the estado conquista visible
	 */
	public EstadoConquista getEstadoConquistaVisible() {
		boolean hayHeroeValidoParaConquistar = false;
		boolean hayBestiaValidaParaConquistar = false;

		for (Personaje pj : unidadesDentroCiudad) {
			if (pj == null || !pj.estaVivo() || !puedeConquistar(pj)) {
				continue;
			}
			if (pj.getFaccion() == Faccion.HEROES) {
				hayHeroeValidoParaConquistar = true;
			} else {
				hayBestiaValidaParaConquistar = true;
			}
		}
		if (hayHeroeValidoParaConquistar && hayBestiaValidaParaConquistar) {
			return new EstadoConquista(TipoEstadoConquista.DISPUTA, null, 0, 0);
		}
		if (!hayHeroeValidoParaConquistar && !hayBestiaValidaParaConquistar) {
			return new EstadoConquista(TipoEstadoConquista.REPOSO, null, 0, 0);
		}

		Faccion faccionPresente = hayHeroeValidoParaConquistar ? Faccion.HEROES : Faccion.BESTIAS;
		ControlCiudad controlEquivalente = (faccionPresente == Faccion.HEROES) ? ControlCiudad.HEROES
				: ControlCiudad.BESTIAS;

		if (controlSobreCiudad == controlEquivalente) {
			return new EstadoConquista(TipoEstadoConquista.REPOSO, null, 0, 0);
		}

		return new EstadoConquista(TipoEstadoConquista.EN_PROGRESO, faccionPresente, turnosDeControlContinuo,
				TURNOS_NECESARIOS_PARA_CONQUISTAR);
	}

	/**
	 * Metódo que controla la salida de un Personaje de la ciudad.
	 *
	 * @param pj Personaje que abandona la ciudad.
	 */
	public void salirCiudad(Personaje pj) {
		unidadesDentroCiudad.remove(pj);
	}

	/**
	 * Método para saber si una ciudad es controlada por el bando Heroes.
	 *
	 * @return true, Si la controlan los Heroes.
	 */
	public boolean esCiudadControladaPorHeroes() {
		return controlSobreCiudad == ControlCiudad.HEROES;
	}

	/**
	 * Método para saber si una ciudad es controlada por el bando Bestias.
	 *
	 * @return true, Si la controlan las Bestias.
	 */
	public boolean esCiudadControladaPorBestias() {
		return controlSobreCiudad == ControlCiudad.BESTIAS;
	}

	/**
	 * Metodo para saber si una ciudad es Neutral.
	 *
	 * @return true, Si es neutral.
	 */
	public boolean esCiudadNeutral() {
		return controlSobreCiudad == ControlCiudad.NEUTRAL;
	}

	/**
	 * Método para saber si una unidad esta en rango para atacar una ciudad..
	 *
	 * @param atacante Personaje que quiera atacar.
	 * @return true, Si está en rango de ataque.
	 */
	public boolean estaEnRangoDeAtaque(Personaje atacante) {
		int rango = atacante.getRangoAtaque();

		for (Point p : casillasDeCiudad) {
			int distanciaX = Math.abs(atacante.getX() - p.x);
			int distanciaY = Math.abs(atacante.getY() - p.y);

			int distancia = distanciaX + distanciaY;

			if (rango == 1) {
				if (distancia == 1) {
					return true;
				}
			} else {
				if (distancia <= rango) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Gets the nombre ciudad.
	 *
	 * @return the nombre ciudad
	 */
	public String getNombreCiudad() {
		return nombreCiudad;
	}

	/**
	 * Gets the turnos necesarios para conquistar.
	 *
	 * @return the turnos necesarios para conquistar
	 */
	public int getTurnosNecesariosParaConquistar() {
		return TURNOS_NECESARIOS_PARA_CONQUISTAR;
	}

	/**
	 * Sets the nombre ciudad.
	 *
	 * @param nombreCiudad the new nombre ciudad
	 */
	public void setNombreCiudad(String nombreCiudad) {
		this.nombreCiudad = nombreCiudad;
	}

	/**
	 * Gets the casillas de ciudad.
	 *
	 * @return the casillas de ciudad
	 */
	public List<Point> getCasillasDeCiudad() {
		return casillasDeCiudad;
	}

	/**
	 * Sets the casillas de ciudad.
	 *
	 * @param casillasDeCiudad the new casillas de ciudad
	 */
	public void setCasillasDeCiudad(List<Point> casillasDeCiudad) {
		this.casillasDeCiudad = casillasDeCiudad;
	}

	/**
	 * Gets the casillas forman territorio.
	 *
	 * @return the casillas forman territorio
	 */
	public List<Point> getCasillasFormanTerritorio() {
		return casillasFormanTerritorio;
	}

	/**
	 * Sets the casillas forman territorio.
	 *
	 * @param casillasFormanTerritorio the new casillas forman territorio
	 */
	public void setCasillasFormanTerritorio(List<Point> casillasFormanTerritorio) {
		this.casillasFormanTerritorio = casillasFormanTerritorio;
	}

	/**
	 * Gets the unidades dentro ciudad.
	 *
	 * @return the unidades dentro ciudad
	 */
	public List<Personaje> getUnidadesDentroCiudad() {
		return unidadesDentroCiudad;
	}

	/**
	 * Gets para saber todas unidades dentro ciudad.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the unidades dentro ciudad
	 */
	public List<Personaje> getUnidadesDentroCiudad(int x, int y) {
		List<Personaje> unidadesFiltradas = new ArrayList<>();
		for (Personaje pj : unidadesDentroCiudad) {
			if (pj.getX() == x && pj.getY() == y) {
				unidadesFiltradas.add(pj);
			}
		}
		return unidadesFiltradas;
	}

	/**
	 * Sets the unidades dentro ciudad.
	 *
	 * @param unidadesDentroCiudad the new unidades dentro ciudad
	 */
	public void setUnidadesDentroCiudad(List<Personaje> unidadesDentroCiudad) {
		this.unidadesDentroCiudad = unidadesDentroCiudad;
	}

	/**
	 * Gets the control sobre ciudad.
	 *
	 * @return the control sobre ciudad
	 */
	public ControlCiudad getControlSobreCiudad() {
		return controlSobreCiudad;
	}

	/**
	 * Gets the control inicial.
	 *
	 * @return the control inicial
	 */
	public ControlCiudad getControlInicial() {
		return controlInicial;
	}

	/**
	 * Metodo para saber el número de unidades dentro de una ciudad.
	 *
	 * @return Numero de unidades dentro de una ciudad.
	 */
	public int getNumeroUnidadesDentroCiudad() {
		return unidadesDentroCiudad.size();
	}

	/**
	 * Sets the control sobre ciudad.
	 *
	 * @param controlSobreCiudad the new control sobre ciudad
	 */
	public void setControlSobreCiudad(ControlCiudad controlSobreCiudad) {
		this.controlSobreCiudad = controlSobreCiudad;
	}

	/**
	 * Gets the faccion en progreso de conquista.
	 *
	 * @return the faccion en progreso de conquista
	 */
	public Faccion getFaccionEnProgresoDeConquista() {
		return faccionEnProgresoDeConquista;
	}

	/**
	 * Sets the faccion en progreso de conquista.
	 *
	 * @param faccionEnProgresoDeConquista the new faccion en progreso de conquista
	 */
	public void setFaccionEnProgresoDeConquista(Faccion faccionEnProgresoDeConquista) {
		this.faccionEnProgresoDeConquista = faccionEnProgresoDeConquista;
	}

	/**
	 * Gets the turnos de control continuo.
	 *
	 * @return the turnos de control continuo
	 */
	public int getTurnosDeControlContinuo() {
		return turnosDeControlContinuo;
	}

	/**
	 * Sets the turnos de control continuo.
	 *
	 * @param turnosDeControlContinuo the new turnos de control continuo
	 */
	public void setTurnosDeControlContinuo(int turnosDeControlContinuo) {
		this.turnosDeControlContinuo = turnosDeControlContinuo;
	}

	/**
	 * Adds the unidad.
	 *
	 * @param pj the pj
	 */
	public void addUnidad(Personaje pj) {
		if (!unidadesDentroCiudad.contains(pj)) {
			unidadesDentroCiudad.add(pj);
		}
	}

	/**
	 * Gets the unidades dentro.
	 *
	 * @return the unidades dentro
	 */
	public List<Personaje> getUnidadesDentro() {
		return unidadesDentroCiudad;
	}

	/**
	 * Gets the unidades en una casilla en concreto dentro de la ciudad.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the unidades dentro
	 */
	public List<Personaje> getUnidadesDentro(int x, int y) {
		return getUnidadesDentroCiudad(x, y);
	}

	/**
	 * Gets the reclutamiento activo.
	 *
	 * @return the reclutamiento activo
	 */
	public OrdenReclutamiento getReclutamientoActivo() {
		return reclutamientoActivo;
	}

	/**
	 * Sets the reclutamiento activo.
	 *
	 * @param reclutamientoActivo the new reclutamiento activo
	 */
	public void setReclutamientoActivo(OrdenReclutamiento reclutamientoActivo) {
		this.reclutamientoActivo = reclutamientoActivo;
	}

	/**
	 * Tiene reclutamiento activo.
	 *
	 * @return true, if successful
	 */
	public boolean tieneReclutamientoActivo() {
		return reclutamientoActivo != null;
	}

}
