package Controlador.componentes;

import Modelo.dto.Player;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;

/**
 * Gestor de entrada del usuario.
 * 
 * Esta clase maneja todos los eventos de entrada del jugador, incluyendo:
 * 
 *   - Eventos de teclado para movimiento del jugador
 *   - Teclas de función para pausar, menú y configuración
 *   - Acceso directo a fórmulas usando números 1-5
 *   - Eventos de mouse para interacción con la interfaz
 *   - Gestión de estado de teclas presionadas
 * 
 * Proporciona una interfaz limpia entre los controles del usuario y
 * la lógica del juego, delegando acciones específicas mediante callbacks.
 */
public class InputManager {
    
    // =====================================
    // ATRIBUTOS DE INSTANCIA
    // =====================================
    
    /** Canvas del juego donde se capturan los eventos */
    private final Canvas gameCanvas;
    
    /** Instancia del jugador controlado */
    private Player player;
    
    /** Gestor del jugador para control avanzado */
    private PlayerManager playerManager;
    
    // =====================================
    // ESTADO DE TECLAS
    // =====================================
    
    /** Estado de la tecla izquierda */
    private boolean leftKeyPressed = false;
    
    /** Estado de la tecla derecha */
    private boolean rightKeyPressed = false;
    
    // =====================================
    // CALLBACKS PARA ACCIONES
    // =====================================
    
    /** Callback para alternar pausa */
    private Runnable onPauseToggle;
    
    /** Callback para volver al menú */
    private Runnable onReturnToMenu;
    
    /** Callback para alternar UI */
    private Runnable onUIToggle;
    
    /** Callback para mostrar configuración */
    private Runnable onShowSettings;
    
    /** Callback para mostrar detalles de fórmula */
    private java.util.function.Consumer<Integer> onFormulaDetails;
    
    /** Callback para mostrar ranking */
    private Runnable onShowRanking;
    
    /** Callback para continuar después de completación */
    private Runnable onContinueAfterCompletion;
    
    /** Callback para clics del mouse */
    private java.util.function.BiConsumer<Double, Double> onMouseClick;
    
    // =====================================
    // CONSTRUCTOR
    // =====================================
    
    /**
     * Constructor del gestor de entrada.
     * 
     * Inicializa el gestor con el canvas donde se capturarán los eventos
     * de teclado y mouse del jugador.
     * 
     * @param gameCanvas Canvas del juego para capturar eventos
     */
    public InputManager(Canvas gameCanvas) {
        this.gameCanvas = gameCanvas;
    }
    
    // =====================================
    // MÉTODOS PÚBLICOS - CONFIGURACIÓN
    // =====================================
    
