package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes;

/**
 * The Class Bestia. Representa a una unidad del bando de las Bestias.
 *
 * Esta clase es abstracta porque no existe una "Bestia genérica" en el juego:
 * solo existen tipos concretos (Orcos, Trasgos, Huargos, etc.).
 *
 */
public abstract class Bestia extends Personaje {

	/**
	 * Coordenada X del destino de patrulla asignado por la IA (-1 si no tiene
	 * ninguno).
	 */
	private int patrullaDestinoX = -1;

	/**
	 * Coordenada X del destino de patrulla asignado por la IA (-1 si no tiene
	 * ninguno)
	 */
	private int patrullaDestinoY = -1;

	/**
	 * Constructor base para culquier unidad del bando de las Bestias.
	 *
	 * @param nombre      Nombre de la unidad.
	 * @param vida        Puntos de vida.
	 * @param armadura    Puntos de armadura
	 * @param rangoAtaque Rango de ataque
	 * @param movimiento  the movimiento
	 * @param faccion     the faccion
	 */
	public Bestia(String nombre, int vida, int armadura, int rangoAtaque, int movimiento, Faccion faccion) {
		super(nombre, vida, armadura, rangoAtaque, movimiento, faccion);
	}

	/**
	 * Sobrescribe el metodo calcularAtaque de Personaje.
	 * 
	 * Calcula el ataque de una unidad Bestia. Este ataque se basa en una única
	 * tirada de dados entre 0 y 90.
	 *
	 * @param enemigo Se le pasa el personaje enimigo al que va a atacar.
	 * @return resultado de la tirada.
	 */
	@Override
	public int calcularAtaque(Personaje enemigo) {
		int dado = (int) (Math.random() * 91);

		setUltimaTiradaDados("🎲 (IA) -> " + getNombre() + ": " + dado);

		return dado;
	}

	/**
	 * Metodo usado para los exploradores y nazgul. Se encarga de revisar si ya
	 * tienen destino para patrullar y buscar a los hobbits
	 *
	 * @return true, if successful
	 */
	public boolean tienePatrullaDestinoAsignado() {
		return patrullaDestinoX >= 0 && patrullaDestinoY >= 0;
	}

	/**
	 * Gets the patrulla destino X.
	 *
	 * @return the patrulla destino X
	 */
	public int getPatrullaDestinoX() {
		return patrullaDestinoX;
	}

	/**
	 * Gets the patrulla destino Y.
	 *
	 * @return the patrulla destino Y
	 */
	public int getPatrullaDestinoY() {
		return patrullaDestinoY;
	}

	/**
	 * Sets the patrulla destino.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void setPatrullaDestino(int x, int y) {
		this.patrullaDestinoX = x;
		this.patrullaDestinoY = y;
	}

}
