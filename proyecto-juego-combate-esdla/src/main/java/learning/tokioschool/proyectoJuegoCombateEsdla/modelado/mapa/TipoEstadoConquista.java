package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa;

/**
 * The Enum TipoEstadoConquista.
 * 
 * Representara los estados de conquista de una ciudad.
 */
public enum TipoEstadoConquista {

	/** No hay conquista en curso. */
	REPOSO,
	/** Hay unidad/es de las dos facciones dentro de la ciudad luchando por ella. */
	DISPUTA,
	/** Una faccion esta en proceso de captura de la ciudad. */
	EN_PROGRESO
}
