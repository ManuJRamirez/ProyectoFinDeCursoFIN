package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;

/**
 * The Class ResultadoPartida.
 * 
 * Esta clase va a representar el resultado final de la partida. Si hay un
 * vencedor o si ha sido empate. Y si hay ganador, que faccion ha sido.
 */
public class ResultadoPartida {

	/** Atributo que guarda la faccion ganadora. Null si fuera empate. */
	private final Faccion ganador;

	/** Atributo que guarda el texto con la condicion de victoria que se ha cumplido o con la del empate. */
	private final String motivo;

	/**
	 * Instantiates a new resultado partida.
	 *
	 * @param ganador faccion ganadora o null.
	 * @param motivo  Texto con el motivo del resultado.
	 */
	public ResultadoPartida(Faccion ganador, String motivo) {
		this.ganador = ganador;
		this.motivo = motivo;
	}

	/**
	 * Es empate.
	 *
	 * @return true, si ha sido empate
	 */
	public boolean esEmpate() {
		return ganador == null;
	}

	/**
	 * Gets the ganador.
	 *
	 * @return the ganador
	 */
	public Faccion getGanador() {
		return ganador;
	}

	/**
	 * Gets the motivo.
	 *
	 * @return the motivo
	 */
	public String getMotivo() {
		return motivo;
	}

}
