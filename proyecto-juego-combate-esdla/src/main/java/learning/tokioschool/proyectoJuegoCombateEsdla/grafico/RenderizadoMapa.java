package learning.tokioschool.proyectoJuegoCombateEsdla.grafico;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Casilla;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ControladorVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;

/**
 * The Class RenderizadoMapa.
 * 
 * Clase encargada de renderizar el mapa en un Canvas(nuestro lienzo en blanco)
 * de JavaFX.
 * 
 * Gestiona la logica del zoom, la camara, el arrastre, la selección y el hover
 * de casillas. Dibuja la cuadricula (rejilla del mapa) y la imagen base del
 * mapa.
 */
public class RenderizadoMapa {

	/** Canvas donde dibujamos el mapa */
	private final Canvas canvas;

	/** Objeto que nos permite dibujar en el canvas. */
	private final GraphicsContext graficos;

	/** Mapa lógico. */
	private final Mapa mapa;

	/** imagen mapa. */
	private final Image imagenMapa;

	/** Tamaño de la celda. */
	private final int tamanioCelda;

	/** casilla actualmente seleccionada por el jugador. */
	private Point casillaSeleccionada = null;

	private Set<String> casillasMovimientoValidas = new HashSet<>();

	/** The aliado en foco. */
	private Personaje aliadoEnFoco;

	private Personaje enemigoEnFoco;

	/** Casilla en la que tenemos el raton. */
	private Point casillaHover = null;

	/** Zoom actual. Por defecto 1.0. */
	private double zoom = 1.0;

	/** Desplazamiento en X de la camara. */
	private double camaraX = 0;

	/** Desplazamiento en Y de la camara. */
	private double camaraY = 0;

	/** Punto inicial del arrastre en la coordenada X (cuando movemos la camara). */
	private double dragStartX;

	/** Punto inicial del arrastre en la coordenada Y (cuando movemos la camara). */
	private double dragStartY;

	/**
	 * Atributo para obtener un int en el que controlaremos los turnos de frodo en
	 * el monte del destino sin ser atacado
	 */
	private IntSupplier activarAnuncioFrodoMonteDestino = () -> -1;

	/**
	 * Constructor de la clase.
	 *
	 * @param canvas       canvas donde se dibuja el mapa.
	 * @param tamanioCelda tamaño de cada celda en el mapa.
	 * @param imagenMapa   la imagen base del mapa.
	 * @param mapa         el mapa lógico del juego.
	 */
	public RenderizadoMapa(Canvas canvas, int tamanioCelda, Image imagenMapa, Mapa mapa) {
		this.canvas = canvas;
		this.graficos = canvas.getGraphicsContext2D();
		this.tamanioCelda = tamanioCelda;
		this.imagenMapa = imagenMapa;
		this.mapa = mapa;
	}

	/**
	 * Modificar zoom.
	 * 
	 * Hace zoom hacia el punto donde está el raton.
	 *
	 * @param modificadorZoom modificador por el que se va a multiplicar el zoom.
	 * @param ratonX          posición X del ratón en el canvas. Se usa para dirigir
	 *                        el zoom hacia allí.
	 * @param ratonY          posición Y del raton en el canvas. Se usa para dirigir
	 *                        el zoom hacia allí.
	 */
	public void modificarZoom(double modificadorZoom, double ratonX, double ratonY) {
		double zoomAnterior = zoom;

		zoom *= modificadorZoom;
		if (zoom < 0.3)
			zoom = 0.3;
		if (zoom > 4.0)
			zoom = 4.0;

		camaraX = (camaraX + ratonX) * (zoom / zoomAnterior) - ratonX;
		camaraY = (camaraY + ratonY) * (zoom / zoomAnterior) - ratonY;

		limitarCamara();
	}

	/**
	 * Guarda el punto inicial de la camara al realizar el arrastre.
	 *
	 * @param x posicion X del raotn.
	 * @param y posición Y del ratón.
	 */
	public void iniciarArrastreCamara(double x, double y) {
		dragStartX = x + camaraX;
		dragStartY = y + camaraY;
	}

