package Controlador;

import Controlador.componentes.VideoManager;
import Controlador.componentes.VideoPlayer;
import Controlador.dialogs.RulesDialog;
import Controlador.dialogs.SettingsDialog;
import Controlador.dialogs.UserProfileDialog;
import Controlador.dialogs.RankingDialog;
import Controlador.navigation.NavigationManager;
import Controlador.constants.GameConstants;
import Controlador.utils.ErrorHandler;
import Controlador.utils.SessionManager;
import Modelo.UsuarioDAO;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainController {
    
    @FXML private Button startButton;
    @FXML private Button rulesButton;
    @FXML private Button settingsButton;
    @FXML private Button logoutButton; // Botón para cerrar sesión
    @FXML private Button userProfileButton; // Botón de perfil de usuario
    @FXML private Button rankingButton; // Botón de ranking
    @FXML private Button biographyButton; // Botón de biografía
    @FXML private Label welcomeLabel;
    
    private String username;    private int userId;    public void initialize() {
        // Configurar acciones para los botones
        startButton.setOnAction(event -> startGame());
        rulesButton.setOnAction(event -> showRules());
        settingsButton.setOnAction(event -> showSettings());
        
        // Configurar el botón de cerrar sesión
        logoutButton.setOnAction(event -> handleLogout());
        
        // Configurar los nuevos botones de interfaz
        userProfileButton.setOnAction(event -> showUserProfile());
        rankingButton.setOnAction(event -> showRanking());
        biographyButton.setOnAction(event -> showBiography());
        
        // Verificar si hay una sesión activa y configurar el usuario
        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager.isLoggedIn()) {
            setUsername(sessionManager.getCurrentUsername());
        }
        
        ErrorHandler.logInfo("MainController inicializado correctamente");
    }
      // Método para establecer el nombre de usuario
    public void setUsername(String username) {
        this.username = username;
        this.userId = UsuarioDAO.obtenerIdUsuario(username);
        
        // Actualizar la etiqueta de bienvenida si existe
        if (welcomeLabel != null) {
            String welcomeMessage = String.format(GameConstants.WELCOME_MESSAGE_FORMAT, username);
            welcomeLabel.setText(welcomeMessage);
            ErrorHandler.logInfo("Mensaje de bienvenida actualizado: " + welcomeMessage);
        } else {
            ErrorHandler.logWarning("welcomeLabel es null, no se puede mostrar mensaje de bienvenida");
        }
    }
    
    private void startGame() {
        try {
            ErrorHandler.logInfo(GameConstants.LOADING_MAP_MESSAGE);
            Stage stage = (Stage) startButton.getScene().getWindow();
            NavigationManager.navigateToMap(stage);
            ErrorHandler.logInfo(GameConstants.MAP_LOADED_MESSAGE);
            
        } catch (IOException e) {
            ErrorHandler.handleNavigationError("mapa de aventuras", e, (Stage) startButton.getScene().getWindow());        } catch (Exception e) {
            ErrorHandler.handleNavigationError("mapa de aventuras", e, (Stage) startButton.getScene().getWindow());
        }
    }
    
    private void showRules() {
        try {
            Stage parentStage = (Stage) rulesButton.getScene().getWindow();
            RulesDialog rulesDialog = new RulesDialog(parentStage);
            rulesDialog.showAndWait();        } catch (Exception e) {
            ErrorHandler.handleDialogError("reglas del juego", e, (Stage) rulesButton.getScene().getWindow());
        }
    }
    
    private void showSettings() {
        try {
            Stage parentStage = (Stage) settingsButton.getScene().getWindow();
            SettingsDialog settingsDialog = new SettingsDialog(parentStage);
            settingsDialog.showAndWait();        } catch (Exception e) {
            ErrorHandler.handleDialogError("configuración", e, (Stage) settingsButton.getScene().getWindow());
        }
    }
      // Método para manejar el cierre de sesión
    private void handleLogout() {
        try {
            ErrorHandler.logInfo(GameConstants.LOGOUT_MESSAGE);
            
            // Cerrar sesión en el SessionManager
            SessionManager.getInstance().logout();
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            NavigationManager.navigateToLogin(stage);
            ErrorHandler.logInfo(GameConstants.LOGOUT_SUCCESS_MESSAGE);
              } catch (IOException e) {
            ErrorHandler.handleNavigationError("pantalla de login", e, (Stage) logoutButton.getScene().getWindow());
        } catch (Exception e) {
            ErrorHandler.handleNavigationError("pantalla de login", e, (Stage) logoutButton.getScene().getWindow());
        }
    }
      /**
     * Muestra el diálogo de perfil de usuario desde el menú principal
     */
    @FXML
    private void showUserProfile() {
        try {
            Stage parentStage = (Stage) userProfileButton.getScene().getWindow();
            UserProfileDialog profileDialog = new UserProfileDialog(parentStage, username, userId);
            profileDialog.showAndWait();
        } catch (Exception e) {
            ErrorHandler.handleDialogError("perfil de usuario", e, (Stage) userProfileButton.getScene().getWindow());
        }
    }
    
    /**
     * Muestra el diálogo de ranking desde el menú principal
     */
    @FXML
    private void showRanking() {
        try {
            Stage parentStage = (Stage) rankingButton.getScene().getWindow();
            RankingDialog rankingDialog = new RankingDialog(parentStage);
            rankingDialog.showAndWait();
        } catch (Exception e) {
            ErrorHandler.handleDialogError("ranking", e, (Stage) rankingButton.getScene().getWindow());
        }
    }
    
    /**
     * Muestra el video de biografía de Newton desde el menú principal
     */
    @FXML
    private void showBiography() {
        try {
            System.out.println("Reproduciendo video de biografía de Newton...");
            
            VideoManager videoManager = VideoManager.getInstance();
            String biographyVideoPath = videoManager.getBiographyVideoPath();
            
            // Verificar que el archivo de video existe
            if (videoManager.videoFileExists(biographyVideoPath)) {
                VideoPlayer videoPlayer = new VideoPlayer();
                Stage parentStage = (Stage) biographyButton.getScene().getWindow();
                
                videoPlayer.playVideo(biographyVideoPath, 
                                    "Biografía de Isaac Newton", 
                                    parentStage);
                
                ErrorHandler.logInfo("Video de biografía iniciado correctamente");
            } else {
                // Mostrar error si el video no existe
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Video no encontrado");
                alert.setHeaderText(null);
                alert.setContentText("El video de biografía no se encuentra disponible.\n" +
                                   "Archivo: " + GameConstants.VIDEO_BIOGRAFIA);
                alert.showAndWait();
                
                ErrorHandler.logWarning("Video de biografía no encontrado: " + biographyVideoPath);
            }
            
        } catch (Exception e) {
            ErrorHandler.handleResourceError("video de biografía", e);
            System.err.println("Error al reproducir video de biografía: " + e.getMessage());
            e.printStackTrace();
        }
    }
}