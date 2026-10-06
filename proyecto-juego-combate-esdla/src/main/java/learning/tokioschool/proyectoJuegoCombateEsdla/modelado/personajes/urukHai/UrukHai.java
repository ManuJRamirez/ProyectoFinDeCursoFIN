package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.urukHai;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoTerreno;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Bestia;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.elfos.Elfo;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.humanos.Humano;

/**
 * The Class UrukHai.
 * 
 * Representa a un Uruk Hai, perteneciente al bando Bestias.
 * 
 * Características por tipo de combate (tipoCombate):
 *
 * Guerrero:
 * <ul>
 * <li>Bonus de 20% ataque contra Guerrero Elfo en terreno LLANO.</li>
 * <li>Bonus de 20% ataque contra Guerrero Humano en terreno LLANO.</li>
 * <li>Bonus de 10% ataque contra todas las unidades Heroes (menos Hobbit)
 * cuando están atacando una ciudad dominada por Heroes y han conseguido entrar
 * dentro de la ciudad, es decir, están atacando a las unidades Heroes que están
 * dentro de la ciudad, desde dentro de la ciudad.</li>
 * <li>No pueden andar por montañas ni ríos.</li>
 * </ul>
 *
 * Arquero:
 * <ul>
 * <li>Bonus de 10% ataque contra Guerrero Elfo.</li>
 * <li>Bonus de 10% ataque contra Guerrero Humano.</li>
 * <li>No puede andar por montañas ni ríos.</li>
 * <li>Bonus 100% ataque, 100% defensa cuando ataca desde la ciudad hacia una
 * unidad enemiga fuera de la ciudad (uso de murallas).</li>
 * </ul>
 */
public class UrukHai extends Bestia {

	/**
	 * Instantiates a new uruk hai.
	 *
	 * @param nombre      the nombre
	 * @param vida        the vida
	 * @param armadura    the armadura
	 * @param rangoAtaque the rango ataque
	 * @param movimiento  the movimiento
	 * @param faccion     the faccion
	 */
	public UrukHai(String nombre, int vida, int armadura, int rangoAtaque, int movimiento, Faccion faccion) {
		super(nombre, vida, armadura, rangoAtaque, movimiento, Faccion.BESTIAS);
	}

	@Override
	public double getMultiplicadorAtaque(Personaje enemigo, Mapa mapa) {
		double multiplicador = 1.0;
		String tipoCombate = tipoCombate();

		if ("GUERRERO".equals(tipoCombate)) {
			boolean esLlano = mapa != null && mapa.getCasilla(enemigo.getX(), enemigo.getY()) != null
					&& mapa.getCasilla(enemigo.getX(), enemigo.getY()).getTerreno() == TipoTerreno.LLANO;
			if (esGuerreroElfoOHumano(enemigo) && esLlano) {
				multiplicador += 0.20;
			}
			if (mapa != null && !(enemigo instanceof Hobbit)) {
				Ciudad ciudadCombate = mapa.buscarCiudadesPorCasilla(getX(), getY());
				boolean atacaDentroDeCiudadHeroes = ciudadCombate != null
						&& ciudadCombate.getControlSobreCiudad() == ControlCiudad.HEROES
						&& mapa.buscarCiudadesPorCasilla(enemigo.getX(), enemigo.getY()) == ciudadCombate;
				if (atacaDentroDeCiudadHeroes) {
					multiplicador += 0.10;
				}
			}
		}
		if ("ARQUERO".equals(tipoCombate) && esGuerreroElfoOHumano(enemigo)) {
			multiplicador += 0.10;
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
	 * @param atacante personaje que ataca.
	 * @param mapa     mapa para saber el tipo del terreno donde se produce el
	 *                 ataque.
	 * @return el modificador defensa especifico en cada caso.
	 */
	@Override
	public double getModificadorDefensa(Personaje atacante, Mapa mapa) { 
		double modificador = 1.0;
 
		if ("ARQUERO".equals(tipoCombate()) && estaProtegidoPorMuralla(atacante, mapa)) {
			modificador += 1.0;
		}
		return Math.max(modificador, 0.05);
	}
	

	/**
	 * Metodo que verifica si el enemigo es un guerrero humano o un guerrero elfo.
	 *
	 * @param enemigo enemigo que examinamos
	 * @return true, si es un guerrero elfo o humano.
	 */
	private boolean esGuerreroElfoOHumano(Personaje enemigo) {
		boolean esGuerrero = "GUERRERO".equalsIgnoreCase(enemigo.getTipoCombate());
		boolean esElfoOHumano = enemigo instanceof Humano || enemigo instanceof Elfo;

		return esGuerrero && esElfoOHumano;
	}
}