	/**
	 * Actualiza la posición de la cámara durante el arrastre.
	 *
	 * @param x Posición X.
	 * @param y Posición Y.
	 */
	public void arrastrarCamara(double x, double y) {
		camaraX = dragStartX - x;
		camaraY = dragStartY - y;
		limitarCamara();
	}

	/**
	 * Mueve la cámara con las flechas del teclado.
	 *
	 * @param distanciaX Desplazamiento horizontal.
	 * @param distanciaY Desplazamiento vertical.
	 */
	public void moverCamaraConFlechas(double distanciaX, double distanciaY) {
		camaraX += distanciaX;
		camaraY += distanciaY;
		limitarCamara();
	}

	/**
	 * Método con el que limitamos el movimiento de la cámara para que no se salga
	 * de los bordes del mapa.
	 */
	private void limitarCamara() {
		double anchoImagenZoom = imagenMapa.getWidth() * zoom;
		double altoImagenZoom = imagenMapa.getHeight() * zoom;

		double maxX = anchoImagenZoom - canvas.getWidth();
		double maxY = altoImagenZoom - canvas.getHeight();

		if (maxX < 0)
			camaraX = maxX / 2;
		else {
			if (camaraX < 0)
				camaraX = 0;
			if (camaraX > maxX)
				camaraX = maxX;
		}

		if (maxY < 0)
			camaraY = maxY / 2;
		else {
			if (camaraY < 0)
				camaraY = 0;
			if (camaraY > maxY)
				camaraY = maxY;
		}
	}

	/**
	 * Método con el que centramos la cámara con respecto al canvas.
	 */
	public void centrarCamara() {
		camaraX = (imagenMapa.getWidth() * zoom - canvas.getWidth()) / 2;
		camaraY = (imagenMapa.getHeight() * zoom - canvas.getHeight()) / 2;
		limitarCamara();
	}

	/**
	 * Método que usamos para seleccionar una celda (casilla). Usamos el objeto
	 * Point de java.
	 *
	 * @param x Coordenada X de la casilla.
	 * @param y Coordenada Y de la casilla.
	 */
	public void seleccionarCasilla(int x, int y) {
		this.casillaSeleccionada = new Point(x, y);
	}

	/**
	 * Actualiza la casilla sobre la que se encuentra el raton usamos el objeto
	 * Point de java.
	 *
	 * @param x Coordenada X.
	 * @param y Coordenada Y.
	 */
	public void actualizarHover(int x, int y) {
		this.casillaHover = new Point(x, y);
	}

	/**
	 * Método encargado de dibujar el mapa. Es el responsable de la imagen base, la
	 * rejilla, la selección y el hover, dandole color a estos dos últimos para que
	 * sea visual.
	 *
	 * @param mapa Mapa lógico del juego.
	 */
	public void dibujarMapa(Mapa mapa) {
		graficos.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		graficos.drawImage(imagenMapa, -camaraX, -camaraY, imagenMapa.getWidth() * zoom, imagenMapa.getHeight() * zoom);

		double celdaImgAncho = (imagenMapa.getWidth() / mapa.getAncho()) * zoom;
		double celdaImgAlto = (imagenMapa.getHeight() / mapa.getAlto()) * zoom;

		// Rejilla del mapa
		graficos.setStroke(Color.rgb(180, 180, 180, 0.25));
		graficos.setLineWidth(1.0);

		for (int x = 0; x <= mapa.getAncho(); x++) {
			double posX = x * celdaImgAncho - camaraX;
			graficos.strokeLine(posX, -camaraY, posX, imagenMapa.getHeight() * zoom - camaraY);
		}

		for (int y = 0; y <= mapa.getAlto(); y++) {
			double posY = y * celdaImgAlto - camaraY;
			graficos.strokeLine(-camaraX, posY, imagenMapa.getWidth() * zoom - camaraX, posY);
		}

		// Gestión del color de la casilla selecciónada.
		if (casillaSeleccionada != null) {
			graficos.setStroke(Color.YELLOW);
			graficos.setLineWidth(2);
			graficos.strokeRect(casillaSeleccionada.x * celdaImgAncho - camaraX,
					casillaSeleccionada.y * celdaImgAlto - camaraY, celdaImgAncho, celdaImgAlto);
		}

		// Gestión del color del hover del raton sobre las casillas.
		if (casillaHover != null && casillaHover.x >= 0 && casillaHover.y >= 0) {
			graficos.setStroke(Color.WHITE);
			graficos.setLineWidth(1.5);
			graficos.strokeRect(casillaHover.x * celdaImgAncho - camaraX, casillaHover.y * celdaImgAlto - camaraY,
					celdaImgAncho, celdaImgAlto);
		}
		// Dibujar bordes ciudad.
		dibujarBordesCiudades(mapa, celdaImgAncho, celdaImgAlto);
		dibujarTerritorios(graficos, mapa, celdaImgAncho, celdaImgAlto);
		dibujarPersonajes();
		dibujarRangosAliado();
		dibujarProgresoFrodoMonteDestino(mapa, celdaImgAncho, celdaImgAlto);

	}

