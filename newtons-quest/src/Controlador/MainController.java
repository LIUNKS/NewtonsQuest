package Controlador;

import Controlador.dialogs.RulesDialog;
import Controlador.dialogs.SettingsDialog;
import Controlador.dialogs.UserProfileDialog;
import Controlador.dialogs.RankingDialog;
import Controlador.navigation.NavigationManager;
import Controlador.constants.GameConstants;
import Controlador.utils.ErrorHandler;
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
    @FXML private Label welcomeLabel;
    
    private String username;    private int userId;
    
    public void initialize() {
        // Configurar acciones para los botones
        startButton.setOnAction(event -> startGame());
        rulesButton.setOnAction(event -> showRules());
        settingsButton.setOnAction(event -> showSettings());
        
        // Configurar el botón de cerrar sesión
        logoutButton.setOnAction(event -> handleLogout());
        
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
}