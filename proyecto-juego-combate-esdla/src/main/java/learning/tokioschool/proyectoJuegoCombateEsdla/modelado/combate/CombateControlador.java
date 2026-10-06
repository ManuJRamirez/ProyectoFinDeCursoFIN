package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.combate;

import java.util.ArrayList;
import java.util.List;

import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.PantallaJuegoControlador;
import learning.tokioschool.proyectoJuegoCombateEsdla.grafico.PantallaJuegoControlador.TipoEvento;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoTerreno;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.humanos.Humano;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.trasgos.Trasgo;

/**
 * The Class CombateControlador.
 * 
 * Clase Controlador encargada de gestionar el combate entre dos unidades, Se
 * encargara de crear los textos para los logs y tendra en cuenta las
 * caracteristicas de cada unidad
 */
public class CombateControlador {

	/** Mapa del juego. */
	private Mapa mapa;

	/** Controlador de la pantalla del juego para dejar reflejado el combate. */
	private PantallaJuegoControlador vista;

	/**
	 * Controlador de clase.
	 *
	 * @param mapa  the mapa
	 * @param vista the vista
	 */
	public CombateControlador(Mapa mapa, PantallaJuegoControlador vista) {
		this.mapa = mapa;
		this.vista = vista;
	}

	/**
	 * Metodo para ejecutar un ataque. Comprueba que se pueda realizar el ataque
	 * (que la unidad aliada pueda atacar a la enemiga y que no haya atacado aun),
	 * calcula el daño, marca la unidad como "ya ha atacado", gestiona la
	 * invisibilidad de los hobbits y la muerte de una unidad.
	 *
	 * @param unidadAliadaSeleccionada  the unidad aliada seleccionada
	 * @param unidadEnemigaSeleccionada the unidad enemiga seleccionada
	 */
	public void ejecutarAtaque(Personaje unidadAliadaSeleccionada, Personaje unidadEnemigaSeleccionada) {
		if (unidadAliadaSeleccionada == null || unidadEnemigaSeleccionada == null) {
			return;
		}

		if (unidadAliadaSeleccionada.HaAtacadoEsteTurno()) {
			vista.registrarAccion("⚠️ " + unidadAliadaSeleccionada.getNombre() + " ya ha atacado este turno.",
					TipoEvento.ATAQUE);
			return;
		}
		if (!unidadAliadaSeleccionada.puedeAtacarObjetivo(unidadEnemigaSeleccionada, mapa)) {
			vista.registrarAccion("🐎 " + unidadAliadaSeleccionada.getNombre()
					+ " no puede atacar a una unidad protegida tras las murallas de la ciudad: debe entrar dentro para atacarla.",
					TipoEvento.ATAQUE);
			return;
		}

		int danioFinal = unidadAliadaSeleccionada.calcularDanioFinal(unidadEnemigaSeleccionada, mapa);
		vista.registrarTiradaDado(unidadAliadaSeleccionada.getUltimaTiradaDados(),
				unidadAliadaSeleccionada.getFaccion());
		unidadEnemigaSeleccionada.recibirDanio(danioFinal);
		unidadAliadaSeleccionada.setHaAtacadoEsteTurno(true);
		vista.actualizarBotonAtacar();

		vista.registrarAccion("⚔️ " + unidadAliadaSeleccionada.getNombre() + " ataca a "
				+ unidadEnemigaSeleccionada.getNombre() + " infligiendo " + danioFinal + " de daño.",
				TipoEvento.ATAQUE);

		if (unidadAliadaSeleccionada instanceof Hobbit) {
			boolean yaEstabaDetectado = unidadAliadaSeleccionada.esDetectadoPorEnemigo();
			unidadAliadaSeleccionada.setTurnosInmovil(0);

			if (!yaEstabaDetectado) {
				unidadAliadaSeleccionada.setDetectadoPorEnemigo(true);
				vista.registrarAccion("👁️ " + unidadAliadaSeleccionada.getNombre()
						+ " ha revelado su posición al atacar. Pierde la invisibilidad.", TipoEvento.DETECCION);
				vista.reproducirAlertaSiEsFrodo(unidadEnemigaSeleccionada);
			}
		}

		if (!unidadEnemigaSeleccionada.estaVivo()) {
			vista.registrarAccion("💀 " + unidadEnemigaSeleccionada.getNombre()
					+ " ha sido derrotado en el campo de batalla por " + unidadAliadaSeleccionada.getNombre() + ".",
					TipoEvento.MUERTE);

			Ciudad ciudadDelCombate = mapa.procesarMuerteEnCombate(unidadEnemigaSeleccionada);

			if (ciudadDelCombate != null) {
				vista.refrescarGuarnicionVisible(ciudadDelCombate);
			}

			vista.seleccionarUnidad(null);

		} else {
			vista.refrescarPanelVida();
			vista.actualizarPanelesBonus();
			if (unidadAliadaSeleccionada instanceof Hobbit) {
				vista.actualizarEstadoVisibilidadHobbit(unidadAliadaSeleccionada);
			}
		}
		vista.getMapaRenderizado().dibujarMapa(mapa);
	}

