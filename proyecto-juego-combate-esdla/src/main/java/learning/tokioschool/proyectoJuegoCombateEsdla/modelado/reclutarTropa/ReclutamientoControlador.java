package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.reclutarTropa;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.CargadorUnidades;
import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.PantallaJuegoControlador;
import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.PantallaJuegoControlador.TipoEvento;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.sonidos.GestorSonido;

// TODO: Auto-generated Javadoc
/**
 * The Class ReclutamientoControlador.
 * 
 * Clase controlador encargada de gestionar el reclutamiento de unidades "TROPA"
 * en las ciudades. Va a comprobar qué tropas estan disponibles para ser
 * reclutadas en cada ciudad, iniciar ordenes de reclutamiento, procesa turno a
 * turno hasta completar el reclutamiento y luego las añadira al mapa en una
 * casilla libre de la ciudad.
 */
public class ReclutamientoControlador {

	/** The mapa. */
	private final Mapa mapa;

	/** The vista. */
	private final PantallaJuegoControlador vista;

	/**
	 * Constructor de clase.
	 *
	 * @param mapa  the mapa
	 * @param vista the vista
	 */
	public ReclutamientoControlador(Mapa mapa, PantallaJuegoControlador vista) {
		this.mapa = mapa;
		this.vista = vista;
	}

	/**
	 * Metodo para obtener un listado de unidades "TROPA" disponibles para reclutar
	 * en la ciudad.
	 *
	 * @param todasLasUnidades todas las tropas disponibles en el juego
	 * @param ciudad           ciudad a evaluar
	 * @param faccion          faccion a evaluar
	 * @return the list listado de unidades Tropa disponible para reclutar en una
	 *         ciudad determinada.
	 */
	public List<Personaje> obtenerTropasReclutablesEnCiudad(List<Personaje> todasLasUnidades, Ciudad ciudad,
			Faccion faccion) {
		List<Personaje> unidadesDisponibles = new ArrayList<>();

		if (todasLasUnidades == null || ciudad == null || faccion == null) {
			return unidadesDisponibles;
		}
		for (Personaje tropa : todasLasUnidades) {
			if (!"TROPA".equalsIgnoreCase(tropa.getCategoria())) {
				continue;
			}
			if (tropa.getFaccion() != faccion) {
				continue;
			}
			if (ciudad.getCasillasDeCiudad().contains(new Point(tropa.getX(), tropa.getY()))) {
				unidadesDisponibles.add(tropa);
			}
		}
		return unidadesDisponibles;
	}

	/**
	 * Metodo con el que contamos todas las unidades TROPA que tiene una faccion en
	 * su ejercito.
	 *
	 * @param faccion the faccion
	 * @return the int
	 */
	public int contarTropasVivas(Faccion faccion) {
		int total = 0;
		for (Personaje pj : mapa.getTodasLasUnidades()) {
			if (pj.estaVivo() && pj.getFaccion() == faccion && "TROPA".equalsIgnoreCase(pj.getCategoria())) {
				total++;
			}
		}
		return total;
	}

	/**
	 * Metodo para comprobar si se puede iniciar un reclutamiento en una ciudad
	 * indicada. Para ello dicha ciudad debe estar controlada por la faccion, no
	 * debe tener un reclutamiento activo y no se debe haber sobrepasado el limite
	 * maximo de cantidad de unidad Tropa.
	 *
	 * @param ciudad        the ciudad
	 * @param faccion       the faccion
	 * @param tropasMaximas the tropas maximas
	 * @return true, if successful
	 */
	public boolean puedeIniciarReclutamiento(Ciudad ciudad, Faccion faccion, int tropasMaximas) {
		if (ciudad == null || faccion == null || tropasMaximas <= 0) {
			return false;
		}
		if (!esControladaPor(ciudad, faccion)) {
			return false;
		}
		if (ciudad.tieneReclutamientoActivo()) {
			return false;
		}
		return contarTropasVivas(faccion) < tropasMaximas;
	}

	/**
	 * Metodo para iniciar el reclutamiento de una tropa y en una ciudad indicadas.
	 *
	 * @param ciudad        the ciudad
	 * @param tropa         the tropa
	 * @param tropasMaximas the tropas maximas
	 * @return true, if successful
	 */
	public boolean iniciarReclutamiento(Ciudad ciudad, Personaje tropa, int tropasMaximas) {
		if (tropa == null || !puedeIniciarReclutamiento(ciudad, tropa.getFaccion(), tropasMaximas)) {
			return false;
		}
		ciudad.setReclutamientoActivo(new OrdenReclutamiento(tropa));
		vista.registrarAccion("🛠️ " + ciudad.getNombreCiudad() + " comienza a reclutar " + tropa.getNombre() + " ("
				+ tropa.getTurnosCreacion() + " turnos).", TipoEvento.CREACION, tropa.getFaccion());
		return true;
	}