    /**
     * Establece el jugador que será controlado.
     * 
     * @param player Instancia del jugador a controlar
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    /**
     * Establece el gestor del jugador para control avanzado.
     * 
     * @param playerManager Gestor del jugador
     */
    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        if (playerManager != null) {
            this.player = playerManager.getPlayer();
        }
    }    
    
    /**
     * Configura todos los callbacks para acciones del juego.
     * 
     * @param onPauseToggle Acción para alternar pausa
     * @param onReturnToMenu Acción para volver al menú
     * @param onUIToggle Acción para alternar UI
     * @param onShowSettings Acción para mostrar configuración
     * @param onFormulaDetails Acción para mostrar detalles de fórmula
     * @param onShowRanking Acción para mostrar ranking
     * @param onContinueAfterCompletion Acción para continuar después de completación
     * @param onMouseClick Acción para clics del mouse
     */
    public void setCallbacks(Runnable onPauseToggle, Runnable onReturnToMenu, 
                            Runnable onUIToggle, Runnable onShowSettings,
                            java.util.function.Consumer<Integer> onFormulaDetails,
                            Runnable onShowRanking, Runnable onContinueAfterCompletion,
                            java.util.function.BiConsumer<Double, Double> onMouseClick) {
        this.onPauseToggle = onPauseToggle;
        this.onReturnToMenu = onReturnToMenu;
        this.onUIToggle = onUIToggle;
        this.onShowSettings = onShowSettings;
        this.onFormulaDetails = onFormulaDetails;
        this.onShowRanking = onShowRanking;
        this.onContinueAfterCompletion = onContinueAfterCompletion;
        this.onMouseClick = onMouseClick;
    }
    
    // =====================================
    // MÉTODOS PÚBLICOS - CONFIGURACIÓN DE EVENTOS
    // =====================================   
     /**
     * Configura los manejadores de eventos de teclado y mouse.
     * 
     * Establece los listeners para capturar entrada del usuario tanto
     * en el canvas como en la escena, manejando el foco automáticamente.
     */
    public void setupKeyHandlers() {
        if (gameCanvas == null) return;
        
        // Configurar listener de escena cuando esté disponible
        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                gameCanvas.requestFocus();
                setupSceneKeyHandlers(newScene);
                
                // Configurar eventos de mouse
                gameCanvas.setOnMouseClicked(event -> {
                    gameCanvas.requestFocus();
                    if (onMouseClick != null) {
                        onMouseClick.accept(event.getX(), event.getY());
                    }
                });
            }
        });
        
        // Si la escena ya está disponible, configurar inmediatamente
        Scene currentScene = gameCanvas.getScene();
        if (currentScene != null) {
            setupSceneKeyHandlers(currentScene);
        }
    }
    
    // =====================================
    // MÉTODOS PRIVADOS - CONFIGURACIÓN INTERNA
    // =====================================    
    /**
     * Configura los manejadores de teclas específicos para la escena.
     * 
     * Establece los listeners para teclas presionadas y liberadas,
     * manejando movimiento del jugador y acciones del juego.
     * 
     * @param scene Escena donde configurar los eventos
     */
    private void setupSceneKeyHandlers(Scene scene) {
        if (scene == null) return;
        
        // Configurar eventos de tecla presionada
        scene.setOnKeyPressed(event -> {
            if (event == null) return;
            
            KeyCode code = event.getCode();
            
            // Movimiento izquierda
            if (code == KeyCode.LEFT || code == KeyCode.A) {
                leftKeyPressed = true;
                if (playerManager != null) {
                    playerManager.setPlayerMovement(true, false);
                } else if (player != null) {
                    player.moveLeft(true);
                }
            }
            // Movimiento derecha  
            else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                rightKeyPressed = true;
                if (playerManager != null) {
                    playerManager.setPlayerMovement(false, true);
                } else if (player != null) {
                    player.moveRight(true);
                }
            }
            // Teclas de función
            else if (code == KeyCode.ESCAPE && onPauseToggle != null) {
                onPauseToggle.run();
            } else if (code == KeyCode.BACK_SPACE && onReturnToMenu != null) {
                onReturnToMenu.run();
            } else if (code == KeyCode.M && onUIToggle != null) {
                onUIToggle.run();
            } else if (code == KeyCode.S && onShowSettings != null) {
                onShowSettings.run();
            } else if (code == KeyCode.R && onShowRanking != null) {
                onShowRanking.run();
            } else if ((code == KeyCode.ENTER || code == KeyCode.SPACE) && onContinueAfterCompletion != null) {
                onContinueAfterCompletion.run();
            }
            // Teclas numéricas para fórmulas
            else if (code.isDigitKey() && onFormulaDetails != null) {
                try {
                    int formulaNumber = Integer.parseInt(code.getName()) - 1;
                    onFormulaDetails.accept(formulaNumber);
                } catch (NumberFormatException e) {
                    // Ignorar error de conversión
                }
            }
        });
        
        // Configurar eventos de tecla liberada
        scene.setOnKeyReleased(event -> {
            if (event == null) return;
            
            KeyCode code = event.getCode();
            
            if (code == KeyCode.LEFT || code == KeyCode.A) {
                leftKeyPressed = false;
                if (playerManager != null) {
                    playerManager.setPlayerMovement(false, rightKeyPressed);
                } else if (player != null) {
                    player.moveLeft(false);
                    if (rightKeyPressed) {
                        player.moveRight(true);
                    }
                }
            } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                rightKeyPressed = false;
                if (playerManager != null) {
                    playerManager.setPlayerMovement(leftKeyPressed, false);
                } else if (player != null) {
                    player.moveRight(false);
                    if (leftKeyPressed) {
                        player.moveLeft(true);
                    }
                }
            }
        });
    }
    
    // =====================================
    // MÉTODOS PÚBLICOS - UTILIDADES
    // =====================================
    
    /**
     * Solicita el foco para el canvas del juego.
     * 
     * Útil para asegurar que el canvas reciba los eventos de teclado
     * después de interacciones con otros elementos de la interfaz.
     */
    public void requestCanvasFocus() {
        if (gameCanvas != null) {
            gameCanvas.requestFocus();
        }
    }
    
    /**
     * Verifica si la tecla izquierda está presionada.
     * 
     * @return true si la tecla izquierda está presionada
     */
    public boolean isLeftKeyPressed() {
        return leftKeyPressed;
    }
    
    /**
     * Verifica si la tecla derecha está presionada.
     * 
     * @return true si la tecla derecha está presionada
     */
    public boolean isRightKeyPressed() {
        return rightKeyPressed;
    }
}
