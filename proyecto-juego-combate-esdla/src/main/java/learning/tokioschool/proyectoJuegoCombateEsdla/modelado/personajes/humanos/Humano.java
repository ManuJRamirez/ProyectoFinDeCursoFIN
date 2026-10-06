package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.humanos;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Casilla;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoTerreno;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Heroe;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.orcos.Orco;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.trasgos.Trasgo;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.urukHai.UrukHai;

/**
 * The Class Humano.
 * 
 * Representa a un Humano, perteneciente al bando Heroes.
 * 
 * Características por tipo de combate (tipoCombate):
 *
 * Guerrero:
 * <ul>
 * <li>Bonus de 20% defensa, cuando está defendiendo su ciudad (Héroes).</li>
 * <li>Bonus ataque 15% vs Trasgos.</li>
 * <li>No puede andar por montañas ni ríos.</li>
 * </ul>
 *
 * Caballería:
 * <ul>
 * <li>Bonus ataque 30% vs exploradores en terreno LLANO.</li>
 * <li>Bonus ataque 25% vs Uruk-hai en terreno LLANO.</li>
 * <li>Bonus ataque 25% vs Orcos en terreno LLANO.</li>
 * <li>Bonus ataque 25% vs Trasgos en terreno LLANO.</li>
 * <li>Bonus ataque 35% vs Arqueros en terreno LLANO.</li>
 * <li>Penalización de ataque del 35% en terreno ciudad (al combatir dentro de
 * una ciudad).</li>
 * <li>No puede andar por montañas ni ríos.</li>
 * <li>No puede atacar desde fuera de la ciudad a una unidad que esté dentro de
 * la ciudad; debe estar dentro de la ciudad para atacar a una unidad dentro de
 * la ciudad.</li>
 * </ul>
 *
 * Mago:
 * <ul>
 * <li>Bonus ataque 25% vs exploradores.</li>
 * <li>Bonus 50% ataque, 100% defensa cuando ataca desde la ciudad hacia una
 * unidad enemiga fuera de la ciudad (uso de murallas).</li>
 * <li>Probabilidad del 10% de restaurar un 10% de vida a las unidades aliadas
 * que estén en su rango de ataque, al finalizar su turno (el turno de los
 * Héroes).</li>
 * <li>No puede andar por montañas ni ríos.</li>
 * </ul>
 */
public class Humano extends Heroe {

	/**
	 * Constructor de clase. Creas una nueva unidad Humano.
	 *
	 * @param nombre      the nombre
	 * @param vida        the vida
	 * @param armadura    the armadura
	 * @param rangoAtaque the rango ataque
	 * @param movimiento  the movimiento
	 * @param faccion     the faccion
	 */
	public Humano(String nombre, int vida, int armadura, int rangoAtaque, int movimiento, Faccion faccion) {
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

		if ("GUERRERO".equals(tipoCombate) && enemigo instanceof Trasgo) {
			multiplicador += 0.15;
		}
		if ("CABALLERIA".equals(tipoCombate)) {
			boolean enLlano = enemigoEstaEnLlano(enemigo, mapa);
			boolean esExplorador = "EXPLORADOR".equalsIgnoreCase(enemigo.getTipoCombate());
			boolean esArquero = "ARQUERO".equalsIgnoreCase(enemigo.getTipoCombate());

			if (enLlano) {
				if (esExplorador) {
					multiplicador += 0.30;
				}
				if (enemigo instanceof UrukHai) {
					multiplicador += 0.25;
				}
				if (enemigo instanceof Orco) {
					multiplicador += 0.25;
				}
				if (enemigo instanceof Trasgo) {
					multiplicador += 0.25;
				}
				if (esArquero) {
					multiplicador += 0.35;
				}
			}
			if (estaAtacandoDentroDeCiudad(mapa)) {
				multiplicador -= 0.35;
			}
		}
		if ("MAGO".equals(tipoCombate) && "EXPLORADOR".equalsIgnoreCase(enemigo.getTipoCombate())) {
			multiplicador += 0.25;
		}
		if ("MAGO".equals(tipoCombate) && estaProtegidoPorMuralla(enemigo, mapa)) {
			multiplicador += 0.50;
		}

		return Math.max(multiplicador, 0.1);
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

		if ("GUERRERO".equals(tipoCombate) && mapa != null) {
			Ciudad combateDentroCiudadPropia = mapa.buscarCiudadesPorCasilla(getX(), getY());
			if (combateDentroCiudadPropia != null
					&& combateDentroCiudadPropia.getControlSobreCiudad() == ControlCiudad.HEROES) {
				modificador += 0.20;
			}
		}
		if ("MAGO".equals(tipoCombate) && estaProtegidoPorMuralla(atacante, mapa)
				&& mapa != null) {
			modificador += 1.0;
		}
		return Math.max(modificador, 0.05);
	}

	/**
	 * Verifica si el enemigo indicado por parametros se encuentra en una casilla
	 * del tipo Llano.
	 *
	 * @param enemigo personaje enemigo sobre el que se consulta.
	 * @param mapa    mapa para saber que tipo de terreno es esa casilla
	 * @return true, si esta en llano.
	 */
	private boolean enemigoEstaEnLlano(Personaje enemigo, Mapa mapa) {
		if (mapa == null) {
			return false;
		}

		Casilla casilla = mapa.getCasilla(enemigo.getX(), enemigo.getY());

		return casilla != null && casilla.getTerreno() == TipoTerreno.LLANO;
	}

	/**
	 * Metodo que usaremos para verificar si la caballeria esta luchando dentro de
	 * la ciudad y aplicarle su respectiva penalizacion.
	 *
	 * @param mapa the mapa
	 * @return true, if successful
	 */
	private boolean estaAtacandoDentroDeCiudad(Mapa mapa) {
		if (mapa == null) {
			return false;
		}
		return mapa.buscarCiudadesPorCasilla(getX(), getY()) != null;
	}

}
