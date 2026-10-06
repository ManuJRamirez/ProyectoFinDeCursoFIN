package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.configuracionIA;

import java.util.List;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.reclutarTropa.ReclutamientoControlador;

// TODO: Auto-generated Javadoc
/**
 * The Class EstrategiaIAFacil.
 * 
 * Clase en la que definimos la estrategia especificamente para el nivel facil.
 * El comportamiento será sencillo en el que se priorizara al enemigo mas
 * cercano. Además cometera errores a la hora de seleccionar un objetivo. El
 * reclutamiento sera aleatorio y con una probabilidad moderada.
 */
public class EstrategiaIAFacil extends EstrategiaIABase {

	/** Atributo en el que definimos la probabilidad de error de la IA. */
	private static final double PROBABILIDAD_DE_ERROR = 0.25;

	/**
	 * valor que se suma a la puntuacion de objetivo, cuando la IA comete un error.
	 */
	private static final double PENALIZACION_POR_ERROR = 3.0;

	/** Atributo donde guardaremos la probabilidad de iniciar un reclutamiento. */
	private static final double PROBABILIDAD_DE_RECLUTAR = 0.5;

	/**
	 * Metodo sobreescrito para puntuar a un objetivo segun la dificultad facil. En
	 * este caso se puntuará segun la distancia hasta él. Tendremos en cuenta la
	 * probabilidad de error y la penalizacion cuando se cometa ese error. Este
	 * valor siempre va a ser negativo y el valor menos negativo sera el eligido.
	 *
	 * @param bestia    the bestia
	 * @param candidato the candidato
	 * @param mapa      the mapa
	 * @return the double puntuacion del candidato.
	 */
	@Override
	protected double puntuarObjetivo(Personaje bestia, Personaje candidato, Mapa mapa) {
		int distancia = distanciaEntre(bestia, candidato);
		double puntuacion = -distancia;

		if (Math.random() < PROBABILIDAD_DE_ERROR) {
			puntuacion -= PENALIZACION_POR_ERROR;
		}

		return puntuacion;
	}

	/**
	 * Recopila los enemigos candidatos que pueden ser elegidos.
	 *
	 * @param bestia the bestia
	 * @param mapa   the mapa
	 * @return the list de enemigos.
	 */
	@Override
	protected List<Personaje> recopilarEnemigosCandidatos(Personaje bestia, Mapa mapa) {
		List<Personaje> candidatos = super.recopilarEnemigosCandidatos(bestia, mapa);
		return anadirHobbitsParaRastreador(bestia, mapa, candidatos);
	}

	/**
	 * Metodo para decidir por cada ciudad natal controlada, si se inicia un
	 * reclutamiento o no.
	 *
	 * @param faccion       the faccion
	 * @param mapa          the mapa
	 * @param rosterTropas  Listado de tropas disponibles para reclutar
	 * @param reclutamiento Controlador de reclutamiento
	 * @param tropasMaximas Numero de tropas maximas que se puede tener por faccion.
	 */
	@Override
	public void decidirReclutamiento(Faccion faccion, Mapa mapa, List<Personaje> rosterTropas,
			ReclutamientoControlador reclutamiento, int tropasMaximas) {
		reclutarEnCiudadesControladas(faccion, mapa, rosterTropas, reclutamiento, tropasMaximas, disponibles -> {
			if (Math.random() >= PROBABILIDAD_DE_RECLUTAR) {
				return null;
			}
			return disponibles.get((int) (Math.random() * disponibles.size()));
		});

	}

}
