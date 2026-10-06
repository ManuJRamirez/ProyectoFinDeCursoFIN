package learning.tokioschool.proyectoJuegoCombateEsdla.grafico;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.combate.CombateControlador;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.configuracionIA.Dificultad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.configuracionIA.IaControlador;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Casilla;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.EstadoConquista;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoEstadoConquista;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ConfiguracionVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ControladorVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ResultadoPartida;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.TipoCondicionVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.DescripcionUnidad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.reclutarTropa.OrdenReclutamiento;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.reclutarTropa.ReclutamientoControlador;
import learning.tokioschool.proyectoJuegoCombateEsdla.sonidos.GestorSonido;

/**
 * The Class PantallaJuegoControlador.
 * 
 * Controla la interacción (los eventos) del usuario con el mapa (zoom, arrastre
 * de la camara, seleccion de casillas y eventos raton/teclado).
 */
public class PantallaJuegoControlador {

	/**
	 * The Enum TipoEvento.
	 */
	public enum TipoEvento {

		/** The movimiento. */
		MOVIMIENTO,
		/** The ataque. */
		ATAQUE,
		/** The muerte. */
		MUERTE,
		/** The conquista. */
		CONQUISTA,
		/** The turno. */
		TURNO,
		/** The deteccion. */
		DETECCION,
		/** The curacion. */
		CURACION,
		/** The creacion. */
		CREACION
	}

	/** The Constant LOG_ANCHO_MINIMO. */
	private static final double LOG_ANCHO_MINIMO = 380.0;

	/** The Constant LOG_ALTO_MINIMO. */
	private static final double LOG_ALTO_MINIMO = 260.0;

	/** The Constant LOG_ANCHO_MAXIMO. */
	private static final double LOG_ANCHO_MAXIMO = LOG_ANCHO_MINIMO * 1.5;

	/** The Constant LOG_ALTO_MAXIMO. */
	private static final double LOG_ALTO_MAXIMO = LOG_ALTO_MINIMO * 1.5;

	/** The tooltip unidad hover. */
	private final Tooltip tooltipUnidadHover = new Tooltip();

	/**
	 * Atributo para el mapa lógico. Se encarga de la estructura interna del mapa
	 * (casillas ocupadas, tipo de terreno, etc).
	 */
	private Mapa mapa;

	/** Atributo que guardamos un objedo del mapa en canvas renderizado. */
	private RenderizadoMapa mapaRenderizado;

	/** Atributo donde guardamos la imagen base del mapa. */
	private Image imagenMapa;

	/** The combate controlador. */
	private CombateControlador combateControlador;

	/** The ia controlador. */
	private IaControlador iaControlador;

	/** The controlador victoria. */
	private ControladorVictoria controladorVictoria;

	/** The reclutamiento controlador. */
	private ReclutamientoControlador reclutamientoControlador;

	/** The partida terminada. */
	private boolean partidaTerminada = false;

	/** The todas las tropas. */
	private List<Personaje> todasLasTropas = new ArrayList<>();

	/** The tropas maximas. */
	private int tropasMaximas = 0;

	/** The unidad aliada seleccionada. */
	private Personaje unidadAliadaSeleccionada;

	/** The unidad enemiga seleccionada. */
	private Personaje unidadEnemigaSeleccionada;

	/** The turno actual. */
	private Faccion turnoActual = Faccion.HEROES;

	/** The contador turno heroes. */
	private int contadorTurnoHeroes = 1;

	/** The contador turno bestias. */
	private int contadorTurnoBestias = 1;

	/** The ciudad visible en hud. */
	private Ciudad ciudadVisibleEnHud;

	/** The ultima deteccion positiva. */
	private boolean ultimaDeteccionPositiva = false;

	/** The anuncio grande. */
	private AnuncioGrandeControlador anuncioGrande;

	/** The drag inicio X. */
	private double dragInicioX;

	/** The drag inicio Y. */
	private double dragInicioY;

	/** The ancho inicio arrastre. */
	private double anchoInicioArrastre;

	/** The alto inicio arrastre. */
	private double altoInicioArrastre;

	/**
	 * Contenedor que ajusta dinámicamente el tamaño del canvas. Es el contenedor
	 * que define el tamaño visible del mapa en cada momento (con mas o menos zoom)
	 */
	@FXML
	private Pane contenedorCanvas;

	/** Atributo donde guardamos la pantalla del juego. */
	@FXML
	private Canvas canvasMapa;

	/** The panel aliado. */
	@FXML
	private VBox panelAliado;

	/** The hbox cabecera aliado. */
	@FXML
	private HBox hboxCabeceraAliado;

	/** The lbl aliado bonus. */
	@FXML
	private Label lblAliadoBonus;

	/** The lbl aliado turno inmovil. */
	@FXML
	private Label lblAliadoTurnoInmovil;

	/** The lbl aliado nombre. */
	@FXML
	private Label lblAliadoNombre;

	/** The lbl aliado tipo. */
	@FXML
	private Label lblAliadoTipo;

	/** The lbl aliado vida. */
	@FXML
	private Label lblAliadoVida;

	/** The lbl aliado armadura. */
	@FXML
	private Label lblAliadoArmadura;

	/** The bar aliado vida. */
	@FXML
	private ProgressBar barAliadoVida;

	/** The panel enemigo. */
	@FXML
	private VBox panelEnemigo;

	/** The hbox cabecera enemigo. */
	@FXML
	private HBox hboxCabeceraEnemigo;

	/** The lbl enemigo bonus. */
	@FXML
	private Label lblEnemigoBonus;

	/** The lbl enemigo nombre. */
	@FXML
	private Label lblEnemigoNombre;

	/** The lbl enemigo tipo. */
	@FXML
	private Label lblEnemigoTipo;

	/** The lbl enemigo vida. */
	@FXML
	private Label lblEnemigoVida;

	/** The lbl enemigo armadura. */
	@FXML
	private Label lblEnemigoArmadura;

	/** The lbles visible. */
	@FXML
	private Label lblesVisible;

	/** The bar enemigo vida. */
	@FXML
	private ProgressBar barEnemigoVida;

	/** The btn atacar. */
	@FXML
	private Button btnAtacar;

	/** The panel info casilla. */
	@FXML
	private VBox panelInfoCasilla;

	/** The lbl cabecera casilla. */
	@FXML
	private Label lblCabeceraCasilla;

	/** The lbl info terreno. */
	@FXML
	private Label lblInfoTerreno;

	/** The lbl ciudad estado conquista. */
	@FXML
	private Label lblCiudadEstadoConquista;

	/** The lbl info frontera. */
	@FXML
	private Label lblInfoFrontera;

	/** The sub panel ciudad. */
	@FXML
	private VBox subPanelCiudad;

	/** The lbl ciudad nombre. */
	@FXML
	private Label lblCiudadNombre;

	/** The lbl ciudad control. */
	@FXML
	private Label lblCiudadControl;

	/** The list guarnicion urbana. */
	@FXML
	private ListView<Personaje> listGuarnicionUrbana;

	/** The btn finalizar turno. */
	@FXML
	private Button btnFinalizarTurno;

	/** The lbl turno. */
	@FXML
	private Label lblTurno;

	/** The lbl estado turno. */
	@FXML
	private Label lblEstadoTurno;

	/** The lbl anuncio conquista. */
	@FXML
	private TextFlow lblAnuncioConquista;

	/** The lbl ronda partida. */
	@FXML
	private Label lblRondaPartida;

	/** The lbl limite turnos. */
	@FXML
	private Label lblLimiteTurnos;

	/** The lbl puntos heroes. */
	@FXML
	private Label lblPuntosHeroes;

	/** The lbl puntos bestias. */
	@FXML
	private Label lblPuntosBestias;

	/** The hbox puntos heroes. */
	@FXML
	private HBox hboxPuntosHeroes;

	/** The hbox puntos bestias. */
	@FXML
	private HBox hboxPuntosBestias;

	/** The hbox tropas heroes. */
	@FXML
	private HBox hboxTropasHeroes;

	/** The hbox tropas bestias. */
	@FXML
	private HBox hboxTropasBestias;

	/** The lbl tropas heroes. */
	@FXML
	private Label lblTropasHeroes;

	/** The lbl tropas bestias. */
	@FXML
	private Label lblTropasBestias;

	/** The vbox reclutamiento. */
	@FXML
	private VBox vboxReclutamiento;

	/** The txt area log. */
	@FXML
	private TextArea txtAreaLog;

	/** The btn alternar log. */
	@FXML
	private Button btnAlternarLog;

	/** The scroll log eventos. */
	@FXML
	private ScrollPane scrollLog;

	/** The flow log. */
	@FXML
	private TextFlow flowLog;

	/** The resize grip log. */
	@FXML
	private Region resizeGripLog;

	/** The btn alternar log dados. */
	@FXML
	private Button btnAlternarLogDados;

	/** The scroll log dados. */
	@FXML
	private ScrollPane scrollLogDados;

	/** The flow log dados. */
	@FXML
	private TextFlow flowLogDados;

	/** The resize grip log dados. */
	@FXML
	private Region resizeGripLogDados;

	/**
	 * Inicializar pantalla con la dificultad por defecto (normal).
	 *
	 * @param mapa                  the mapa
	 * @param configuracionVictoria the configuracion victoria
	 */
	public void inicializarPantalla(Mapa mapa, ConfiguracionVictoria configuracionVictoria) {
		inicializarPantalla(mapa, configuracionVictoria, Dificultad.NORMAL);
	}