	/**
	 * Metodo que usaremos para dibujar o señalar el dominio sobre una ciudad..
	 *
	 * @param mapa          the mapa
	 * @param celdaImgAncho the celda img ancho
	 * @param celdaImgAlto  the celda img alto
	 */
	private void dibujarBordesCiudades(Mapa mapa, double celdaImgAncho, double celdaImgAlto) {

		for (Ciudad ciudad : mapa.getCiudades()) {

			Color colorBorde = Color.WHITE;
			if (ciudad.esCiudadControladaPorHeroes()) {
				colorBorde = Color.CYAN;
			}
			if (ciudad.esCiudadControladaPorBestias()) {
				colorBorde = Color.RED;
			}

			graficos.setStroke(colorBorde);
			graficos.setLineWidth(3);

			for (Point celda : ciudad.getCasillasDeCiudad()) {

				int x = celda.x;
				int y = celda.y;

				double canvasX = x * celdaImgAncho - camaraX;
				double canvasY = y * celdaImgAlto - camaraY;

				boolean bordeArriba = esParteDeCiudad(ciudad, x, y - 1);
				boolean bordeAbajo = esParteDeCiudad(ciudad, x, y + 1);
				boolean bordeIzq = esParteDeCiudad(ciudad, x - 1, y);
				boolean bordeDer = esParteDeCiudad(ciudad, x + 1, y);

				if (!bordeArriba) {
					graficos.strokeLine(canvasX, canvasY, canvasX + celdaImgAncho, canvasY);
				}

				if (!bordeAbajo) {
					graficos.strokeLine(canvasX, canvasY + celdaImgAlto, canvasX + celdaImgAncho,
							canvasY + celdaImgAlto);
				}

				if (!bordeIzq) {
					graficos.strokeLine(canvasX, canvasY, canvasX, canvasY + celdaImgAlto);
				}

				if (!bordeDer) {
					graficos.strokeLine(canvasX + celdaImgAncho, canvasY, canvasX + celdaImgAncho,
							canvasY + celdaImgAlto);
				}

			}
		}
	}

	/**
	 * Metodo que nos permite marcar y dibujar los territorios de las ciudades. En
	 * concreto lo usaremos para marcar los territorios bestias en rojo y la
	 * frontera entre Bestias y Heroes.
	 *
	 * @param graficos     the graficos
	 * @param mapa         the mapa
	 * @param tamanioCelda the tamanio celda
	 */

