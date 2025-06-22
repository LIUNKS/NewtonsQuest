package Main;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class _Main extends Application {    @Override
    public void start(Stage primaryStage) throws Exception {        // Obtener la ruta del archivo FXML
        String loginFxmlPath = "src/Vista/Login.fxml";
        String loginCssPath = "src/Vista/resources/login.css";

        // Verificar si estamos en desarrollo o en producción
        File loginFxmlFile = new File(loginFxmlPath);
        File loginCssFile = new File(loginCssPath);

        FXMLLoader loader;
        String cssPath;

        if (loginFxmlFile.exists()) {
            // Estamos en desarrollo, usar ruta de archivo
            loader = new FXMLLoader(loginFxmlFile.toURI().toURL());
            cssPath = loginCssFile.toURI().toURL().toExternalForm();        } else {
            // Estamos en producción, usar getResource
            loader = new FXMLLoader(getClass().getResource("/Vista/Login.fxml"));
            cssPath = getClass().getResource("/Vista/resources/login.css").toExternalForm();
        }

        // Cargar la pantalla de inicio de sesión
        Parent root = loader.load();        // Crear la escena
        Scene scene = new Scene(root, 900, 700);

        // Añadir el CSS específico para la pantalla de login
        scene.getStylesheets().add(cssPath);

        // Configurar la ventana
        primaryStage.setTitle("Newton's Apple Quest - Inicio de Sesión");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
