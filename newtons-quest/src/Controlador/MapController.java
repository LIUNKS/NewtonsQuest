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

public class MapController {
    
    @FXML private Button btnJugar;
    @FXML private Button btnVideo;
    @FXML private Button btnQuiz;
    @FXML private AnchorPane mapBackground; // Añadimos referencia al AnchorPane
    
    // Managers para acceso a información del juego
    private VideoManager videoManager;
    private RankingManager rankingManager;
    
    public void initialize() {
        try {
            System.out.println("Inicializando MapController");
            
            // Inicializar managers
            videoManager = VideoManager.getInstance();
            rankingManager = RankingManager.getInstance();
            
            // Cargar la imagen de fondo
            loadBackgroundImage();
            
            // Configurar el estado inicial del botón del quiz
            updateQuizButtonState();
            
            // Debug temporal para verificar datos del usuario
            debugUserData();
            
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
     * Actualiza el estado visual del botón del quiz según el progreso del usuario
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
                System.out.println("Botón del quiz habilitado - Todas las fórmulas desbloqueadas");
            } else {
                // No todas las fórmulas desbloqueadas - botón deshabilitado visualmente
                btnQuiz.setDisable(false); // Mantenemos habilitado para mostrar el mensaje
                btnQuiz.setText("Quiz (" + unlockedCount + "/5)");
                btnQuiz.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
                System.out.println("Botón del quiz marcado como bloqueado - Fórmulas: " + unlockedCount + "/5");
            }
            
        } catch (Exception e) {
            System.err.println("Error al actualizar estado del botón del quiz: " + e.getMessage());
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
        try {
            System.out.println("Mostrando diálogo de selección de videos...");
            
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
    @FXML
    private void onQuizButtonClick(ActionEvent event) {
        try {
            System.out.println("Verificando acceso al quiz...");
            
            // Verificar si el usuario ha desbloqueado todas las fórmulas
            if (!areAllFormulasUnlocked()) {
                System.out.println("Acceso al quiz denegado - No todas las fórmulas están desbloqueadas");
                
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
            
            System.out.println("Acceso al quiz permitido - Todas las fórmulas desbloqueadas");
            System.out.println("Iniciando quiz desde el mapa...");
            
            Stage stage = (Stage) btnQuiz.getScene().getWindow();
            NavigationManager.navigateToQuiz(stage);
            
            System.out.println("Quiz iniciado correctamente");
            
        } catch (IOException e) {
            System.err.println("Error al cargar la pantalla del quiz: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al iniciar el quiz: " + e.getMessage());
            e.printStackTrace();
        }
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
     * Obtiene las fórmulas desbloqueadas del usuario actual
     * Se basa en el mejor puntaje del usuario para determinar qué fórmulas tiene disponibles
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
                int formulasCompletadas = Modelo.UsuarioDAO.obtenerFormulasCompletadasUsuario(currentUserId);
                
                // Desbloquear fórmulas basándose en el progreso guardado
                for (int i = 0; i < 5; i++) {
                    unlockedFormulas[i] = (i < formulasCompletadas);
                }
                
                System.out.println("Usuario " + currentUserId + " tiene " + formulasCompletadas + 
                                 " fórmulas completadas - Videos desbloqueados: " + countUnlocked(unlockedFormulas) + "/5");
            } else {
                System.out.println("No hay usuario en sesión, todas las fórmulas bloqueadas");
                // Si no hay usuario en sesión, todas las fórmulas están bloqueadas
                for (int i = 0; i < 5; i++) {
                    unlockedFormulas[i] = false;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al obtener fórmulas desbloqueadas: " + e.getMessage());
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
     * Método público para actualizar el estado del mapa cuando el usuario regrese del juego
     * Esto permite que el botón del quiz se actualice si se desbloquearon más fórmulas
     */
    public void refreshMapState() {
        updateQuizButtonState();
        System.out.println("Estado del mapa actualizado");
    }
    
    /**
     * Método temporal de debug para verificar los datos del usuario
     */
    private void debugUserData() {
        try {
            SessionManager sessionManager = SessionManager.getInstance();
            if (sessionManager.isLoggedIn()) {
                int currentUserId = sessionManager.getCurrentUserId();
                String username = sessionManager.getCurrentUsername();
                
                // Sincronizar datos del ranking con la tabla usuarios si es necesario
                Modelo.UsuarioDAO.sincronizarDatosRankingAUsuarios(currentUserId);
                
                System.out.println("=== DEBUG DATOS USUARIO ===");
                System.out.println("Usuario ID: " + currentUserId);
                System.out.println("Username: " + username);
                
                // Verificar datos en tabla usuarios
                int puntajeUsuarios = Modelo.UsuarioDAO.obtenerMejorPuntajeUsuario(currentUserId);
                int formulasUsuarios = Modelo.UsuarioDAO.obtenerFormulasCompletadasUsuario(currentUserId);
                String ultimaPartida = Modelo.UsuarioDAO.obtenerUltimaPartidaUsuario(currentUserId);
                
                System.out.println("--- Tabla USUARIOS ---");
                System.out.println("Mejor puntaje: " + puntajeUsuarios);
                System.out.println("Fórmulas completadas: " + formulasUsuarios);
                System.out.println("Última partida: " + ultimaPartida);
                
                // Verificar datos en tabla ranking
                System.out.println("--- Tabla RANKING ---");
                RankingManager rankingManager = RankingManager.getInstance();
                int posicion = rankingManager.getCurrentUserPosition();
                System.out.println("Posición en ranking: " + posicion);
                
                // Botón temporal para sincronizar datos
                System.out.println("--- SINCRONIZACIÓN ---");
                int sincronizados = Modelo.UsuarioDAO.sincronizarTodosLosDatosRanking();
                System.out.println("Usuarios sincronizados: " + sincronizados);
                
                System.out.println("=== FIN DEBUG ===");
            }
        } catch (Exception e) {
            System.err.println("Error en debug: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
