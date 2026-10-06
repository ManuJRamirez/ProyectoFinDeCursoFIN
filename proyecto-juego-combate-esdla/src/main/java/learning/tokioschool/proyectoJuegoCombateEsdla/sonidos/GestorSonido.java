package learning.tokioschool.proyectoJuegoCombateEsdla.sonidos;

import java.net.URL;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

// TODO: Auto-generated Javadoc
/**
 * The Class GestorSonido.
 * 
 * Clase para gestionar el sonido. Reproduce sonido de fondo y los sonidos de
 * efectos. Ademas permitira silenciar el sonido de fondo y los efectos de forma
 * independiente.
 */
public class GestorSonido {

	/** The Constant RUTA_BASE. */
	private static final String RUTA_BASE = "/sonidos/";

	/** The Constant ARCHIVO_MUSICA_FONDO. */
	private static final String ARCHIVO_MUSICA_FONDO = "sonido_fondo_1.mp3";

	/** The Constant ARCHIVO_FRODO_DETECTADO. */
	private static final String ARCHIVO_FRODO_DETECTADO = "sonido_deteccion_frodo.mp3";

	/** The Constant ARCHIVO_ALIADO_ATACADO. */
	private static final String ARCHIVO_ALIADO_ATACADO = "sonido_unidad_aliada_atacada.mp3";

	/** The Constant ARCHIVO_ALIADO_ELIMINADO. */
	private static final String ARCHIVO_ALIADO_ELIMINADO = "sonido_unidad_aliada_eliminada.mp3";

	/** The Constant ARCHIVO_VICTORIA. */
	private static final String ARCHIVO_VICTORIA = "sonido_victoria.mp3";

	/** The Constant ARCHIVO_DERROTA. */
	private static final String ARCHIVO_DERROTA = "sonido_derrota.mp3";

	/** The Constant ARCHIVO_CIUDAD_ASEDIADA. */
	private static final String ARCHIVO_CIUDAD_ASEDIADA = "sonido_ciudad_aliada_atacada.mp3";

	/** The Constant ARCHIVO_CIUDAD_PERDIDA. */
	private static final String ARCHIVO_CIUDAD_PERDIDA = "sonido_ciudad_perdida.mp3";

	/** The Constant ARCHIVO_RECLUTAMIENTO. */
	private static final String ARCHIVO_RECLUTAMIENTO = "sonido_reclutar.mp3";

	/** The Constant ARCHIVO_MI_TURNO. */
	private static final String ARCHIVO_MI_TURNO = "sonido_mi_turno.mp3";

	/** The musica fondo. */
	private static MediaPlayer musicaFondo;

	/** The clip frodo detectado. */
	private static AudioClip clipFrodoDetectado;

	/** The clip aliado atacado. */
	private static AudioClip clipAliadoAtacado;

	/** The clip aliado eliminado. */
	private static AudioClip clipAliadoEliminado;

	/** The clip victoria. */
	private static AudioClip clipVictoria;

	/** The clip derrota. */
	private static AudioClip clipDerrota;

	/** The clip ciudad asediada. */
	private static AudioClip clipCiudadAsediada;

	/** The clip ciudad perdida. */
	private static AudioClip clipCiudadPerdida;

	/** The clip tropa reclutada. */
	private static AudioClip clipTropaReclutada;

	/** The clip inicio turno. */
	private static AudioClip clipInicioTurno;

	/** The efectos cargados. */
	private static boolean efectosCargados = false;

	/** The musica silenciada. */
	private static boolean musicaSilenciada = false;

	/** The efectos silenciados. */
	private static boolean efectosSilenciados = false;

	/** Volumen actual de la musica de fondo. */
	private static double volumenMusicaFondo = 0.65;

	/** Volumen actual de los efectos. */
	private static double volumenEfectos = 0.75;

	/**
	 * Instantiates a new gestor sonido.
	 */
	private GestorSonido() {

	}

	/**
	 * Metodo para inicializar los archivos de los sonidos en cada uno de los
	 * correspondientes atributos.
	 */
	public static void inicializar() {
		if (efectosCargados) {
			return;
		}

		clipFrodoDetectado = cargarClip(ARCHIVO_FRODO_DETECTADO);
		clipAliadoAtacado = cargarClip(ARCHIVO_ALIADO_ATACADO);
		clipAliadoEliminado = cargarClip(ARCHIVO_ALIADO_ELIMINADO);
		clipVictoria = cargarClip(ARCHIVO_VICTORIA);
		clipDerrota = cargarClip(ARCHIVO_DERROTA);
		clipCiudadAsediada = cargarClip(ARCHIVO_CIUDAD_ASEDIADA);
		clipCiudadPerdida = cargarClip(ARCHIVO_CIUDAD_PERDIDA);
		clipTropaReclutada = cargarClip(ARCHIVO_RECLUTAMIENTO);
		clipInicioTurno = cargarClip(ARCHIVO_MI_TURNO);
		efectosCargados = true;
	}

	/**
	 * Metodo para cargar los archivos de audio aplicandole el volumen pre
	 * establecido.
	 *
	 * @param nombreArchivo Nombre del archivo definido en los atributos estaticos.
	 * @return the audio clip
	 */
	private static AudioClip cargarClip(String nombreArchivo) {
		URL url = GestorSonido.class.getResource(RUTA_BASE + nombreArchivo);

		if (url == null) {
			System.err.println("GestorSonido: no se ha encontrado el archivo de audio " + RUTA_BASE + nombreArchivo);
			return null;
		}
		try {
			AudioClip clip = new AudioClip(url.toExternalForm());
			clip.setVolume(volumenEfectos);
			return clip;
		} catch (Exception e) {
			System.err.println("GestorSonido: error cargando " + nombreArchivo + ": " + e.getMessage());
			return null;
		}
	}

