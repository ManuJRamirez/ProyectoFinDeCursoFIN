package learning.tokioschool.proyectoJuegoCombateEsdla.grafico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Ciudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.ControlCiudad;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;

/**
 * The Class CargadorCiudades.
 * 
 * Clase encargada de cargar la información de las Ciudades desde el archivo
 * JSON "ciudades.json" situado en "data" dentro de "src/main/resources".
 * 
 * El formato del JSON es el siguiente:
 * 
 * <pre>
 * {
 *   "ciudades": [
 *     {
 *       "nombre": "Minas Tirith",
 *       "casillas": [[10, 20], [11, 20], [12, 20]],
 *       "territorio": [[9, 19], [13, 21]],
 *       "control": "HEROES"
 *     }
 *   ]
 * }
 * </pre>
 */
public class CargadorCiudades {

	/** The Constant RUTA_JSON. */
	private static final String RUTA_JSON = "/data/ciudades.json";

	/**
	 * Método estático el cual realiza la operación de cargar los datos de las
	 * ciudades desde el JSON. Además de cargarlos, gestiona los datos que obtiene
	 * del archivo, creando la ciudad con el nombre establecido en el JSON y
	 * asignandole las casillas que forman la ciudad y las casillas que estan dentro
	 * del territorio de dicha ciudad. Para esto usamos la dependencia GSON.
	 * 
	 *
	 * @param mapa the mapa
	 */
	public static void cargarCiudades(Mapa mapa) {

		Gson gson = new Gson();

		InputStream is = CargadorCiudades.class.getResourceAsStream(RUTA_JSON);

		if (is == null) {
			System.err.println("No se encontro el archivo JSON: " + RUTA_JSON);
			return;
		}

		try (BufferedReader leerArchivo = new BufferedReader(new InputStreamReader(is))) {

			JsonObject archivoJson = gson.fromJson(leerArchivo, JsonObject.class);

			if (archivoJson == null || !archivoJson.has("ciudades")) {
				System.err.println("Error en la estructura del archivo json cargador de ciudades.");
				return;
			}

			JsonArray ciudadesJSON = archivoJson.getAsJsonArray("ciudades");

			for (JsonElement elementoJSON : ciudadesJSON) {
				JsonObject ciudadJSON = elementoJSON.getAsJsonObject();
				String nombreCiudad = "Ciudad Desconocida";

				if (ciudadJSON.has("nombre")) {
					nombreCiudad = ciudadJSON.get("nombre").getAsString();
				}

				Ciudad ciudad = new Ciudad(nombreCiudad);

				JsonArray casillas = ciudadJSON.getAsJsonArray("casillas");
				for (JsonElement punto : casillas) {
					JsonArray xy = punto.getAsJsonArray();
					ciudad.addCasillaCiudad(xy.get(0).getAsInt(), xy.get(1).getAsInt());
				}

				if (ciudadJSON.has("territorio")) {
					JsonArray territorio = ciudadJSON.getAsJsonArray("territorio");
					for (JsonElement punto : territorio) {
						JsonArray xy = punto.getAsJsonArray();
						ciudad.addTerritorio(xy.get(0).getAsInt(), xy.get(1).getAsInt());
					}
				}

				if (ciudadJSON.has("control")) {
					ciudad.setControlSobreCiudad(ControlCiudad.valueOf(ciudadJSON.get("control").getAsString()));
				}
				
				ciudad.fijarControlInicial();

				mapa.addCiudad(ciudad);
			}

		} catch (IOException e) {
			System.err.println("Error leyendo archivo JSON: " + RUTA_JSON);
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error procesando ciudades desde el JSON");
			e.printStackTrace();
		}

	}
}
