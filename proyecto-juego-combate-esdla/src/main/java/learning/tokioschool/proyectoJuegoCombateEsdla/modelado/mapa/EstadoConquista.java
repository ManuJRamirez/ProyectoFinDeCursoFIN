
package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;

/**
 * The Class EstadoConquista.
 * 
 * Clase que representa el estado en el que se encuentra la conquista de una
 * ciudad. Se usa sobretodo para mostrarlo en el HUD.
 */
public class EstadoConquista {

	/**
	 * Atributo en el que se guarda el estado en el que se encuentra la conquista
	 * (reposo, disputa o en progreso).
	 */
	private final TipoEstadoConquista tipo;

	/** Faccion que esta implicada en la conquista. Null si no hay ninguna. */
	private final Faccion faccion;

	/** Numero de turnos consecutivos que lleva capturando la ciudad. */
	private final int turnosConquistando;

	/**
	 * Atributo que va a guardar los turnos necesarios de forma consecutiva para
	 * conquistar una ciudad. Actualmente son 3.
	 */
	private final int turnosNecesarios;

	/**
	 * Constructor de clase
	 *
	 * @param tipo               tipo de estado
	 * @param faccion            faccion
	 * @param turnosConquistando turnos conquistando consecutivos
	 * @param turnosNecesarios   turnos necesarios
	 */
	public EstadoConquista(TipoEstadoConquista tipo, Faccion faccion, int turnosConquistando, int turnosNecesarios) {
		this.tipo = tipo;
		this.faccion = faccion;
		this.turnosConquistando = turnosConquistando;
		this.turnosNecesarios = turnosNecesarios;
	}

	/**
	 * Gets the tipo.
	 *
	 * @return the tipo
	 */
	public TipoEstadoConquista getTipo() {
		return tipo;
	}

	/**
	 * Gets the faccion.
	 *
	 * @return the faccion
	 */
	public Faccion getFaccion() {
		return faccion;
	}

	/**
	 * Gets the turnos conquistando.
	 *
	 * @return the turnos conquistando
	 */
	public int getTurnosConquistando() {
		return turnosConquistando;
	}

	/**
	 * Gets the turnos necesarios.
	 *
	 * @return the turnos necesarios
	 */
	public int getTurnosNecesarios() {
		return turnosNecesarios;
	}

}
