package learning.tokioschool.proyectoJuegoCombateEsdla.menuPrincipal;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.CargadorCiudades;
import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.CargadorTipoTerreno;
import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.CargadorUnidades;
import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.PantallaJuegoControlador;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.configuracionIA.Dificultad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ConfiguracionVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.TipoCondicionVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.DescripcionUnidad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;

// TODO: Auto-generated Javadoc
/**
 * The Class MenuPrincipalControlador.
 * 
 * Controlador del menu inicial del juego. Se encarga de seleccionar al menos 5
 * unidades en cada bando y comenzar la partida al pulsar el botón. La unidad
 * FRODO siempre va a ir seleccionada ya que es clave para nuestra partida. En
 * esta clase cargaremos la lista de los Paladines de ambos bandos. Y abrirá el
 * mapa de juego al clickar en el boton "Empezar partida".
 */
public class MenuPrincipalControlador {

	/**
	 * Botón encargado de iniciar la partida. Cerrar el menú principal y abrir el
	 * menu del mapa.
	 */
	@FXML
	private Button btnEmpezarPartida;

	/**
	 * Lista de los Paladines Heroes que se muestra en el menu para ser
	 * seleccionados.
	 */
	@FXML
	private ListView<Personaje> listaHeroes;

	/**
	 * Lista de los Paladines Bestias que se muestra en el menu para ser
	 * seleccionados.
	 */
	@FXML
	private ListView<Personaje> listaBestias;

	/** The chk seleccionar todos heroes. */
	@FXML
	private CheckBox chkSeleccionarTodosHeroes;

	/** The chk seleccionar todos bestias. */
	@FXML
	private CheckBox chkSeleccionarTodosBestias;

	/** The txt turnos maximos. */
	@FXML
	private TextField txtTurnosMaximos;

	/** The txt puntos maximos. */
	@FXML
	private TextField txtPuntosMaximos;

	/** The txt tropas maximas. */
	@FXML
	private TextField txtTropasMaximas;

	/** The chk puntos al finalizar turnos. */
	@FXML
	private CheckBox chkPuntosAlFinalizarTurnos;

	/** The chk limite de puntos. */
	@FXML
	private CheckBox chkLimiteDePuntos;

	/** The chk frodo monte del destino. */
	@FXML
	private CheckBox chkFrodoMonteDelDestino;

	/** The chk frodo eliminado. */
	@FXML
	private CheckBox chkFrodoEliminado;

	/** The chk dominio. */
	@FXML
	private CheckBox chkDominio;

	/** The radio dificultad facil. */
	@FXML
	private RadioButton radioDificultadFacil;

	/** The radio dificultad normal. */
	@FXML
	private RadioButton radioDificultadNormal;

	/** The radio dificultad dificil. */
	@FXML
	private RadioButton radioDificultadDificil;

	/**
	 * Inicializa el menu principal, cargando los paladines de cada facción. Además
	 * va a gestionar la acción de seleccionar y deseleccionar paladines sin tener
	 * que pulsar el boton CTRL del teclado.
	 * 
	 */
	@FXML
	public void initialize() {
		List<Personaje> todasLasUnidades = CargadorUnidades.cargarUnidades();

		List<Personaje> paladinesHeroes = todasLasUnidades.stream()
				.filter(pj -> pj.getCategoria().equals("PALADIN") && pj.getFaccion() == Faccion.HEROES).toList();
		List<Personaje> paladinesBestias = todasLasUnidades.stream()
				.filter(pj -> pj.getCategoria().equals("PALADIN") && pj.getFaccion() == Faccion.BESTIAS).toList();

		listaHeroes.getItems().addAll(paladinesHeroes);
		listaBestias.getItems().addAll(paladinesBestias);

		listaHeroes.setCellFactory(lista -> crearCeldaConTooltip());
		listaBestias.setCellFactory(lista -> crearCeldaConTooltip());

		listaHeroes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listaBestias.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		chkSeleccionarTodosHeroes.selectedProperty().addListener((obs, valorAntiguo, marcado) -> {
			if (marcado) {
				listaHeroes.getSelectionModel().selectAll();
			} else {
				listaHeroes.getSelectionModel().clearSelection();
			}
		});

		chkSeleccionarTodosBestias.selectedProperty().addListener((obs, valorAntiguo, marcado) -> {
			if (marcado) {
				listaBestias.getSelectionModel().selectAll();
			} else {
				listaBestias.getSelectionModel().clearSelection();
			}
		});

		listaHeroes.addEventFilter(MouseEvent.MOUSE_PRESSED, evento -> {
			Node node = evento.getPickResult().getIntersectedNode();

			while (node != null && !(node instanceof ListCell)) {
				node = node.getParent();
			}

			if (node instanceof ListCell) {
				ListCell<?> cell = (ListCell<?>) node;
				if (!cell.isEmpty()) {
					listaHeroes.requestFocus();

					int indice = cell.getIndex();

					if (listaHeroes.getSelectionModel().isSelected(indice)) {
						listaHeroes.getSelectionModel().clearSelection(indice);
					} else {
						listaHeroes.getSelectionModel().select(indice);
					}

					evento.consume();
				}
			}
		});

		listaBestias.addEventFilter(MouseEvent.MOUSE_PRESSED, evento -> {
			Node node = evento.getPickResult().getIntersectedNode();
			while (node != null && !(node instanceof ListCell)) {
				node = node.getParent();
			}

			if (node instanceof ListCell) {
				ListCell<?> cell = (ListCell<?>) node;
				if (!cell.isEmpty()) {

					listaBestias.requestFocus();

					int indice = cell.getIndex();

					if (listaBestias.getSelectionModel().isSelected(indice)) {
						listaBestias.getSelectionModel().clearSelection(indice);
					} else {
						listaBestias.getSelectionModel().select(indice);
					}

					evento.consume();
				}
			}
		});

		txtTurnosMaximos.setDisable(true);
		txtPuntosMaximos.setDisable(true);

		chkPuntosAlFinalizarTurnos.selectedProperty().addListener((obs, valorAntiguo, marcado) -> {
			txtTurnosMaximos.setDisable(!marcado);
			if (!marcado) {
				txtTurnosMaximos.clear();
			}
		});

		chkLimiteDePuntos.selectedProperty().addListener((obs, valorAntiguo, marcado) -> {
			txtPuntosMaximos.setDisable(!marcado);
			if (!marcado) {
				txtPuntosMaximos.clear();
			}
		});
	}