	private void dibujarTerritorios(GraphicsContext graficos, Mapa mapa, double celdaImgAncho, double celdaImgAlto) {

		for (Ciudad ciudad : mapa.getCiudades()) {
			if (ciudad.getControlSobreCiudad() != ControlCiudad.BESTIAS) {
				continue;
			}

			for (Point p : ciudad.getCasillasFormanTerritorio()) {
				double canvasX = p.x * celdaImgAncho - camaraX;
				double canvasY = p.y * celdaImgAlto - camaraY;
				double celdaX = celdaImgAncho;
				double celdaY = celdaImgAlto;

				graficos.setFill(Color.rgb(255, 0, 0, 0.04));
				graficos.fillRect(canvasX - 2, canvasY - 2, celdaX + 4, celdaY + 4);

				graficos.setFill(Color.rgb(255, 0, 0, 0.08));
				graficos.fillRect(canvasX - 1, canvasY - 1, celdaX + 2, celdaY + 2);

				graficos.setFill(Color.rgb(255, 0, 0, 0.12));
				graficos.fillRect(canvasX, canvasY, celdaX, celdaY);
			}

			graficos.setStroke(Color.RED);
			graficos.setLineWidth(1.5);

			for (Point p : ciudad.getCasillasFormanTerritorio()) {

				int x = p.x;
				int y = p.y;

				double canvasX = x * celdaImgAncho - camaraX;
				double canvasY = y * celdaImgAlto - camaraY;

				double celdaX = celdaImgAncho;
				double celdaY = celdaImgAlto;

				boolean bordeArriba = mapa.esTerritorioBestia(x, y - 1);
				boolean bordeAbajo = mapa.esTerritorioBestia(x, y + 1);
				boolean bordeIzq = mapa.esTerritorioBestia(x - 1, y);
				boolean bordeDer = mapa.esTerritorioBestia(x + 1, y);

				if (!bordeArriba) {
					graficos.strokeLine(canvasX, canvasY, canvasX + celdaX, canvasY);
				}
				if (!bordeAbajo) {
					graficos.strokeLine(canvasX, canvasY + celdaY, canvasX + celdaX, canvasY + celdaY);
				}
				if (!bordeIzq) {
					graficos.strokeLine(canvasX, canvasY, canvasX, canvasY + celdaY);
				}
				if (!bordeDer) {
					graficos.strokeLine(canvasX + celdaX, canvasY, canvasX + celdaX, canvasY + celdaY);
				}
			}
		}
	}

	/**
	 * Metodo por el cual dibujamos o representamos en el mapa las unidades con las
	 * que vamos a jugar. Dibujaremos con circulos los personajes Paladines (los que
	 * no se pueden reclutar) y con Triangulos los personajes o unidades Tropa (las
	 * que se pueden reclutar). Azul Heroe, rojo Bestia.
	 * 
	 * Ademas aquellas que comparten casillas, como por ejemplo al iniciar la
	 * partida que puede haber varias en la misma casilla, con un tomo mas clarito,
	 * tanto del azul como del rojo.
	 */
	public void dibujarPersonajes() {
		double celdaImgAncho = (imagenMapa.getWidth() / mapa.getAncho()) * zoom;
		double celdaImgAlto = (imagenMapa.getHeight() / mapa.getAlto()) * zoom;

		Map<String, Set<String>> unidadesPorCelda = new HashMap<>();
		for (Personaje u : mapa.getTodasLasUnidades()) {
			String clave = u.getX() + "," + u.getY();
			unidadesPorCelda.computeIfAbsent(clave, k -> new HashSet<>()).add(u.getNombre());
		}

		for (int x = 0; x < mapa.getAncho(); x++) {
			for (int y = 0; y < mapa.getAlto(); y++) {
				Casilla casilla = mapa.getCasilla(x, y);

				if (casilla != null && !casilla.estaVacia()) {
					Personaje pj = casilla.getPersonaje();

					double canvasX = x * celdaImgAncho - camaraX;
					double canvasY = y * celdaImgAlto - camaraY;

					double margenX = celdaImgAncho * 0.075;
					double margenY = celdaImgAlto * 0.075;
					double diametroAncho = celdaImgAncho * 0.85;
					double diametroAlto = celdaImgAlto * 0.85;

					double posXFinal = canvasX + margenX;
					double posYFinal = canvasY + margenY;

					for (int i = 3; i >= 1; i--) {
						double offsetSombra = i * 1.5 * zoom;
						graficos.setFill(Color.rgb(0, 0, 0, 0.08 / i));
						graficos.fillOval(posXFinal - (offsetSombra / 2) + (1 * zoom),
								posYFinal - (offsetSombra / 2) + (1 * zoom), diametroAncho + offsetSombra,
								diametroAlto + offsetSombra);
					}

					boolean esHeroe = pj.getFaccion() == Faccion.HEROES;

					Set<String> nombresEnCelda = unidadesPorCelda.get(x + "," + y);
					int totalUnidadesEnCasilla = (nombresEnCelda != null) ? nombresEnCelda.size() : 1;

					if (totalUnidadesEnCasilla > 1) {
						if (esHeroe) {
							graficos.setFill(Color.rgb(100, 190, 255));
						} else {
							graficos.setFill(Color.rgb(255, 150, 150));
						}
					} else {
						if (esHeroe) {
							graficos.setFill(Color.rgb(30, 144, 255));
						} else {
							graficos.setFill(Color.rgb(255, 0, 50));
						}
					}

					if (esHeroe && pj.HaAtacadoEsteTurno()) {
						graficos.setFill(Color.rgb(106, 137, 167));
					}

					boolean esTropa = "TROPA".equalsIgnoreCase(pj.getCategoria());
					rellenarForma(esTropa, posXFinal, posYFinal, diametroAncho, diametroAlto);

					if (pj == aliadoEnFoco) {
						graficos.setStroke(Color.YELLOW);
						graficos.setLineWidth(3.0 * zoom);

					} else if (pj == enemigoEnFoco) {
						graficos.setStroke(Color.rgb(255, 149, 0));

						graficos.setLineWidth(3.0 * zoom);
					} else {
						graficos.setStroke(Color.BLACK);
						graficos.setLineWidth(2.5);
					}
					bordearForma(esTropa, posXFinal, posYFinal, diametroAncho, diametroAlto);
					graficos.setStroke(Color.WHITE);
					graficos.setLineWidth(0.8);
					bordearForma(esTropa, posXFinal + 1, posYFinal + 1, diametroAncho - 2, diametroAlto - 2);
				}
			}
		}
	}

