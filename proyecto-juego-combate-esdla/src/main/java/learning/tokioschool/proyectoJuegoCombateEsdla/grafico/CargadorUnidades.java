package learning.tokioschool.proyectoJuegoCombateEsdla.grafico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.elfos.Elfo;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.hobbits.Hobbit;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.humanos.Humano;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.nazgul.Nazgul;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.orcos.Orco;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.trasgos.Trasgo;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.urukHai.UrukHai;

/**
 * The Class CargadorUnidades.
 * 
 * Clase encargada de cargar las unidades/personajes junto con toda su
 * informacion y caracteristicas, leyendolos del archivo "unidades.json". Para
 * ellos usaremos la dependencia GSON.
 */
public class CargadorUnidades {

	/** The Constant RUTA_JSON. */
	private static final String RUTA_JSON = "/data/unidades.json";

	/**
	 * Metodo para cargar las unidades del archivo json y crea una instancia de
	 * personaje.
	 *
	 * @return the list
	 */
	public static List<Personaje> cargarUnidades() {
		List<Personaje> listaPersonajes = new ArrayList<>();
		Gson gson = new Gson();
		InputStream canalEntrada = CargadorUnidades.class.getResourceAsStream(RUTA_JSON);

		if (canalEntrada == null) {
			System.err.println("No se encontró el archivo JSON: " + RUTA_JSON);
			return listaPersonajes;
		}

		try (BufferedReader leerArchivo = new BufferedReader(new InputStreamReader(canalEntrada))) {
			JsonObject archivoJson = gson.fromJson(leerArchivo, JsonObject.class);

			if (!archivoJson.has("unidades")) {
				System.err.println("Error en la estructura del archivo JSON de las unidades.");
				return listaPersonajes;
			}

			JsonArray unidadesJSON = archivoJson.getAsJsonArray("unidades");

			for (JsonElement elemento : unidadesJSON) {
				JsonObject unidadJSON = elemento.getAsJsonObject();

				String raza = unidadJSON.get("raza").getAsString();
				String categoria = unidadJSON.get("categoria").getAsString();
				String tipoCombate = unidadJSON.get("tipoCombate").getAsString();
				String nombre = unidadJSON.get("nombre").getAsString();
				Faccion faccion = Faccion.valueOf(unidadJSON.get("faccion").getAsString());
				int x = unidadJSON.get("x").getAsInt();
				int y = unidadJSON.get("y").getAsInt();
				int vida = unidadJSON.get("vida").getAsInt();
				int armadura = unidadJSON.get("armadura").getAsInt();
				int movimiento = unidadJSON.get("movimiento").getAsInt();
				int rango = unidadJSON.get("rangoAtaque").getAsInt();

				Personaje pj = crearUnidad(raza, nombre, vida, armadura, rango, movimiento, faccion);
				pj.setPosicion(x, y);
				pj.setxAnterior(x);
				pj.setyAnterior(y);
				pj.setCategoria(categoria);
				pj.setTipoCombate(tipoCombate);
				pj.setRaza(raza);

				listaPersonajes.add(pj);

				if (unidadJSON.has("turnosCreacion")) {
					pj.setTurnosCreacion(unidadJSON.get("turnosCreacion").getAsInt());
				}
			}

		} catch (IOException e) {
			System.err.println("Error leyendo archivo JSON: " + RUTA_JSON);
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error procesando unidades desde el JSON");
			e.printStackTrace();
		}

		return listaPersonajes;
	}

	/**
	 * Metodo usado para el reclutamiento que se encarga de clonar o crear una nueva
	 * instancia de una unidad.
	 *
	 * @param pj the pj
	 * @return the personaje
	 */
	public static Personaje clonarUnidad(Personaje pj) {
		Personaje nuevaUnidad = crearUnidad(pj.getRaza(), pj.getNombre(), pj.getVidaMaxima(), pj.getArmadura(),
				pj.getRangoAtaque(), pj.getMovimiento(), pj.getFaccion());
		nuevaUnidad.setCategoria(pj.getCategoria());
		nuevaUnidad.setTipoCombate(pj.getTipoCombate());
		nuevaUnidad.setRaza(pj.getRaza());
		nuevaUnidad.setTurnosCreacion(pj.getTurnosCreacion());

		return nuevaUnidad;
	}

	/**
	 * Metodo usado para crear una nueva unidad, en funcion de la raza del
	 * personaje.
	 *
	 * @param raza       the raza
	 * @param nombre     the nombre
	 * @param vida       the vida
	 * @param armadura   the armadura
	 * @param rango      the rango
	 * @param movimiento the movimiento
	 * @param faccion    the faccion
	 * @return the personaje
	 */
	private static Personaje crearUnidad(String raza, String nombre, int vida, int armadura, int rango, int movimiento,
			Faccion faccion) {

		return switch (raza) {
		case "ELFO" -> new Elfo(nombre, vida, armadura, rango, movimiento, faccion);
		case "HUMANO" -> new Humano(nombre, vida, armadura, rango, movimiento, faccion);
		case "HOBBIT" -> new Hobbit(nombre, vida, armadura, rango, movimiento, faccion);
		case "ORCO" -> new Orco(nombre, vida, armadura, rango, movimiento, faccion);
		case "TRASGO" -> new Trasgo(nombre, vida, armadura, rango, movimiento, faccion);
		case "URUKHAI" -> new UrukHai(nombre, vida, armadura, rango, movimiento, faccion);
		case "NAZGUL" -> new Nazgul(nombre, vida, armadura, rango, movimiento, faccion);

		default -> throw new IllegalArgumentException("Tipo de unidad desconocida");
		};
	}

}
