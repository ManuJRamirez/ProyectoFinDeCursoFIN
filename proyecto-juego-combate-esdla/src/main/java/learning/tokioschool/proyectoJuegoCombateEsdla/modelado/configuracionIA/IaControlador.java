package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.configuracionIA;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.CargadorUnidades;
import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.PantallaJuegoControlador;
import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.PantallaJuegoControlador.TipoEvento;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida.ConfiguracionVictoria;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.reclutarTropa.ReclutamientoControlador;
import learning.tokioschool.proyectoJuegoCombateEsdla.sonidos.GestorSonido;

/**
 * The Class IaControlador.
 * 
 * Clase controlador encargada de gestionar el turno de la faccion Bestia que
 * estara guiada por una IA. Se encargara de todo lo relacionado con esta
 * faccion, desde atacar, mover, defender, explorar, conquistar, etc
 */
public class IaControlador {

	/** Atributo donde guardamos el listado de tropas disponibles para reclutar. */
	private final List<Personaje> rosterTropasBestias;

	/** Atributo para guardar el mapa del juego */
	private Mapa mapa;

	/**
	 * Controlador de la pantalla del juego, donde reflejaremos los cambios que
	 * realice la IA.
	 */
	private PantallaJuegoControlador vista;

	/** Nivel de dificultad de la ia. */
	private EstrategiaIA estrategia;

	/** The reclutamiento controlador. */
	private ReclutamientoControlador reclutamientoControlador;

	/** Configuraciones de victoria activas. */
	private ConfiguracionVictoria configuracionVictoria;

	/** Limite de unidades "Tropa" maximas. Por defecto es 0. */
	private int tropasMaximas = 0;

	/**
	 * Constructor base, en el que la dificultad preseleccionada sera la Normal. No
	 * hay selecionada ninguna condicion de victoria
	 *
	 * @param mapa  the mapa
	 * @param vista the vista
	 */
	public IaControlador(Mapa mapa, PantallaJuegoControlador vista) {
		this(mapa, vista, Dificultad.NORMAL);
	}

	/**
	 * Contructor en el que tendremos que añadir la dificultad de la IA. No hay
	 * selecionada ninguna condicion de victoria
	 *
	 * @param mapa       the mapa
	 * @param vista      the vista
	 * @param dificultad the dificultad
	 */
	public IaControlador(Mapa mapa, PantallaJuegoControlador vista, Dificultad dificultad) {
		this(mapa, vista, dificultad, null);
	}

	/**
	 * Contructor en el que cargará el roster de tropas disponibles, y deberemos
	 * pasarle un nivel de dificultad de la ia y unas condiciones de victorias
	 * seleccionadas.
	 *
	 * @param mapa                  the mapa
	 * @param vista                 the vista
	 * @param dificultad            the dificultad
	 * @param configuracionVictoria the configuracion victoria
	 */
	public IaControlador(Mapa mapa, PantallaJuegoControlador vista, Dificultad dificultad,
			ConfiguracionVictoria configuracionVictoria) {
		this.mapa = mapa;
		this.vista = vista;
		this.estrategia = EstrategiaIA.dificultadElegida(dificultad);
		this.configuracionVictoria = configuracionVictoria;
		this.rosterTropasBestias = CargadorUnidades.cargarUnidades().stream()
				.filter(pj -> pj.getFaccion() == Faccion.BESTIAS && "TROPA".equalsIgnoreCase(pj.getCategoria()))
				.toList();
	}

	/**
	 * En este metodo vamos a configurar el controlador de reclutamiento, que debera
	 * respetar el numero maximo de tropas permitidas.
	 *
	 * @param reclutamientoControlador the reclutamiento controlador
	 * @param tropasMaximas            the tropas maximas
	 */
	public void configurarReclutamiento(ReclutamientoControlador reclutamientoControlador, int tropasMaximas) {
		this.reclutamientoControlador = reclutamientoControlador;
		this.tropasMaximas = tropasMaximas;
	}

