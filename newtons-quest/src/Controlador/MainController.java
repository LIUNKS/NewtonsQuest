package Controlador;

import Controlador.componentes.VideoManager;
import Controlador.componentes.VideoPlayer;
import Controlador.dialogs.RulesDialog;
import Controlador.dialogs.SettingsDialog;
import Controlador.dialogs.UserProfileDialog;
import Controlador.dialogs.RankingDialog;
import Controlador.navigation.NavigationManager;
import Controlador.constants.GameConstants;
import Controlador.utils.CertificateGenerator;
import Controlador.utils.ErrorHandler;
import Controlador.utils.SessionManager;
import Modelo.dao.UsuarioDAO;
import Modelo.dto.QuizResult;
import Modelo.dto.Player;
import Modelo.dao.QuizDAO;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MainController {
    
    @FXML private Button startButton;
    @FXML private Button rulesButton;
    @FXML private Button settingsButton;
    @FXML private Button logoutButton; // Botón para cerrar sesión
    @FXML private Button userProfileButton; // Botón de perfil de usuario
    @FXML private Button rankingButton; // Botón de ranking
    @FXML private Button biographyButton; // Botón de biografía
    @FXML private Button certificateButton; // Botón para descargar certificado
    @FXML private Label welcomeLabel;
    @FXML private Tooltip certificateTooltip; // Tooltip para el botón de certificado
    
    private String username;
    private int userId;    public void initialize() {
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
        certificateButton.setOnAction(event -> downloadCertificate());
        
        // Verificar si hay una sesión activa y configurar el usuario
        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager.isLoggedIn()) {
            setUsername(sessionManager.getCurrentUsername());
        }
       
        // MainController inicializado
    }
      // Método para establecer el nombre de usuario
    public void setUsername(String username) {
        this.username = username;
        this.userId = UsuarioDAO.obtenerIdUsuario(username);
        
        // Actualizar la etiqueta de bienvenida si existe
        if (welcomeLabel != null) {
            String welcomeMessage = String.format(GameConstants.WELCOME_MESSAGE_FORMAT, username);
            welcomeLabel.setText(welcomeMessage);
            // Mensaje de bienvenida actualizado
        } else {
            ErrorHandler.logWarning("welcomeLabel es null, no se puede mostrar mensaje de bienvenida");
        }
        
        // Verificar si el usuario es elegible para un certificado
        checkCertificateEligibility();
    }
    
    private void startGame() {
        try {
            // Cargando mapa
            Stage stage = (Stage) startButton.getScene().getWindow();
            NavigationManager.navigateToMap(stage);
            // Mapa cargado
            
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
            // Cerrando sesión
            
            // Cerrar sesión en el SessionManager
            SessionManager.getInstance().logout();
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            NavigationManager.navigateToLogin(stage);
            // Sesión cerrada exitosamente
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
            // Reproduciendo video de biografía de Newton
            
            VideoManager videoManager = VideoManager.getInstance();
            String biographyVideoPath = videoManager.getBiographyVideoPath();
            
            // Verificar que el archivo de video existe
            if (videoManager.videoFileExists(biographyVideoPath)) {
                VideoPlayer videoPlayer = new VideoPlayer();
                Stage parentStage = (Stage) biographyButton.getScene().getWindow();
                
                videoPlayer.playVideo(biographyVideoPath, 
                                    "Biografía de Isaac Newton", 
                                    parentStage);
                
                // Video de biografía iniciado correctamente
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
            // Error silencioso al reproducir video de biografía
        }
    }
    
    /**
     * Verifica si el usuario actual es elegible para un certificado y 
     * actualiza la interfaz de usuario en consecuencia.
     */
    private void checkCertificateEligibility() {
        if (certificateButton == null || certificateTooltip == null) {
            return; // Controles no inicializados
        }
        
        if (userId <= 0) {
            // Usuario no autenticado o ID no válido
            certificateTooltip.setText("Inicia sesión para verificar elegibilidad para certificado");
            certificateButton.getStyleClass().add("ineligible-certificate-button");
            certificateButton.getStyleClass().remove("eligible-certificate-button");
            return;
        }
        
        try {
            // Obtener el último resultado de quiz del usuario
            QuizResult latestResult = QuizDAO.obtenerUltimoResultadoQuiz(userId);
            
            if (latestResult != null && CertificateGenerator.isEligibleForCertificate(latestResult)) {
                // El usuario es elegible para un certificado
                certificateTooltip.setText("¡Felicidades! Descarga tu certificado de excelencia");
                certificateButton.getStyleClass().add("eligible-certificate-button");
                certificateButton.getStyleClass().remove("ineligible-certificate-button");
                // Usuario elegible para certificado
            } else {
                // El usuario no es elegible para un certificado
                if (latestResult == null) {
                    certificateTooltip.setText("Completa el quiz para desbloquear tu certificado");
                } else {
                    double puntuacion = latestResult.getPercentage();
                    certificateTooltip.setText(String.format("Necesitas 85%% para tu certificado (Actual: %.1f%%)", puntuacion));
                }
                
                certificateButton.getStyleClass().add("ineligible-certificate-button");
                certificateButton.getStyleClass().remove("eligible-certificate-button");
                // Usuario no elegible para certificado
            }
        } catch (Exception e) {
            ErrorHandler.logError("Error al verificar elegibilidad para certificado: " + e.getMessage());
            certificateTooltip.setText("Completa el quiz con éxito para obtener tu certificado");
            certificateButton.getStyleClass().add("ineligible-certificate-button");
            certificateButton.getStyleClass().remove("eligible-certificate-button");
        }
    }
    
    /**
     * Maneja la descarga del certificado cuando el usuario hace clic en el botón correspondiente.
     */
    @FXML
    private void downloadCertificate() {
        try {
            // Verificar si el usuario está autenticado
            if (userId <= 0) {
                showCertificateAlert(false, "Debes iniciar sesión para poder descargar un certificado.");
                return;
            }
            
            // Obtener el último resultado de quiz del usuario
            QuizResult latestResult = QuizDAO.obtenerUltimoResultadoQuiz(userId);
            
            if (latestResult == null || !CertificateGenerator.isEligibleForCertificate(latestResult)) {
                // Si el usuario no es elegible, mostrar un mensaje informativo
                if (latestResult == null) {
                    showCertificateAlert(false, "Completa el quiz para desbloquear tu certificado.");
                } else {
                    double puntuacion = latestResult.getPercentage();
                    showCertificateAlert(false, String.format("Necesitas obtener al menos 85%% en el quiz para desbloquear tu certificado.\nTu puntuación actual: %.1f%%", puntuacion));
                }
                return;
            }
            
            // Obtener los datos del jugador
            Player player = UsuarioDAO.obtenerDatosJugador(userId);
            
            if (player == null) {
                showCertificateAlert(false, "No se pudieron recuperar los datos del jugador.");
                return;
            }
            
            // Generar el certificado
            Stage stage = (Stage) certificateButton.getScene().getWindow();
            String certificatePath = CertificateGenerator.generateCertificate(player, latestResult, stage);
            
            if (certificatePath != null) {
                showCertificateAlert(true, "¡Certificado generado con éxito!");
                // Certificado generado correctamente
            } else {
                showCertificateAlert(false, "Hubo un problema al generar el certificado. Inténtalo de nuevo.");
                ErrorHandler.logWarning("Falló la generación de certificado para: " + username);
            }
            
        } catch (Exception e) {
            ErrorHandler.logError("Error al generar certificado: " + e.getMessage());
            showCertificateAlert(false, "Error al generar el certificado: " + e.getMessage());
        }
    }
    
    /**
     * Muestra un mensaje de alerta sobre el certificado.
     * 
     * @param success Indica si la operación fue exitosa
     * @param message El mensaje a mostrar
     */
    private void showCertificateAlert(boolean success, String message) {
        Alert alert = new Alert(success ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setTitle(success ? "Certificado Generado" : "Error de Certificado");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}