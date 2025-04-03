package Main;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Cargar el archivo FXML
        // Cambia la ruta al archivo FXML según tu estructura de proyecto
        FXMLLoader loader = new FXMLLoader(new File("C:/Users/johan/OneDrive/Documentos/NetBeansProjects/AlgoritmosYEstructurasDeDatos/newtons-quest/src/Main/Main.fxml").toURI().toURL());
        Parent root = loader.load();

        // Crear la escena
        Scene scene = new Scene(root, 800, 600);

        // Añadir el CSS (comentado por ahora para simplificar)
        // scene.getStylesheets().add(getClass().getResource("/Vista/main.css").toExternalForm());
        // Configurar la ventana
        primaryStage.setTitle("Newton's Apple Quest");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