	/**
	 * Metodo encargado de ejecutar el turno completo de la faccion Bestia
	 * controlada por la IA. Actualiza la deteccion de hobbits, recorre cada unidad
	 * Bestia viva y ejecuta una accion sobre ella, gestiona el reclutamiento y
	 * finaliza su turno pasandoselo a los Heroes.
	 */
	public void ejecutarTurnoIA() {
		vista.mostrarEstadoTurnoIA("👹 Las Bestias planean su estrategia en las sombras...");

		new Thread(() -> {
			try {
				Thread.sleep(1500);

				Platform.runLater(() -> {
					try {
						List<Personaje> hobbitsConCambioDeteccion = mapa.actualizarDeteccionHobbits();
						if (!hobbitsConCambioDeteccion.isEmpty()) {
							vista.notificarCambiosDeteccion(hobbitsConCambioDeteccion);
						}
					} catch (Exception e) {
						System.err.println("Error actualizando la detección de hobbits al inicio del turno IA:");
						e.printStackTrace();
					}
				});

				List<Personaje> ejercitoMordor = new ArrayList<>(mapa.getTodasLasUnidades().stream()
						.filter(pj -> pj.getFaccion() == Faccion.BESTIAS && pj.estaVivo()).toList());

				estrategia.iniciarTurno();

				for (Personaje bestia : ejercitoMordor) {
					if (vista.estaPartidaAcabada()) {
						break;
					}
					if (!bestia.estaVivo()) {
						continue;
					}
					try {
						procesarTurnoDeBestias(bestia);

					} catch (Exception e) {
						System.err.println("Error procesando el turno de la bestia '" + bestia.getNombre() + "':");
						e.printStackTrace();
					}
					Thread.sleep(600);
				}

				gestionarReclutamientoDeLasBestias();

				Thread.sleep(800);
				finalizarTurnoDeFormaSegura();

			} catch (Exception e) {
				System.err.println("Error en el procesador del turno de la IA:");
				e.printStackTrace();
				finalizarTurnoDeFormaSegura();
			}
		}).start();
	}

