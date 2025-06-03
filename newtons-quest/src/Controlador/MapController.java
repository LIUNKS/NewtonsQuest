package Controlador;

import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class MapController {
    
    @FXML private Button btnJugar;
    @FXML private Button btnVideo;
    @FXML private Button btnQuiz;
    
    public void initialize() {
        try {
            System.out.println("Inicializando MapController");
            
            // Configurar eventos de teclado para la escena
            btnJugar.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ESCAPE) {
                            returnToMainMenu();
                        }
                    });
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error al inicializar MapController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onJugarButtonClick(ActionEvent event) {
        try {
            System.out.println("Iniciando juego desde el mapa...");
            
            // Cargar la pantalla del juego
            String gameFxmlPath = "src/Main/Game.fxml";
            String gameCssPath = "src/Vista/game.css";
            
            // Verificar si estamos en desarrollo o en producción
            File gameFxmlFile = new File(gameFxmlPath);
            File gameCssFile = new File(gameCssPath);
            
            FXMLLoader loader;
            String cssPath;
            
            if (gameFxmlFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                loader = new FXMLLoader(gameFxmlFile.toURI().toURL());
                cssPath = gameCssFile.toURI().toURL().toExternalForm();
            } else {
                // Estamos en producción, usar getResource
                loader = new FXMLLoader(getClass().getResource("/Main/Game.fxml"));
                cssPath = getClass().getResource("/Vista/game.css").toExternalForm();
            }
            
            // Cargar la pantalla del juego
            Parent root = loader.load();
            
            // Crear una nueva escena
            Scene scene = new Scene(root, 900, 700);
            
            // Añadir el CSS
            scene.getStylesheets().add(cssPath);
            
            // Obtener el stage actual
            Stage stage = (Stage) btnJugar.getScene().getWindow();
            
            // Cambiar la escena
            stage.setScene(scene);
            stage.setTitle("Newton's Apple Quest - Juego");
            
            System.out.println("Juego iniciado correctamente");
            
        } catch (IOException e) {
            System.err.println("Error al cargar la pantalla del juego: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al iniciar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onVideoButtonClick(ActionEvent event) {
        // Este botón solo imprime un mensaje en la consola por el momento
        System.out.println("Botón Ver Video presionado - Funcionalidad no implementada aún");
    }
    
    @FXML
    private void onQuizButtonClick(ActionEvent event) {
        // Este botón solo imprime un mensaje en la consola por el momento
        System.out.println("Botón Quiz presionado - Funcionalidad no implementada aún");
    }
    
    // Método para volver al menú principal
    private void returnToMainMenu() {
        try {
            System.out.println("Volviendo al menú principal desde el mapa...");
            
            // Obtener la ruta del archivo FXML
            String mainFxmlPath = "src/Main/Main.fxml";
            String mainCssPath = "src/Vista/main.css";
            
            // Verificar si estamos en desarrollo o en producción
            File mainFxmlFile = new File(mainFxmlPath);
            File mainCssFile = new File(mainCssPath);
            
            FXMLLoader loader;
            String cssPath;
            
            if (mainFxmlFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                loader = new FXMLLoader(mainFxmlFile.toURI().toURL());
                cssPath = mainCssFile.toURI().toURL().toExternalForm();
            } else {
                // Estamos en producción, usar getResource
                loader = new FXMLLoader(getClass().getResource("/Main/Main.fxml"));
                cssPath = getClass().getResource("/Vista/main.css").toExternalForm();
            }
            
            // Cargar el menú principal
            Parent root = loader.load();
            
            // Crear una nueva escena
            Scene scene = new Scene(root, 900, 700);
            
            // Añadir el CSS
            scene.getStylesheets().add(cssPath);
            
            // Obtener el stage actual
            Stage stage = (Stage) btnJugar.getScene().getWindow();
            
            // Cambiar la escena
            stage.setScene(scene);
            stage.setTitle("Newton's Apple Quest - Menú Principal");
            
            System.out.println("Vuelto al menú principal correctamente");
            
        } catch (IOException e) {
            System.err.println("Error al cargar el menú principal: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al volver al menú principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
