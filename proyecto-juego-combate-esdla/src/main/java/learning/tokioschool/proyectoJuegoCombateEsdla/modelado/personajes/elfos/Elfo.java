package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.elfos;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Heroe;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.orcos.Orco;

/**
 * The Class Elfo.
 * 
 * Representa a un Elfo. Es una unidad perteneciente al bando Heroes.
 * 
 * Características por tipo de combate (tipoCombate):
 *
 * Arquero:
 * <ul>
 * <li>Bonus ataque 25% vs voladores.</li>
 * <li>Bonus 100% ataque, 100% defensa cuando ataca desde la ciudad hacia una
 * unidad enemiga fuera de la ciudad (uso de murallas).</li>
 * <li>No puede andar por montañas ni ríos.</li>
 * </ul>
 *
 * Guerrero:
 * <ul>
 * <li>Bonus de 20% de defensa, cuando está defendiendo su ciudad (Héroes).</li>
 * <li>Bonus ataque 10% vs Orcos.</li>
 * <li>No puede andar por montañas ni ríos.</li>
 * </ul>
 *
 * Mago:
 * <ul>
 * <li>Bonus ataque 25% vs voladores.</li>
 * <li>Bonus 50% ataque, 100% defensa cuando ataca desde la ciudad hacia una
 * unidad enemiga fuera de la ciudad (uso de murallas).</li>
 * <li>No puede andar por montañas ni ríos.</li>
 * </ul>
 */
public class Elfo extends Heroe {

	/**
	 * Constructor de clase. Creas una nueva unidad Elfo.
	 *
	 * @param nombre      the nombre
	 * @param vida        the vida
	 * @param armadura    the armadura
	 * @param rangoAtaque the rango ataque
	 * @param movimiento  the movimiento
	 * @param faccion     the faccion
	 */
	public Elfo(String nombre, int vida, int armadura, int rangoAtaque, int movimiento, Faccion faccion) {
		super(nombre, vida, armadura, rangoAtaque, movimiento, Faccion.HEROES);
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
		String tipoCombate = tipoCombate();
		boolean enemigoVolador = enemigo instanceof Nazgul;

		if (("ARQUERO".equals(tipoCombate) || "MAGO".equals(tipoCombate)) && enemigoVolador) {
			multiplicador += 0.25;
		}

		if ("GUERRERO".equals(tipoCombate) && enemigo instanceof Orco) {
			multiplicador += 0.10;
		}

		if (estaProtegidoPorMuralla(enemigo, mapa)) {
			if ("ARQUERO".equals(tipoCombate)) {
				multiplicador += 1.0;
			} else if ("MAGO".equals(tipoCombate)) {
				multiplicador += 0.5;
			}
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
		double modificador = 1.0;
		String tipoCombate = tipoCombate();

		if (("ARQUERO".equals(tipoCombate) || "MAGO".equals(tipoCombate))
				&& estaProtegidoPorMuralla(atacante, mapa) && mapa != null) {
			modificador += 1.0;
		}

		if ("GUERRERO".equals(tipoCombate) && mapa != null) {
			Ciudad ciudadPropiaQueDefiende = mapa.buscarCiudadesPorCasilla(getX(), getY());
			if (ciudadPropiaQueDefiende != null
					&& ciudadPropiaQueDefiende.getControlSobreCiudad() == ControlCiudad.HEROES) {
				modificador += 0.20;
			}
		}
		return Math.max(modificador, 0.05);

	}


}
