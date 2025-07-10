package Controlador;

import Controlador.componentes.LevelManager;
import Controlador.componentes.RankingManager;
import Controlador.componentes.VideoManager;
import Controlador.dialogs.VideoSelectionDialog;
import Controlador.navigation.NavigationManager;
import Controlador.utils.ErrorHandler;
import Controlador.utils.SessionManager;
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

/**
 * Controlador para la pantalla del mapa en Newton's Apple Quest.
 * 
 * Esta clase gestiona la navegación y funcionalidades disponibles en la pantalla del mapa,
 * incluyendo el acceso al juego, videos educativos y quiz.
 * 
 * Funcionalidades principales:
 * - Navegación al juego principal
 * - Acceso a videos educativos sobre física
 * - Acceso al quiz (requiere desbloqueo previo de todas las fórmulas)
 * - Gestión del progreso del usuario y desbloqueo de contenido
 * - Retroalimentación visual sobre el progreso del usuario
 * 
 * Flujo de navegación:
 * - Desde el mapa, el usuario puede acceder al juego, videos o quiz (si está desbloqueado)
 * - El acceso al quiz está condicionado al desbloqueo de todas las fórmulas físicas
 * - El usuario puede regresar al menú principal desde esta pantalla
 */
public class MapController {
    
    @FXML private Button btnJugar;
    @FXML private Button btnVideo;
    @FXML private Button btnQuiz;
    @FXML private AnchorPane mapBackground; // Añadimos referencia al AnchorPane
    
    // Managers para acceso a información del juego
    private VideoManager videoManager;
    private RankingManager rankingManager;
    
