package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.configuracionIA;

import java.util.List;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ConfiguracionVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.reclutarTropa.ReclutamientoControlador;

/**
 * The Interface EstrategiaIA.
 * 
 * Interfaz que usamos para definir las estrategias de la IA, es decir faccion
 * Bestias. Se compondra por tres niveles.
 */
public interface EstrategiaIA {

	/**
	 * Decidira que acción va a realizar una unidad Bestia. Atacar, moverse o
	 * esperar.
	 *
	 * @param bestia                Unidad sobre la que se decide la acción.
	 * @param mapa                  Mapa lógico del juego.
	 * @param configuracionVictoria Condiciones de victoria activas en la partida.
	 * @return Acción decidida.
	 */
	AccionIA decidirAccion(Personaje bestia, Mapa mapa, ConfiguracionVictoria configuracionVictoria);

	/**
	 * Metodo invicado al comenzar el turno de la IA.
	 */
	default void iniciarTurno() {

	}

	/**
	 * Metodo que va a decidir para cada una de las ciudades natales de las Bestias
	 * si inicia un reclutamiento de tropa y que tropa va a reclutar en el caso que
	 * tenga mas de una opcion en esa ciudad.
	 *
	 * @param faccion       Facción para la que se decide el reclutamiento.
	 * @param mapa          Mapa lógico del juego.
	 * @param rosterTropas  Lista de tropas disponibles para reclutar.
	 * @param reclutamiento Controlador de reclutamiento a utilizar.
	 * @param tropasMaximas Límite máximo de tropas vivas por facción.
	 */
	default void decidirReclutamiento(Faccion faccion, Mapa mapa, List<Personaje> rosterTropas,
			ReclutamientoControlador reclutamiento, int tropasMaximas) {

	}

	/**
	 * Metodo que tomara la decision de aprovechar el moviento extra de 2 casillas
	 * de un nazgul, tras haber realizado un ataque, para retirarlo del frente de
	 * combate.
	 *
	 * @param bestia Nazgul que acaba de atacar.
	 * @param mapa   Mapa lógico del juego.
	 * @return Lista de coordenadas de casillas cadidatas a las que el nazgul se
	 *         podria mover.
	 */
	default List<int[]> casillasCandidatasParaMovimientoPostAtaqueNazgul(Personaje bestia, Mapa mapa) {
		return List.of();
	}

	/**
	 * Metodo mediante el cual vamos a seleccionar la dificultad elegida y devolvera
	 * una nueva instancia de esa dificultad. Normal sera por defecto.
	 *
	 * @param dificultad Dificultad elegida (Normal si es null).
	 * @return Nueva instancia de la estrategia correspondiente.
	 */
	static EstrategiaIA dificultadElegida(Dificultad dificultad) {
		if (dificultad == null) {
			return new EstrategiaIANormal();
		}
		switch (dificultad) {
		case FACIL:
			return new EstrategiaIAFacil();
		case DIFICIL:
			return new EstrategiaIADificil();
		case NORMAL:
		default:
			return new EstrategiaIANormal();
		}
	}

}
