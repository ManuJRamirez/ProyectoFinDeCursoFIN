package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.trasgos.Trasgo;

// TODO: Auto-generated Javadoc
/**
 * The Class Mapa. Representa el mapa lógico del juego.
 *
 * El mapa está compuesto por:
 * <ul>
 * <li>- Una matriz de casillas {@link Casilla}.</li>
 * <li>- Un conjunto de ciudades ubicadas dentro del mapa.</li>
 * <li>- Información sobre el tamaño (ancho y alto).</li>
 * </ul>
 * Esta clase gestiona:
 * <ul>
 * <li>- Validación de límites.</li>
 * <li>- Movimiento de personajes.</li>
 * <li>- Comprobación de terrenos transitables.</li>
 * <li>- Detección de ciudades en una casilla.</li>
 * </ul>
 */
public class Mapa {

	/** Ancho del mapa (en casillas). */
	private int ancho;

	/** Alto del mapa (en casilla). */
	private int alto;

	/** Matriz de casillas que forman el mapa. */
	private Casilla[][] panelGrid;

	/** Listado de ciudades del mapa. */
	private List<Ciudad> ciudades = new ArrayList<>();

	/**
	 * Constructor de clase en el que creamos un Mapa. Inicializamos las casillas al
	 * tipo de terreno LLANO. Sobreescribiremos cada casilla especifica con un JSON
	 * que defina su tipo de terreno especifico.
	 *
	 * @param ancho Casillas del eje X.
	 * @param alto  Casillas del eje Y.
	 */
	public Mapa(int ancho, int alto) {
		this.ancho = ancho;
		this.alto = alto;
		this.panelGrid = new Casilla[ancho][alto];

		for (int x = 0; x < ancho; x++) {
			for (int y = 0; y < alto; y++) {
				panelGrid[x][y] = new Casilla(TipoTerreno.LLANO);
			}
		}
	}

	/**
	 * Añade una ciudad al mapa.
	 *
	 * @param ciudad Ciudad para añadir.
	 */
	public void addCiudad(Ciudad ciudad) {
		ciudades.add(ciudad);
	}

	/**
	 * Busca si una casilla pertenece a una ciudad.
	 *
	 * @param x Coordenada X.
	 * @param y Coordenada Y.
	 * @return Una Ciudad si la casilla corresponde a ella, o null si no pertenece a
	 *         ninguna.
	 */
	public Ciudad buscarCiudadesPorCasilla(int x, int y) {
		for (Ciudad ciudad : ciudades) {
			for (Point p : ciudad.getCasillasDeCiudad()) {
				if (p.x == x && p.y == y) {
					return ciudad;
				}
			}
		}
		return null;
	}

	/**
	 * Busca una ciudad a partir de una casilla, ya sea del nucleo urbano o de su
	 * territorio. Se usa principalmente para mostrar informacion en el HUD, al ser
	 * clickada una casilla.
	 *
	 * @param x Coordenada X de la casilla clickada.
	 * @param y Coordenada Y de la casilla clickada.
	 * @return devuelve la {@link Ciudad} correspondiente si la casilla pertenece a
	 *         su area urbana o a su territorio. Null si no corresponde a nadie.
	 */
	public Ciudad buscarCiudadPorCualquierCasillaHUD(int x, int y) {
		Point casillaClickada = new Point(x, y);
		for (Ciudad ciudad : ciudades) {
			if (ciudad.getCasillasDeCiudad().contains(casillaClickada)) {
				return ciudad;
			}
			if (ciudad.getCasillasFormanTerritorio() != null
					&& ciudad.getCasillasFormanTerritorio().contains(casillaClickada)) {
				return ciudad;
			}
		}

		return null;
	}

	/**
	 * Método que usaremos si queremos cambiar el terreno de una casilla.
	 *
	 * @param x       Coordenada X de la casilla.
	 * @param y       Coordenada Y de la casilla.
	 * @param terreno Tipo de terreno que le queremos reasignar.
	 */
	public void setTerreno(int x, int y, TipoTerreno terreno) {
		panelGrid[x][y] = new Casilla(terreno);
	}

	/**
	 * Método para saber si una casilla esta dentro del mapa.
	 *
	 * @param x Coordenada X de la casilla.
	 * @param y Coordenada Y de la casilla.
	 * @return true, Si está dentro del mapa.
	 */
	public boolean estaDentroDelMapa(int x, int y) {
		return x >= 0 && x < ancho && y >= 0 && y < alto;
	}

