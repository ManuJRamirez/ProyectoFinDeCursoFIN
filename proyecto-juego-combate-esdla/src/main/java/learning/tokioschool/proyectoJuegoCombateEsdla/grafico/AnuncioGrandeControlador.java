package learning.tokioschool.proyectoJuegoCombateEsdla.grafico;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;

// TODO: Auto-generated Javadoc
/**
 * The Class AnuncioGrandeControlador.
 * 
 * Controlador encargado de mostrar los anuncions grandes en la pantalla de
 * juego. Anuncios como por ejemplo, las conquistas y el resultado final de la
 * partida.
 */
public class AnuncioGrandeControlador {

	/** Color usado para los textos de la faccion Heroes */
	private static final String COLOR_HEROES = "#3a86ff";

	/** Color usado para los textos de la faccion Bestias. */
	private static final String COLOR_BESTIAS = "#ff3333";

	/** Color usado para el resultado en Empate. */
	private static final String COLOR_EMPATE = "#ffd700";

	/**
	 * Atributo estatico para definir el tamaño de la letra del anuncio de
	 * conquista.
	 */
	private static final double TAMANO_LETRA_CONQUISTA = 30.0;

	/**
	 * Atributo estatico para definir el tamaño de la letra del anuncio de
	 * resultado.
	 */
	private static final double TAMANO_LETRA_RESULTADO = 40.0;

	/**
	 * Atributo estatico para definir el tiempo que esta visible el anuncio de
	 * estado de conquista
	 */
	private static final double SEGUNDOS_VISIBLE_CONQUISTA = 5.0;

	/** Contenedor de texto sobre el que se va a mostrar los anuncios */
	private final TextFlow contenedor;

	/**
	 * Atributo que se va a encargar de ocultar automaticamente el anuncio de
	 * conquista.
	 */
	private PauseTransition temporizador;

	/**
	 * atributo para guardar el ultimo texto de conquista mostrado. Se usara para
	 * evitar repetir el mismo anuncio
	 */
	private String ultimoTextoDeConquistaMostrado = "";

	/**
	 * Constructor de clase.
	 *
	 * @param contenedor the contenedor
	 */
	public AnuncioGrandeControlador(TextFlow contenedor) {
		this.contenedor = contenedor;
	}

	/**
	 * Metodo para mostrar los anuncios de ciudades, usando el color de la faccion.
	 * Intenta evitar la repeticion del mismo texto.
	 *
	 * @param lineas the lineas
	 */
	public void mostrarConquistas(List<LineaAnuncio> lineas) {
		if (lineas.isEmpty()) {
			ultimoTextoDeConquistaMostrado = "";
			return;
		}

		String textoCompleto = textoDeLineas(lineas);
		if (textoCompleto.equals(ultimoTextoDeConquistaMostrado)) {
			return;
		}
		ultimoTextoDeConquistaMostrado = textoCompleto;

		List<Text> nodos = new ArrayList<>();
		for (int i = 0; i < lineas.size(); i++) {
			LineaAnuncio linea = lineas.get(i);
			String color = (linea.getFaccion() == Faccion.HEROES) ? COLOR_HEROES : COLOR_BESTIAS;
			nodos.add(crearTexto(linea.getMensaje(), color, TAMANO_LETRA_CONQUISTA));
			if (i < lineas.size() - 1) {
				nodos.add(new Text("\n"));
			}
		}
		mostrar(nodos);

		if (temporizador != null) {
			temporizador.stop();
		}
		temporizador = new PauseTransition(Duration.seconds(SEGUNDOS_VISIBLE_CONQUISTA));
		temporizador.setOnFinished(evento -> ocultar());
		temporizador.play();
	}

	/**
	 * Metodo encargado en mostrar el resultado de la partida con el color
	 * correspondiente en funcion del ganador.
	 *
	 * @param texto   the texto
	 * @param ganador the ganador
	 * @param empate  the empate
	 */
	public void mostrarResultado(String texto, Faccion ganador, boolean empate) {
		String color = empate ? COLOR_EMPATE : (ganador == Faccion.HEROES ? COLOR_HEROES : COLOR_BESTIAS);

		if (temporizador != null) {
			temporizador.stop();
		}
		mostrar(List.of(crearTexto(texto, color, TAMANO_LETRA_RESULTADO)));
	}

	/**
	 * Metodo que oculta el anuncio.
	 */
	public void ocultar() {
		if (temporizador != null) {
			temporizador.stop();
		}
		contenedor.getChildren().clear();
		contenedor.setVisible(false);
		contenedor.setManaged(false);
	}

	/**
	 * Metodo para mostrar los anuncios dentro del contenedor.
	 *
	 * @param nodos Texto a mostrar.
	 */
	private void mostrar(List<Text> nodos) {
		contenedor.getChildren().setAll(nodos);
		contenedor.setVisible(true);
		contenedor.setManaged(true);
	}

	/**
	 * Metodo para crear los nodos de texto que se van a mostrar en los anuncios.
	 *
	 * @param mensaje     the mensaje
	 * @param colorHex    the color hex
	 * @param tamanoLetra the tamano letra
	 * @return the text
	 */
	private Text crearTexto(String mensaje, String colorHex, double tamanoLetra) {
		Text texto = new Text(mensaje);
		texto.setStyle("-fx-fill: " + colorHex + "; -fx-font-weight: bold; -fx-font-size: " + tamanoLetra + "px;");
		return texto;
	}

	/**
	 * Metodo para concadenar todas las lineas en un unico texto. Añadiremos un
	 * salto de linea entre cada elemento de la lista.
	 *
	 * @param lineas the lineas
	 * @return the string
	 */
	private String textoDeLineas(List<LineaAnuncio> lineas) {
		StringBuilder texto = new StringBuilder();
		for (int i = 0; i < lineas.size(); i++) {
			if (i > 0) {
				texto.append("\n");
			}
			texto.append(lineas.get(i).getMensaje());
		}
		return texto.toString();
	}
}
