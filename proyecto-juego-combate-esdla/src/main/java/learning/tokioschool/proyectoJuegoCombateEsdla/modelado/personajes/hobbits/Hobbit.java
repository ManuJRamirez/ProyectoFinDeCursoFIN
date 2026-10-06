package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Heroe;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;

/**
 * The Class Hobbit.
 * 
 * Representa a un Hobbit. Es una unidad perteneciente al bando Heroes.
 * 
 *
 * Características especiales de Frodo y Sam:
 *
 * Frodo:
 * <ul>
 * <li>Bonus de ataque contra Explorador del 100%. Si Sam está en un rango de 4
 * casillas, bonus del 200%.</li>
 * <li>SIEMPRE JUEGA, lo elija el humano o no.</li>
 * <li>Unidad invisible solo localizable por voladores y exploradores. Si es
 * detectada, todo el bando enemigo la ve, y sabe dónde está mientras el
 * volador/explorador esté en el rango de detección. Si Frodo deja de moverse
 * durante 2 turnos, se volvería invisible de nuevo, pero si se mueve y el
 * explorador/volador está en rango de detección sería detectado de nuevo.</li>
 * <li>Si llega a la ciudad "Monte del Destino", se acaba la partida y ganan los
 * Héroes.</li>
 * <li>Las unidades voladoras no pueden atacarlo, pero sí descubrirlo.</li>
 * <li>Si muere se acaba la partida y las Bestias ganan.</li>
 * <li>Puede andar por montañas y ríos pero perdiendo 2 de movilidad en montaña
 * y 3 de movilidad en río.</li>
 * <li>No puede atacar a Nazgûl.</li>
 * <li>Si ataca pierde la invisibilidad.</li>
 * <li>Pierde la invisibilidad cuando entra en la ciudad "Monte del
 * Destino".</li>
 * </ul>
 *
 * Sam:
 * <ul>
 * <li>Bonus de ataque contra Explorador del 100%. Si Frodo está en un rango de
 * 4 casillas, bonus del 200%.</li>
 * <li>Unidad invisible solo localizable por voladores y exploradores. Si es
 * detectada, todo el bando enemigo la ve, y sabe dónde está mientras el
 * volador/explorador esté en el rango de detección. Si Sam deja de moverse
 * durante 2 turnos, se volvería invisible de nuevo, pero si se mueve y el
 * explorador/volador está en rango de detección sería detectado de nuevo.</li>
 * <li>Las unidades voladoras no pueden atacarlo, pero sí descubrirlo.</li>
 * <li>Puede andar por montañas y ríos pero perdiendo 2 de movilidad en montaña
 * y 3 de movilidad en río.</li>
 * <li>No puede atacar a Nazgûl.</li>
 * <li>Si ataca pierde la invisibilidad.</li>
 * <li>Pierde la invisibilidad cuando entra en la ciudad "Monte del
 * Destino".</li>
 * </ul>
 * 
 */
public class Hobbit extends Heroe {

	/**
	 * Constructor de clase. Creas una nueva unidad Hobbit.
	 *
	 * @param nombre      the nombre
	 * @param vida        the vida
	 * @param armadura    the armadura
	 * @param rangoAtaque the rango ataque
	 * @param movimiento  the movimiento
	 * @param faccion     the faccion
	 */
	public Hobbit(String nombre, int vida, int armadura, int rangoAtaque, int movimiento, Faccion faccion) {
		super(nombre, vida, armadura, rangoAtaque, movimiento, Faccion.HEROES);
	}

	/**
	 * Aplica el bonus de Frodo y Sam contra unidades Trasgos Exploradoras: +100% de
	 * daño, o +200% si además el compañero está en rango.
	 *
	 * @param enemigo Personaje al que se ataca.
	 * @param mapa    Mapa necesario para localizar al compañero.
	 * @return Multiplicador de ataque a aplicar.
	 */
	@Override
	public double getMultiplicadorAtaque(Personaje enemigo, Mapa mapa) {
		double multiplicador = 1.0;

		if (esFrodoOSam() && "EXPLORADOR".equalsIgnoreCase(enemigo.getTipoCombate())) {
			if (companieroEnRango(mapa)) {
				multiplicador += 2.0;
			} else {
				multiplicador += 1.0;
			}
		}
		return multiplicador;
	}

	/**
	 * Metodo para saber si es Frodo o Sam sobre los que vamos a trabajar.
	 *
	 * @return true, si es uno de los dos.
	 */
	private boolean esFrodoOSam() {
		return "Frodo".equalsIgnoreCase(getNombre()) || "Sam".equalsIgnoreCase(getNombre());
	}

	/**
	 * Metodo para saber quien tenemos en objetivo y quien es su compañero en ese
	 * momento.
	 *
	 * @return string, devuelve el nombre del hobbit compañero de tu objetivo ahora
	 *         mismo.
	 */
	private String nombreDelCompaniero() {
		return "Frodo".equalsIgnoreCase(getNombre()) ? "Sam" : "Frodo";
	}

	/**
	 * Metodo que comprueba si el hay compañero hobbit (Frodo o Sam) vivo y si esta
	 * en el rango maximo de 4 casillas para aplicar los bonus de compañero.
	 *
	 * @param mapa se le pasa el Mapa para calcular las posiciones.
	 * @return true, si estan en rango.
	 */
	public boolean companieroEnRango(Mapa mapa) {
		if (mapa == null) {
			return false;
		}
		String companiero = nombreDelCompaniero();
		for (Personaje pj : mapa.getTodasLasUnidades()) {
			if (companiero.equalsIgnoreCase(pj.getNombre()) && pj.estaVivo()) {
				int distX = Math.abs(getX() - pj.getX());
				int distY = Math.abs(getY() - pj.getY());
				if (Math.max(distX, distY) <= 4) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the nombre companiero.
	 *
	 * @return the nombre companiero
	 */
	public String getNombreCompaniero() {
		return nombreDelCompaniero();
	}

}
