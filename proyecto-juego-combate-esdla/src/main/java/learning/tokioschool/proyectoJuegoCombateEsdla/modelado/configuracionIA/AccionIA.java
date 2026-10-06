package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.configuracionIA;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;

// TODO: Auto-generated Javadoc
/**
 * The Class AccionIA.
 * 
 * Clase en la que se engloban las acciones que la IA puede realizar sobre una
 * unidad Bestia durante su turno.
 */
public class AccionIA {

	/**
	 * The Enum Tipo.
	 * 
	 * Tipos de accion que puede realizar la IA.
	 */
	public enum Tipo {

		/** Moverse a una casilla. */
		MOVER,
		/** Atacar a un Heroe. */
		ATACAR,
		/** No hacer nada durante este turno. */
		ESPERAR
	}

	/** Atributo donde guardaremos el tipo de accion decidida. */
	private final Tipo tipoAccion;

	/** Coordenada X si decidimos mover o atacar. */
	private final int destinoX;

	/** Coordenada X si decidimos mover o atacar. */
	private final int destinoY;

	/** Heroe seleccionado si la IA decide atacar. Null si no decide atacar. */
	private final Personaje objetivo;

	/**
	 * Instantiates a new accion IA.
	 *
	 * @param tipoAccion the tipo accion
	 * @param destinoX   the destino X
	 * @param destinoY   the destino Y
	 * @param objetivo   the objetivo
	 */
	public AccionIA(Tipo tipoAccion, int destinoX, int destinoY, Personaje objetivo) {
		this.tipoAccion = tipoAccion;
		this.destinoX = destinoX;
		this.destinoY = destinoY;
		this.objetivo = objetivo;
	}

	/**
	 * Mover.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the accion IA
	 */
	public static AccionIA mover(int x, int y) {
		return new AccionIA(Tipo.MOVER, x, y, null);
	}

	/**
	 * Atacar.
	 *
	 * @param objetivo the objetivo
	 * @return the accion IA
	 */
	public static AccionIA atacar(Personaje objetivo) {
		return new AccionIA(Tipo.ATACAR, objetivo.getX(), objetivo.getY(), objetivo);
	}

	/**
	 * Esperar.
	 *
	 * @return the accion IA
	 */
	public static AccionIA esperar() {
		return new AccionIA(Tipo.ESPERAR, -1, -1, null);
	}

	/**
	 * Gets the tipo accion.
	 *
	 * @return the tipo accion
	 */
	public Tipo getTipoAccion() {
		return tipoAccion;
	}

	/**
	 * Gets the destino X.
	 *
	 * @return the destino X
	 */
	public int getDestinoX() {
		return destinoX;
	}

	/**
	 * Gets the destino Y.
	 *
	 * @return the destino Y
	 */
	public int getDestinoY() {
		return destinoY;
	}

	/**
	 * Gets the objetivo.
	 *
	 * @return the objetivo
	 */
	public Personaje getObjetivo() {
		return objetivo;
	}

}
