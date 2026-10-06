package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;

// TODO: Auto-generated Javadoc
/**
 * The Class Personaje.
 *
 * Clase base para todos los personajes del juego.
 *
 * Contiene atributos comunes como:
 * <ul>
 * <li>- Nombre.</li>
 * <li>- Vida.</li>
 * <li>- Armadura.</li>
 * <li>- Rango de ataque.</li>
 * <li>- Posición en el mapa.</li>
 * </ul>
 */
public abstract class Personaje {

	/** Nombre Personaje. */
	private String nombre;

	/** Vida. */
	private int vida;

	/** The vida maxima. */
	private int vidaMaxima;

	/** Armadura . */
	private int armadura;

	/** Rango ataque. */
	private int rangoAtaque;

	/** The movimiento. */
	private int movimiento;

	/** The faccion. */
	private Faccion faccion;

	/** The categoria. */
	private String categoria;

	/** The raza. */
	private String raza;

	/** The tipo combate. */
	private String tipoCombate;

	/**
	 * Numero de turnos que tarda en crear la unidad, en caso que sea de la
	 * categoria "Tropa".
	 */
	private int turnosCreacion = 0;

	/** The ha movido este turno. */
	private boolean haMovidoEsteTurno = false;

	/** The ha atacado este turno. */
	private boolean haAtacadoEsteTurno = false;

	/** Movimiento extra para las unidades voladoras. */
	private boolean haUsadoMovimientoExtra = false;

	/** Indica si el personaje ha sido atacado en esta ronda. */
	private boolean hasidoAtacadoEstaRonda = false;

	/** The detectado por enemigo. */
	private boolean detectadoPorEnemigo = false;

	/** The turnos inmovil para la invisibilidad de los Hobbits. */
	private int turnosInmovil = 0;

	/** Queda registrado la tirada de dados, para poder mostrarlo en el log. */
	private String ultimaTiradaDados;

	/**
	 * Atributo que va a guardar la posicion X anterior para saber si un pj se ha
	 * movido de la casilla. Se usara para los Hobbits y su invisibilidad.
	 */
	private int xAnterior = -1;

	/**
	 * Atributo que va a guardar la posicion X anterior para saber si un pj se ha
	 * movido de la casilla. Se usara para los Hobbits y su invisibilidad.
	 */
	private int yAnterior = -1;

	/** Posicion X en el mapa. */
	private int x;

	/** Posicion Y en el mapa. */
	private int y;

	/**
	 * Constructor de clase.
	 *
	 * @param nombre      the nombre
	 * @param vida        the vida
	 * @param armadura    the armadura
	 * @param rangoAtaque the rango ataque
	 * @param movimiento  the movimiento
	 * @param faccion     the faccion
	 */
	public Personaje(String nombre, int vida, int armadura, int rangoAtaque, int movimiento, Faccion faccion) {
		this.nombre = nombre;
		this.vida = vida;
		this.vidaMaxima = vida;
		this.armadura = armadura;
		this.rangoAtaque = rangoAtaque;
		this.movimiento = movimiento;
		this.faccion = faccion;
	}

	/**
	 * Método abstracto para calcular daño de ataque base.
	 *
	 * @param enemigo Personaje enemigo al que se ataca
	 * @return int
	 */
	public abstract int calcularAtaque(Personaje enemigo);

	/**
	 * Multiplicador de ataque que aplica ESTA unidad (la atacante) en función del
	 * enemigo al que se enfrenta y del contexto del mapa (terreno, si está atacando
	 * desde una ciudad, etc.). Por defecto no hay bonificación (1.0).
	 *
	 * @param enemigo Personaje al que se ataca.
	 * @param mapa    Mapa del juego necesario para consultar terrenos y ciudades.
	 * @return multiplicador de ataque. Por defecto 1.0.
	 */
	public double getMultiplicadorAtaque(Personaje enemigo, Mapa mapa) {
		return 1.0;
	}

	/**
	 * Modificador que aplica esta unidad sobre la armadura DEL ENEMIGO cuando ataca
	 * (ej: los Orcos reducen la armadura enemiga un 10% en cada ataque). Por
	 * defecto no hay reducción (1.0 = armadura intacta).
	 *
	 * @return Multiplicador a aplicar sobre la armadura del enemigo.
	 */
	public double getModificadorArmaduraEnemigo() {
		return 1.0;
	}