    /**
     * Inicializa el controlador del mapa.
     * Configura los componentes visuales, carga la imagen de fondo,
     * actualiza el estado del botón del quiz y configura los eventos de teclado.
     */
    public void initialize() {
        try {
            // Inicializar managers
            videoManager = VideoManager.getInstance();
            rankingManager = RankingManager.getInstance();
            
            // Cargar la imagen de fondo
            loadBackgroundImage();
            
            // Configurar el estado inicial del botón del quiz
            updateQuizButtonState();
            
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
            System.err.println("Error durante la inicialización del mapa: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Actualiza el estado visual del botón del quiz según el progreso del usuario.
     * Habilita o deshabilita visualmente el botón según la cantidad de fórmulas desbloqueadas.
     */
    private void updateQuizButtonState() {
        try {
            boolean allFormulasUnlocked = areAllFormulasUnlocked();
            int unlockedCount = countUnlockedFormulas();
            
            if (allFormulasUnlocked) {
                // Todas las fórmulas desbloqueadas - botón habilitado
                btnQuiz.setDisable(false);
                btnQuiz.setText("Quiz");
                btnQuiz.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
            } else {
                // No todas las fórmulas desbloqueadas - botón deshabilitado visualmente
                btnQuiz.setDisable(false); // Mantenemos habilitado para mostrar el mensaje
                btnQuiz.setText("Quiz (" + unlockedCount + "/5)");
                btnQuiz.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
            }
            
        } catch (Exception e) {
            System.err.println("Error al actualizar estado del botón del quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga la imagen de fondo del mapa
     */
    private void loadBackgroundImage() {
        try {
            // Ruta de la imagen
            String imagePath = "src/recursos/imagenes/map_background.jpg";
            
            // Cargar imagen directamente desde archivo (modo desarrollo)
            Image backgroundImage = new Image(new FileInputStream(new File(imagePath)));
            
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
            ErrorHandler.handleResourceError("imagen de fondo del mapa", e);
        }
    }
      @FXML
    private void onJugarButtonClick(ActionEvent event) {
        try {
            // Iniciar el juego desde el mapa
            String gameFxmlPath = "src/Vista/Game.fxml";
            String gameCssPath = "src/Vista/resources/game.css";
            
            // Cargar en modo desarrollo
            FXMLLoader loader = new FXMLLoader(new File(gameFxmlPath).toURI().toURL());
            String cssPath = new File(gameCssPath).toURI().toURL().toExternalForm();
            
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
            
        } catch (IOException e) {
            System.err.println("Error al cargar la pantalla del juego: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al iniciar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Manejador del evento de clic en el botón de Video.
     * Muestra el diálogo de selección de videos educativos.
     * 
     * @param event El evento de acción generado por el clic
     */
    @FXML
    private void onVideoButtonClick(ActionEvent event) {
        try {
            // Obtener las fórmulas desbloqueadas del usuario actual
            boolean[] unlockedFormulas = getUserUnlockedFormulas();
            
            // Crear y mostrar el diálogo de selección de videos
            VideoSelectionDialog videoDialog = new VideoSelectionDialog();
            Stage currentStage = (Stage) btnVideo.getScene().getWindow();
            videoDialog.show(currentStage, unlockedFormulas);
            
        } catch (Exception e) {
            System.err.println("Error al mostrar videos: " + e.getMessage());
            e.printStackTrace();
            ErrorHandler.handleResourceError("sistema de videos", e);
        }
    }    
    /**
     * Manejador del evento de clic en el botón de Quiz.
     * Verifica si el usuario ha desbloqueado todas las fórmulas antes de permitir
     * el acceso al quiz. Si no están todas desbloqueadas, muestra un mensaje informativo.
     * 
     * @param event El evento de acción generado por el clic
     */
    @FXML
    private void onQuizButtonClick(ActionEvent event) {
        try {
            // Verificar si el usuario ha desbloqueado todas las fórmulas
            if (!areAllFormulasUnlocked()) {
                // Acceso al quiz denegado
                
                // Mostrar mensaje informativo al usuario
                Stage currentStage = (Stage) btnQuiz.getScene().getWindow();
                int unlockedCount = countUnlockedFormulas();
                
                String mensaje = "Para acceder al quiz y poner a prueba tus conocimientos sobre las leyes de Newton, " +
                               "primero debes desbloquear todas las fórmulas físicas jugando.\n\n" +
                               "📊 PROGRESO ACTUAL: " + unlockedCount + "/5 fórmulas desbloqueadas\n\n" +
                               getProgressDetails() + "\n" +
                               "🎮 CÓMO CONTINUAR:\n" +
                               "• Juega y recoge manzanas para ganar puntos\n" +
                               "• Evita las manzanas rojas y usa las pociones sabiamente\n" +
                               "• Cada umbral de puntaje desbloquea una nueva fórmula\n\n" +
                               "🏆 Una vez que hayas desbloqueado las 5 fórmulas, podrás acceder al quiz " +
                               "y demostrar que eres un verdadero experto en física como Newton.";
                
                ErrorHandler.showInfoDialog(
                    "🔒 Quiz Bloqueado",
                    "¡Completa tu aprendizaje primero!",
                    mensaje,
                    currentStage
                );
                return;
            }
            
            // Acceso al quiz permitido - Todas las fórmulas desbloqueadas
            Stage stage = (Stage) btnQuiz.getScene().getWindow();
            NavigationManager.navigateToQuiz(stage);
            
        } catch (IOException e) {
            System.err.println("Error al cargar la pantalla del quiz: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al iniciar el quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Manejador del evento de clic en el botón de volver al menú principal.
     * 
     * @param event El evento de acción generado por el clic
     */
    @FXML
    private void onBackToMainMenuClick(ActionEvent event) {
        returnToMainMenu();
    }
    
    /**
     * Navega de regreso al menú principal.
     * Utiliza el NavigationManager para gestionar la transición entre pantallas.
     */
    private void returnToMainMenu() {
        try {
            Stage stage = (Stage) btnJugar.getScene().getWindow();
            NavigationManager.navigateToMainWithUser(stage);
            
        } catch (IOException e) {
            System.err.println("Error al cargar el menú principal: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al volver al menú principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene información detallada sobre el progreso de fórmulas del usuario
     * @return String con información detallada del progreso
     */
    private String getProgressDetails() {
        boolean[] unlockedFormulas = getUserUnlockedFormulas();
        StringBuilder details = new StringBuilder();
        
        // Nombres de las fórmulas físicas
        String[] formulaNames = {
            "F = m × g (Fuerza de gravedad)",
            "v = d / t (Velocidad media)",
            "U = m × g × h (Energía potencial)",
            "K = ½ × m × v² (Energía cinética)",
            "a = (vf - vi) / t (Aceleración)"
        };
        
        // Umbrales de puntaje para cada fórmula
        int[] thresholds = {100, 250, 450, 700, 1000};
        
        details.append("📋 ESTADO DE LAS FÓRMULAS:\n\n");
        
        for (int i = 0; i < formulaNames.length; i++) {
            if (unlockedFormulas[i]) {
                details.append("✅ ").append(formulaNames[i]).append(" - DESBLOQUEADA\n");
            } else {
                details.append("🔒 ").append(formulaNames[i]).append(" - Requiere ").append(thresholds[i]).append(" puntos\n");
            }
        }
        
        return details.toString();
    }
    
    /**
     * Verifica si todas las fórmulas están desbloqueadas para el usuario actual
     * @return true si todas las fórmulas están desbloqueadas, false en caso contrario
     */
    private boolean areAllFormulasUnlocked() {
        boolean[] unlockedFormulas = getUserUnlockedFormulas();
        
        // Verificar que todas las 5 fórmulas estén desbloqueadas
        for (boolean unlocked : unlockedFormulas) {
            if (!unlocked) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Cuenta el número de fórmulas desbloqueadas por el usuario actual
     * @return número de fórmulas desbloqueadas (0-5)
     */
    private int countUnlockedFormulas() {
        boolean[] unlockedFormulas = getUserUnlockedFormulas();
        int count = 0;
        
        for (boolean unlocked : unlockedFormulas) {
            if (unlocked) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Obtiene las fórmulas desbloqueadas del usuario actual.
     * Se basa en el progreso guardado en la base de datos para determinar
     * qué fórmulas tiene disponibles.
     * 
     * @return Array de booleanos indicando qué fórmulas están desbloqueadas
     */
    private boolean[] getUserUnlockedFormulas() {
        boolean[] unlockedFormulas = new boolean[5];
        
        try {
            // Obtener el ID del usuario actual de la sesión
            SessionManager sessionManager = SessionManager.getInstance();
            
            if (sessionManager.isLoggedIn()) {
                int currentUserId = sessionManager.getCurrentUserId();
                
                // Obtener el número de fórmulas completadas desde la tabla usuarios
                int formulasCompletadas = Modelo.dao.UsuarioDAO.obtenerFormulasCompletadasUsuario(currentUserId);
                
                // Desbloquear fórmulas basándose en el progreso guardado
                for (int i = 0; i < 5; i++) {
                    unlockedFormulas[i] = (i < formulasCompletadas);
                }
            } else {
                // Si no hay usuario en sesión, todas las fórmulas están bloqueadas
                for (int i = 0; i < 5; i++) {
                    unlockedFormulas[i] = false;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al obtener fórmulas desbloqueadas: " + e.getMessage());
            e.printStackTrace();
            // En caso de error, asumir que no hay fórmulas desbloqueadas
            for (int i = 0; i < 5; i++) {
                unlockedFormulas[i] = false;
            }
        }
        
        return unlockedFormulas;
    }
    
    /**
     * Cuenta cuántas fórmulas están desbloqueadas
     * @param unlockedFormulas Array de fórmulas desbloqueadas
     * @return Número de fórmulas desbloqueadas
     */
    private int countUnlocked(boolean[] unlockedFormulas) {
        int count = 0;
        for (boolean unlocked : unlockedFormulas) {
            if (unlocked) count++;
        }
        return count;
    }
    
    /**
     * Método público para actualizar el estado del mapa cuando el usuario regrese del juego.
     * Actualiza el botón del quiz si se desbloquearon más fórmulas.
     */
    public void refreshMapState() {
        updateQuizButtonState();
    }
}