	/**
	 * Metodo en el que le pedimos a la estrategia que decida una accion. Si va a
	 * decidir atacar, mover o esperar.
	 *
	 * @param bestia the bestia
	 */
	private void procesarTurnoDeBestias(Personaje bestia) {
		AccionIA accion = estrategia.decidirAccion(bestia, mapa, configuracionVictoria);

		if (accion.getTipoAccion() == AccionIA.Tipo.ESPERAR) {
			return;
		}

		if (accion.getTipoAccion() == AccionIA.Tipo.ATACAR) {
			final Personaje objetivoFinal = accion.getObjetivo();

			Platform.runLater(() -> {
				try {
					List<Personaje> hobbitsConCambioDeteccion = mapa.actualizarDeteccionHobbits();

					if (!hobbitsConCambioDeteccion.isEmpty()) {
						vista.notificarCambiosDeteccion(hobbitsConCambioDeteccion);
					}
					int danio = bestia.calcularDanioFinal(objetivoFinal, mapa);
					bestia.setHaAtacadoEsteTurno(true);
					vista.registrarTiradaDado(bestia.getUltimaTiradaDados(), bestia.getFaccion());
					objetivoFinal.recibirDanio(danio);
					vista.registrarAccion("💥 IA: " + bestia.getNombre() + " ataca a " + objetivoFinal.getNombre()
							+ " infligiendo " + danio + " HP.", TipoEvento.ATAQUE);

					if (objetivoFinal.getFaccion() == Faccion.HEROES && objetivoFinal.estaVivo()) {
						GestorSonido.reproducirAliadaAtacada();
					}

					if (!objetivoFinal.estaVivo()) {
						if (objetivoFinal.getFaccion() == Faccion.HEROES) {
							GestorSonido.reproducirAliadoEliminado();
						}

						Ciudad ciudadDeEliminacion = mapa.procesarMuerteEnCombate(objetivoFinal);

						if (ciudadDeEliminacion != null) {
							vista.refrescarGuarnicionVisible(ciudadDeEliminacion);
						}

						vista.registrarAccion("💀 " + objetivoFinal.getNombre()
								+ " ha caido defendiendo la posición ante la bestia " + bestia.getNombre() + ".",
								TipoEvento.MUERTE);

						vista.comprobarCondicionesDeVictoriaInmediatas();
					}

					if (bestia instanceof Nazgul) {
						for (int[] destinoHuida : estrategia.casillasCandidatasParaMovimientoPostAtaqueNazgul(bestia,
								mapa)) {
							if (mapa.moverUnidadPostAtaque(objetivoFinal, destinoHuida[0], destinoHuida[1])) {
								vista.registrarAccion(
										"💨 IA: " + bestia.getNombre() + " aprovecha su vuelo para retirarse a ("
												+ destinoHuida[0] + "," + destinoHuida[1] + ").",
										TipoEvento.MOVIMIENTO);
								List<Personaje> hobbitsTrasRetirada = mapa.actualizarDeteccionHobbits();

								if (!hobbitsTrasRetirada.isEmpty()) {
									vista.notificarCambiosDeteccion(hobbitsTrasRetirada);
								}
								break;
							}
						}
					}

					vista.refrescarPanelVida();
					vista.getMapaRenderizado().dibujarMapa(mapa);

				} catch (Exception e) {
					System.err.println("Error procesando el turno de la bestia '" + bestia.getNombre() + "':");
					e.printStackTrace();
				}
			});

			return;
		}

		final int fDestinoX = accion.getDestinoX();
		final int fDestinoY = accion.getDestinoY();

		Platform.runLater(() -> {
			try {
				boolean exito = mapa.moverUnidad(bestia, fDestinoX, fDestinoY);

				if (exito) {
					vista.registrarAccion(
							"🚶 IA: " + bestia.getNombre() + " avanza a (" + fDestinoX + "," + fDestinoY + ")",
							TipoEvento.MOVIMIENTO);

					List<Personaje> hobbitsConCambioDeteccion = mapa.actualizarDeteccionHobbits();

					if (!hobbitsConCambioDeteccion.isEmpty()) {
						vista.notificarCambiosDeteccion(hobbitsConCambioDeteccion);
					}
				}

				vista.getMapaRenderizado().dibujarMapa(mapa);

			} catch (Exception e) {
				System.err.println("Error moviendo a la bestia '" + bestia.getNombre() + "':");
				e.printStackTrace();
			}
		});
	}

	/**
	 * Metodo para gestinar el reclutamiento de tropas. Se le pide a estrategia,
	 * segun dificultad que decida que va a reclutar.
	 */
	private void gestionarReclutamientoDeLasBestias() {

		if (reclutamientoControlador == null || tropasMaximas <= 0) {
			return;
		}
		Platform.runLater(() -> {
			try {
				estrategia.decidirReclutamiento(Faccion.BESTIAS, mapa, rosterTropasBestias, reclutamientoControlador,
						tropasMaximas);
				vista.actualizarHudTropas();

			} catch (Exception e) {
				System.err.println("Error gestionando el reclutamiento de las Bestias:");
				e.printStackTrace();
			}
		});
	}

	/**
	 * Metodo para finalizar el turno de la IA. Comprobaremos primero que la partida
	 * no haya sido terminada.
	 */
	private void finalizarTurnoDeFormaSegura() {
		Platform.runLater(() -> {
			try {
				if (!vista.estaPartidaAcabada()) {
					vista.finalizarTurno();
				}
			} catch (Exception e) {
				System.err.println("Error al finalizar el turno de la IA (el turno podría haberse quedado a medias):");
				e.printStackTrace();
			}
		});
	}
}