	/**
	 * Modificador de defensa que aplica a esta unidad (la defensora) cuando recibe
	 * un ataque, en función de quién la ataca y el contexto del mapa (ej: bonus
	 * defendiendo su propia ciudad, o el Nazgul resistiendo más a quien no sea
	 * Arquero/Mago). Por defecto no hay bonificación (1.0 = daño completo).
	 *
	 * @param atacante Personaje que realiza el ataque.
	 * @param mapa     Mapa lógico, necesario para consultar terreno/ciudades.
	 * @return Multiplicador a aplicar sobre el daño final recibido (valores menores
	 *         a 1.0 reducen el daño recibido).
	 */
	public double getModificadorDefensa(Personaje atacante, Mapa mapa) {
		return 1.0;
	}

	/**
	 * Calcula el daño final que este personaje infligiría al enemigo indicado,
	 * combinado el ataque base, el multiplicador de ataque propio, la armadura del
	 * enemigo y el modificador de defensa que aplica el enemigo.
	 *
	 * @param enemigo the enemigo
	 * @param mapa    the mapa
	 * @return the int
	 */
	public int calcularDanioFinal(Personaje enemigo, Mapa mapa) {
		int danioBase = calcularAtaque(enemigo);

		double multiplicadorAtaque = getMultiplicadorAtaque(enemigo, mapa);
		double danioConBonus = danioBase * multiplicadorAtaque;

		double hayPenetracionDeArmadura = enemigo.getArmadura() * getModificadorArmaduraEnemigo();

		double modDefensaEnemigo = enemigo.getModificadorDefensa(this, mapa);
		double armaduraEfectivaFinal = hayPenetracionDeArmadura * modDefensaEnemigo;

		double danioTrasArmadura = danioConBonus - armaduraEfectivaFinal;

		int resultado = (int) Math.round(danioTrasArmadura);

		return Math.max(resultado, 0);

	}

	/**
	 * Determina si un otro personaje es aliado al actual.
	 *
	 * @param pj the pj
	 * @return true, if successful
	 */
	public boolean esAliado(Personaje pj) {
		return (this instanceof Heroe && pj instanceof Heroe) || (this instanceof Bestia && pj instanceof Bestia);
	}

	/**
	 * Aplica el daño al personaje que lo recibe.
	 *
	 * @param cantidadDanio Se le pasa la cantidad de daño que recibe.
	 */
	public void recibirDanio(int cantidadDanio) {
		this.vida -= cantidadDanio;
		if (this.vida < 0) {
			this.vida = 0;
		}
		this.hasidoAtacadoEstaRonda = true;
	}
	
	/**
	 * Metodo para saber si una unidad esta atacando desde dentro de una ciudad
	 * hacia un enemigo que esta fuera de la ciudad. Esto se usa para aplicar el
	 * bonus de muralla en arqueros y magos.
	 *
	 * @param enemigo the enemigo
	 * @param mapa the mapa
	 * @return true, if successful
	 */
	protected boolean estaProtegidoPorMuralla(Personaje enemigo, Mapa mapa) {
		if(mapa == null || enemigo == null) {
			return false;
		}
		Ciudad ciudadDelPersonajeAliado = mapa.buscarCiudadesPorCasilla(getX(), getY());
		if (ciudadDelPersonajeAliado == null) {
			return false;
		}
		Ciudad estaElEnemigoEnUnaCiudad = mapa.buscarCiudadesPorCasilla(enemigo.getX(), enemigo.getY());
 
		return estaElEnemigoEnUnaCiudad == null;
	}

	/**
	 * Comprueba si este personaje puede atacar al objetivo indicado. Este método es
	 * importante sobretodo si una unidad de caballeria se encuentra fuera de una
	 * ciudad y quiere atacar a una unidad dentro de ciudad.
	 *
	 * @param enemigo the enemigo
	 * @param mapa    the mapa
	 * @return true, if successful
	 */
	public boolean puedeAtacarObjetivo(Personaje enemigo, Mapa mapa) {
		if (enemigo == null || mapa == null) {
			return true;
		}
		if (!"CABALLERIA".equalsIgnoreCase(tipoCombate)) {
			return true;
		}
		Ciudad ciudadDelEnemigo = mapa.buscarCiudadesPorCasilla(enemigo.getX(), enemigo.getY());
		if (ciudadDelEnemigo == null) {
			return true;
		}
		Ciudad ciudadDelAtacante = mapa.buscarCiudadesPorCasilla(this.getX(), this.getY());

		return ciudadDelAtacante == ciudadDelEnemigo;
	}

