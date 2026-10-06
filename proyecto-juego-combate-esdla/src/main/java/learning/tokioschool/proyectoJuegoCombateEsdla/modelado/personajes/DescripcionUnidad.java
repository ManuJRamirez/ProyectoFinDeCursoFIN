package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.humanos.Humano;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.trasgos.Trasgo;

/**
 * The Class DescripcionUnidad.
 * 
 * Se encarga para construir el texto descriptivo que resume las estadisticas y
 * habilidades especiales de cada personajes en los tooltips del juego.
 */
public class DescripcionUnidad {

	/**
	 * Instantiates a new descripcion unidad.
	 */
	public DescripcionUnidad() {

	}

	/**
	 * Generar el texto descriptivo del personaje. nombre, raza, tipo de
	 * combate, estadísticas base, restricciones de terreno y rasgos especiales
	 * según su raza y tipo de combate...
	 *
	 * @param pj personaje sobre el que se genera el texto
	 * @return el texto descriptivo de la unidad
	 */
	public static String generar(Personaje pj) {
		if (pj == null) {
			return "";
		}
		StringBuilder texto = new StringBuilder();
		texto.append(pj.getNombre()).append(" (").append(pj.getRaza());

		if (pj.getTipoCombate() != null && !pj.getTipoCombate().isBlank()) {
			texto.append(" - ").append(pj.getTipoCombate());
		}
		texto.append(")\n\n");
		texto.append("❤️ Vida: ").append(pj.getVidaMaxima()).append("\n");
		texto.append("🛡️ Defensa: ").append(pj.getArmadura()).append("\n");
		texto.append("🏃 Movilidad: ").append(pj.getMovimiento()).append(" casillas\n");
		texto.append("⚔️ Rango de ataque: ").append(pj.getRangoAtaque()).append(" casillas\n");
		texto.append(descripcionTerrenos(pj));

		boolean esExplorador = pj instanceof Trasgo && "EXPLORADOR".equalsIgnoreCase(pj.getTipoCombate());
		boolean esGuerreroTrasgo = pj instanceof Trasgo && "GUERRERO".equalsIgnoreCase(pj.getTipoCombate());
		boolean esMago = pj instanceof Humano && "MAGO".equalsIgnoreCase(pj.getTipoCombate());

		if (pj instanceof Nazgul) {
			texto.append("🦅 Unidad voladora.\n");
			texto.append("🚫 No puede atacar a los Hobbits (solo descubrirlos).\n");
			texto.append("🚫 No puede conquistar ciudades.\n");
		}

		if (esExplorador) {
			texto.append("👁️ Explorador: puede detectar a los Hobbits invisibles que tenga cerca.\n");
		}

		if (pj instanceof Hobbit) {
			texto.append("🫥 Unidad invisible: los enemigos no pueden atacarle salvo que le detecten.\n");
			texto.append("🚫 No puede conquistar ciudades.\n");
			if ("Frodo".equalsIgnoreCase(pj.getNombre())) {
				texto.append("💍 Es el portador del Anillo: si llega al Monte del Destino, ganan los Héroes.\n");
			}
		}

		if (esGuerreroTrasgo) {
			texto.append("💚 Puede curarse a sí mismo al finalizar su turno.\n");
		}

		if (esMago) {
			texto.append("💚 Puede curarse a sí mismo y a sus aliados en rango de ataque al finalizar su turno.\n");
		}
		return texto.toString().trim();
	}

	/**
	 * Crea las lineas de texto que describen las restricciones de terreno de un personaje.
	 *
	 * @param pj el presonaje sobre el que se crea.
	 * @return texto que detalla las restricciones.
	 */
	public static String descripcionTerrenos(Personaje pj) {
		StringBuilder texto = new StringBuilder();

		if (pj instanceof Nazgul) {
			texto.append("🌋 Puede sobrevolar todos los tipos de terreno sin ningún problema.\n");
			return texto.toString();
		}

		texto.append("🚫 No puede cruzar la lava.\n");

		if (pj instanceof Hobbit) {
			texto.append("🌊 Puede vadear los ríos.\n");
		} else {
			texto.append("🚫 No puede cruzar ríos.\n");
		}

		boolean puedeMontania = pj instanceof Hobbit
				|| (pj instanceof Trasgo && "EXPLORADOR".equalsIgnoreCase(pj.getTipoCombate()));
		if (puedeMontania) {
			texto.append("⛰️ Puede subir montañas.\n");
		} else {
			texto.append("🚫 No puede subir montañas.\n");
		}

		return texto.toString();
	}

}
