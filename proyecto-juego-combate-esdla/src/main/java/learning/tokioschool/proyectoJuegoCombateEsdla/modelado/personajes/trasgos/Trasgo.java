package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.trasgos;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Bestia;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;

/**
 * The Class Trasgo.
 * 
 * Representa a un Trasgo, perteneciente al bando Bestias.
 * 
 * Su ataque se basa en un sólo dado con valores entre 0 y 90. No tienen
 * bonificaciones ni penalizaciones raciales.
 * 
 * Características por tipo de combate (tipoCombate):
 *
 * Guerrero:
 * <ul>
 * <li>Tienen un 25% de probabilidad de curarse un 15% de su vida al finalizar
 * su turno (turno de las Bestias).</li>
 * <li>No pueden andar por montañas ni ríos.</li>
 * </ul>
 *
 * Explorador:
 * <ul>
 * <li>Unidad capaz de detectar a los Hobbits.</li>
 * <li>Pueden andar por montañas con una penalización de movimiento de 3.</li>
 * <li>Rango normal de detección de 2.</li>
 * <li>Penalización del campo de detección en montaña, reduciéndose a 1.</li>
 * <li>No pueden andar por ríos.</li>
 * </ul>
 */
public class Trasgo extends Bestia {

	/**
	 * Instantiates a new trasgo.
	 *
	 * @param nombre      the nombre
	 * @param vida        the vida
	 * @param armadura    the armadura
	 * @param rangoAtaque the rango ataque
	 * @param movimiento  the movimiento
	 * @param faccion     the faccion
	 */
	public Trasgo(String nombre, int vida, int armadura, int rangoAtaque, int movimiento, Faccion faccion) {
		super(nombre, vida, armadura, rangoAtaque, movimiento, Faccion.BESTIAS);
	}

}