	/**
	 * Metodo para obtener el tipo de combate de una unidad.
	 *
	 * @return el tipo de combate un personaje en mayúsculas, o cadena vacía si no
	 *         tiene asignado
	 */
	public String tipoCombate() {
		return getTipoCombate() == null ? "" : getTipoCombate().toUpperCase();
	}
	
	/**
	 * Comprueba si esta vivo.
	 *
	 * @return true, Si está vivo.
	 */
	public boolean estaVivo() {
		return vida > 0;
	}

	/**
	 * Checks if is ha movido este turno.
	 *
	 * @return true, if is ha movido este turno
	 */
	public boolean HaMovidoEsteTurno() {
		return haMovidoEsteTurno;
	}
	
	/**
	 * Sets the ha movido este turno.
	 *
	 * @param haMovidoEsteTurno the new ha movido este turno
	 */
	public void setHaMovidoEsteTurno(boolean haMovidoEsteTurno) {
		this.haMovidoEsteTurno = haMovidoEsteTurno;
	}
	

	/**
	 * Establece la posicion del personaje en el mapa.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void setPosicion(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the ultima tirada dados.
	 *
	 * @return the ultima tirada dados
	 */
	public String getUltimaTiradaDados() {
		return ultimaTiradaDados;
	}

	/**
	 * Sets the ultima tirada dados.
	 *
	 * @param ultimatiradaDados the new ultima tirada dados
	 */
	public void setUltimaTiradaDados(String ultimatiradaDados) {
		this.ultimaTiradaDados = ultimatiradaDados;
	}

	/**
	 * Gets the nombre.
	 *
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Sets the nombre.
	 *
	 * @param nombre the new nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Gets the vida maxima.
	 *
	 * @return the vida maxima
	 */
	public int getVidaMaxima() {
		return vidaMaxima;
	}

	/**
	 * Gets the vida.
	 *
	 * @return the vida
	 */
	public int getVida() {
		return vida;
	}

	/**
	 * Sets the vida.
	 *
	 * @param vida the new vida
	 */
	public void setVida(int vida) {
		this.vida = vida;
	}

	/**
	 * Gets the armadura.
	 *
	 * @return the armadura
	 */
	public int getArmadura() {
		return armadura;
	}

	/**
	 * Sets the armadura.
	 *
	 * @param armadura the new armadura
	 */
	public void setArmadura(int armadura) {
		this.armadura = armadura;
	}

	/**
	 * Gets the rango ataque.
	 *
	 * @return the rango ataque
	 */
	public int getRangoAtaque() {
		return rangoAtaque;
	}

	/**
	 * Sets the rango ataque.
	 *
	 * @param rangoAtaque the new rango ataque
	 */
	public void setRangoAtaque(int rangoAtaque) {
		this.rangoAtaque = rangoAtaque;
	}

	/**
	 * Gets the movimiento.
	 *
	 * @return the movimiento
	 */
	public int getMovimiento() {
		return movimiento;
	}

	/**
	 * Sets the movimiento.
	 *
	 * @param movimiento the new movimiento
	 */
	public void setMovimiento(int movimiento) {
		this.movimiento = movimiento;
	}

	/**
	 * Gets the faccion.
	 *
	 * @return the faccion
	 */
	public Faccion getFaccion() {
		return faccion;
	}

	/**
	 * Sets the faccion.
	 *
	 * @param faccion the new faccion
	 */
	public void setFaccion(Faccion faccion) {
		this.faccion = faccion;
	}

	/**
	 * Gets the categoria.
	 *
	 * @return the categoria
	 */
	public String getCategoria() {
		return categoria;
	}

	/**
	 * Sets the categoria.
	 *
	 * @param categoria the new categoria
	 */
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	/**
	 * Gets the raza.
	 *
	 * @return the raza
	 */
	public String getRaza() {
		return raza;
	}

	/**
	 * Sets the raza.
	 *
	 * @param raza the new raza
	 */
	public void setRaza(String raza) {
		this.raza = raza;
	}

