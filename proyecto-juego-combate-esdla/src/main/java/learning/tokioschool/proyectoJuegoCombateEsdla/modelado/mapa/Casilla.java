package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;

/**
 * The Class Casilla.
 * 
 * Esta clase representa una unica casilla(celda) del mapa. Guarda su tipo de
 * terreno y el personaje que la ocupa, en caso de que haya uno.
 */
public class Casilla {

	/** Tipo de terreno de cada casilla. */
	private TipoTerreno terreno;

	/** Atributo donde guardamos el personaje. Cuando lo haya */
	private Personaje personaje;

	/**
	 * Constructor de clase, en el que crearemos cada casilla, dandole un tipo de
	 * terreno.
	 *
	 * @param terreno the terreno
	 */
	public Casilla(TipoTerreno terreno) {
		this.terreno = terreno;
	}

	/**
	 * Metodo con el cual comprobamos si una casilla esta vacia o hay algun
	 * personaje dentro.
	 *
	 * @return true, si está vacia.
	 */
	public boolean estaVacia() {
		return personaje == null;
	}

	/**
	 * Metodo que vacia una casilla al salir un personaje de ella.
	 */
	public void personajeSaleCasilla() {
		this.personaje = null;
	}

	/**
	 * Gets the terreno.
	 *
	 * @return the terreno
	 */
	public TipoTerreno getTerreno() {
		return terreno;
	}

	/**
	 * Sets the terreno.
	 *
	 * @param terreno the new terreno
	 */
	public void setTerreno(TipoTerreno terreno) {
		this.terreno = terreno;
	}

	/**
	 * Gets the personaje.
	 *
	 * @return the personaje
	 */
	public Personaje getPersonaje() {
		return personaje;
	}

	/**
	 * Sets the personaje.
	 *
	 * @param personaje the new personaje
	 */
	public void setPersonaje(Personaje personaje) {
		this.personaje = personaje;
	}

}