	/**
	 * Metodo encargado de gestionar los textos descriptivos en cuento al movimiento
	 * y deteccion de cada unidad en las tarjetas de seleccion.
	 *
	 * @param pj the pj
	 * @return the string
	 */
	public String construirTextoBonus(Personaje pj) {
		if (pj == null || mapa == null) {
			return "";
		}

		List<String> lineas = new ArrayList<>();
		boolean puedeMontania = mapa.puedeAtravesarTerreno(pj, TipoTerreno.MONTANIA);
		boolean puedeRio = mapa.puedeAtravesarTerreno(pj, TipoTerreno.RIO);

		if (!puedeMontania && !puedeRio) {
			lineas.add("🚫 No puede andar por montañas ni ríos.");

		} else {
			if (puedeMontania) {
				int coste = mapa.costePaso(pj, TipoTerreno.MONTANIA);
				lineas.add(
						"⛰️ Puede andar por montañas" + (coste > 1 ? " (coste de movimiento: " + coste + ")." : "."));
			}
			if (puedeRio) {
				int coste = mapa.costePaso(pj, TipoTerreno.RIO);
				lineas.add("🌊 Puede andar por ríos" + (coste > 1 ? " (coste de movimiento: " + coste + ")." : "."));
			}
		}

		int rangoDeteccionNormal = mapa.calcularRangoDeteccion(pj, false);
		if (rangoDeteccionNormal > 0) {
			int rangoDeteccionMontania = mapa.calcularRangoDeteccion(pj, true);
			if (rangoDeteccionMontania != rangoDeteccionNormal) {
				lineas.add("👁️ Detecta Hobbits en radio " + rangoDeteccionNormal + " (radio " + rangoDeteccionMontania
						+ " si está en montaña).");
			} else {
				lineas.add("👁️ Detecta Hobbits en radio " + rangoDeteccionNormal + ".");
			}
		}
		lineas.addAll(construirRasgosEspeciales(pj));

		return String.join("\n", lineas);
	}

	/**
	 * Añade una lista de lineas de texto descriptivo con los rasgos especiales de
	 * cada personaje
	 *
	 * @param pj the pj
	 * @return the list lista de lineas de texto con la descripcion de las unidades.
	 */
	public List<String> construirRasgosEspeciales(Personaje pj) {
		List<String> lineas = new ArrayList<>();

		if (pj instanceof Nazgul) {
			lineas.add("🚫 No puede atacar a los Hobbits ni conquistar ciudades.");
		}

		if (pj instanceof Trasgo) {
			String tipoCombate = pj.tipoCombate();
			if ("GUERRERO".equalsIgnoreCase(tipoCombate)) {
				lineas.add("💚 Puede curarse a sí mismo, al finalizar su turno.");
			}
		}
		if (pj instanceof Humano) {
			String tipoCombate = pj.tipoCombate();
			if ("MAGO".equalsIgnoreCase(tipoCombate)) {
				lineas.add("💚 Puede curarse a sí mismo y a sus aliados en rango de ataque al finalizar su turno.");
			}
		}

		if (pj instanceof Hobbit) {
			Hobbit hobbit = (Hobbit) pj;
			lineas.add("🚫 No puede conquistar ciudades.");
			lineas.add("🗡️ Los voladores no pueden atacarle (solo descubrirle).");

			if (hobbit.companieroEnRango(mapa)) {
				lineas.add("🤝 " + hobbit.getNombreCompaniero()
						+ " está cerca (≤4 casillas): bono vs Exploradores al +200%.");
			}

			if ("Frodo".equalsIgnoreCase(pj.getNombre())) {
				lineas.add("🏆 Si llega al Monte del Destino, ganan los Héroes.");
				lineas.add("⚠️ Si es eliminado, ganan las Bestias.");
			}
		}
		return lineas;
	}

	/**
	 * Metodo para añadir lineas a las tarjetas de seleccion donde añadira el
	 * porcentaje de bonus o penalizacion de ataque de una unidad aliada
	 * seleccionada contra una unidad enemiga seleccionada, si los tuviera.
	 *
	 * @param atacante the atacante
	 * @param defensor the defensor
	 * @return the string
	 */
	public String construirTextoBonusAtaque(Personaje atacante, Personaje defensor) {
		if (atacante == null || defensor == null || !defensor.estaVivo() || mapa == null) {
			return null;
		}

		double multiplicador = atacante.getMultiplicadorAtaque(defensor, mapa);
		double diferencia = (multiplicador - 1.0) * 100;

		if (Math.abs(diferencia) < 0.5) {
			return null;
		}
		if (diferencia > 0) {
			return String.format("🟢 Bonus de ataque vs %s: +%.0f%%", defensor.getNombre(), diferencia);
		}

		return String.format("🔴 Penalización de ataque vs %s: %.0f%%", defensor.getNombre(), diferencia);
	}

