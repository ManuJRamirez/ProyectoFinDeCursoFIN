package learning.tokioschool.proyectoJuegoCombateEsdla.modelado.partida;

// TODO: Auto-generated Javadoc
/**
 * The Enum TipoCondicionVictoria.
 * 
 * * Enum con las distintas condiciones de victoria que se pueden activar al
 * configurar una partida.
 */
public enum TipoCondicionVictoria {

	/** Gana quien tenga más puntos al finalizar el número máximo de turnos. No se puede activar sola. Tiene que ir acompañada de otra.*/
	PUNTOS_AL_FINALIZAR_TURNOS,
	
	/**Gana el primer bando que alcance un número determinado de puntos. */
	LIMITE_DE_PUNTOS,
	
	/** Ganan los Héroes si Frodo resiste varias rondas sin ser atacado en el Monte del Destino. No se puede activar solo. Tiene que ir acompañada de otra*/
	FRODO_LLEGA_AL_MONTE_DEL_DESTINO,
	
	/** Ganan las Bestias si consiguen eliminar a Frodo. No se puede activar solo. Tiene que ir acompañada de otra*/
	FRODO_ELIMINADO,
	
	/** Gana el bando que consiga conquistar todas las ciudades del rival. */
	DOMINIO
}
