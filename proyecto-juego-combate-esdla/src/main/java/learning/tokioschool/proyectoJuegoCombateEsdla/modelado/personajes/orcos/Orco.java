package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.orcos;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoTerreno;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Bestia;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.elfos.Elfo;

/**
 * The Class Orco.
 * 
 * Representa a un Orco perteneciente al bando Bestias.
 * 
 * Su ataque se basa en un sólo dado con valores entre 0 y 90. Tienen un bonus
 * de raza que les hace reducir la armadura del enemigo un 10% en cada ataque
 * 
 * Características por tipo de combate (tipoCombate):
 *
 * Guerrero:
 * <ul>
 * <li>Bajan la defensa del enemigo un 10% cuando realizan el ataque.</li>
 * <li>No puede andar por montañas ni ríos.</li>
 * </ul>
 *
 * Caballería:
 * <ul>
 * <li>Bajan la defensa del enemigo un 10% cuando realizan el ataque.</li>
 * <li>Bonus de ataque del 20% contra Arquero Elfo en terreno LLANO.</li>
 * <li>Penalización de ataque del 35% cuando esta unidad está combatiendo dentro
 * de una ciudad.</li>
 * <li>No puede andar por montañas ni ríos.</li>
 * <li>No puede atacar desde fuera de la ciudad a una unidad que esté dentro de
 * la ciudad; debe estar dentro de la ciudad para atacar a una unidad dentro de
 * la ciudad.</li>
 * </ul>
 *
 * Arquero:
 * <ul>
 * <li>Bajan la defensa del enemigo un 10% cuando realizan el ataque.</li>
 * <li>Bonus 100% ataque, 100% defensa cuando ataca desde la ciudad hacia una
 * unidad enemiga fuera de la ciudad (uso de murallas).</li>
 * <li>No puede andar por montañas ni ríos.</li>
 * </ul>
 */
public class Orco extends Bestia {

	/**
	 * Constructor de clase. Creas una nueva unidad Orco.
	 *
	 * @param nombre      the nombre
	 * @param vida        the vida
	 * @param armadura    the armadura
	 * @param rangoAtaque the rango ataque
	 * @param movimiento  the movimiento
	 * @param faccion     the faccion
	 */
	public Orco(String nombre, int vida, int armadura, int rangoAtaque, int movimiento, Faccion faccion) {
		super(nombre, vida, armadura, rangoAtaque, movimiento, Faccion.BESTIAS);
	}

	/**
	 * Sobreescribe el metodo de la clase Personje. En este caso, se aplica una
	 * reducción de la armadura del enemigo del 10%.
	 *
	 * @return devuelve el modificador de la armadura del enemigo con la reduccion
	 *         aplicada.
	 */
	@Override
	public double getModificadorArmaduraEnemigo() {
		return 0.9;
	}

	/**
	 * Aplica las bonificaciones de ataque segun los parámetros estipulados en el
	 * archivo "bases de ataque y movimiento de personajes".
	 * 
	 * En este caso aplica una bonificacion del 20% de daño contra Arqueros Elfos
	 * que estén en terreno LLANO, y una reduccion del 35% de daño si la Caballeria
	 * Orco está atacando desde dentro de una ciudad.
	 *
	 * @param enemigo se le pasa el enemigo al que se va a atacar.
	 * @param mapa    tambien se le pasa el mapa, para saber en que terreno se
	 *                lucha.
	 * @return the multiplicador ataque
	 */
	@Override
	public double getMultiplicadorAtaque(Personaje enemigo, Mapa mapa) {
		double multiplicador = 1.0;
		String tipoCombate = tipoCombate();

		if ("CABALLERIA".equals(tipoCombate)) {
			boolean esArqueroElfo = enemigo instanceof Elfo && "ARQUERO".equalsIgnoreCase(enemigo.getTipoCombate());
			boolean esLlano = mapa != null && mapa.getCasilla(enemigo.getX(), enemigo.getY()) != null
					&& mapa.getCasilla(enemigo.getX(), enemigo.getY()).getTerreno() == TipoTerreno.LLANO;

			if (esArqueroElfo && esLlano) {
				multiplicador += 0.20;
			}
			if (mapa != null && mapa.buscarCiudadesPorCasilla(getX(), getY()) != null) {
				multiplicador -= 0.35;
			}
		}

		if ("ARQUERO".equals(tipoCombate) && estaProtegidoPorMuralla(enemigo, mapa)) {
			multiplicador += 1.0;
		}
		return multiplicador;
	}

	/**
	 * Calcula el modificador de defensa. Aplica las condiciones establecidas en el
	 * encabezado de la clase.
	 *
	 * @param atacante the atacante
	 * @param mapa the mapa
	 * @return the modificador defensa
	 */
	@Override
	public double getModificadorDefensa(Personaje atacante, Mapa mapa) {
		double modificador = 1.0;

		if ("ARQUERO".equals(tipoCombate()) && estaProtegidoPorMuralla(atacante, mapa)) {
			modificador += 1.0;
		}
		return Math.max(modificador, 0.05);
	}
	
	

}
