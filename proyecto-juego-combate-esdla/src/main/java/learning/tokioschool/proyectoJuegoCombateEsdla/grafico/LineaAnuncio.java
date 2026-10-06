package learning.tokioschool.proyectoJuegoCombateEsdla.grafico;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;

// TODO: Auto-generated Javadoc
/**
 * The Class LineaAnuncio.
 * 
 * Clase encargada de crear una linea que se va a mostrar por pantalla, como por
 * ejemplo es la conquista de una ciudad.
 */
public class LineaAnuncio {

	/** Texto a mostrar. */
	private final String mensaje;

	/** Faccion. */
	private final Faccion faccion;

	/**
	 * Constructor de clase
	 *
	 * @param mensaje the mensaje
	 * @param faccion the faccion
	 */
	public LineaAnuncio(String mensaje, Faccion faccion) {
		this.mensaje = mensaje;
		this.faccion = faccion;
	}

	/**
	 * Gets the mensaje.
	 *
	 * @return the mensaje
	 */
	public String getMensaje() {
		return mensaje;
	}

	/**
	 * Gets the faccion.
	 *
	 * @return the faccion
	 */
	public Faccion getFaccion() {
		return faccion;
	}
}