	/**
	 * Metodo para reproducir la musica de fondo en bucle.
	 */
	public static void reproducirMusicaFondo() {
		if (musicaFondo != null) {
			return;
		}
		URL url = GestorSonido.class.getResource(RUTA_BASE + ARCHIVO_MUSICA_FONDO);

		if (url == null) {
			System.err.println("GestorSonido: no se ha encontrado la música de fondo " + ARCHIVO_MUSICA_FONDO);
			return;
		}

		try {
			Media media = new Media(url.toExternalForm());
			musicaFondo = new MediaPlayer(media);
			musicaFondo.setCycleCount(MediaPlayer.INDEFINITE);
			musicaFondo.setVolume(musicaSilenciada ? 0.0 : volumenMusicaFondo);
			musicaFondo.play();
		} catch (Exception e) {
			System.err.println("⚠️ GestorSonido: error reproduciendo la música de fondo: " + e.getMessage());
			musicaFondo = null;
		}
	}

	/**
	 * Metodo para detener la musica de fondo.
	 */
	public static void detenerMusicaFondo() {
		if (musicaFondo == null) {
			return;
		}
		musicaFondo.stop();
		musicaFondo.dispose();
		musicaFondo = null;
	}

	/**
	 * Metodo para reproducir efecto Frodo detectado.
	 */
	public static void reproducirFrodoDetectado() {
		reproducirClip(clipFrodoDetectado);
	}

	/**
	 * Metodo para reproducir efecto unidad aliada atacada.
	 */
	public static void reproducirAliadaAtacada() {
		reproducirClip(clipAliadoAtacado);
	}

	/**
	 * Metodo para reproducir efecto unidad aliada eliminada.
	 */
	public static void reproducirAliadoEliminado() {
		reproducirClip(clipAliadoEliminado);
	}

	/**
	 * Metodo para reproducir efecto de victoria.
	 */
	public static void reproducirVictoria() {
		reproducirClip(clipVictoria);
	}

	/**
	 * Metodo para reproducir efecto de derrota.
	 */
	public static void reproducirDerrota() {
		reproducirClip(clipDerrota);
	}

	/**
	 * Metodo para reproducir efecto de ciudad propia asediada.
	 */
	public static void reproducirCiudadPropiaAsediada() {
		reproducirClip(clipCiudadAsediada);
	}

	/**
	 * Metodo para reproducir efecto de ciudad propia perdida.
	 */
	public static void reproducirCiudadPropiaPerdida() {
		reproducirClip(clipCiudadPerdida);
	}

	/**
	 * Metodo para reproducir efecto de tropa reclutada.
	 */
	public static void reproducirTropaReclutada() {
		reproducirClip(clipTropaReclutada);
	}

	/**
	 * Metodo para reproducir efecto de inicio mi turno.
	 */
	public static void reproducirInicioMiTurno() {
		reproducirClip(clipInicioTurno);
	}

	/**
	 * Metodo para reproducir el clip de audio indicado, si los efectos no estan
	 * silenciados. En caso de que efectosSilenciados sea true, no se reproducira el
	 * clip.
	 *
	 * @param clip the clip
	 */
	public static void reproducirClip(AudioClip clip) {
		if (clip != null && !efectosSilenciados) {
			clip.play();
		}
	}

	/**
	 * Metodo para establecer el volumen de la musica de fondo. Por ahora esto sera
	 * pre establecida en el back-end y no tendra implementacion grafica.
	 *
	 * @param volumen the new volumen musica fondo
	 */
	public static void setVolumenMusicaFondo(double volumen) {
		volumenMusicaFondo = volumen;
		if (musicaFondo != null && !musicaSilenciada) {
			musicaFondo.setVolume(volumen);
		}
	}

	/**
	 * Metodo para establecer el volumen de los efectos de sonidos. Por ahora esto
	 * sera pre establecida en el back-end y no tendra implementacion grafica.
	 *
	 * @param volumen the new volumen efectos
	 */
	public static void setVolumenEfectos(double volumen) {
		volumenEfectos = volumen;
	}

	/**
	 * Metodo para saber si la musica de fondo esta silenciada.
	 *
	 * @return true, if successful
	 */
	public static boolean estaMusicaSilenciada() {
		return musicaSilenciada;
	}

	/**
	 * Metodo apra saber si los efectos de sonidos estan silenciados
	 *
	 * @return true, if successful
	 */
	public static boolean estaEfectosSilenciados() {
		return efectosSilenciados;
	}

	/**
	 * Metodo para hacer que si la musica esta activa, pasar a ser silenciada y
	 * viceversa.
	 */
	public static void alternarMusicaSilenciada() {
		setMusicaSilenciada(!musicaSilenciada);
	}

	/**
	 * Metodo para silenciar la musica, si se le pasa un true por parametros.
	 *
	 * @param silenciada the new musica silenciada
	 */
	public static void setMusicaSilenciada(boolean silenciada) {
		musicaSilenciada = silenciada;
		if (musicaFondo != null) {
			musicaFondo.setVolume(silenciada ? 0.0 : volumenMusicaFondo);
		}
	}

	/**
	 * Metodo para hacer que si los efectos estan activos, pasar a ser silenciados y
	 * viceversa. Se complementa junto con el metodo reproducir clip.
	 */
	public static void alternarEfectosSilenciados() {
		efectosSilenciados = !efectosSilenciados;
	}
}
