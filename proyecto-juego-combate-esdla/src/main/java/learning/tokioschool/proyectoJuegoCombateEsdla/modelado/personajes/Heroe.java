package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.orcos.Orco;

/**
 * The Class Heroe.
 * 
 * Representa a una unidad del bando de los Héroes.
 * 
 * Esta clase es abstracta porque no existe un "Héroe genérico". Solo existen
 * clases concretas como Humanos, Elfos, Hobbits, etc
 */
public abstract class Heroe extends Personaje {

	/**
	 * Constructor base para cualquier unidad del bando de los Héroes.
	 *
	 * @param nombre      Nombre del personaje.
	 * @param vida        Puntos de vida iniciales.
	 * @param armadura    Valor de armadura.
	 * @param rangoAtaque Rango de ataque (1 = melee, 3 = distancia).
	 * @param movimiento  Casillas de movimiento por turno.
	 * @param faccion     Facción a la que pertenece. En este caso sera la Faccion
	 *                    Heroes.
	 */
	public Heroe(String nombre, int vida, int armadura, int rangoAtaque, int movimiento, Faccion faccion) {
		super(nombre, vida, armadura, rangoAtaque, movimiento, faccion);
	}

	/**
	 * Sobrescribe el metodo calcularAtaque de personaje.
	 * 
	 * Calcula el daño de ataque de un Héroe tirando dos dados de 0 a 100 y
	 * quedándose con el valor más alto. Si el enemigo es un Orco, se añade un bonus
	 * fijo de 10 puntos de daño.
	 *
	 * @param enemigo Personaje al que se ataca.
	 * @return Daño de ataque calculado.
	 */
	@Override
	public int calcularAtaque(Personaje enemigo) {

		int dado1 = (int) (Math.random() * 101);
		int dado2 = (int) (Math.random() * 101);

		int danioAtaque = Math.max(dado1, dado2);

		if (enemigo instanceof Orco) {
			danioAtaque += 10;
		}

		setUltimaTiradaDados("🎲 (HUMANO) -> " + getNombre() + ": " + dado1 + " / " + dado2 + " (se queda con "
				+ Math.max(dado1, dado2) + ")");

		return danioAtaque;
	}

}