	/**
	 * Metodo para procesar el fin de turno en cuanto a lo que se refiere al
	 * reclutamiento. Avanza todas las ordenes y cuando se completa el
	 * reclutamiento, si la faccion no ha superado el limite de tropas maximas,
	 * coloca la nueva unidad en una casilla libre de la ciudad
	 *
	 * @param faccion       the faccion
	 * @param tropasMaximas the tropas maximas
	 */
	public void procesarFinDeTurno(Faccion faccion, int tropasMaximas) {
		if (tropasMaximas <= 0) {
			return;
		}
		boolean sonidoReclutamientoYaHaSonado = false;

		for (Ciudad ciudad : mapa.getCiudades()) {
			if (!ciudad.tieneReclutamientoActivo() || !esControladaPor(ciudad, faccion)) {
				continue;
			}
			OrdenReclutamiento orden = ciudad.getReclutamientoActivo();
			orden.avanzarTurno();

			if (!orden.estaCompletada()) {
				continue;
			}
			if (contarTropasVivas(faccion) >= tropasMaximas) {
				continue;
			}

			Point casillaDestino = buscarCasillaParaReclutar(ciudad, orden.getTropa());
			if (casillaDestino == null) {
				vista.registrarAccion(
						"⏸️ " + ciudad.getNombreCiudad() + " no puede reclutar a " + orden.getTropa().getNombre()
								+ ": no hay ninguna casilla libre en la ciudad. El reclutamiento queda en pausa.",
						TipoEvento.CREACION, faccion);
				continue;
			}

			Personaje nuevaTropa = CargadorUnidades.clonarUnidad(orden.getTropa());
			mapa.colocarUnidad(casillaDestino.x, casillaDestino.y, nuevaTropa);
			ciudad.setReclutamientoActivo(null);

			vista.registrarAccion(
					"⚔️ ¡" + nuevaTropa.getNombre() + " ha sido reclutado en " + ciudad.getNombreCiudad() + "!",
					TipoEvento.CREACION, faccion);

			if (faccion == Faccion.HEROES && !sonidoReclutamientoYaHaSonado) {
				GestorSonido.reproducirTropaReclutada();
				sonidoReclutamientoYaHaSonado = true;
			}
		}
	}

	/**
	 * Metodo para calcular la casilla mas cercana disponible a la posicion base
	 * (definida en archivo "unidades.json") en caso de que ésta esté ocupada. Si
	 * estuviera libre la coloca directamente en su posicion base.
	 *
	 * @param ciudad         the ciudad
	 * @param tropaPlantilla the tropa plantilla
	 * @return the point las coordenadas de la casilla.
	 */
	private Point buscarCasillaParaReclutar(Ciudad ciudad, Personaje tropaPlantilla) {
		Point casillaBase = new Point(tropaPlantilla.getX(), tropaPlantilla.getY());

		if (mapa.getCasilla(casillaBase.x, casillaBase.y).estaVacia()) {
			return casillaBase;
		}

		Point casillaMasCercana = null;
		int menorDistancia = Integer.MAX_VALUE;

		for (Point casilla : ciudad.getCasillasDeCiudad()) {
			if (!mapa.getCasilla(casilla.x, casilla.y).estaVacia()) {
				continue;
			}
			int distancia = Math.max(Math.abs(casilla.x - casillaBase.x), Math.abs(casilla.y - casillaBase.y));
			if (distancia < menorDistancia) {
				menorDistancia = distancia;
				casillaMasCercana = casilla;
			}
		}
		return casillaMasCercana;
	}

	/**
	 * Metodo para saber si una ciudad indicada, esta controlada por una faccion pasada tambien por parametros.
	 *
	 * @param ciudad  the ciudad
	 * @param faccion the faccion
	 * @return true, if successful
	 */
	private boolean esControladaPor(Ciudad ciudad, Faccion faccion) {
		ControlCiudad control = ciudad.getControlSobreCiudad();

		if (control == null || control == ControlCiudad.NEUTRAL) {
			return false;
		}

		return (control == ControlCiudad.HEROES && faccion == Faccion.HEROES)
				|| (control == ControlCiudad.BESTIAS && faccion == Faccion.BESTIAS);
	}
}