	/**
	 * Gets the tipo combate.
	 *
	 * @return the tipo combate
	 */
	public String getTipoCombate() {
		return tipoCombate;
	}

	/**
	 * Sets the tipo combate.
	 *
	 * @param tipoCombate the new tipo combate
	 */
	public void setTipoCombate(String tipoCombate) {
		this.tipoCombate = tipoCombate;
	}

	/**
	 * Gets the turnos creacion.
	 *
	 * @return the turnos creacion
	 */
	public int getTurnosCreacion() {
		return turnosCreacion;
	}

	/**
	 * Sets the turnos creacion.
	 *
	 * @param turnosCreacion the new turnos creacion
	 */
	public void setTurnosCreacion(int turnosCreacion) {
		this.turnosCreacion = turnosCreacion;
	}


	/**
	 * Checks if is ha atacado este turno.
	 *
	 * @return true, if is ha atacado este turno
	 */
	public boolean HaAtacadoEsteTurno() {
		return haAtacadoEsteTurno;
	}

	/**
	 * Sets the ha atacado este turno.
	 *
	 * @param haAtacadoEsteTurno the new ha atacado este turno
	 */
	public void setHaAtacadoEsteTurno(boolean haAtacadoEsteTurno) {
		this.haAtacadoEsteTurno = haAtacadoEsteTurno;
	}

	/**
	 * Es detectado por enemigo.
	 *
	 * @return true, if successful
	 */
	public boolean esDetectadoPorEnemigo() {
		return detectadoPorEnemigo;
	}

	/**
	 * Checks if is hasido atacado esta ronda.
	 *
	 * @return true, if is hasido atacado esta ronda
	 */
	public boolean isHasidoAtacadoEstaRonda() {
		return hasidoAtacadoEstaRonda;
	}

	/**
	 * Sets the hasido atacado esta ronda. Ronda es un turno de Heroes y un turno de Bestias.
	 *
	 * @param hasidoAtacadoEstaRonda the new hasido atacado esta ronda
	 */
	public void setHasidoAtacadoEstaRonda(boolean hasidoAtacadoEstaRonda) {
		this.hasidoAtacadoEstaRonda = hasidoAtacadoEstaRonda;
	}

	/**
	 * Ha usado movimiento extra.
	 *
	 * @return true, if successful
	 */
	public boolean HaUsadoMovimientoExtra() {
		return haUsadoMovimientoExtra;
	}

	/**
	 * Sets the ha usado movimiento extra.
	 *
	 * @param haUsadoMovimientoExtra the new ha usado movimiento extra
	 */
	public void setHaUsadoMovimientoExtra(boolean haUsadoMovimientoExtra) {
		this.haUsadoMovimientoExtra = haUsadoMovimientoExtra;
	}

	/**
	 * Sets the detectado por enemigo.
	 *
	 * @param detectadoPorEnemigo the new detectado por enemigo
	 */
	public void setDetectadoPorEnemigo(boolean detectadoPorEnemigo) {
		this.detectadoPorEnemigo = detectadoPorEnemigo;
	}

	/**
	 * Gets the turnos inmovil.
	 *
	 * @return the turnos inmovil
	 */
	public int getTurnosInmovil() {
		return turnosInmovil;
	}

	/**
	 * Sets the turnos inmovil.
	 *
	 * @param turnosInmovil the new turnos inmovil
	 */
	public void setTurnosInmovil(int turnosInmovil) {
		this.turnosInmovil = turnosInmovil;
	}

	/**
	 * Gets the x anterior.
	 *
	 * @return the x anterior
	 */
	public int getxAnterior() {
		return xAnterior;
	}

	/**
	 * Sets the x anterior.
	 *
	 * @param xAnterior the new x anterior
	 */
	public void setxAnterior(int xAnterior) {
		this.xAnterior = xAnterior;
	}

	/**
	 * Gets the y anterior.
	 *
	 * @return the y anterior
	 */
	public int getyAnterior() {
		return yAnterior;
	}

	/**
	 * Sets the y anterior.
	 *
	 * @param yAnterior the new y anterior
	 */
	public void setyAnterior(int yAnterior) {
		this.yAnterior = yAnterior;
	}

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x.
	 *
	 * @param x the new x
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the y.
	 *
	 * @param y the new y
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return nombre + " (" + this.getClass().getSimpleName() + ")";
	}
}