	/**
	 * Metodo con el cual vamos a calcular y dibujar el rango de movimiento y ataque
	 * de las unidades aliadas cuando clickamos en ella.
	 */
	public void dibujarRangosAliado() {
		if (aliadoEnFoco == null) {
			return;
		}

		double celdaImgAncho = (imagenMapa.getWidth() / mapa.getAncho()) * zoom;
		double celdaImgAlto = (imagenMapa.getHeight() / mapa.getAlto()) * zoom;

		int origenX = aliadoEnFoco.getX();
		int origenY = aliadoEnFoco.getY();
		int rMov = aliadoEnFoco.getMovimiento();
		int rAtk = aliadoEnFoco.getRangoAtaque();

		int radioMaximo = Math.max(rMov, rAtk);
		boolean dibujarAtaquePrimero = rAtk >= rMov;

		this.casillasMovimientoValidas.clear();
		this.casillasMovimientoValidas.addAll(mapa.calcularCasillasAlcanzables(aliadoEnFoco));

		for (int x = origenX - radioMaximo; x <= origenX + radioMaximo; x++) {
			for (int y = origenY - radioMaximo; y <= origenY + radioMaximo; y++) {

				if (x == origenX && y == origenY) {
					continue;
				}

				if (mapa.estaDentroDelMapa(x, y)) {
					int distanciaTactica = Math.max(Math.abs(x - origenX), Math.abs(y - origenY));
					double canvasX = x * celdaImgAncho - camaraX;
					double canvasY = y * celdaImgAlto - camaraY;

					if (dibujarAtaquePrimero) {
						if (distanciaTactica <= rAtk) {
							pintarCeldaAtaque(canvasX, canvasY, celdaImgAncho, celdaImgAlto);
						}
						if (this.casillasMovimientoValidas.contains(x + "," + y)) {
							pintarCeldaMovimiento(canvasX, canvasY, celdaImgAncho, celdaImgAlto);
						}
					} else {
						if (this.casillasMovimientoValidas.contains(x + "," + y)) {
							pintarCeldaMovimiento(canvasX, canvasY, celdaImgAncho, celdaImgAlto);
						}
						if (distanciaTactica <= rAtk) {
							pintarCeldaAtaque(canvasX, canvasY, celdaImgAncho, celdaImgAlto);
						}
					}
				}
			}
		}
	}

