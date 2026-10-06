package learning.tokioschool.proyectoJuegoCombateEsdla.grafico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.Mapa;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.mapa.TipoTerreno;

/**
 * The Class CargadorTipoTerreno.
 * 
 * Clase encargada de cargar y gestionar el archivo "tipoTerrenos.json". Este
 * archivo definirá el terreno de cada casilla. En el constructor de la clase
 * Mapa, inicializaremos todas las casillas al tipo de terreno "LLANO". Con este
 * archivo lo que haremos será sobreescribir las casillas que aparecen con el
 * tipo de terreno definido en el JSON.
 * 
 * El archivo "tipoTerreno.json" se encuentra en la carpeta "data" dentro de
 * "src/main/resources".
 * 
 * La estructura del JSON es la siguiente:
 * 
 * <pre>
 * {
 *   "terreno": [
 *     { "x": 10, "y": 20, "tipo": "BOSQUE" },
 *     { "x": 11, "y": 20, "tipo": "RIO" },
 *     { "x": 12, "y": 20, "tipo": "LLANO" }
 *   ]
 * }
 * </pre>
 */
public class CargadorTipoTerreno {

	/** The Constant RUTA_JSON. */
	private static final String RUTA_JSON = "/data/tipoTerreno.json";

	/**
	 * Método estático el cual realiza la operación de cargar los datos de los tipos
	 * de terrenos para cada una de las casillas desde el JSON. Además de cargarlos,
	 * gestiona los datos que obtiene del archivo, extrayendo las coordenadas de las
	 * casillas, y modificando su tipo de terreno al especificado en el archivo.json
	 *
	 * @param mapa the mapa
	 */
	public static void cargarTerreno(Mapa mapa) {
		Gson gson = new Gson();

		InputStream canalEntrada = CargadorTipoTerreno.class.getResourceAsStream(RUTA_JSON);

		if (canalEntrada == null) {
			System.err.println("No se encontro el archivo JSON: " + RUTA_JSON);
			return;
		}

		try (BufferedReader leerArchivo = new BufferedReader(new InputStreamReader(canalEntrada))) {
			JsonObject archivoJson = gson.fromJson(leerArchivo, JsonObject.class);

			if (archivoJson == null || !archivoJson.has("terreno")) {
				System.err.println("Error en la estructura del archivo JSON del tipo del terreno.");
				return;
			}

			JsonArray terrenosJson = archivoJson.getAsJsonArray("terreno");

			for (JsonElement elemento : terrenosJson) {
				JsonObject terrenoJSON = elemento.getAsJsonObject();

				int x = terrenoJSON.get("x").getAsInt();
				int y = terrenoJSON.get("y").getAsInt();
				String tipoTerreno = terrenoJSON.get("tipo").getAsString();

				mapa.setTerreno(x, y, TipoTerreno.valueOf(tipoTerreno));
			}
		} catch (IOException e) {
			System.err.println("Error leyendo el archivo JSON. " + RUTA_JSON);
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error procesando el tipo de terreno desde el JSON");
			e.printStackTrace();
		}

	}

}