	/**
	 * Empezar partida.
	 * 
	 * Acción que ocurre al pulsar el botón "Empezar Partida". Cierra la ventana del
	 * menú principal y abre la ventana del mapa de juego.
	 *
	 * @throws Exception Salta una excepción si ocurre un error al cargar el archivo
	 *                   FXML.
	 */
	@FXML
	private void empezarPartida() throws Exception {

		List<Personaje> seleccionHeroes = new java.util.ArrayList<>();
		seleccionHeroes.addAll(listaHeroes.getSelectionModel().getSelectedItems());

		List<Personaje> seleccionBestias = new java.util.ArrayList<>();
		seleccionBestias.addAll(listaBestias.getSelectionModel().getSelectedItems());

		boolean tieneAFrodo = seleccionHeroes.stream().anyMatch(pj -> pj.getNombre().equalsIgnoreCase("Frodo"));
		if (!tieneAFrodo) {
			for (Personaje pj : listaHeroes.getItems()) {
				if (pj.getNombre().equalsIgnoreCase("Frodo")) {
					seleccionHeroes.add(pj);
					break;
				}
			}
		}

		if (seleccionHeroes.size() < 5) {
			mostrarError("Debes seleccionar al menos 5 Heroes");
			return;
		}

		if (seleccionBestias.size() < 5) {
			mostrarError("Debes seleccionar al menos 5 Bestias");
			return;
		}

		ConfiguracionVictoria configuracionVictoria = construirConfiguracionVictoria();
		if (configuracionVictoria == null) {
			return;
		}

		// Cargamos el mapa de juego.
		FXMLLoader cargarPantallaJuego = new FXMLLoader(
				getClass().getResource("/pantallaJuegoRecursos/pantalla_juego.fxml"));

		Parent root = cargarPantallaJuego.load();

		// Cargamos el controlador de la pantalla de juego.
		PantallaJuegoControlador controladorPantallaJuego = cargarPantallaJuego.getController();

		// Creamos un mapa lógico.
		Mapa mapa = new Mapa(52 * 2, 30 * 2);
		CargadorCiudades.cargarCiudades(mapa);
		CargadorTipoTerreno.cargarTerreno(mapa);

		seleccionHeroes.forEach(pj -> mapa.colocarUnidad(pj.getX(), pj.getY(), pj));
		seleccionBestias.forEach(pj -> mapa.colocarUnidad(pj.getX(), pj.getY(), pj));

		Dificultad dificultadElegida = obtenerDificultadSeleccionada();

		// Inicializamos la pantalla del juego con el mapa recien creado.
		controladorPantallaJuego.inicializarPantalla(mapa, configuracionVictoria, dificultadElegida);

		// Creamos la ventana para el mapa.
		Stage stageMapa = new Stage();
		stageMapa.setTitle("A por ellos");
		stageMapa.setScene(new Scene(root));
		stageMapa.setMaximized(true);

		// Cerramos la ventana del menú principal.
		Stage stageMenuPrincipal = (Stage) btnEmpezarPartida.getScene().getWindow();
		stageMenuPrincipal.close();

		// Mostramos la ventana de juego.
		stageMapa.show();
	}