	/**
	 * Representa el progreso de rondas que esta frodo en el monte del destino.
	 *
	 * @param mapa          the mapa
	 * @param celdaImgAncho ancho de la celda ya escalado por el zoom.
	 * @param celdaImgAlto  alto de la celda ya escalado por el zoom.
	 */
	private void dibujarProgresoFrodoMonteDestino(Mapa mapa, double celdaImgAncho, double celdaImgAlto) {
		int rondas = activarAnuncioFrodoMonteDestino.getAsInt();
		if (rondas < 0) {
			return;
		}

		Ciudad monteDelDestino = null;
		for (Ciudad ciudad : mapa.getCiudades()) {
			if ("Monte del Destino".equalsIgnoreCase(ciudad.getNombreCiudad())) {
				monteDelDestino = ciudad;
				break;
			}
		}
		if (monteDelDestino == null || monteDelDestino.getCasillasDeCiudad().isEmpty()) {
			return;
		}
		int minY = Integer.MAX_VALUE;
		double sumaX = 0;
		int total = 0;
		for (Point casilla : monteDelDestino.getCasillasDeCiudad()) {
			minY = Math.min(minY, casilla.y);
			sumaX += casilla.x;
			total++;
		}
		double centroXCasillas = sumaX / total;
		double canvasX = centroXCasillas * celdaImgAncho - camaraX;
		double canvasY = minY * celdaImgAlto - camaraY - (18 * zoom);
		String texto = rondas + "/" + ControladorVictoria.RONDAS_NECESARIAS_EN_MONTE_DEL_DESTINO;

		graficos.setTextAlign(TextAlignment.CENTER);
		graficos.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15 * zoom));
		graficos.setFill(Color.rgb(0, 0, 0, 0.75));

		for (double dx = -1.5; dx <= 1.5; dx += 1.5) {
			for (double dy = -1.5; dy <= 1.5; dy += 1.5) {
				if (dx == 0 && dy == 0)
					continue;
				graficos.fillText("💍 " + texto, canvasX + dx, canvasY + dy);
			}
		}
		graficos.setFill(Color.rgb(255, 215, 0));
		graficos.fillText("💍 " + texto, canvasX, canvasY);
	}

	// Métodos auxiliares limpios para evitar duplicar código de dibujo en el Canvas

	/**
	 * Pinta de rojo de una casilla dentro del rango de ataque.
	 *
	 * @param cx Coordenada X en canvas.
	 * @param cy Coordenada Y en canvas.
	 * @param w  Ancho de la celda.
	 * @param h  Alto de la celda.
	 */
	private void pintarCeldaAtaque(double cx, double cy, double w, double h) {
		graficos.setFill(Color.rgb(255, 40, 0, 0.40));
		graficos.fillRect(cx, cy, w, h);
		graficos.setStroke(Color.rgb(255, 80, 0, 0.70));
		graficos.setLineWidth(1.5);
		graficos.strokeRect(cx, cy, w, h);
	}

	/**
	 * Pinta de verde de una celda alcanzable por movimiento.
	 *
	 * @param cx Coordenada X en canvas.
	 * @param cy Coordenada Y en canvas.
	 * @param w  Ancho de la celda.
	 * @param h  Alto de la celda.
	 */
	private void pintarCeldaMovimiento(double cx, double cy, double w, double h) {
		graficos.setFill(Color.rgb(0, 255, 80, 0.45));
		graficos.fillRect(cx, cy, w, h);
		graficos.setStroke(Color.rgb(0, 255, 120, 0.75));
		graficos.setLineWidth(1.5);
		graficos.strokeRect(cx, cy, w, h);
	}

	/**
	 * Devuelve las coordenadas x (horizontal) dentro de la celda de los tres puntos
	 * para dibujar el triangulo de las unidades Tropas.
	 *
	 * @param x Coordenada X de la esquina superior izquierda de la celda.
	 * @param w Ancho de la celda.
	 * @return Coordenadas X de los 3 vértices del triángulo (icono de Tropa).
	 */
	private double[] puntosXTriangulo(double x, double w) {
		return new double[] { x + w / 2, x, x + w };
	}

	/**
	 * Devuelve las coordenadas y (vertical) dentro de la celda de los tres puntos
	 * para dibujar el triangulo de las unidades Tropas.
	 *
	 * @param y Coordenada Y de la esquina superior izquierda de la celda.
	 * @param h Alto de la celda.
	 * @return Coordenadas Y de los 3 vértices del triángulo (icono de Tropa).
	 */
	private double[] puntosYTriangulo(double y, double h) {
		return new double[] { y, y + h, y + h };
	}

	/**
	 * Rellena la forma de las unidades. Triangulo para unidades Tropa y Circulo
	 * para unidades Paladines.
	 *
	 * @param esTropa true si la unidad es de categoría Tropa.
	 * @param x       Coordenada X.
	 * @param y       Coordenada Y.
	 * @param w       Ancho de la forma.
	 * @param h       Alto de la forma.
	 */
	private void rellenarForma(boolean esTropa, double x, double y, double w, double h) {
		if (esTropa) {
			graficos.fillPolygon(puntosXTriangulo(x, w), puntosYTriangulo(y, h), 3);
		} else {
			graficos.fillOval(x, y, w, h);
		}
	}

	/**
	 * Dibuja el borde de los iconos de las unidades.
	 *
	 * @param esTropa true si la unidad es de categoría Tropa.
	 * @param x       Coordenada X.
	 * @param y       Coordenada Y.
	 * @param w       Ancho de la forma.
	 * @param h       Alto de la forma.
	 */
	private void bordearForma(boolean esTropa, double x, double y, double w, double h) {
		if (esTropa) {
			graficos.strokePolygon(puntosXTriangulo(x, w), puntosYTriangulo(y, h), 3);
		} else {
			graficos.strokeOval(x, y, w, h);
		}
	}

	/**
	 * Verificamos si la casilla con las coordenadas aportada por parametros X e Y,
	 * es parte de la ciudad.
	 *
	 * @param ciudad the ciudad
	 * @param x      coordenada x
	 * @param y      coordenada y
	 * @return true, Si forma parte de la ciudad.
	 */
	private boolean esParteDeCiudad(Ciudad ciudad, int x, int y) {
		return ciudad.getCasillasDeCiudad().contains(new Point(x, y));
	}

	/**
	 * Metodo para lanzar el anuncio cuando frodo entra en el monte del destino.
	 *
	 * @param activarAnuncioFrodoMonteDestino the new activar anuncio frodo monte
	 *                                        destino
	 */
	public void setActivarAnuncioFrodoMonteDestino(IntSupplier activarAnuncioFrodoMonteDestino) {
		this.activarAnuncioFrodoMonteDestino = activarAnuncioFrodoMonteDestino;
	}

	/**
	 * Gets the zoom.
	 *
	 * @return the zoom
	 */
	public double getZoom() {
		return zoom;
	}

	/**
	 * Sets the zoom.
	 *
	 * @param zoom the new zoom
	 */
	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	/**
	 * Gets the camara X.
	 *
	 * @return the camara X
	 */
	public double getCamaraX() {
		return camaraX;
	}

	/**
	 * Gets the camara Y.
	 *
	 * @return the camara Y
	 */
	public double getCamaraY() {
		return camaraY;
	}

	/**
	 * Gets the tamanio celda.
	 *
	 * @return the tamanio celda
	 */
	public int getTamanioCelda() {
		return tamanioCelda;
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
	 * Sets the aliado en foco (Seleccionarlo).
	 *
	 * @param pj the new aliado en foco
	 */
	public void setAliadoEnFoco(Personaje pj) {
		this.aliadoEnFoco = pj;
	}

	/**
	 * Sets the enemigo en foco (seleccionarlo).
	 *
	 * @param pj the new enemigo en foco
	 */
	public void setEnemigoEnFoco(Personaje pj) {
		this.enemigoEnFoco = pj;
	}

	/**
	 * Gets the casillas movimiento validas.
	 *
	 * @return the casillas movimiento validas
	 */
	public Set<String> getCasillasMovimientoValidas() {
		return this.casillasMovimientoValidas;
	}
}
