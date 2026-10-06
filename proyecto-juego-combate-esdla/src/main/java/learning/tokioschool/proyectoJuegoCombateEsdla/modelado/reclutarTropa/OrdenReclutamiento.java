package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.reclutarTropa;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;

// TODO: Auto-generated Javadoc
/**
 * The Class OrdenReclutamiento.
 * 
 * Clase usada para representar una orden de relcutamiento en curso en una
 * ciudad.
 */
public class OrdenReclutamiento {

	/** Personaje Tropa que se va a reclutar. */
	private final Personaje tropa;

	/** Turnos de que tardara en reclutar. */
	private final int turnosCreacionTotales;

	/** The turnos creacion restantes. */
	private int turnosCreacionRestantes;

	/**
	 * Constructor de clase
	 *
	 * @param tropa the tropa
	 */
	public OrdenReclutamiento(Personaje tropa) {
		this.tropa = tropa;
		this.turnosCreacionTotales = Math.max(1, tropa.getTurnosCreacion());
		this.turnosCreacionRestantes = this.turnosCreacionTotales;
	}

	/**
	 * Metodo para avanzar el turno en el reclutamiento, decrementando los turnos
	 * restantes para la creacion.
	 */
	public void avanzarTurno() {
		if (turnosCreacionRestantes > 0) {
			turnosCreacionRestantes--;
		}
	}

	/**
	 * Metodo para comprobar si el reclutamiento se ha concluido.
	 *
	 * @return true, if successful
	 */
	public boolean estaCompletada() {
		return turnosCreacionRestantes <= 0;
	}

	/**
	 * Gets the tropa.
	 *
	 * @return the tropa
	 */
	public Personaje getTropa() {
		return tropa;
	}

	/**
	 * Gets the turnos creacion totales.
	 *
	 * @return the turnos creacion totales
	 */
	public int getTurnosCreacionTotales() {
		return turnosCreacionTotales;
	}

	/**
	 * Gets the turnos creacion restantes.
	 *
	 * @return the turnos creacion restantes
	 */
	public int getTurnosCreacionRestantes() {
		return turnosCreacionRestantes;
	}

}
