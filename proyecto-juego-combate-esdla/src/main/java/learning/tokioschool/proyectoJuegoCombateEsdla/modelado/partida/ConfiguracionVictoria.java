package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 * The Class ConfiguracionVictoria.
 * 
 * Esta clase se encarga de agrupar las condiciones de victorias activas, junto
 * con el numero máximo de turnos, tropas y puntos, en caso de que los haya.
 */
public class ConfiguracionVictoria {

	/** Numero maximo de turnos (0 si no esta activo). */
	private final int turnosMaximos;

	/** Numero maximo de puntos (0 si no esta activo). */
	private final int puntosMaximos;

	/** Numero maximo de tropas maximas que se pueden reclutar (0 si no esta activo). */
	private final int tropasMaximas;

	/** Conjunto de condiciones de victoria activas para la partida. */
	private final Set<TipoCondicionVictoria> condicionesActivas;

	/**
	 * Constructor de clase para una partida sin tropas.
	 *
	 * @param turnosMaximos      numero de turnos maximos
	 * @param puntosMaximos      numero de puntos maximos
	 * @param condicionesActivas condiciones de victoria activas
	 */
	public ConfiguracionVictoria(int turnosMaximos, int puntosMaximos,
			Collection<TipoCondicionVictoria> condicionesActivas) {
		this(turnosMaximos, puntosMaximos, 0, condicionesActivas);
	}

	/**
	 * Constructor de clase.
	 *
	 * @param turnosMaximos      numero de turnos maximos
	 * @param puntosMaximos      numero de puntos maximos
	 * @param tropasMaximas      numero de tropas maximas
	 * @param condicionesActivas condiciones de victoria activas
	 */
	public ConfiguracionVictoria(int turnosMaximos, int puntosMaximos, int tropasMaximas,
			Collection<TipoCondicionVictoria> condicionesActivas) {
		this.turnosMaximos = turnosMaximos;
		this.puntosMaximos = puntosMaximos;
		this.tropasMaximas = tropasMaximas;

		if (condicionesActivas == null || condicionesActivas.isEmpty()) {
			this.condicionesActivas = EnumSet.noneOf(TipoCondicionVictoria.class);
		} else {
			this.condicionesActivas = EnumSet.copyOf(condicionesActivas);
		}
	}

	/**
	 * Comprueba si una condicion de victoria especifica esta activa.
	 *
	 * @param condicion condicion de victoria a evaluar
	 * @return true, si esta activa
	 */
	public boolean estaActiva(TipoCondicionVictoria condicion) {
		return condicionesActivas.contains(condicion);
	}

	/**
	 * Gets the turnos maximos.
	 *
	 * @return the turnos maximos
	 */
	public int getTurnosMaximos() {
		return turnosMaximos;
	}

	/**
	 * Gets the puntos maximos.
	 *
	 * @return the puntos maximos
	 */
	public int getPuntosMaximos() {
		return puntosMaximos;
	}

	/**
	 * Gets the tropas maximas.
	 *
	 * @return the tropas maximas
	 */
	public int getTropasMaximas() {
		return tropasMaximas;
	}

	/**
	 * Gets the condiciones activas.
	 *
	 * @return the condiciones activas
	 */
	public Set<TipoCondicionVictoria> getCondicionesActivas() {
		return condicionesActivas;
	}

}