	/**
	 * Inicializa la pantalla del juego, configurando el renderizado del mapa, los
	 * eventos de entrar (del ratón y teclado) y el comportamiento del zoom y la
	 * camara. Ademas podremos cancelar seleccion y abrir menu de pausa con el boton
	 * ESC
	 *
	 * @param mapa                  Se le pasa por parámetros un mapa lógico (que no
	 *                              es la imagen del mapa).
	 * @param configuracionVictoria the configuracion victoria
	 * @param dificultad            the dificultad
	 */
	public void inicializarPantalla(Mapa mapa, ConfiguracionVictoria configuracionVictoria, Dificultad dificultad) {
		this.mapa = mapa;

		combateControlador = new CombateControlador(mapa, this);
		iaControlador = new IaControlador(mapa, this, dificultad, configuracionVictoria);
		controladorVictoria = new ControladorVictoria(configuracionVictoria);
		reclutamientoControlador = new ReclutamientoControlador(mapa, this);
		anuncioGrande = new AnuncioGrandeControlador(lblAnuncioConquista);
		imagenMapa = new Image("/pantallaJuegoRecursos/mapa_base.png");

		GestorSonido.inicializar();
		GestorSonido.reproducirMusicaFondo();

		this.tropasMaximas = configuracionVictoria.getTropasMaximas();
		this.todasLasTropas = CargadorUnidades.cargarUnidades().stream()
				.filter(pj -> "TROPA".equalsIgnoreCase(pj.getCategoria())).toList();

		iaControlador.configurarReclutamiento(reclutamientoControlador, tropasMaximas);

		boolean mostrarHudTropas = tropasMaximas > 0;
		hboxTropasHeroes.setVisible(mostrarHudTropas);
		hboxTropasHeroes.setManaged(mostrarHudTropas);
		hboxTropasBestias.setVisible(mostrarHudTropas);
		hboxTropasBestias.setManaged(mostrarHudTropas);
		actualizarHudTropas();

		if (configuracionVictoria.estaActiva(TipoCondicionVictoria.PUNTOS_AL_FINALIZAR_TURNOS)) {
			lblLimiteTurnos.setText(" / " + configuracionVictoria.getTurnosMaximos());
		} else {
			lblLimiteTurnos.setText("");
		}
		boolean mostrarHudPuntuacion = configuracionVictoria.estaActiva(TipoCondicionVictoria.LIMITE_DE_PUNTOS);
		hboxPuntosHeroes.setVisible(mostrarHudPuntuacion);
		hboxPuntosHeroes.setManaged(mostrarHudPuntuacion);
		hboxPuntosBestias.setVisible(mostrarHudPuntuacion);
		hboxPuntosBestias.setManaged(mostrarHudPuntuacion);
		actualizarHudPuntuacion();

		registrarAccion("¡Que comience la partida! ⚔️ ", TipoEvento.TURNO);

		registrarTituloLogDados();

		configurarRedimensionadoLogs();

		// Ajuste dinamico del canvas al tamaño del contenedor.
		canvasMapa.widthProperty().bind(contenedorCanvas.widthProperty());
		canvasMapa.heightProperty().bind(contenedorCanvas.heightProperty());

		mapaRenderizado = new RenderizadoMapa(canvasMapa, 16, imagenMapa, mapa);

		mapaRenderizado.setActivarAnuncioFrodoMonteDestino(() -> {
			if (!configuracionVictoria.estaActiva(TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO)) {
				return -1;
			}
			Personaje frodo = mapa.getTodasLasUnidades().stream()
					.filter(pj -> pj != null && pj.estaVivo() && "Frodo".equalsIgnoreCase(pj.getNombre())).findFirst()
					.orElse(null);
			if (frodo == null) {
				return -1;
			}
			Ciudad ciudadDeFrodo = mapa.buscarCiudadesPorCasilla(frodo.getX(), frodo.getY());
			if (ciudadDeFrodo == null || !"Monte del Destino".equalsIgnoreCase(ciudadDeFrodo.getNombreCiudad())) {
				return -1;
			}
			return controladorVictoria.getRondasFrodoEnMonteSinAtaque();
		});

		hacerArrastrable(hboxCabeceraAliado, panelAliado);
		hacerArrastrable(hboxCabeceraEnemigo, panelEnemigo);
		hacerArrastrable(lblCabeceraCasilla, panelInfoCasilla);

		// Renderizado inicial donde centramos el mapa en la pantalla y le damos un zoom
		// inicial.
		Platform.runLater(() -> {
			mapaRenderizado.setZoom(1.0);
			mapaRenderizado.centrarCamara();
			mapaRenderizado.dibujarMapa(mapa);
		});

		// Listener encargado de redibujar el tamaño del canvas.
		canvasMapa.widthProperty().addListener((obs, viejo, nuevo) -> {
			if (mapaRenderizado != null) {
				mapaRenderizado.dibujarMapa(mapa);
			}
		});

		// Listener encargado de redibujar el tamaño del canvas.
		canvasMapa.heightProperty().addListener((obs, viejo, nuevo) -> {
			if (mapaRenderizado != null) {
				mapaRenderizado.dibujarMapa(mapa);
			}
		});

		// Zoom con la rueda del ratón.
		canvasMapa.setOnScroll(evento -> {
			double ratonX = evento.getX();
			double ratonY = evento.getY();

			mapaRenderizado.modificarZoom(evento.getDeltaY() > 0 ? 1.05 : 0.95, ratonX, ratonY);
			mapaRenderizado.dibujarMapa(mapa);
		});

		// Inicio del arrastre, donde se guarda el punto inicial.
		canvasMapa.setOnMousePressed(evento -> {
			if (evento.getButton() == MouseButton.SECONDARY) {
				mapaRenderizado.iniciarArrastreCamara(evento.getX(), evento.getY());
			}
		});

		// Arrastre de la cámara. SOLO CON BOTON DERECHO.
		canvasMapa.setOnMouseDragged(evento -> {
			if (evento.isSecondaryButtonDown()) {
				mapaRenderizado.arrastrarCamara(evento.getX(), evento.getY());
				mapaRenderizado.dibujarMapa(mapa);
			}
		});

		// Evento para seleccionar una casilla
		canvasMapa.setOnMouseClicked(evento -> {
			if (evento.getButton() != MouseButton.PRIMARY) {
				return;
			}
			if (partidaTerminada) {
				return;
			}

			double xImg = (evento.getX() + mapaRenderizado.getCamaraX()) / mapaRenderizado.getZoom();
			double yImg = (evento.getY() + mapaRenderizado.getCamaraY()) / mapaRenderizado.getZoom();

			double celdaImgAncho = imagenMapa.getWidth() / mapa.getAncho();
			double celdaImgAlto = imagenMapa.getHeight() / mapa.getAlto();

			int x = (int) (xImg / celdaImgAncho);
			int y = (int) (yImg / celdaImgAlto);

			if (!mapa.estaDentroDelMapa(x, y)) {
				return;
			}

			Casilla casillaClicada = mapa.getCasilla(x, y);
			Ciudad ciudad = mapa.buscarCiudadesPorCasilla(x, y);

			if (unidadAliadaSeleccionada != null && esTurnoDe(unidadAliadaSeleccionada)) {
				String claveClicada = x + "," + y;

				if (mapaRenderizado.getCasillasMovimientoValidas().contains(claveClicada)) {
					boolean seHaMovidoConExito = mapa.moverUnidad(unidadAliadaSeleccionada, x, y);

					if (seHaMovidoConExito) {
						seleccionarUnidad(unidadAliadaSeleccionada);
						mapaRenderizado.setAliadoEnFoco(unidadAliadaSeleccionada);
						mapaRenderizado.seleccionarCasilla(x, y);
						registrarAccion(
								"🚶 " + unidadAliadaSeleccionada.getNombre() + " se ha movido a (" + x + "," + y + ")",
								TipoEvento.MOVIMIENTO);

						comprobarCondicionesDeVictoriaInmediatas();
						if (partidaTerminada) {
							mapaRenderizado.dibujarMapa(mapa);
							return;
						}

						Ciudad ciudadActual = mapa.buscarCiudadesPorCasilla(x, y);

						if (ciudadActual != null && "Monte del Destino".equalsIgnoreCase(ciudadActual.getNombreCiudad())
								&& unidadAliadaSeleccionada instanceof Hobbit
								&& !unidadAliadaSeleccionada.esDetectadoPorEnemigo()) {
							unidadAliadaSeleccionada.setDetectadoPorEnemigo(true);
							registrarAccion("👁️ " + unidadAliadaSeleccionada.getNombre()
									+ " entra en el Monte del Destino y pierde su invisibilidad: ¡queda expuesto!",
									TipoEvento.DETECCION);
						}

						if (ciudadActual != null) {
							// Si entramos a una ciudad (o nos movemos dentro de ella), actualizamos el HUD
							lblCiudadNombre.setText("Ciudad: " + ciudadActual.getNombreCiudad());
							listGuarnicionUrbana.getItems().clear();
							listGuarnicionUrbana.getItems().addAll(ciudadActual.getUnidadesDentro());
							actualizarEstadoConquistaHUD(ciudadActual);
							ciudadVisibleEnHud = ciudadActual;

							subPanelCiudad.setVisible(true);
							subPanelCiudad.setManaged(true);
						} else {
							// Si hemos salido completamente al campo abierto, vaciamos y ocultamos el
							// subpanel
							listGuarnicionUrbana.getItems().clear();
							subPanelCiudad.setVisible(false);
							subPanelCiudad.setManaged(false);
							actualizarEstadoConquistaHUD(null);
						}

						mapaRenderizado.dibujarMapa(mapa);
						return;
					}
				}

			}

			if (casillaClicada != null) {
				lblInfoTerreno.setText("Terreno: " + casillaClicada.getTerreno());

				Ciudad ciudadDueniaTerreno = mapa.buscarCiudadPorCualquierCasillaHUD(x, y);

				if (ciudadDueniaTerreno != null) {
					String faccionControlaTerreno = "NEUTRAL";
					if (ciudadDueniaTerreno.getControlSobreCiudad() != null) {
						faccionControlaTerreno = ciudadDueniaTerreno.getControlSobreCiudad().toString();
					}

					lblInfoFrontera.setText("Soberanía: Región de: " + ciudadDueniaTerreno.getNombreCiudad() + " ("
							+ faccionControlaTerreno + ")");

					if (ciudad != null) {
						lblCiudadNombre.setText("Ciudad: " + ciudad.getNombreCiudad());
						actualizarEstadoConquistaHUD(ciudad);

						ciudadVisibleEnHud = ciudad;
						listGuarnicionUrbana.getItems().clear();
						listGuarnicionUrbana.getItems().addAll(ciudad.getUnidadesDentro());

						subPanelCiudad.setVisible(true);
						subPanelCiudad.setManaged(true);
					} else {
						ciudadVisibleEnHud = null;
						subPanelCiudad.setVisible(false);
						subPanelCiudad.setManaged(false);
						actualizarEstadoConquistaHUD(null);
					}
				} else {
					ciudadVisibleEnHud = null;
					lblInfoFrontera.setText("Soberanía: Neutral (Salvaje)");
					subPanelCiudad.setVisible(false);
					subPanelCiudad.setManaged(false);
					actualizarEstadoConquistaHUD(null);
				}
			}
			if (casillaClicada != null && !casillaClicada.estaVacia()) {
				Personaje pjEncontrado = casillaClicada.getPersonaje();

				if (ciudad != null) {
					List<Personaje> unidades = ciudad.getUnidadesDentroCiudad(x, y);
					if (unidades != null && unidades.size() > 1) {
						Platform.runLater(() -> {
							mostrarMenuSeleccionUnidades(unidades);
						});
						mapaRenderizado.seleccionarCasilla(x, y);
						mapaRenderizado.dibujarMapa(mapa);
						return;
					}
				}

				seleccionarUnidad(pjEncontrado);

			} else {
				seleccionarUnidad(null);
			}

			mapaRenderizado.seleccionarCasilla(x, y);
			mapaRenderizado.dibujarMapa(mapa);
		});

		// Evento encargado de señalar a qué casilla esta apuntando el ratón.
		canvasMapa.setOnMouseMoved(evento -> {

			double xImg = (evento.getX() + mapaRenderizado.getCamaraX()) / mapaRenderizado.getZoom();
			double yImg = (evento.getY() + mapaRenderizado.getCamaraY()) / mapaRenderizado.getZoom();

			double celdaImgAncho = imagenMapa.getWidth() / mapa.getAncho();
			double celdaImgAlto = imagenMapa.getHeight() / mapa.getAlto();

			int x = (int) (xImg / celdaImgAncho);
			int y = (int) (yImg / celdaImgAlto);

			if (x >= 0 && x < mapa.getAncho() && y >= 0 && y < mapa.getAlto()) {
				mapaRenderizado.actualizarHover(x, y);
			} else {
				mapaRenderizado.actualizarHover(-1, -1);
			}
			Personaje pjBajoRaton = null;
			if (x >= 0 && x < mapa.getAncho() && y >= 0 && y < mapa.getAlto()) {
				Casilla casillaBajoRaton = mapa.getCasilla(x, y);

				if (casillaBajoRaton != null && !casillaBajoRaton.estaVacia()) {
					pjBajoRaton = casillaBajoRaton.getPersonaje();
				}
			}
			if (pjBajoRaton != null) {
				String estadoMovimiento = pjBajoRaton.HaMovidoEsteTurno() ? "✅ Ya se ha movido este turno"
						: "➡️ Aún no se ha movido este turno";
				tooltipUnidadHover.setText(pjBajoRaton.getNombre() + "\n" + estadoMovimiento);
				tooltipUnidadHover.setStyle("-fx-font-size: 14px;");
				tooltipUnidadHover.show(canvasMapa, evento.getScreenX() + 16, evento.getScreenY() + 16);
			} else {
				tooltipUnidadHover.hide();
			}
			mapaRenderizado.dibujarMapa(mapa);
		});

		// Permitimos que pueda recibir eventos del teclado.
		canvasMapa.setFocusTraversable(true);

		// Evento que se encarga de mover el mapa usando las flechas (Arriba - Abajo -
		// Izquierda - Derecha) del teclado.
		canvasMapa.setOnKeyPressed(evento -> {
			double velocidad = 40;
			boolean esFlecha = evento.getCode() == KeyCode.UP || evento.getCode() == KeyCode.DOWN
					|| evento.getCode() == KeyCode.LEFT || evento.getCode() == KeyCode.RIGHT;

			if (evento.getCode() == KeyCode.UP) {
				mapaRenderizado.moverCamaraConFlechas(0, -velocidad);
			}
			if (evento.getCode() == KeyCode.DOWN) {
				mapaRenderizado.moverCamaraConFlechas(0, velocidad);
			}
			if (evento.getCode() == KeyCode.LEFT) {
				mapaRenderizado.moverCamaraConFlechas(-velocidad, 0);
			}
			if (evento.getCode() == KeyCode.RIGHT) {
				mapaRenderizado.moverCamaraConFlechas(velocidad, 0);
			}

			if (esFlecha) {
				evento.consume();
			}

			mapaRenderizado.dibujarMapa(mapa);
		});

		canvasMapa.sceneProperty().addListener((obs, escenaVieja, escenaNueva) -> {
			if (escenaNueva != null) {
				escenaNueva.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {
					if (evento.getCode() == KeyCode.ESCAPE) {
						if (unidadAliadaSeleccionada != null || unidadEnemigaSeleccionada != null) {
							if (unidadAliadaSeleccionada != null) {
								cerrarPanelAliado();
							}
							if (unidadEnemigaSeleccionada != null) {
								cerrarPanelEnemigo();
							}
							tooltipUnidadHover.hide();
							mapaRenderizado.dibujarMapa(mapa);
						} else {
							mostrarMenuPausa();
						}
					}
				});
			}
		});

