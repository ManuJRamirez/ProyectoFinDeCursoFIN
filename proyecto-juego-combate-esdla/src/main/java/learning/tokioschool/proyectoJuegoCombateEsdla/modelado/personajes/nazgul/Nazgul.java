/*
 * 
 */
package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoTerreno;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Bestia;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Heroe;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;

/**
 * The Class Nazgul.
 * 
 * * Representa a un Nazgul, perteneciente al bando Bestias.
 * 
 * Su mision principal es localizar a los Hobbits (unidad invisible) y ataques
 * rapidos contra unidades en Llano.
 * 
 * Características por tipo de combate (tipoCombate):
 *
 * Volador:
 * <ul>
 * <li>Unidad capaz de detectar a los Hobbits.</li>
 * <li>Rango de detección normal es 3.</li>
 * <li>Rango de detección en montaña 1.</li>
 * <li>No puede atacar a Hobbit.</li>
 * <li>No puede conquistar ninguna ciudad.</li>
 * <li>Puede andar por cualquier zona del mapa.</li>
 * <li>Bonus de ataque del 50% contra cualquier unidad de los Heroes (excepto
 * Hobbits) en terreno llano.</li>
 * <li>Puede moverse, atacar y tener un segundo movimiento de 2 casillas
 * máximo.</li>
 * <li>Bonus defensa del 80% contra unidades que no sean Arquero o Mago.</li>
 * </ul>
 */
public class Nazgul extends Bestia {

	/**
	 * Instantiates a new nazgul.
	 *
	 * @param nombre      the nombre
	 * @param vida        the vida
	 * @param armadura    the armadura
	 * @param rangoAtaque the rango ataque
	 * @param movimiento  the movimiento
	 * @param faccion     the faccion
	 */
	public Nazgul(String nombre, int vida, int armadura, int rangoAtaque, int movimiento, Faccion faccion) {
		super(nombre, vida, armadura, rangoAtaque, movimiento, Faccion.BESTIAS);
	}

	/**
	 * Metodo que calcula el multiplicador de ataque. Aplica las condiciones
	 * establecidas en el encabezado de la clase.
	 *
	 * @param enemigo personaje al que se ataca.
	 * @param mapa    mapa para saber el tipo del terreno donde se produce el
	 *                ataque.
	 * @return devuelve el multiplicador de ataque especifico en cada caso.
	 */
	@Override
	public double getMultiplicadorAtaque(Personaje enemigo, Mapa mapa) {
		double multiplicador = 1.0;
		boolean esHeroeNoHobbit = enemigo instanceof Heroe && !(enemigo instanceof Hobbit);
		boolean esLlano = mapa != null && mapa.getCasilla(enemigo.getX(), enemigo.getY()) != null
				&& mapa.getCasilla(enemigo.getX(), enemigo.getY()).getTerreno() == TipoTerreno.LLANO;

		if (esHeroeNoHobbit && esLlano) {
			multiplicador += 0.50;
		}
		return multiplicador;
	}

	/**
	 * Calcula el modificador de defensa. Aplica las condiciones establecidas en el
	 * encabezado de la clase.
	 *
	 * @param atacante personaje que ataca.
	 * @param mapa     mapa para saber el tipo del terreno donde se produce el
	 *                 ataque.
	 * @return el modificador defensa especifico en cada caso.
	 */
	@Override
	public double getModificadorDefensa(Personaje atacante, Mapa mapa) {
		String tipoAtacante = atacante.getTipoCombate() == null ? "" : atacante.getTipoCombate().toUpperCase();
		boolean esArqueroOMago = "ARQUERO".equals(tipoAtacante) || "MAGO".equals(tipoAtacante);

		if (!esArqueroOMago) {
			return 1.80;
		}
		return 1.0;
	}
}
