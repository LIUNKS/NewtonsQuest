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
        // Cargar la pantalla de inicio de sesión
        FXMLLoader loader = new FXMLLoader(new File("C:/Users/johan/OneDrive/Documentos/NetBeansProjects/AlgoritmosYEstructurasDeDatos/newtons-quest/src/Main/Login.fxml").toURI().toURL());
        Parent root = loader.load();

        // Crear la escena
        Scene scene = new Scene(root, 800, 600);
        
        // Añadir el CSS específico para la pantalla de login
        scene.getStylesheets().add(new File("C:/Users/johan/OneDrive/Documentos/NetBeansProjects/AlgoritmosYEstructurasDeDatos/newtons-quest/src/Vista/login.css").toURI().toURL().toExternalForm());
        
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