		listGuarnicionUrbana.setCellFactory(lv -> {
			ListCell<Personaje> celda = new ListCell<>() {
				@Override
				protected void updateItem(Personaje item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setTooltip(null);
					} else {
						setText(item.toString());
						Tooltip tooltip = new Tooltip(DescripcionUnidad.generar(item));
						tooltip.setStyle("-fx-font-size: 12px;");
						setTooltip(tooltip);
					}
				}
			};

			celda.setOnMouseClicked(evt -> {
				if (!celda.isEmpty() && evt.getButton() == MouseButton.PRIMARY) {
					Personaje seleccionado = celda.getItem();
					if (seleccionado != null) {
						seleccionarUnidad(seleccionado);
						if (seleccionado.getFaccion() == Faccion.HEROES) {
							mapaRenderizado.setAliadoEnFoco(seleccionado);
						} else {
							mapaRenderizado.setEnemigoEnFoco(seleccionado);
						}

						mapaRenderizado.seleccionarCasilla(seleccionado.getX(), seleccionado.getY());
						mapaRenderizado.dibujarMapa(mapa);
					}
				}
			});
			return celda;
		});
		btnFinalizarTurno.setOnAction(evento -> finalizarTurno());

		lblEstadoTurno.setVisible(false);
		lblEstadoTurno.setManaged(false);
		lblRondaPartida.setText(String.valueOf(contadorTurnoHeroes));

		if (turnoActual == Faccion.HEROES) {
			lblTurno.setText("Heroes");
			lblTurno.setStyle("-fx-text-fill: #00ff66; -fx-font-weight: 900; -fx-font-size: 14px;");
		} else {
			lblTurno.setText("BESTIAS");
			lblTurno.setStyle(
					"-fx-text-fill: #ff0055; -fx-font-weight: 900; -fx-font-size: 14px; -fx-font-family: 'Segoe UI', sans-serif;");
		}
	}

	/**
	 * Metodo para gestinar el boton Atacar, modificando el texto en funcion de, si
	 * es tu turno, si esta en rango, si ya has atacado, si no le puedes atacar...
	 * etc
	 */
	@FXML
	public void actualizarBotonAtacar() {
		if (unidadAliadaSeleccionada == null || unidadEnemigaSeleccionada == null) {
			return;
		}
		if (!esTurnoDe(unidadAliadaSeleccionada)) {
			btnAtacar.setDisable(true);
			btnAtacar.setText("No es tu turno");
			return;
		}
		if (unidadAliadaSeleccionada instanceof Hobbit && unidadEnemigaSeleccionada instanceof Nazgul) {
			btnAtacar.setDisable(true);
			btnAtacar.setText("No puede atacar a un Nazgul");
			return;
		}
		if (unidadAliadaSeleccionada.HaAtacadoEsteTurno()) {
			btnAtacar.setDisable(true);
			btnAtacar.setText("Ya ha atacado");
			return;
		}
		if (!unidadAliadaSeleccionada.puedeAtacarObjetivo(unidadEnemigaSeleccionada, mapa)) {
			btnAtacar.setDisable(true);
			btnAtacar.setText("🐎 No puede escalar murallas");
			return;
		}

		int distX = Math.abs(unidadAliadaSeleccionada.getX() - unidadEnemigaSeleccionada.getX());
		int distY = Math.abs(unidadAliadaSeleccionada.getY() - unidadEnemigaSeleccionada.getY());
		if (Math.max(distX, distY) <= unidadAliadaSeleccionada.getRangoAtaque()) {
			btnAtacar.setDisable(false);
			btnAtacar.setText("¡Atacar!");
		} else {
			btnAtacar.setDisable(true);
			btnAtacar.setText("Fuera de Rango");
		}
	}

	/**
	 * Metodo para cerrar el panel de unidad aliada seleccionada, eliminando esa
	 * seleccion y focus sobre ella.
	 */
	@FXML
	private void cerrarPanelAliado() {
		this.unidadAliadaSeleccionada = null;
		mapaRenderizado.setAliadoEnFoco(null);
		panelAliado.setVisible(false);
		panelAliado.setManaged(false);

		btnAtacar.setDisable(true);
		btnAtacar.setText("Selecciona un Aliado");

		lblAliadoBonus.setText("");
		if (unidadEnemigaSeleccionada != null) {
			actualizarPanelesBonus();
		}
		mapaRenderizado.dibujarMapa(mapa);
	}

	/**
	 * Metodo para cerrar el panel de unidad aliada enemiga, eliminando esa
	 * seleccion y focus sobre ella.
	 */
	@FXML
	private void cerrarPanelEnemigo() {
		this.unidadEnemigaSeleccionada = null;
		mapaRenderizado.setEnemigoEnFoco(null);
		panelEnemigo.setVisible(false);
		panelEnemigo.setManaged(false);

		lblEnemigoBonus.setText("");
		if (unidadAliadaSeleccionada != null) {
			actualizarPanelesBonus();
		}
		mapaRenderizado.dibujarMapa(mapa);
	}

	/**
	 * Metodo encargado de realizar el ataque, llamando a la clase
	 * combatecontrolador para la ejecucion del ataque, cuando se pulsa el boton de
	 * ataque.
	 */
	@FXML
	private void ejecutarAtaque() {
		if (partidaTerminada) {
			return;
		}
		combateControlador.ejecutarAtaque(unidadAliadaSeleccionada, unidadEnemigaSeleccionada);
	}

	/**
	 * Metodo para manejar el boton de Finalizar turno, el cual finalizara el turno
	 * de los heroes que son los unicos que pueden pulsar ese boton ya que las
	 * bestias se manejan por IA
	 */
	@FXML
	private void finalizarTurnoPulsado() {
		if (turnoActual == Faccion.HEROES) {
			finalizarTurno();
		}
	}

	/**
	 * Metodo para alternar entre ver el log de eventos del juego o esconderlo para
	 * que no moleste.
	 */
	@FXML
	private void alternarVisibilidadPanelLog() {
		boolean estaVisible = scrollLog.isVisible();

		if (estaVisible) {
			scrollLog.setVisible(false);
			scrollLog.setManaged(false);

			resizeGripLog.setVisible(false);
			resizeGripLog.setManaged(false);

			btnAlternarLog.setText("▶");
			btnAlternarLog.setStyle(
					"-fx-background-color: rgba(40, 45, 55, 0.9); -fx-text-fill: #ffd700; -fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: rgba(58, 134, 255, 0.4); -fx-border-width: 1.5; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;");
		} else {
			scrollLog.setVisible(true);
			scrollLog.setManaged(true);

			resizeGripLog.setVisible(true);
			resizeGripLog.setManaged(true);

			btnAlternarLog.setText("◀");
			btnAlternarLog.setStyle(
					"-fx-background-color: rgba(40, 45, 55, 0.9); -fx-text-fill: #ffd700; -fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: rgba(58, 134, 255, 0.4); -fx-border-width: 1.5 1.5 1.5 0; -fx-border-radius: 0 4 4 0; -fx-background-radius: 0 4 4 0; -fx-cursor: hand;");
		}
	}

	/**
	 * Metodo para alternar entre ver el log de dados del juego o esconderlo para
	 * que no moleste.
	 */
	@FXML
	public void alternarVisibilidadPanelLogDados() {
		boolean estaVisible = scrollLogDados.isVisible();

		if (estaVisible) {
			scrollLogDados.setVisible(false);
			scrollLogDados.setManaged(false);

			resizeGripLogDados.setVisible(false);
			resizeGripLogDados.setManaged(false);

			btnAlternarLogDados.setText("◀");
			btnAlternarLogDados.setStyle(
					"-fx-background-color: rgba(40, 45, 55, 0.9); -fx-text-fill: #ffd700; -fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: rgba(255, 51, 51, 0.4); -fx-border-width: 1.5; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;");
		} else {
			scrollLogDados.setVisible(true);
			scrollLogDados.setManaged(true);

			resizeGripLogDados.setVisible(true);
			resizeGripLogDados.setManaged(true);

			btnAlternarLogDados.setText("▶");
			btnAlternarLogDados.setStyle(
					"-fx-background-color: rgba(40, 45, 55, 0.9); -fx-text-fill: #ffd700; -fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: rgba(255, 51, 51, 0.4); -fx-border-width: 1.5 0 1.5 1.5; -fx-border-radius: 4 0 0 4; -fx-background-radius: 4 0 0 4; -fx-cursor: hand;");
		}
	}

	/**
	 * Metodo para finalizar el turno. Resetea las variables de ya ha atacado y ya
	 * se ha movido de todas las unides, ejecuta las curaciones, conquistas,
	 * reclutamientos, comprobaciones de victorias, actualiza HUD, lanza turno de
	 * IA, etc
	 */
	@FXML
	public void finalizarTurno() {
		if (partidaTerminada) {
			return;
		}

		for (Personaje pj : mapa.getTodasLasUnidades()) {
			pj.setHaMovidoEsteTurno(false);
			pj.setHaAtacadoEsteTurno(false);
			pj.setHaUsadoMovimientoExtra(false);
		}

		if (turnoActual == Faccion.HEROES) {
			mapa.actualizarTurnosSinMovimientoHobbits();
			combateControlador.ejecutarCuracionFinTurnoHeroes();
			comprobarYAnunciarConquistas(false);

			if (tropasMaximas > 0) {
				reclutamientoControlador.procesarFinDeTurno(Faccion.HEROES, tropasMaximas);
				actualizarHudTropas();
			}

			for (Personaje pj : mapa.getTodasLasUnidades()) {
				pj.setHasidoAtacadoEstaRonda(false);
			}

			registrarAccion("🔥 Las huestes oscuras marchan. Fase de la Inteligencia Artificial. Turno número: "
					+ contadorTurnoBestias, TipoEvento.TURNO);
			turnoActual = Faccion.BESTIAS;
		} else {
			int rondaQueTermina = contadorTurnoHeroes;

			contadorTurnoHeroes++;
			contadorTurnoBestias++;

			List<Personaje> hobbitsConCambioDeteccion = mapa.actualizarDeteccionHobbits();
			if (!hobbitsConCambioDeteccion.isEmpty()) {
				notificarCambiosDeteccion(hobbitsConCambioDeteccion);
			}

			combateControlador.ejecutarCuracionFinTurnoBestias();
			comprobarYAnunciarConquistas(true);

			if (tropasMaximas > 0) {
				reclutamientoControlador.procesarFinDeTurno(Faccion.BESTIAS, tropasMaximas);
				actualizarHudTropas();
			}

			if (controladorVictoria != null && !controladorVictoria.estaPartidaFinalizada()) {
				controladorVictoria.actualizarPuntosPorRonda(mapa);
				actualizarHudPuntuacion();
				ResultadoPartida resultado = controladorVictoria.comprobarVictoriaPorDominio(mapa);
				if (resultado == null) {
					resultado = controladorVictoria.comprobarVictoriaPorLimiteDePuntos();
				}
				if (resultado == null) {
					resultado = controladorVictoria.actualizarProgresoFrodoMonteDelDestino(mapa);
					if (resultado == null) {
						mostrarProgresoFrodoSiCorresponde();
					}
				}
				if (resultado == null) {
					resultado = controladorVictoria.comprobarVictoriaPorTurnosAgotados(rondaQueTermina, mapa);
				}
				if (resultado != null) {
					mostrarFinDePartida(resultado);
					return;
				}
			}

			registrarAccion("🛡️ ¡Las líneas aliadas se reorganizan! Comienza tu fase táctica. Turno número: "
					+ contadorTurnoHeroes, TipoEvento.TURNO);
			turnoActual = Faccion.HEROES;

			GestorSonido.reproducirInicioMiTurno();
		}

		seleccionarUnidad(null);

		// Actualizamos el número de la ronda global de la partida
		lblRondaPartida.setText(String.valueOf(contadorTurnoHeroes));

		if (turnoActual == Faccion.HEROES) {
			lblTurno.setText("HÉROES");
			lblTurno.setStyle("-fx-text-fill: #00ff66; -fx-font-weight: 900; -fx-font-size: 14px;");
		} else {
			lblTurno.setText("BESTIAS");
			lblTurno.setStyle("-fx-text-fill: #ff0055; -fx-font-weight: 900; -fx-font-size: 14px;");
		}
		// ===================================================================

		if (turnoActual == Faccion.BESTIAS) {
			btnFinalizarTurno.setDisable(true);
			btnFinalizarTurno.setText("🛑 Turno del Rival");

			lblEstadoTurno.setVisible(true);
			lblEstadoTurno.setManaged(true);

			iaControlador.ejecutarTurnoIA();

		} else {
			btnFinalizarTurno.setDisable(false);
			btnFinalizarTurno.setText("⏳ Finalizar Turno");

			lblEstadoTurno.setVisible(false);
			lblEstadoTurno.setManaged(false);
		}
		if (ciudadVisibleEnHud != null) {
			actualizarEstadoConquistaHUD(ciudadVisibleEnHud);
		}

		mapaRenderizado.dibujarMapa(mapa);
	}

	/**
	 * Controla la selección centralizada de unidades en la interfaz táctica. Carga
	 * dinámicamente los iconos PNG de 16px según la facción e inyecta los textos.
	 *
	 * @param personaje the personaje
	 */
	public void seleccionarUnidad(Personaje personaje) {
		if (personaje == null) {
			unidadAliadaSeleccionada = null;
			unidadEnemigaSeleccionada = null;
			mapaRenderizado.setAliadoEnFoco(null);
			mapaRenderizado.setEnemigoEnFoco(null);

			lblAliadoNombre.setGraphic(null);
			lblEnemigoNombre.setGraphic(null);

			panelAliado.setVisible(false);
			panelAliado.setManaged(false);
			panelEnemigo.setVisible(false);
			panelEnemigo.setManaged(false);

			lblAliadoBonus.setText("");
			lblEnemigoBonus.setText("");

			return;
		}

		boolean esHeroe = personaje.getFaccion() == Faccion.HEROES;

		if (esHeroe) {
			this.unidadAliadaSeleccionada = personaje;
			mapaRenderizado.setAliadoEnFoco(personaje);

			asignarIconoPNG(lblAliadoNombre, personaje);
			lblAliadoNombre.setText(" Nombre: " + personaje.getNombre());
			lblAliadoTipo.setText("Clase: " + personaje.getClass().getSimpleName());

			if (personaje instanceof Hobbit) {
				actualizarEstadoVisibilidadHobbit(personaje);
				lblAliadoArmadura.setText("Armadura: " + personaje.getArmadura());

			} else {
				lblAliadoArmadura.setText("Armadura: " + personaje.getArmadura());
				lblAliadoArmadura.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
				lblesVisible.setVisible(false);
				lblesVisible.setManaged(false);
			}

			lblAliadoVida.setText("HP: " + personaje.getVida() + "/" + personaje.getVidaMaxima());

			double porcentaje = (double) personaje.getVida() / personaje.getVidaMaxima();
			barAliadoVida.setProgress(porcentaje);

			panelAliado.setVisible(true);
			panelAliado.setManaged(true);

			if (unidadEnemigaSeleccionada != null) {
				panelEnemigo.setVisible(true);
				panelEnemigo.setManaged(true);
				actualizarBotonAtacar();
			}
		} else {
			this.unidadEnemigaSeleccionada = personaje;
			mapaRenderizado.setEnemigoEnFoco(personaje);

			asignarIconoPNG(lblEnemigoNombre, personaje);
			lblEnemigoNombre.setText(" Nombre: " + personaje.getNombre());

			lblEnemigoTipo.setText("Clase: " + personaje.getClass().getSimpleName());
			lblEnemigoArmadura.setText("Armadura: " + personaje.getArmadura());
			lblEnemigoVida.setText("HP: " + personaje.getVida() + "/" + personaje.getVidaMaxima());

			double porcentaje = (double) personaje.getVida() / personaje.getVidaMaxima();
			barEnemigoVida.setProgress(porcentaje);

			if (unidadAliadaSeleccionada != null) {
				actualizarBotonAtacar();

			} else {
				btnAtacar.setDisable(true);
				btnAtacar.setText("Selecciona un Aliado");
			}

			panelEnemigo.setVisible(true);
			panelEnemigo.setManaged(true);
		}

		actualizarPanelesBonus();
	}

	/**
	 * Metodo para manejar el redimensionado del los paneles de los logs de forma
	 * manual arrastrando con el raton.
	 */
	private void configurarRedimensionadoLogs() {
		// Panel izquierdo (log de eventos): crece hacia abajo-derecha.
		resizeGripLog.setOnMousePressed(this::iniciarArrastreResize);
		resizeGripLog.setOnMouseDragged(evento -> {
			double deltaX = evento.getSceneX() - dragInicioX;
			double deltaY = evento.getSceneY() - dragInicioY;

			double nuevoAncho = clamp(anchoInicioArrastre + deltaX, LOG_ANCHO_MINIMO, LOG_ANCHO_MAXIMO);
			double nuevoAlto = clamp(altoInicioArrastre + deltaY, LOG_ALTO_MINIMO, LOG_ALTO_MAXIMO);

			scrollLog.setPrefWidth(nuevoAncho);
			scrollLog.setPrefHeight(nuevoAlto);
		});

		// Panel derecho (log de dados): el grip está a la izquierda, así que arrastrar
		// hacia la izquierda
		resizeGripLogDados.setOnMousePressed(this::iniciarArrastreResize);
		resizeGripLogDados.setOnMouseDragged(evento -> {
			double deltaX = evento.getSceneX() - dragInicioX;
			double deltaY = evento.getSceneY() - dragInicioY;

			double nuevoAncho = clamp(anchoInicioArrastre - deltaX, LOG_ANCHO_MINIMO, LOG_ANCHO_MAXIMO);
			double nuevoAlto = clamp(altoInicioArrastre + deltaY, LOG_ALTO_MINIMO, LOG_ALTO_MAXIMO);

			scrollLogDados.setPrefWidth(nuevoAncho);
			scrollLogDados.setPrefHeight(nuevoAlto);
		});
	}

	/**
	 * Metodo clamp con el que limitaremos un valor a un tamaño maximo y minimo.
	 * Esto lo usaremos para limitar los tamaños de los paneles logs
	 *
	 * @param valor  valor que se quiere limitar
	 * @param minimo valor minimo
	 * @param maximo valor maximo
	 * @return the double
	 */
	private double clamp(double valor, double minimo, double maximo) {
		return Math.max(minimo, Math.min(maximo, valor));
	}

	/**
	 * Metodo que cuando se inicia el evento de arrastre, se quede registrado el
	 * tamaño inicial.
	 *
	 * @param evento the evento
	 */
	private void iniciarArrastreResize(MouseEvent evento) {
		dragInicioX = evento.getSceneX();
		dragInicioY = evento.getSceneY();

		if (evento.getSource() == resizeGripLog) {
			anchoInicioArrastre = scrollLog.getPrefWidth();
			altoInicioArrastre = scrollLog.getPrefHeight();
		} else {
			anchoInicioArrastre = scrollLogDados.getPrefWidth();
			altoInicioArrastre = scrollLogDados.getPrefHeight();
		}
	}

	/**
	 * Metodo usado para arrastrar el panel pulsando o haciendo click en su
	 * cabecera. Este metodo se usara para arrastrar los paneles de aliado/enemigo y
	 * el de ciudad.
	 *
	 * @param cabecera the cabecera
	 * @param panel    the panel
	 */
	private void hacerArrastrable(Node cabecera, Node panel) {
		if (cabecera == null || panel == null) {
			return;
		}
		final double[] offset = new double[2];
		cabecera.setCursor(Cursor.MOVE);
		cabecera.setOnMousePressed(evento -> {
			offset[0] = evento.getSceneX() - panel.getTranslateX();
			offset[1] = evento.getSceneY() - panel.getTranslateY();
		});

		cabecera.setOnMouseDragged(evento -> {
			panel.setTranslateX(evento.getSceneX() - offset[0]);
			panel.setTranslateY(evento.getSceneY() - offset[1]);
			evento.consume();
		});
	}

	/**
	 * Registra una accion/evento en el log.
	 *
	 * @param mensaje the mensaje
	 * @param tipo    the tipo
	 */
	public void registrarAccion(String mensaje, TipoEvento tipo) {
		registrarAccion(mensaje, tipo, null);
	}

	/**
	 * Registra una accion en el log, añadiendo prefijo, color de texto en funcion
	 * del evento/faccion. El log se ira desplazando hacia abajo de forma
	 * automatica.
	 *
	 * @param mensaje      the mensaje
	 * @param tipo         the tipo
	 * @param faccionColor the faccion color
	 */
	public void registrarAccion(String mensaje, TipoEvento tipo, Faccion faccionColor) {
		String prefijo = "";

		if (tipo == TipoEvento.TURNO || tipo == TipoEvento.CONQUISTA) {
			prefijo = "📜 [SISTEMA]: ";
		} else {
			boolean esHeroe = (turnoActual == Faccion.HEROES);
			if (esHeroe) {
				prefijo = "Turno " + contadorTurnoHeroes + " -> (Humano) Heroes: ";
			} else {
				prefijo = "Turno " + contadorTurnoBestias + " -> (IA) Bestias: ";
			}
		}

		Text nodoTexto = new Text(prefijo + mensaje + "\n");
		String colorHex = "#ffffff";

		switch (tipo) {
		case MOVIMIENTO -> {
			colorHex = (turnoActual == Faccion.HEROES) ? "#b0b0b0" : "#d67d38";
		}
		case ATAQUE -> {
			colorHex = (turnoActual == Faccion.HEROES) ? "#3a86ff" : "#ff3333";
		}
		case MUERTE -> {
			colorHex = (turnoActual == Faccion.HEROES) ? "#1a4d96" : "#80001a";
		}
		case CONQUISTA -> {
			Faccion faccionParaColor = (faccionColor != null) ? faccionColor : turnoActual;
			colorHex = (faccionParaColor == Faccion.HEROES) ? "#00f0ff" : "#ff8080";
		}
		case TURNO -> {
			colorHex = "#ffd700";
		}
		case DETECCION -> {
			colorHex = ultimaDeteccionPositiva ? "#ff8c00" : "#00e676";
		}
		case CURACION -> {
			colorHex = "#7CFC00";
		}
		case CREACION -> {
			Faccion faccionParaColor = (faccionColor != null) ? faccionColor : turnoActual;
			colorHex = (faccionParaColor == Faccion.HEROES) ? "#4fd6ff" : "#ff7a33";
		}
		}
		nodoTexto.setStyle("-fx-fill: " + colorHex + "; " + "-fx-font-family: 'Consolas', 'Courier New', monospace; "
				+ "-fx-font-size: 15px; " + "-fx-font-weight: bold; "
				+ "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.9), 3, 1, 0, 0);");

		flowLog.getChildren().add(nodoTexto);

		scrollLog.layout();
		scrollLog.setVvalue(1.0);
	}

	/**
	 * Comprobar condiciones de victoria inmediatas.
	 */
	public void comprobarCondicionesDeVictoriaInmediatas() {
		if (controladorVictoria == null || partidaTerminada || controladorVictoria.estaPartidaFinalizada()) {
			return;
		}
		ResultadoPartida resultado = controladorVictoria.comprobarCondicionesInmediatas(mapa);
		if (resultado != null) {
			mostrarFinDePartida(resultado);
		}
	}

	/**
	 * Esta partida acabada.
	 *
	 * @return true, if successful
	 */
	public boolean estaPartidaAcabada() {
		return partidaTerminada;
	}

	/**
	 * Metodo para actualizar el hud de puntuaciones de ambas facciones.
	 */
	public void actualizarHudPuntuacion() {
		if (controladorVictoria == null) {
			return;
		}
		int limitePuntos = controladorVictoria.getConfiguracion().getPuntosMaximos();
		String sufijo = limitePuntos > 0 ? ("/" + limitePuntos) : "";
		lblPuntosHeroes.setText(controladorVictoria.getPuntosHeroes() + sufijo);
		lblPuntosBestias.setText(controladorVictoria.getPuntosBestias() + sufijo);
	}

	/**
	 * Metodo para actualizar el hud de cantidad de unidades Tropa de cada faccion.
	 */
	public void actualizarHudTropas() {
		if (tropasMaximas <= 0 || reclutamientoControlador == null) {
			return;
		}
		lblTropasHeroes.setText(reclutamientoControlador.contarTropasVivas(Faccion.HEROES) + "/" + tropasMaximas);
		lblTropasBestias.setText(reclutamientoControlador.contarTropasVivas(Faccion.BESTIAS) + "/" + tropasMaximas);
	}

	/**
	 * Metodo para mostrar el progreso de frodo en el monte del destino si esta la
	 * condicion de victoria activa. Muestra un anuncio en grande contando los
	 * turnos que quedan.
	 */
	private void mostrarProgresoFrodoSiCorresponde() {
		if (controladorVictoria == null || !controladorVictoria.getConfiguracion()
				.estaActiva(TipoCondicionVictoria.FRODO_LLEGA_AL_MONTE_DEL_DESTINO)) {
			return;
		}
		Personaje frodo = mapa.getTodasLasUnidades().stream()
				.filter(pj -> pj != null && pj.estaVivo() && "Frodo".equalsIgnoreCase(pj.getNombre())).findFirst()
				.orElse(null);
		if (frodo == null) {
			return;
		}
		Ciudad ciudadDeFrodo = mapa.buscarCiudadesPorCasilla(frodo.getX(), frodo.getY());
		if (ciudadDeFrodo == null || !"Monte del Destino".equalsIgnoreCase(ciudadDeFrodo.getNombreCiudad())) {
			return;
		}
		int rondas = controladorVictoria.getRondasFrodoEnMonteSinAtaque();
		int turnosRestantes = Math.max(0, ControladorVictoria.RONDAS_NECESARIAS_EN_MONTE_DEL_DESTINO - rondas);
		String texto = "💍 Frodo resiste en el Monte del Destino: faltan " + turnosRestantes
				+ (turnosRestantes == 1 ? " turno" : " turnos") + " para destruir el Anillo";
		anuncioGrande.mostrarConquistas(List.of(new LineaAnuncio(texto, Faccion.HEROES)));
	}

	/**
	 * Metodo para mostrar el anuncio por defecto de fin de partida.
	 *
	 * @param resultado the resultado
	 */
	private void mostrarFinDePartida(ResultadoPartida resultado) {
		mostrarFinDePartida(resultado, null);
	}

	/**
	 * Metodo para marcar la partida como finalizada. Mostrar anuncio de fin de
	 * partida con el texto personalizado. Actualiza la interfaz, reproduce sonido,
	 *
	 * @param resultado           the resultado
	 * @param tituloPersonalizado the titulo personalizado
	 */
	private void mostrarFinDePartida(ResultadoPartida resultado, String tituloPersonalizado) {
		partidaTerminada = true;

		btnFinalizarTurno.setDisable(true);
		btnFinalizarTurno.setText("🏁 Partida finalizada");

		lblEstadoTurno.setVisible(false);
		lblEstadoTurno.setManaged(false);

		String titulo;

		if (tituloPersonalizado != null) {
			titulo = tituloPersonalizado;
		} else if (resultado.esEmpate()) {
			titulo = "⚖️ ¡EMPATE!";
		} else if (resultado.getGanador() == Faccion.HEROES) {
			titulo = "🏆 ¡VICTORIA DE LOS HÉROES!";
		} else {
			titulo = "🏆 ¡VICTORIA DE LAS BESTIAS!";
		}

		registrarAccion(titulo + " " + resultado.getMotivo(), TipoEvento.TURNO);

		anuncioGrande.mostrarResultado(titulo + "\n" + resultado.getMotivo(), resultado.getGanador(),
				resultado.esEmpate());

		GestorSonido.detenerMusicaFondo();
		if (!resultado.esEmpate() && resultado.getGanador() == Faccion.HEROES) {
			GestorSonido.reproducirVictoria();
		} else {
			GestorSonido.reproducirDerrota();
		}

		mapaRenderizado.dibujarMapa(mapa);

		ButtonType btnVolverMenu = new ButtonType("🏠 Volver al menú principal");
		ButtonType btnSalirJuego = new ButtonType("🚪 Salir del juego");

		Alert alerta = new Alert(Alert.AlertType.INFORMATION);
		alerta.setTitle("¡Fin de la partida!");
		alerta.setHeaderText(titulo);
		alerta.setContentText(resultado.getMotivo());
		alerta.getButtonTypes().setAll(btnVolverMenu, btnSalirJuego);

		Optional<ButtonType> eleccion = alerta.showAndWait();
		if (eleccion.isPresent() && eleccion.get() == btnVolverMenu) {
			volverAlMenuPrincipal();
		} else {
			Platform.exit();
			System.exit(0);
		}
	}

	/**
	 * Metodo para mostrar el menu de pausa. Ademas gestiona las diferenctes
	 * opciones.
	 */
	private void mostrarMenuPausa() {
		if (partidaTerminada) {
			return;
		}

		ButtonType btnContinuar = new ButtonType("▶️ Continuar", ButtonData.CANCEL_CLOSE);
		String textoMusica = GestorSonido.estaMusicaSilenciada() ? "🔇 Activar música" : "🔊 Silenciar música";
		String textoEfectos = GestorSonido.estaEfectosSilenciados() ? "🔇 Activar efectos" : "🔊 Silenciar efectos";
		ButtonType btnMusica = new ButtonType(textoMusica, ButtonData.OTHER);
		ButtonType btnEfectos = new ButtonType(textoEfectos, ButtonData.OTHER);
		ButtonType btnRendirse = new ButtonType("X Rendirse X", ButtonData.OTHER);

		Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
		alerta.setTitle("Partida en pausa");
		alerta.setHeaderText("Menu Pausa");
		alerta.setContentText(null);
		alerta.getButtonTypes().setAll(btnContinuar, btnMusica, btnEfectos, btnRendirse);

		Optional<ButtonType> eleccion = alerta.showAndWait();
		if (eleccion.isPresent() && eleccion.get() == btnRendirse) {
			confirmarRendicion();
		} else if (eleccion.isPresent() && eleccion.get() == btnMusica) {
			GestorSonido.alternarMusicaSilenciada();
			mostrarMenuPausa();
		} else if (eleccion.isPresent() && eleccion.get() == btnEfectos) {
			GestorSonido.alternarEfectosSilenciados();
			mostrarMenuPausa();
		}
	}

	/**
	 * Metodo para mostrar un dialogo y confirmar la rendicion de tu bando.
	 */
	private void confirmarRendicion() {
		ButtonType btnSi = new ButtonType("✅ Sí, rendirme");
		ButtonType btnNo = new ButtonType("↩️ No, volver");

		Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
		confirmacion.setTitle("Confirmar rendición");
		confirmacion.setHeaderText("¿Seguro que deseas rendirte?");
		confirmacion.setContentText("Esta acción no se puede deshacer: las Bestias ganarán la partida inmediatamente.");
		confirmacion.getButtonTypes().setAll(btnNo, btnSi);

		Optional<ButtonType> respuesta = confirmacion.showAndWait();
		if (respuesta.isPresent() && respuesta.get() == btnSi) {
			rendirse();
		} else {
			mostrarMenuPausa();
		}
	}

	/**
	 * Metodo para ejecutar la rendicion de la faccion Heroes (la controlada por el
	 * humano) y finalizando con la victoria de las bestias
	 */
	private void rendirse() {
		if (partidaTerminada || controladorVictoria == null || controladorVictoria.estaPartidaFinalizada()) {
			return;
		}
		ResultadoPartida resultado = controladorVictoria.rendirse(Faccion.HEROES);

		if (resultado == null) {
			return;
		}
		registrarAccion("🏳️ Te has rendido. Las Bestias se alzan con la victoria.", TipoEvento.TURNO);
		mostrarFinDePartida(resultado, "😔 Derrota, te has rendido...");
	}

	/**
	 * Metodo para comprobar y anunciar las conquistas de las ciudades. Detecta las
	 * ciudades que han cambiado de faccion. Ademas lanza los sonidos
	 * correspondientes.
	 *
	 * @param actualizarContadores parametro que en caso de ser true le indica al
	 *                             metodo
	 */
	private void comprobarYAnunciarConquistas(boolean actualizarContadores) {
		Set<Ciudad> ciudadesHeroesAntes = new HashSet<>();
		if (actualizarContadores) {
			for (Ciudad ciudad : mapa.getCiudades()) {
				if (ciudad.esCiudadControladaPorHeroes()) {
					ciudadesHeroesAntes.add(ciudad);
				}
			}
		}

		List<Ciudad> ciudadesConquistadas = actualizarContadores ? mapa.actualizarConquistas() : new ArrayList<>();
		List<LineaAnuncio> lineasAnuncioGrande = new ArrayList<>();

		for (Ciudad ciudadConquistada : ciudadesConquistadas) {
			boolean esHeroes = ciudadConquistada.esCiudadControladaPorHeroes();
			String emoji = esHeroes ? "🏰" : "💀";
			String bando = esHeroes ? "los Heroes" : "las Bestias";
			String mensaje = ciudadConquistada.getNombreCiudad() + " ha sido capturada por " + bando + "!";
			Faccion faccionMensaje = esHeroes ? Faccion.HEROES : Faccion.BESTIAS;
			registrarAccion(emoji + " ¡" + mensaje, TipoEvento.CONQUISTA, esHeroes ? Faccion.HEROES : Faccion.BESTIAS);
			lineasAnuncioGrande.add(new LineaAnuncio(emoji + " ¡" + mensaje, faccionMensaje));

			if (!esHeroes && ciudadesHeroesAntes.contains(ciudadConquistada)) {
				GestorSonido.reproducirCiudadPropiaPerdida();
			}
		}

		if (actualizarContadores) {
			for (Ciudad ciudad : mapa.getCiudades()) {
				if (ciudadesConquistadas.contains(ciudad)) {
					continue;
				}
				EstadoConquista estado = ciudad.getEstadoConquistaVisible();
				if (estado.getTipo() == TipoEstadoConquista.EN_PROGRESO) {
					String nombreFaccion = (estado.getFaccion() == Faccion.HEROES) ? "los Heroes" : "las Bestias";
					String emojiProgreso = (estado.getFaccion() == Faccion.HEROES) ? "🔷" : "🔻";
					String turnosTexto = estado.getTurnosConquistando() + "/" + estado.getTurnosNecesarios()
							+ " turnos restantes.";
					String mensaje = ciudad.getNombreCiudad() + " está siendo capturada por " + nombreFaccion + ", "
							+ turnosTexto;
					registrarAccion(emojiProgreso + " " + mensaje, TipoEvento.CONQUISTA, estado.getFaccion());
					String mensajeBanner = ciudad.getNombreCiudad() + " está siendo capturada por " + nombreFaccion
							+ ",\n" + turnosTexto;
					lineasAnuncioGrande.add(new LineaAnuncio(emojiProgreso + " " + mensajeBanner, estado.getFaccion()));

					if (estado.getFaccion() == Faccion.BESTIAS
							&& ciudad.getControlSobreCiudad() == ControlCiudad.HEROES) {
						GestorSonido.reproducirCiudadPropiaAsediada();
					}
				}
			}
		}
		anuncioGrande.mostrarConquistas(lineasAnuncioGrande);
	}

	/**
	 * Metodo para volver al menu principal, detiene la musica y carga la pantalla
	 * del menu principal.
	 */
	public void volverAlMenuPrincipal() {
		GestorSonido.detenerMusicaFondo();
		try {
			Stage stageActual = (Stage) btnFinalizarTurno.getScene().getWindow();
			Parent raizMenuPrincipal = FXMLLoader
					.load(getClass().getResource("/menuPrincipalRecursos/menu_principal.fxml"));

			stageActual.setMaximized(false);
			stageActual.setScene(new Scene(raizMenuPrincipal));
			stageActual.setTitle("Manuel J. Ramirez Lozano -> Juego de combate ESDLA");
			stageActual.setResizable(false);
			stageActual.sizeToScene();

		} catch (IOException | NullPointerException e) {
			System.err.println("No se ha podido volver al menú principal: " + e.getMessage());
		}
	}

	/**
	 * Metodo para crear el titulo del log de los dados.
	 */
	public void registrarTituloLogDados() {
		Text nodoTitulo = new Text("🎲 Tiradas de dados:\n");
		nodoTitulo.setStyle("-fx-fill: #ffd700; " + "-fx-font-family: 'Consolas', 'Courier New', monospace; "
				+ "-fx-font-size: 15px; " + "-fx-font-weight: bold; "
				+ "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.9), 3, 1, 0, 0);");

		flowLogDados.getChildren().add(nodoTitulo);
		scrollLogDados.layout();
		scrollLogDados.setVvalue(1.0);
	}

	/**
	 * Registra en el log de dados una linea de tirada de dados, dandole el color en
	 * funcion de la faccion que roleó los dados.
	 *
	 * @param mensaje the mensaje
	 * @param faccion the faccion
	 */
	public void registrarTiradaDado(String mensaje, Faccion faccion) {
		if (mensaje == null) {
			return;
		}

		Text nodoTexto = new Text(mensaje + "\n");
		String colorHex = (faccion == Faccion.HEROES) ? "#3a86ff" : "#ff3333";

		nodoTexto.setStyle("-fx-fill: " + colorHex + "; " + "-fx-font-family: 'Consolas', 'Courier New', monospace; "
				+ "-fx-font-size: 15px; " + "-fx-font-weight: bold; "
				+ "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.9), 3, 1, 0, 0);");

		flowLogDados.getChildren().add(nodoTexto);
		scrollLogDados.layout();
		scrollLogDados.setVvalue(1.0);
	}

	/**
	 * Metodo para registrar en el log de eventos los cambios en el estado de
	 * deteccion de los hobbits. Ademas reproduce un sonido para notificar ese
	 * evento.
	 *
	 * @param hobbitsConCambio the hobbits con cambio
	 */
	public void notificarCambiosDeteccion(List<Personaje> hobbitsConCambio) {
		boolean alertaSonoraYaReproducida = false;

		for (Personaje pj : hobbitsConCambio) {
			if (pj.esDetectadoPorEnemigo()) {
				ultimaDeteccionPositiva = true;
				registrarAccion("🔴 ¡" + pj.getNombre() + " ha sido LOCALIZADO por el enemigo! Todo Mordor lo sabe.",
						TipoEvento.DETECCION);
				if (!alertaSonoraYaReproducida && esPersonajeVigilado(pj)) {
					GestorSonido.reproducirFrodoDetectado();
					alertaSonoraYaReproducida = true;
				}
			} else {
				ultimaDeteccionPositiva = false;
				registrarAccion("🟢 " + pj.getNombre() + " ha recuperado la INVISIBILIDAD tras mantenerse inmóvil.",
						TipoEvento.DETECCION);
			}
			if (unidadAliadaSeleccionada != null && unidadAliadaSeleccionada == pj) {
				seleccionarUnidad(pj);
			}
		}
	}

	/**
	 * Metodo para actualizar o refrescar la barra de vida de los
	 * personajes/unidades
	 */
	public void refrescarPanelVida() {
		if (unidadAliadaSeleccionada != null) {
			lblAliadoVida.setText(
					"HP: " + unidadAliadaSeleccionada.getVida() + "/" + unidadAliadaSeleccionada.getVidaMaxima());
			double porcentajeVida = (double) unidadAliadaSeleccionada.getVida()
					/ unidadAliadaSeleccionada.getVidaMaxima();
			barAliadoVida.setProgress(porcentajeVida);
		}
		if (unidadEnemigaSeleccionada != null && unidadEnemigaSeleccionada.estaVivo()) {
			lblEnemigoVida.setText(
					"HP: " + unidadEnemigaSeleccionada.getVida() + "/" + unidadEnemigaSeleccionada.getVidaMaxima());
			double porcentajeVida = (double) unidadEnemigaSeleccionada.getVida()
					/ unidadEnemigaSeleccionada.getVidaMaxima();
			barEnemigoVida.setProgress(porcentajeVida);
		}
	}

	/**
	 * Metodo para actualizar el estado de deteccion de los hobbits, en el panel de
	 * seleccion aliado.
	 *
	 * @param pj the pj
	 */
	public void actualizarEstadoVisibilidadHobbit(Personaje pj) {
		String estadoHobbit = pj.esDetectadoPorEnemigo() ? "⚠️ DETECTADO" : "👁️ INVISIBLE";

		lblesVisible.setText(" | " + estadoHobbit);
		lblesVisible.setStyle(
				pj.esDetectadoPorEnemigo() ? "-fx-text-fill: #ff6644; -fx-font-size: 13px; -fx-font-weight: bold;"
						: "-fx-text-fill: #aaffcc; -fx-font-size: 13px; -fx-font-weight: bold;");

		lblesVisible.setVisible(true);
		lblesVisible.setManaged(true);
	}

	/**
	 * Actualiza los textos de los paneles de seleccion aliado y enemigo cuando
	 * tienes seleccionados ambos, mostrando los bonus/penalizaciones entre ambas
	 * unidades.
	 */
	public void actualizarPanelesBonus() {
		if (turnoActual != Faccion.HEROES) {
			lblAliadoBonus.setText("");
			lblEnemigoBonus.setText("");
			actualizarTurnoInmovilAliado();
			return;
		}
		if (unidadAliadaSeleccionada != null) {
			List<String> lineasAliado = new ArrayList<>();
			String condicionesGenerales = combateControlador.construirTextoBonus(unidadAliadaSeleccionada);
			if (!condicionesGenerales.isEmpty()) {
				lineasAliado.add(condicionesGenerales);
			}

			String bonusAtaque = combateControlador.construirTextoBonusAtaque(unidadAliadaSeleccionada,
					unidadEnemigaSeleccionada);
			if (bonusAtaque != null) {
				lineasAliado.add(bonusAtaque);
			}
			lblAliadoBonus.setText(String.join("\n", lineasAliado));
		}
		if (unidadEnemigaSeleccionada != null) {
			List<String> lineasEnemigo = new ArrayList<>();
			String condicionesGenerales = combateControlador.construirTextoBonus(unidadEnemigaSeleccionada);

			if (!condicionesGenerales.isBlank()) {
				lineasEnemigo.add(condicionesGenerales);
			}
			String bonusDefensa = combateControlador.construirTextoBonusDefensa(unidadEnemigaSeleccionada,
					unidadAliadaSeleccionada);
			if (bonusDefensa != null) {
				lineasEnemigo.add(bonusDefensa);
			}
			lblEnemigoBonus.setText(String.join("\n", lineasEnemigo));
		}

		actualizarTurnoInmovilAliado();

	}

	/**
	 * Metodo usado para actualizar, en el panel de aliados, los turnos restantes
	 * que debe estar el hobbit inmovil para recuperar la invisibilidad. Si se
	 * encuentra en estado invisible, este dato no se mostrara
	 */
	private void actualizarTurnoInmovilAliado() {
		boolean mostrar = false;
		String texto = "";

		if (turnoActual == Faccion.HEROES && unidadAliadaSeleccionada instanceof Hobbit
				&& unidadAliadaSeleccionada.esDetectadoPorEnemigo()) {
			int turnoInmovilRestante = Math.max(0, 2 - unidadAliadaSeleccionada.getTurnosInmovil());
			texto = "⏳ Turno inmóvil restante: " + turnoInmovilRestante;
			mostrar = true;
		}
		lblAliadoTurnoInmovil.setText(texto);
		lblAliadoTurnoInmovil.setVisible(true);
		lblAliadoTurnoInmovil.setManaged(mostrar);
	}

	/**
	 * Metodo para refrescar la lista de unidades que se encuenta en una ciudad
	 * (guarnicion)
	 */
	public void refrescarGuarnicionVisible() {
		refrescarGuarnicionVisible(ciudadVisibleEnHud);
	}

	/**
	 * Metodo para refrescar la lista de unidades que se encuenta en una ciudad
	 * (guarnicion), mostrandolo en el HUD de la ciudad indicada y ocultando el
	 * subpanel de guarnicion si no hay ninguna unidad dentro.
	 *
	 * @param ciudad the ciudad
	 */
	public void refrescarGuarnicionVisible(Ciudad ciudad) {
		if (ciudad == null) {
			return;
		}
		ciudadVisibleEnHud = ciudad;

		listGuarnicionUrbana.getItems().clear();
		if (ciudad.tieneUnidadesDentro()) {
			listGuarnicionUrbana.getItems().addAll(ciudad.getUnidadesDentro());
		}

		lblCiudadNombre.setText("Ciudad: " + ciudad.getNombreCiudad());
		actualizarEstadoConquistaHUD(ciudad);

		boolean hayAlgoQueMostrar = ciudad.tieneUnidadesDentro() || vboxReclutamiento.isVisible();
		subPanelCiudad.setVisible(hayAlgoQueMostrar);
		subPanelCiudad.setManaged(hayAlgoQueMostrar);

		if (!hayAlgoQueMostrar) {
			ciudadVisibleEnHud = null;
		}
	}

	/**
	 * Metodo para actualizar el HUD del estado de las conquista de ciudades,
	 * mostrando si esta en disputa, en reposo o en progreso de conquista. En este
	 * ultimo caso tambien mostrara los turnos restantes para ser conquistadas.
	 *
	 * @param ciudad the ciudad
	 */
	public void actualizarEstadoConquistaHUD(Ciudad ciudad) {
		actualizarPanelReclutamiento(ciudad);

		if (ciudad == null) {
			lblCiudadEstadoConquista.setText("");
			return;
		}
		String faccionTexto = "Neutral";
		if (ciudad.getControlSobreCiudad() != null) {
			faccionTexto = ciudad.getControlSobreCiudad().toString();
		}
		lblCiudadControl.setText("Facción: " + faccionTexto);
		lblInfoFrontera.setText("Soberanía: Region de: " + ciudad.getNombreCiudad() + " (" + faccionTexto + ")");

		EstadoConquista estado = ciudad.getEstadoConquistaVisible();

		switch (estado.getTipo()) {
		case DISPUTA:
			lblCiudadEstadoConquista.setText("⚔️ En disputa");
			lblCiudadEstadoConquista.setStyle("-fx-text-fill: #ffaa00; -fx-font-weight: bold; -fx-font-size: 13px;");
			break;

		case EN_PROGRESO:
			int turnosConquistando = estado.getTurnosConquistando();
			int turnosNecesarios = estado.getTurnosNecesarios();
			int turnosRestantes = Math.max(0, turnosNecesarios - turnosConquistando);
			String nombreFaccion = (estado.getFaccion() == Faccion.HEROES) ? "Heroes" : "Bestias";
			String colorHex = (estado.getFaccion() == Faccion.HEROES) ? "#3a86ff" : "#ff3333";
			lblCiudadEstadoConquista.setText("⏳ Conquistando (" + nombreFaccion + "): turno " + turnosConquistando + "/"
					+ turnosNecesarios + " (quedan " + turnosRestantes + ")");
			lblCiudadEstadoConquista
					.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-weight: bold; -fx-font-size: 13px;");
			break;

		case REPOSO:

		default:
			lblCiudadEstadoConquista.setText("");
			break;
		}

	}

	/**
	 * Metodo que gestiona el reclutamiento de una ciudad indicada. Indica si hay
	 * reclutamiento activo y muestra su progreso en caso afirmativo. Si es nuestro
	 * turno (Heroes) y la ciudad Heroe tiene opcion de reclutamiento disponible,
	 * nos mostrará el boton para reclutar. Si la ciudad es enemiga nos dara la
	 * opcion de visualidad que unidades pueden reclutar las Bestias en esa ciudad.
	 *
	 * @param ciudad the ciudad
	 */
	public void actualizarPanelReclutamiento(Ciudad ciudad) {
		if (vboxReclutamiento == null) {
			return;
		}
		vboxReclutamiento.getChildren().clear();

		if (ciudad == null || tropasMaximas <= 0 || reclutamientoControlador == null) {
			vboxReclutamiento.setVisible(false);
			vboxReclutamiento.setManaged(false);
			return;
		}

		Faccion faccionCiudad;
		if (ciudad.getControlSobreCiudad() == ControlCiudad.HEROES) {
			faccionCiudad = Faccion.HEROES;

		} else if (ciudad.getControlSobreCiudad() == ControlCiudad.BESTIAS) {
			faccionCiudad = Faccion.BESTIAS;
		} else {
			faccionCiudad = null;
		}

		if (faccionCiudad == null) {
			vboxReclutamiento.setVisible(false);
			vboxReclutamiento.setManaged(false);
			return;
		}

		vboxReclutamiento.setVisible(true);
		vboxReclutamiento.setManaged(true);

		if (ciudad.tieneReclutamientoActivo()) {
			OrdenReclutamiento orden = ciudad.getReclutamientoActivo();
			int turnosPasados = orden.getTurnosCreacionTotales() - orden.getTurnosCreacionRestantes();
			Label lblProgreso = new Label("🛠️ Reclutando " + orden.getTropa().getNombre() + ": turno " + turnosPasados
					+ "/" + orden.getTurnosCreacionTotales());
			lblProgreso.setWrapText(true);
			lblProgreso.setMaxWidth(290);
			lblProgreso.setStyle("-fx-text-fill: #ffe27a; -fx-font-size: 12px; -fx-font-weight: bold;");
			vboxReclutamiento.getChildren().add(lblProgreso);
			return;
		}
		boolean esCiudadPropia = faccionCiudad == Faccion.HEROES;
		boolean esMiTurnoYMiCiudad = esCiudadPropia && turnoActual == Faccion.HEROES;

		if (!esMiTurnoYMiCiudad) {
			if (!esCiudadPropia) {
				mostrarReclutamientoEnemigoSoloConsulta(ciudad, faccionCiudad);
			} else {
				vboxReclutamiento.setVisible(false);
				vboxReclutamiento.setManaged(false);
			}
			return;
		}

		List<Personaje> tropasDisponibles = reclutamientoControlador.obtenerTropasReclutablesEnCiudad(todasLasTropas,
				ciudad, faccionCiudad);
		if (tropasDisponibles.isEmpty()) {
			vboxReclutamiento.setVisible(false);
			vboxReclutamiento.setManaged(false);
			return;
		}

		Label lblTitulo = new Label("Reclutar Tropa: ");
		lblTitulo.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 12px; -fx-font-weight: bold;");
		vboxReclutamiento.getChildren().add(lblTitulo);

		boolean hayHueco = reclutamientoControlador.contarTropasVivas(faccionCiudad) < tropasMaximas;

		for (Personaje tropa : tropasDisponibles) {
			Button btnReclutarTropa = new Button(
					"➕ " + tropa.getNombre() + " (" + tropa.getTurnosCreacion() + " turnos)");
			btnReclutarTropa.setMaxWidth(Double.MAX_VALUE);
			btnReclutarTropa.setStyle(
					"-fx-background-color: rgba(58,134,255,0.25); -fx-text-fill: #ffd700; -fx-font-size: 12px; -fx-border-color: #3a86ff; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;");
			btnReclutarTropa.setDisable(!hayHueco);

			StringBuilder textoTooltip = new StringBuilder();
			textoTooltip.append(DescripcionUnidad.generar(tropa));
			String bonus = combateControlador.construirTextoBonus(tropa);

			if (bonus != null && !bonus.isBlank()) {
				textoTooltip.append("\n\n").append(bonus);
			}
			textoTooltip.append("\n\n⏳ Turnos de creación: ").append(tropa.getTurnosCreacion());
			Tooltip tooltip = new Tooltip(textoTooltip.toString());
			tooltip.setStyle("-fx-font-size: 14px;");
			btnReclutarTropa.setTooltip(tooltip);

			btnReclutarTropa.setOnAction(evento -> {
				boolean iniciado = reclutamientoControlador.iniciarReclutamiento(ciudad, tropa, tropasMaximas);
				if (iniciado) {
					actualizarPanelReclutamiento(ciudad);
					actualizarHudTropas();
				}
			});
			vboxReclutamiento.getChildren().add(btnReclutarTropa);
		}
		if (!hayHueco) {
			Label lblLimite = new Label("⚠️ Límite de tropas alcanzado");
			lblLimite.setStyle("-fx-text-fill: #ff6644; -fx-font-size: 11px;");
			vboxReclutamiento.getChildren().add(lblLimite);
		}
	}

	/**
	 * Metodo que gestiona el texto que aparece cuando es el turnos de la IA.
	 *
	 * @param texto the texto
	 */
	public void mostrarEstadoTurnoIA(String texto) {
		lblEstadoTurno.setText(texto);
		lblEstadoTurno.setStyle(
				"-fx-text-fill: #ff3333; -fx-font-weight: bold; -fx-font-size: 26px; -fx-effect: dropshadow(three-pass-box, black, 5, 1, 0, 0); -fx-padding: 40 0 0 0;");
	}

	/**
	 * Metodo que muestra una ventana cuando hay apilamiento de unidades, es decir,
	 * cuando hay mas de una unidad en una casilla. Esto solo va a pasar al
	 * principio de la partida
	 *
	 * @param unidades the unidades
	 */
	private void mostrarMenuSeleccionUnidades(List<Personaje> unidades) {
		Personaje defecto = unidades.get(0);

		ChoiceDialog<Personaje> dialogo = new ChoiceDialog<>(defecto, unidades);
		dialogo.setTitle("Guarnición de la Ciudad");
		dialogo.setHeaderText("Múltiples unidades detectadas en la fortaleza");
		dialogo.setContentText("Selecciona qué unidad deseas comandar:");

		Optional<Personaje> resultado = dialogo.showAndWait();

		resultado.ifPresent(this::seleccionarUnidad);
	}

	/**
	 * Metodo para mostrar las unidades que se pueden reclutar en una ciudad en
	 * funcion a su faccion. No hace nada mas que mostrar las unidades. Se usará
	 * para ver las unidades que puede reclutar las Bestias en sus ciudades.
	 *
	 * @param ciudad        the ciudad
	 * @param faccionCiudad the faccion ciudad
	 */
	private void mostrarReclutamientoEnemigoSoloConsulta(Ciudad ciudad, Faccion faccionCiudad) {
		List<Personaje> tropasDisponibles = reclutamientoControlador.obtenerTropasReclutablesEnCiudad(todasLasTropas,
				ciudad, faccionCiudad);
		if (tropasDisponibles.isEmpty()) {
			vboxReclutamiento.setVisible(false);
			vboxReclutamiento.setManaged(false);
			return;
		}

		Label lblTitulo = new Label("🔎 Recluta en esta ciudad (solo consulta):");
		lblTitulo.setWrapText(true);
		lblTitulo.setMaxWidth(290);
		lblTitulo.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 12px; -fx-font-weight: bold;");
		vboxReclutamiento.getChildren().add(lblTitulo);

		for (Personaje tropa : tropasDisponibles) {
			Label lblTropa = new Label("🔺 " + tropa.getNombre());
			lblTropa.setMaxWidth(Double.MAX_VALUE);
			lblTropa.setStyle(
					"-fx-background-color: rgba(255,0,80,0.12); -fx-text-fill: #ffb3b3; -fx-font-size: 12px; -fx-border-color: #ff0055; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 4 8 4 8;");

			StringBuilder textoTooltip = new StringBuilder();
			textoTooltip.append(DescripcionUnidad.generar(tropa));
			String bonus = combateControlador.construirTextoBonus(tropa);
			if (bonus != null && !bonus.isBlank()) {
				textoTooltip.append("\n\n").append(bonus);
			}
			textoTooltip.append("\n\n⏳ Turnos de creación: ").append(tropa.getTurnosCreacion());
			Tooltip tooltip = new Tooltip(textoTooltip.toString());
			tooltip.setStyle("-fx-font-size: 14px;");
			lblTropa.setTooltip(tooltip);

			vboxReclutamiento.getChildren().add(lblTropa);
		}
	}

	/**
	 * Metodo para asignar un icono a cada unidad segun su tipo de combate.
	 *
	 * @param etiquetaNombre the etiqueta nombre
	 * @param pj             the pj
	 */
	private void asignarIconoPNG(Label etiquetaNombre, Personaje pj) {
		if (pj == null || pj.getTipoCombate() == null) {
			etiquetaNombre.setGraphic(null);
			return;
		}

		String tipoCombate = pj.getTipoCombate().toLowerCase().trim();
		String rutaIcono = "/data/" + tipoCombate + ".png";

		try (InputStream canalEntrada = getClass().getResourceAsStream(rutaIcono)) {
			if (canalEntrada != null) {
				Image icono = new Image(canalEntrada);
				ImageView vistaDelIcono = new ImageView(icono);

				vistaDelIcono.setFitWidth(16);
				vistaDelIcono.setFitHeight(16);
				vistaDelIcono.setPreserveRatio(true);

				etiquetaNombre.setGraphic(vistaDelIcono);
			} else {
				System.err.println("No se encontro el icono en: " + rutaIcono);
				etiquetaNombre.setGraphic(null);
			}
		} catch (IOException e) {
			System.err.println("Error al cargar el icono PNG: " + rutaIcono);
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error procesando la imagen");
			e.printStackTrace();
		}

	}

	/**
	 * Metodo para reproducir una alerta si frodo o sam son detectados.
	 *
	 * @param pj the pj
	 */
	public void reproducirAlertaSiEsFrodo(Personaje pj) {
		if (esPersonajeVigilado(pj)) {
			GestorSonido.reproducirFrodoDetectado();
		}
	}

	/**
	 * Metodo para comprobar si un personaje indicado esta considerado como
	 * "vigilado" que seria Frodo y Sam
	 *
	 * @param pj the pj
	 * @return true, if successful
	 */
	private boolean esPersonajeVigilado(Personaje pj) {
		return pj != null && ("Frodo".equalsIgnoreCase(pj.getNombre()) || "Sam".equalsIgnoreCase(pj.getNombre()));
	}

	/**
	 * Metodo que comprueba si el turno actual le corresponde a la faccion del
	 * personaje que se le pasa.
	 *
	 * @param pj the pj
	 * @return true, if successful
	 */
	private boolean esTurnoDe(Personaje pj) {
		return pj.getFaccion() == turnoActual;
	}

	/**
	 * Gets the mapa.
	 *
	 * @return the mapa
	 */
	public Mapa getMapa() {
		return mapa;
	}

	/**
	 * Gets the mapa renderizado.
	 *
	 * @return the mapa renderizado
	 */
	public RenderizadoMapa getMapaRenderizado() {
		return mapaRenderizado;
	}

}
