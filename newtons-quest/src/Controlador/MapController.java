package Controlador;

import Controlador.navigation.NavigationManager;
import Controlador.utils.ErrorHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.stage.Stage;

public class MapController {
    
    @FXML private Button btnJugar;
    @FXML private Button btnVideo;
    @FXML private Button btnQuiz;
    @FXML private AnchorPane mapBackground; // Añadimos referencia al AnchorPane
    
    public void initialize() {
        try {
            System.out.println("Inicializando MapController");
            
            // Cargar la imagen de fondo
            loadBackgroundImage();
            
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
    
    /**
     * Carga la imagen de fondo directamente desde código
     */
    private void loadBackgroundImage() {
        try {
            // Ruta de la imagen
            String imagePath = "src/recursos/imagenes/map_background.jpg";
            File imageFile = new File(imagePath);
            
            Image backgroundImage;
            
            if (imageFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                backgroundImage = new Image(new FileInputStream(imageFile));
                ErrorHandler.logInfo("Imagen de fondo del mapa cargada desde: " + imagePath);
            } else {
                // Estamos en producción, usar getResource
                backgroundImage = new Image(getClass().getResourceAsStream("/recursos/imagenes/map_background.jpg"));
                ErrorHandler.logInfo("Imagen de fondo del mapa cargada desde recursos");
            }
            
            // Crear el objeto BackgroundImage
            BackgroundImage bgImage = new BackgroundImage(
                backgroundImage, 
                BackgroundRepeat.NO_REPEAT, 
                BackgroundRepeat.NO_REPEAT, 
                BackgroundPosition.CENTER, 
                new BackgroundSize(100, 100, true, true, false, true)
            );
            
            // Establecer el fondo
            mapBackground.setBackground(new Background(bgImage));
              } catch (Exception e) {
            System.err.println("Error al cargar la imagen de fondo del mapa: " + e.getMessage());
            e.printStackTrace();
            ErrorHandler.logWarning("No se pudo cargar la imagen de fondo del mapa: " + e.getMessage());
            // También podemos usar handleResourceError que es específico para errores de recursos
            ErrorHandler.handleResourceError("imagen de fondo del mapa", e);
        }
    }
      @FXML
    private void onJugarButtonClick(ActionEvent event) {
        try {
            System.out.println("Iniciando juego desde el mapa...");
              // Cargar la pantalla del juego
            String gameFxmlPath = "src/Vista/Game.fxml";
            String gameCssPath = "src/Vista/resources/game.css";
            
            // Verificar si estamos en desarrollo o en producción
            File gameFxmlFile = new File(gameFxmlPath);
            File gameCssFile = new File(gameCssPath);
            
            FXMLLoader loader;
            String cssPath;
            
            if (gameFxmlFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                loader = new FXMLLoader(gameFxmlFile.toURI().toURL());
                cssPath = gameCssFile.toURI().toURL().toExternalForm();            } else {                // Estamos en producción, usar getResource
                loader = new FXMLLoader(getClass().getResource("/Vista/Game.fxml"));
                cssPath = getClass().getResource("/Vista/resources/game.css").toExternalForm();
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
    
    @FXML
    private void onBackToMainMenuClick(ActionEvent event) {
        returnToMainMenu();
    }
    
    // Método para volver al menú principal
    private void returnToMainMenu() {
        try {
            System.out.println("Volviendo al menú principal desde el mapa...");
            
            Stage stage = (Stage) btnJugar.getScene().getWindow();
            NavigationManager.navigateToMainWithUser(stage);
            
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