	/**
	 * Metodo para añadir lineas a las tarjetas de seleccion donde añadira el
	 * porcentaje de bonus o penalizacion de defensa de una unidad aliada
	 * seleccionada contra una unidad enemiga seleccionada, si los tuviera.
	 *
	 * @param defensor the defensor
	 * @param atacante the atacante
	 * @return the string
	 */
	public String construirTextoBonusDefensa(Personaje defensor, Personaje atacante) {
		if (defensor == null || atacante == null || !defensor.estaVivo() || mapa == null) {
			return null;
		}

		if (defensor instanceof Nazgul && atacante instanceof Hobbit) {
			return null;
		}

		double modificador = defensor.getModificadorDefensa(atacante, mapa);
		double diferencia = (modificador - 1.0) * 100;

		if (Math.abs(diferencia) < 0.5) {
			return null;
		}
		if (diferencia > 0) {
			return String.format("🛡️ Bonus de defensa vs %s: +%.0f%%", atacante.getNombre(), diferencia);
		}

		return String.format("🔴 Penalización de defensa vs %s: %.0f%%", atacante.getNombre(), diferencia);
	}

	/**
	 * Metodo por el cual intenta curar al finalizar el turno, usando un porcentaje
	 * de probabilidad que se le aportara segun cada clase.
	 *
	 * @param sanador            pj que cura
	 * @param probabilidad       probabilidad de que se ejecute esa cura
	 * @param porcentajeCuraVida porcentaje de vida que va a curar
	 * @param curaAliadosEnRango condicion si va a curar a personajes aliados o solo
	 *                           a si mismo.
	 */
	public void intentarCuracion(Personaje sanador, double probabilidad, double porcentajeCuraVida,
			boolean curaAliadosEnRango) {

		if (sanador == null || !sanador.estaVivo() || mapa == null) {
			return;
		}
		if (Math.random() >= probabilidad) {
			return;
		}

		List<Personaje> objetivos = new ArrayList<>();
		objetivos.add(sanador);

		if (curaAliadosEnRango) {
			for (Personaje pj : mapa.getTodasLasUnidades()) {
				if (pj == sanador || !pj.estaVivo() || pj.getFaccion() != sanador.getFaccion()) {
					continue;
				}
				int distancia = Math.max(Math.abs(sanador.getX() - pj.getX()), Math.abs(sanador.getY() - pj.getY()));
				if (distancia <= sanador.getRangoAtaque()) {
					objetivos.add(pj);
				}
			}
		}

		for (Personaje pjParaCurar : objetivos) {
			int curacion = (int) Math.round(pjParaCurar.getVidaMaxima() * porcentajeCuraVida);
			if (curacion <= 0) {
				continue;
			}

			int vidaAntes = pjParaCurar.getVida();
			int vidaNueva = Math.min(pjParaCurar.getVidaMaxima(), vidaAntes + curacion);
			int curacionReal = vidaNueva - vidaAntes;

			if (curacionReal > 0) {
				pjParaCurar.setVida(vidaNueva);
				vista.registrarAccion("💚 " + sanador.getNombre() + " cura a " + pjParaCurar.getNombre() + " (+"
						+ curacionReal + " HP).", TipoEvento.CURACION);
			}
		}
	}

	/**
	 * Metodo que va a ejecutar la curacion del bando de los Heroes al finalizar su
	 * turno.
	 */
	public void ejecutarCuracionFinTurnoHeroes() {
		if (mapa == null) {
			return;
		}

		for (Personaje pj : new ArrayList<>(mapa.getTodasLasUnidades())) {
			if (pj instanceof Humano && pj.estaVivo()) {
				String tipoCombate = pj.tipoCombate();
				if ("MAGO".equalsIgnoreCase(tipoCombate)) {
					intentarCuracion(pj, 0.10, 0.10, true);
				}
			}
		}
	}

	/**
	 * Metodo que va a ejecutar la curacion del bando de las Bestias al finalizar su
	 * turno.
	 */
	public void ejecutarCuracionFinTurnoBestias() {
		if (mapa == null) {
			return;
		}
		for (Personaje pj : new ArrayList<>(mapa.getTodasLasUnidades())) {
			if (pj instanceof Trasgo && pj.estaVivo()) {
				String tipoCombate = pj.tipoCombate();
				if ("GUERRERO".equalsIgnoreCase(tipoCombate)) {
					intentarCuracion(pj, 0.25, 0.15, false);
				}
			}
		}
	}

}