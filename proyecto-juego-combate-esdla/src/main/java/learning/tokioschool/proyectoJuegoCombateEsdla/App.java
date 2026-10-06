package learning.tokioschool.proyectoJuegoCombateEsdla;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



/**
 * The Class App.
 * 
 * Clase principal de la aplicacion JavaFX.
 * 
 * Desde esta clase cargamos el menú principal.
 */
public class App extends Application{
	
    /**
     * Metodo main.
     * 
     * Desde este método lanzamos la aplicación JavaFX.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

	/**
	 * Start.
	 * 
	 * Carga el menú principal desde el Archivo FXML y muestra la ventana.
	 *
	 * @param stage Se le pasa la ventana principal de JavaFX.
	 * @throws Exception salta excepción si ocurre algun error al cargar el archivo FXML.
	 */
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader cargarMenuPrincipal = new FXMLLoader(getClass().getResource("/menuPrincipalRecursos/menu_principal.fxml"));
		
		Parent root = cargarMenuPrincipal.load();
		
		stage.setTitle("Manuel J. Ramirez Lozano -> Juego de combate ESDLA");
		stage.setScene(new Scene(root));
		stage.setResizable(false);
		stage.setMaximized(false);
		
		stage.show();
		
	}
}