	/**
	 * Método que determina si una casilla es transitable o no por un personaje.
	 *
	 * @param pj Personaje que quiere pasar por esa casilla.
	 * @param x  Coordenada X de la casilla.
	 * @param y  Coordenada Y de la casilla.
	 * @return true, Si es transitable para ese personaje.
	 */
	public boolean casillaTransitable(Personaje pj, int x, int y) {
		if (!estaDentroDelMapa(x, y)) {
			return false;
		}
		return puedeAtravesarTerreno(pj, panelGrid[x][y].getTerreno());
	}

	/**
	 * Metodo con el cual verificamos si una unidad puede pasar a traves del terreno
	 * indicado. Se basara en funcion a sus limitaciones de raza y tipo de combate,
	 * marcado en el archivo "bases de ataque y movimiento de personajes.txt"
	 *
	 * @param pj      Personaje que vamos a evaluar
	 * @param terreno tipo de terreno.
	 * @return true, si puede atravesar ese terreno.
	 */
	public boolean puedeAtravesarTerreno(Personaje pj, TipoTerreno terreno) {

		if (pj instanceof Nazgul) {
			return true;
		}

		if (terreno == TipoTerreno.LAVA) {
			return false;
		}

		if (terreno == TipoTerreno.RIO) {
			return pj instanceof Hobbit;
		}
		if (terreno == TipoTerreno.MONTANIA) {
			if (pj instanceof Hobbit) {
				return true;
			}
			if (pj instanceof Trasgo && "EXPLORADOR".equalsIgnoreCase(pj.getTipoCombate())) {
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * Metodo por el cual podemos reducir el movimiento de los personajes en funcion
	 * del tipo del terreno. Por ejemplo los hobbits o los exploradores, tienen un
	 * movimiento mas amplio en llano, pero mas limitado por montaña.
	 *
	 * @param pj      the pj
	 * @param terreno the terreno
	 * @return el costo de ese movimiento.
	 */
	public int costePaso(Personaje pj, TipoTerreno terreno) {
		if (pj instanceof Nazgul) {
			return 1;
		}
		if (pj instanceof Hobbit) {
			if (terreno == TipoTerreno.MONTANIA) {
				return 3;
			}
			if (terreno == TipoTerreno.RIO) {
				return 4;
			}
			return 1;
		}
		if (pj instanceof Trasgo && "EXPLORADOR".equalsIgnoreCase(pj.getTipoCombate())) {
			if (terreno == TipoTerreno.MONTANIA) {
				return 4;
			}
			return 1;
		}
		return 1;
	}

	/**
	 * Metodo por el cual siguiendo una linea recta entre el origen y el destino,
	 * comprobando si un personaje puede llegar hasta la casilla destino sin cruzar
	 * ningun terreno intransitable y sin superar su capacidad limite de movimiento.
	 * 
	 * Algoritmo de Bresenham.
	 *
	 * @param pj       Personaje
	 * @param origenX  the origen X
	 * @param origenY  the origen Y
	 * @param destinoX the destino X
	 * @param destinoY the destino Y
	 * @return true, si puede realizar el movimiento.
	 */
	public boolean caminoValido(Personaje pj, int origenX, int origenY, int destinoX, int destinoY) {
		int distX = Math.abs(destinoX - origenX);
		int distY = Math.abs(destinoY - origenY);
		int sx = origenX < destinoX ? 1 : -1;
		int sy = origenY < destinoY ? 1 : -1;
		int err = distX - distY;

		int x = origenX;
		int y = origenY;
		int costeAcumulado = 0;

		while (x != destinoX || y != destinoY) {
			int e2 = 2 * err;
			if (e2 > -distY) {
				err -= distY;
				x += sx;
			}
			if (e2 < distX) {
				err += distX;
				y += sy;
			}
			if (!estaDentroDelMapa(x, y)) {
				return false;
			}
			TipoTerreno terreno = panelGrid[x][y].getTerreno();
			if (!puedeAtravesarTerreno(pj, terreno)) {
				return false;
			}
			costeAcumulado += costePaso(pj, terreno);
			if (costeAcumulado > pj.getMovimiento()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Metodo que calcula el conjunto de casillas a las que puede llegar un
	 * personaje. Respeta las restricciones de terreno que tiene un personaje y
	 * corta el avance si el terreno tiene mas coste de movimiento, como puede ser
	 * una montaña o rio.
	 *
	 * @param pj Personaje que se va a mover
	 * @return Conjunto de coordenadas que el personaje puede alcanzar en formato
	 *         (x,y);
	 */
	public Set<String> calcularCasillasAlcanzables(Personaje pj) {
		Set<String> alcanzables = new HashSet<>();

		int origenX = pj.getX();
		int origenY = pj.getY();

		Queue<int[]> cola = new LinkedList<>();
		Map<String, Integer> costoGastado = new HashMap<>();

		cola.add(new int[] { origenX, origenY });
		costoGastado.put(origenX + "," + origenY, 0);

		TipoTerreno terrenoOrigen = panelGrid[origenX][origenY].getTerreno();
		boolean origenEsRioOMontania = terrenoOrigen == TipoTerreno.RIO || terrenoOrigen == TipoTerreno.MONTANIA;

		while (!cola.isEmpty()) {
			int[] actual = cola.poll();
			int actualX = actual[0];
			int actualY = actual[1];
			int costeActual = costoGastado.get(actualX + "," + actualY);

			if (costeActual >= pj.getMovimiento()) {
				continue;
			}

			TipoTerreno terrenoActual = panelGrid[actualX][actualY].getTerreno();
			boolean actualEsCasillaDeSalida = origenEsRioOMontania && terrenoActual != terrenoOrigen
					&& !(actualX == origenX && actualY == origenY);
			if (actualEsCasillaDeSalida) {
				continue;
			}

			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					if (dx == 0 && dy == 0) {
						continue;
					}

					int vecinoX = actualX + dx;
					int vecinoY = actualY + dy;
					String claveVecino = vecinoX + "," + vecinoY;

					if (!estaDentroDelMapa(vecinoX, vecinoY)) {
						continue;
					}

					Casilla casillaVecina = getCasilla(vecinoX, vecinoY);
					TipoTerreno terrenoVecino = casillaVecina.getTerreno();

					if (!puedeAtravesarTerreno(pj, terrenoVecino)) {
						continue;
					}

					boolean ocupable = casillaVecina.estaVacia() || (vecinoX == origenX && vecinoY == origenY);
					if (!ocupable) {
						continue;
					}

					int costeDeEstePaso = Math.max(costePaso(pj, terrenoActual), costePaso(pj, terrenoVecino));
					int nuevoCoste = costeActual + costeDeEstePaso;

					if (nuevoCoste <= pj.getMovimiento()
							&& (!costoGastado.containsKey(claveVecino) || nuevoCoste < costoGastado.get(claveVecino))) {
						costoGastado.put(claveVecino, nuevoCoste);
						alcanzables.add(claveVecino);
						cola.add(new int[] { vecinoX, vecinoY });
					}
				}

			}
		}
		return alcanzables;
	}

	/**
	 * Metodo que intenta mover un personaje en el mapa.
	 * 
	 * Gestiona:
	 * <ul>
	 * <li>- Validación de límites.</li>
	 * <li>- Entrada en ciudades.</li>
	 * <li>- Comprobación de terreno transitable.</li>
	 * <li>- Comprobación de casilla ocupada.</li>
	 * </ul>
	 *
	 * @param pj       personaje a mover.
	 * @param destinoX the destino X
	 * @param destinoY the destino Y
	 * @return true si el movimiento fue exitoso.
	 */
	public boolean moverUnidad(Personaje pj, int destinoX, int destinoY) {
		if (pj.HaMovidoEsteTurno() || pj.HaAtacadoEsteTurno()) {
			return false;
		}

		Set<String> alcanzables = calcularCasillasAlcanzables(pj);
		if (!alcanzables.contains(destinoX + "," + destinoY)) {
			return false;
		}
		boolean exito = reposicionarFisicamente(pj, destinoX, destinoY);
		if (exito) {
			pj.setHaMovidoEsteTurno(true);
		}

		return exito;
	}

	/**
	 * Permite a un nazgul realizar un movimiento adicional de hasta 2 casillas
	 * despues de haber atacado en su turno
	 *
	 * @param pj       Personaje que intenta realizar el movimiento extra. Tiene que
	 *                 ser Nazgul.
	 * @param destinoX coordenada destino X
	 * @param destinoY coordenada destino Y
	 * @return true, if successful
	 */
	public boolean moverUnidadPostAtaque(Personaje pj, int destinoX, int destinoY) {
		if (!(pj instanceof Nazgul)) {
			return false;
		}
		if (!pj.HaAtacadoEsteTurno()) {
			return false;
		}
		if (pj.HaUsadoMovimientoExtra()) {
			return false;
		}

		int distanciaX = Math.abs(pj.getX() - destinoX);
		int distanciaY = Math.abs(pj.getY() - destinoY);
		if (Math.max(distanciaX, distanciaY) > 2) {
			return false;
		}

		if (!caminoValido(pj, pj.getX(), pj.getY(), destinoX, destinoY)) {
			return false;
		}

		boolean exito = reposicionarFisicamente(pj, destinoX, destinoY);

		if (exito) {
			pj.setHaUsadoMovimientoExtra(true);
		}
		return exito;
	}

	/**
	 * Metodo que se encarga de realizar el movimiento "fisico" del personaje en el
	 * mapa. Valida que la casilla destino esté libre, actualiza las listas de
	 * guarnicion, actualiza la posicion nueva del personaje y asigna una unidad
	 * rezagada a la casilla origen en el caso de que haya varias unidades en la
	 * misma casilla como ocurre al empezar la partida.
	 *
	 * @param pj       personje
	 * @param destinoX the destino X
	 * @param destinoY the destino Y
	 * @return true, si el reposicionamiento se hizo con exito.
	 */
	private boolean reposicionarFisicamente(Personaje pj, int destinoX, int destinoY) {
		Ciudad ciudadDestino = buscarCiudadesPorCasilla(destinoX, destinoY);
		Ciudad ciudadOrigen = buscarCiudadesPorCasilla(pj.getX(), pj.getY());

		if (ciudadDestino == null) {
			Casilla casillaDestino = getCasilla(destinoX, destinoY);
			if (!casillaDestino.estaVacia()) {
				return false;
			}
		} else {
			List<Personaje> unidadesEnCasilla = ciudadDestino.getUnidadesDentro(destinoX, destinoY);
			for (Personaje otroPj : unidadesEnCasilla) {
				if (otroPj.getFaccion() != pj.getFaccion()) {
					return false;
				}
			}
		}

		int origenX = pj.getX();
		int origenY = pj.getY();
		Casilla casillaOrigen = getCasilla(origenX, origenY);
		Casilla casillaDestino = getCasilla(destinoX, destinoY);

		if (casillaOrigen != null) {
			casillaOrigen.personajeSaleCasilla();
		}
		if (origenX != destinoX || origenY != destinoY) {
			if (ciudadOrigen != null) {
				ciudadOrigen.salirCiudad(pj);
			}
			if (ciudadDestino != null) {
				ciudadDestino.addUnidad(pj);
			}
		}
		pj.setPosicion(destinoX, destinoY);
		if (casillaDestino != null) {
			casillaDestino.setPersonaje(pj);
		}

		if (casillaOrigen != null && ciudadOrigen != null) {
			Personaje rezagado = null;

			for (Personaje unidad : ciudadOrigen.getUnidadesDentro()) {
				if (unidad.getX() == origenX && unidad.getY() == origenY) {
					rezagado = unidad;
					break;
				}
			}
			if (rezagado != null) {
				casillaOrigen.setPersonaje(rezagado);
			}
		}

		return true;
	}

	/**
	 * Metodo encargado de gestionar la eliminacion de una unidad. Vacia la casila
	 * del mapa y si estaba dentro de una ciudad, la elimina tambien de la
	 * guarnicion. Así mismo coloca en su posicion una unidad rezagada en el caso en
	 * el que hubiera varias unidades en la misma casilla como ocurre al empezar la
	 * partida.
	 *
	 * @param unidadEliminada the unidad eliminada
	 * @return the ciudad donde se ha producido el combate. Si es fuera de ciudad
	 *         sera Null y si es dentro, permitira actualizar el HUD de la
	 *         guarnicion de la ciudad
	 */
	public Ciudad procesarMuerteEnCombate(Personaje unidadEliminada) {
		if (unidadEliminada == null) {
			return null;
		}
		int muertoX = unidadEliminada.getX();
		int muertoY = unidadEliminada.getY();
		Casilla casillaCombate = getCasilla(muertoX, muertoY);

		if (casillaCombate != null) {
			casillaCombate.personajeSaleCasilla();
		}

		Ciudad ciudadDelCombate = buscarCiudadesPorCasilla(muertoX, muertoY);

		if (ciudadDelCombate != null) {
			ciudadDelCombate.salirCiudad(unidadEliminada);
			Personaje rezagado = null;

			for (Personaje pj : ciudadDelCombate.getUnidadesDentro()) {
				if (pj.getX() == muertoX && pj.getY() == muertoY && pj.estaVivo()) {
					rezagado = pj;
					break;
				}
			}
			if (rezagado != null && casillaCombate != null) {
				casillaCombate.setPersonaje(rezagado);
			}
		}
		return ciudadDelCombate;
	}

	/**
	 * Devuelve una lista con todas las unidades presentes en el mapa, esten vivas o
	 * muertas.
	 *
	 * @return the todas las unidades
	 */
	public List<Personaje> getTodasLasUnidades() {
		List<Personaje> lista = new ArrayList<>();
		Set<Personaje> yaAnadidos = new HashSet<>();

		for (Ciudad ciudad : ciudades) {
			for (Personaje pj : ciudad.getUnidadesDentro()) {
				if (yaAnadidos.add(pj)) {
					lista.add(pj);
				}
			}
		}

		for (int x = 0; x < ancho; x++) {
			for (int y = 0; y < alto; y++) {
				Casilla casilla = panelGrid[x][y];
				if (casilla.getPersonaje() != null) {
					Personaje pj = casilla.getPersonaje();
					if (buscarCiudadesPorCasilla(x, y) == null) {
						if (yaAnadidos.add(pj)) {
							lista.add(pj);
						}
					}
				}
			}
		}
		return lista;
	}

	/**
	 * Coloca una unidad (Personaje) en la casilla del mapa indicada.
	 * 
	 * Con este metodo controlamos que si la casilla donde estamos colocando el
	 * personaje es una casilla de ciudad, puedes tener varios personajes en la
	 * misma casilla, pero en cambio si la casilla es de fuera de la ciudad, no
	 * podrás tener más de un personaje por casilla.
	 * 
	 * Esto se usa principalmente para colocar las unidades al arrancar la partida.
	 * Luego ya no se usará ya que cada unidad va a una casilla distinta.
	 * 
	 *
	 * @param x  Coordenada X de la casilla.
	 * @param y  Coordenada Y de la casilla.
	 * @param pj Personaje que vamos a colocar.
	 */
	public void colocarUnidad(int x, int y, Personaje pj) {
		if (!estaDentroDelMapa(x, y)) {
			return;
		}

		Casilla casilla = panelGrid[x][y];
		Ciudad ciudad = buscarCiudadesPorCasilla(x, y);

		if (ciudad != null) {
			if (!ciudad.getUnidadesDentroCiudad().contains(pj)) {
				ciudad.getUnidadesDentroCiudad().add(pj);
			}
		} else {
			if (!casilla.estaVacia()) {
				System.err.println("La casilla (" + x + "," + y + ") ya está ocupada");
				return;
			}
		}

		casilla.setPersonaje(pj);
		pj.setPosicion(x, y);
	}

	/**
	 * Metodo que se encarga de actualizar el estado de invisibilidad de los
	 * Hobbits. Pasaran a estado detectado de estan en rango de deteccion de los
	 * Nazgul y/o Exploradores. Tambien pasara a estado detectado si el Hobbit
	 * realiza un ataque o si entra a la ciudad Monte del Destino.
	 *
	 * @return una Lista con los cambios de estado en la invisibilidad de los
	 *         hobbits.
	 */
	public List<Personaje> actualizarDeteccionHobbits() {
		List<Personaje> hobbits = new ArrayList<>();
		List<Personaje> detectores = new ArrayList<>();

		for (Personaje pj : getTodasLasUnidades()) {
			if (pj instanceof Hobbit && pj.estaVivo()) {
				hobbits.add(pj);
			}
			if (pj.getFaccion() == Faccion.BESTIAS && pj.estaVivo()) {
				boolean esExplorador = pj instanceof Trasgo && "EXPLORADOR".equalsIgnoreCase(pj.getTipoCombate());
				boolean esNazgul = pj instanceof Nazgul;

				if (esExplorador || esNazgul) {
					detectores.add(pj);
				}
			}
		}

		List<Personaje> hobbitsConCambio = new ArrayList<>();
		for (Personaje hobbit : hobbits) {
			if (estaEnMonteDelDestino(hobbit)) {
				hobbit.setDetectadoPorEnemigo(true);
				hobbit.setTurnosInmovil(0);
				continue;
			}
			boolean detectadoAhora = false;

			for (Personaje detector : detectores) {
				int rangoDeteccion = calcularRangoDeteccion(detector);
				int distX = Math.abs(hobbit.getX() - detector.getX());
				int distY = Math.abs(hobbit.getY() - detector.getY());
				int distancia = Math.max(distX, distY);

				if (distancia <= rangoDeteccion) {
					detectadoAhora = true;
					break;
				}
			}

			if (detectadoAhora && !hobbit.esDetectadoPorEnemigo()) {
				hobbit.setDetectadoPorEnemigo(true);
				hobbit.setTurnosInmovil(0);
				hobbitsConCambio.add(hobbit);

			} else if (!detectadoAhora && hobbit.esDetectadoPorEnemigo()) {
				if (hobbit.getTurnosInmovil() >= 2) {
					hobbit.setDetectadoPorEnemigo(false);
					hobbitsConCambio.add(hobbit);
				}
			}
		}

		return hobbitsConCambio;
	}

	/**
	 * Metodo usado para calcular el rango de deteccion de las unidades nazguls y
	 * exploradores que pueden encontrar a los hobbits. Reducirá el campo de vision
	 * si se intenta detectar en terrenos de tipo montaña. Este metodo se usara para
	 * saber el rango de deteccion en una casilla en la que la unidad no esta ahora
	 * mismo. Este método evalua el rango de detección en la casilla en la que se
	 * encuentra actualmente la unidad rastreadora.
	 *
	 * @param detector the detector
	 * @return the int
	 */
	public int calcularRangoDeteccion(Personaje detector) {
		TipoTerreno terreno = panelGrid[detector.getX()][detector.getY()].getTerreno();
		boolean enMontania = terreno == TipoTerreno.MONTANIA;

		if (detector instanceof Nazgul) {
			return enMontania ? 1 : 3;
		}
		if (detector instanceof Trasgo && "EXPLORADOR".equalsIgnoreCase(detector.getTipoCombate())) {
			return enMontania ? 1 : 2;
		}

		return 0;
	}

	/**
	 * Metodo sobrecargado usado para calcular el rango de deteccion de las unidades
	 * nazguls y exploradores que pueden encontrar a los hobbits. Reducirá el campo
	 * de vision si se intenta detectar en terrenos de tipo montaña. Este metodo se
	 * usara para saber el rango de deteccion en una casilla en la que la unidad no
	 * esta ahora mismo. Es la principal diferencia con el metodo de un solo
	 * párametro.
	 *
	 * @param detector   Personaje que esta rastreando a los hobbits
	 * @param enMontania parametro que comprueba si el terreno es de tipo montaña.
	 * @return el valor de deteccion del rastreador.
	 */
	public int calcularRangoDeteccion(Personaje detector, boolean enMontania) {
		if (detector instanceof Nazgul) {
			return enMontania ? 1 : 3;
		}
		if (detector instanceof Trasgo && "EXPLORADOR".equalsIgnoreCase(detector.getTipoCombate())) {
			return enMontania ? 1 : 2;
		}

		return 0;
	}

	/**
	 * Metodo por el cual actualizamos los turnos consecutivos que un hobbit lleva
	 * sin moverse. Este metodo es importante a la hora de recuperar la
	 * invisibilidad de un hobbit ya que necesitara estar hasta 2 turnos sin moverse
	 * despues de ser detectado.
	 */
	public void actualizarTurnosSinMovimientoHobbits() {
		for (Personaje pj : getTodasLasUnidades()) {
			if (pj instanceof Hobbit && pj.estaVivo()) {
				boolean seMovio = pj.getX() != pj.getxAnterior() || pj.getY() != pj.getyAnterior();

				if (seMovio) {
					pj.setTurnosInmovil(0);
				} else {
					pj.setTurnosInmovil(pj.getTurnosInmovil() + 1);
				}
				pj.setxAnterior(pj.getX());
				pj.setyAnterior(pj.getY());
			}
		}
	}

	/**
	 * Métdo usado para comprobar directamente si el personaje se encuentra dentro
	 * de la ciudad "Monte del Destino".
	 *
	 * @param pj personaje que evaluamos
	 * @return true, si se encuentra dentro.
	 */
	private boolean estaEnMonteDelDestino(Personaje pj) {
		Ciudad ciudad = buscarCiudadesPorCasilla(pj.getX(), pj.getY());
		return ciudad != null && "Monte del Destino".equalsIgnoreCase(ciudad.getNombreCiudad());
	}

	/**
	 * Comprobamos si la casilla que le pasamos esta dentro del territorio
	 * controlado por las Bestias.
	 *
	 * @param x Coordenada X
	 * @param y Coordenada Y
	 * @return true, Si esta la casilla corresponde a un territorio controlado por
	 *         las Bestias.
	 */
	public boolean esTerritorioBestia(int x, int y) {
		for (Ciudad c : ciudades) {
			if (c.getControlSobreCiudad() == ControlCiudad.BESTIAS) {
				if (c.getCasillasFormanTerritorio().contains(new Point(x, y))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Comprobamos si la casilla que le pasamos está dentro del territorio
	 * controlado por los Heroes.
	 *
	 * @param x Coordenada X.
	 * @param y Coordenada Y.
	 * @return true, Si la casilla corresponde a un territorio controlado por los
	 *         Heroes.
	 */
	public boolean esTerritorioHeroe(int x, int y) {
		for (Ciudad c : ciudades) {
			if (c.getControlSobreCiudad() == ControlCiudad.HEROES) {
				if (c.getCasillasFormanTerritorio().contains(new Point(x, y))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Metodo para actualizar el proceso de conquista de las ciudades y devuelve aquellas que han sido conquistada en esta actualizacion.
	 *
	 * @return Listado de ciudades que han cambiado de faccion desde la ultima actualizacion, al usar este metodo.
	 */
	public List<Ciudad> actualizarConquistas() {
		List<Ciudad> ciudadesConquistadasEsteTurno = new ArrayList<>();
		for (Ciudad ciudad : ciudades) {
			Faccion faccionConquistadora = ciudad.actualizarProgresoDeConquista();
			if (faccionConquistadora != null) {
				ciudadesConquistadasEsteTurno.add(ciudad);
			}
		}
		return ciudadesConquistadasEsteTurno;
	}

	/**
	 * Devuelve la casilla de la posición indicada.
	 *
	 * @param x Coordenada X de la casilla.
	 * @param y Coordenada Y de la casilla.
	 * @return La Casilla o null si esta fuera del mapa.
	 */
	public Casilla getCasilla(int x, int y) {
		if (!estaDentroDelMapa(x, y)) {
			return null;
		}
		return panelGrid[x][y];
	}

	/**
	 * Gets the ancho.
	 *
	 * @return the ancho
	 */
	public int getAncho() {
		return ancho;
	}

	/**
	 * Sets the ancho.
	 *
	 * @param ancho the new ancho
	 */
	public void setAncho(int ancho) {
		this.ancho = ancho;
	}

	/**
	 * Gets the alto.
	 *
	 * @return the alto
	 */
	public int getAlto() {
		return alto;
	}

	/**
	 * Sets the alto.
	 *
	 * @param alto the new alto
	 */
	public void setAlto(int alto) {
		this.alto = alto;
	}

	/**
	 * Gets the panel grid.
	 *
	 * @return the panel grid
	 */
	public Casilla[][] getPanelGrid() {
		return panelGrid;
	}

	/**
	 * Sets the panel grid.
	 *
	 * @param panelGrid the new panel grid
	 */
	public void setPanelGrid(Casilla[][] panelGrid) {
		this.panelGrid = panelGrid;
	}

	/**
	 * Gets the ciudades.
	 *
	 * @return the ciudades
	 */
	public List<Ciudad> getCiudades() {
		return ciudades;
	}

	/**
	 * Sets the ciudades.
	 *
	 * @param ciudades the new ciudades
	 */
	public void setCiudades(List<Ciudad> ciudades) {
		this.ciudades = ciudades;
	}

}