	/**
	 * Lee los checkboxes y valida que sea una combinacion de condiciones de
	 * victoria logica. Intentando evitar que haya una partida infinita.
	 *
	 * @return the configuracion victoria
	 */
	private ConfiguracionVictoria construirConfiguracionVictoria() {

		boolean puntosAlFinalizarTurnos = chkPuntosAlFinalizarTurnos.isSelected();
		boolean limiteDePuntos = chkLimiteDePuntos.isSelected();
		boolean frodoMonteDelDestino = chkFrodoMonteDelDestino.isSelected();
		boolean frodoEliminado = chkFrodoEliminado.isSelected();
		int turnosMaximos = 0;
		int puntosMaximos = 0;
		int tropasMaximas = 0;

		List<TipoCondicionVictoria> condicionesActivas = new ArrayList<>();

		if (chkPuntosAlFinalizarTurnos.isSelected()) {
			condicionesActivas.add(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS);
		}
		if (chkLimiteDePuntos.isSelected()) {
			condicionesActivas.add(TipoCondicionVictoria.LIMITE_DE_PUNTOS);
		}
		if (chkFrodoMonteDelDestino.isSelected()) {
			condicionesActivas.add(TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO);
		}
		if (chkFrodoEliminado.isSelected()) {
			condicionesActivas.add(TipoCondicionVictoria.FRODO_ELIMINADO);
		}
		if (chkDominio.isSelected()) {
			condicionesActivas.add(TipoCondicionVictoria.DOMINIO);
		}
		if (condicionesActivas.isEmpty()) {
			mostrarError(
					"Debes marcar al menos una condición (o dos, depende de la condición) para que se pueda producir la victoria.");
			return null;
		}

		int totalCondicionesActivas = condicionesActivas.size();

		if (puntosAlFinalizarTurnos && totalCondicionesActivas < 2) {
			mostrarError(
					"\"Gana quien cumpla la otra condición al finalizar los turnos,\" no se puede marcar sola: marca también otra condición.");
			return null;
		}
		if (frodoMonteDelDestino && totalCondicionesActivas < 2) {
			mostrarError(
					"\"Ganan los Heroes si Frodo llega al Monte del Destino\" no se puede marcar sola: marca también otra condición.");
			return null;
		}
		if (frodoEliminado && totalCondicionesActivas < 2) {
			mostrarError(
					"\"Ganan las Bestias si eliminan a Frodo\" no se puede marcar sola: marca también otra condición.");
			return null;
		}

		if (puntosAlFinalizarTurnos) {
			try {
				turnosMaximos = Integer.parseInt(txtTurnosMaximos.getText().trim());
			} catch (NumberFormatException e) {
				mostrarError("El número máximo de turnos debe ser un número entero válido");
				return null;
			}
			if (turnosMaximos <= 0) {
				mostrarError("El número maximo de turnos debe ser mayor que 0");
				return null;
			}
		}
		if (limiteDePuntos) {
			try {
				puntosMaximos = Integer.parseInt(txtPuntosMaximos.getText().trim());
			} catch (NumberFormatException e) {
				mostrarError("El número máximo de puntos debe ser un número entero válido");
				return null;
			}
			if (puntosMaximos <= 0) {
				mostrarError("El número máximo de puntos debe ser mayor que 0");
				return null;
			}
		}
		String textoTropas = txtTropasMaximas.getText() == null ? "" : txtTropasMaximas.getText().trim();
		if (!textoTropas.isEmpty()) {
			try {
				tropasMaximas = Integer.parseInt(textoTropas);
			} catch (NumberFormatException e) {
				mostrarError("El número máximo de tropas debe ser un número entero válido");
				return null;
			}
			if (tropasMaximas < 0 || tropasMaximas > 25) {
				mostrarError("El número máximo de tropas debe estar entre 0 y 25");
				return null;
			}
		}

		return new ConfiguracionVictoria(turnosMaximos, puntosMaximos, tropasMaximas, condicionesActivas);
	}

	/**
	 * Metodo para obtener el nivel de dificultad seleccionda.
	 *
	 * @return the dificultad
	 */
	private Dificultad obtenerDificultadSeleccionada() {
		if (radioDificultadFacil.isSelected()) {
			return Dificultad.FACIL;
		}
		if (radioDificultadNormal.isSelected()) {
			return Dificultad.NORMAL;
		}
		return Dificultad.DIFICIL;
	}

	/**
	 * Metodo para crear el tooltip cuando pasamos el cursor por encima de los
	 * nombre de las unidades en las diferentes tablas.
	 *
	 * @return the list cell
	 */
	private ListCell<Personaje> crearCeldaConTooltip() {
		return new ListCell<>() {
			@Override
			protected void updateItem(Personaje pj, boolean vacia) {
				super.updateItem(pj, vacia);
				if (vacia || pj == null) {
					setText(null);
					setTooltip(null);
				} else {
					setText(pj.toString());
					Tooltip tooltip = new Tooltip(DescripcionUnidad.generar(pj));
					tooltip.setStyle("-fx-font-size: 14px;");
					setTooltip(tooltip);
				}
			}
		};
	}

	/**
	 * Muestra una ventana de alerta con el mensaje que se le pasa por parámetro.
	 *
	 * @param mensaje Es el texto que se quiere mostrar como error.
	 */
	private void mostrarError(String mensaje) {
		Alert alerta = new Alert(Alert.AlertType.ERROR);
		alerta.setHeaderText(null);
		alerta.setContentText(mensaje);
		alerta.showAndWait();
	}

}
