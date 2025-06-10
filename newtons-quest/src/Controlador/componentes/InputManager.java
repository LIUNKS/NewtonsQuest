package Controlador.componentes;

import Modelo.Player;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;

/**
 * Clase encargada de gestionar la entrada del usuario.
 * Separa la lógica de manejo de eventos de teclado del controlador principal.
 */
public class InputManager {
    
    private final Canvas gameCanvas;
    private Player player;
    
    // Estado de las teclas
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    
    // Callbacks para acciones
    private Runnable onPauseToggle;
    private Runnable onReturnToMenu;    private Runnable onUIToggle;
    private java.util.function.Consumer<Integer> onFormulaDetails;
    private Runnable onHideFormulaDetails;
    
    /**
     * Constructor del InputManager
     * @param gameCanvas Canvas del juego para configurar eventos
     */
    public InputManager(Canvas gameCanvas) {
        this.gameCanvas = gameCanvas;
        // Verificar si el canvas es nulo
        if (this.gameCanvas == null) {
            System.err.println("ADVERTENCIA: Se ha inicializado InputManager con un canvas nulo");
        } else {
            System.out.println("InputManager inicializado con canvas válido");
        }
    }
    
    /**
     * Establece el jugador que será controlado
     * @param player Objeto jugador a controlar
     */
    public void setPlayer(Player player) {
        try {
            if (player == null) {
                System.err.println("ADVERTENCIA: Intentando establecer un jugador nulo en InputManager");
            } else {
                this.player = player;
                System.out.println("Jugador configurado en InputManager: " + player);
            }
        } catch (Exception e) {
            System.err.println("Error al configurar el jugador en InputManager: " + e.getMessage());
            e.printStackTrace();
        }
    }    /**
     * Configura los callbacks para acciones específicas
     * @param onPauseToggle Acción para alternar pausa
     * @param onReturnToMenu Acción para volver al menú
     * @param onUIToggle Acción para alternar UI
     * @param onFormulaDetails Acción para mostrar detalles de fórmula
     */
    public void setCallbacks(Runnable onPauseToggle, Runnable onReturnToMenu, 
                            Runnable onUIToggle, java.util.function.Consumer<Integer> onFormulaDetails) {
        try {
            this.onPauseToggle = onPauseToggle;
            this.onReturnToMenu = onReturnToMenu;
            this.onUIToggle = onUIToggle;
            this.onFormulaDetails = onFormulaDetails;
            System.out.println("Callbacks configurados en InputManager (versión 1)");
        } catch (Exception e) {
            System.err.println("Error al configurar callbacks en InputManager: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configura los callbacks para acciones específicas incluyendo ocultar detalles de fórmula
     * @param onPauseToggle Acción para alternar pausa
     * @param onReturnToMenu Acción para volver al menú
     * @param onUIToggle Acción para alternar UI
     * @param onFormulaDetails Acción para mostrar detalles de fórmula
     * @param onHideFormulaDetails Acción para ocultar detalles de fórmula
     */
    public void setCallbacks(Runnable onPauseToggle, Runnable onReturnToMenu, 
                            Runnable onUIToggle, java.util.function.Consumer<Integer> onFormulaDetails,
                            Runnable onHideFormulaDetails) {
        try {
            this.onPauseToggle = onPauseToggle;
            this.onReturnToMenu = onReturnToMenu;
            this.onUIToggle = onUIToggle;
            this.onFormulaDetails = onFormulaDetails;
            this.onHideFormulaDetails = onHideFormulaDetails;
            System.out.println("Callbacks configurados en InputManager (versión 2)");
        } catch (Exception e) {
            System.err.println("Error al configurar callbacks en InputManager: " + e.getMessage());
            e.printStackTrace();
        }
    }    /**
     * Configura los manejadores de teclas para el juego
     */
    public void setupKeyHandlers() {
        try {
            if (gameCanvas == null) {
                System.err.println("ERROR: El canvas es null al intentar configurar los manejadores de teclas");
                return;
            }
            
            System.out.println("Configurando manejadores de teclas...");
            System.out.println("Canvas: OK");
            
            try {
                // Agregar un listener directo al canvas para probar si recibe eventos
                gameCanvas.setOnKeyPressed(event -> {
                    if (event != null) {
                        System.out.println("Tecla presionada directamente en el canvas: " + event.getCode());
                    }
                });
                System.out.println("Listener de teclas configurado en el canvas");
            } catch (Exception e) {
                System.err.println("Error al configurar listener de teclas en el canvas: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                // Obtener la escena
                gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    System.out.println("Escena cambiada: " + (newScene != null ? "Nueva escena disponible" : "Escena nula"));
                    
                    if (newScene != null) {
                        // Solicitar el foco para el canvas cuando se carga la escena
                        gameCanvas.requestFocus();
                        System.out.println("Foco solicitado para el canvas");
                        
                        // Configurar los eventos de teclado para la escena
                        setupSceneKeyHandlers(newScene);
                        
                        // También añadir el foco al canvas cuando se hace clic en él
                        try {
                            gameCanvas.setOnMouseClicked(event -> {
                                gameCanvas.requestFocus();
                                System.out.println("Foco solicitado para el canvas por clic de ratón");
                            });
                            System.out.println("Listener de ratón configurado en el canvas");
                        } catch (Exception e) {
                            System.err.println("Error al configurar listener de ratón: " + e.getMessage());
                            e.printStackTrace();
                        }
                        
                        System.out.println("Eventos de teclado configurados correctamente");
                    } else {
                        System.err.println("ADVERTENCIA: La nueva escena es nula, no se pueden configurar los eventos");
                    }
                });
                System.out.println("Listener de cambio de escena configurado");
                
                // Si la escena ya está disponible, configurar los eventos ahora mismo
                Scene currentScene = gameCanvas.getScene();
                if (currentScene != null) {
                    System.out.println("Escena ya disponible, configurando eventos inmediatamente");
                    setupSceneKeyHandlers(currentScene);
                }
            } catch (Exception e) {
                System.err.println("Error al acceder a la propiedad de escena: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Error general al configurar los eventos de teclado: " + e.getMessage());
            e.printStackTrace();
        }
    }    /**
     * Configura los manejadores de teclas específicos para la escena
     * @param scene Escena a configurar
     */
    private void setupSceneKeyHandlers(Scene scene) {
        // Verificar que la escena no sea nula
        if (scene == null) {
            System.err.println("ERROR: Intento de configurar manejadores de teclas en una escena nula");
            return;
        }
        
        try {
            System.out.println("Configurando manejadores de teclas para la escena");
            
            scene.setOnKeyPressed(event -> {
                if (event == null) return;
                
                KeyCode code = event.getCode();
                System.out.println("Tecla presionada: " + code);
                
                if (code == KeyCode.LEFT || code == KeyCode.A) {
                    leftKeyPressed = true;
                    if (player != null) {
                        player.moveLeft(true);
                        System.out.println("Moviendo a la izquierda");
                    } else {
                        System.out.println("No se puede mover a la izquierda: player es null");
                    }
                } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    rightKeyPressed = true;
                    if (player != null) {
                        player.moveRight(true);
                        System.out.println("Moviendo a la derecha");
                    } else {
                        System.out.println("No se puede mover a la derecha: player es null");
                    }
                } else if (code == KeyCode.ESCAPE) {
                    // Si hay una ventana de detalles de fórmula abierta, cerrarla primero
                    if (onHideFormulaDetails != null) {
                        onHideFormulaDetails.run();
                        System.out.println("Cerrando detalles de fórmula");
                    }
                    // Si no, alternar pausa
                    else if (onPauseToggle != null) {
                        onPauseToggle.run();
                        System.out.println("Alternando pausa");
                    } else {
                        System.out.println("No hay callback configurado para onPauseToggle");
                    }
                } else if (code == KeyCode.BACK_SPACE) {
                    if (onReturnToMenu != null) {
                        onReturnToMenu.run();
                        System.out.println("Volviendo al menú principal");
                    } else {
                        System.out.println("No hay callback configurado para onReturnToMenu");
                    }
                } else if (code == KeyCode.M) {
                    if (onUIToggle != null) {
                        onUIToggle.run();
                        System.out.println("Alternando interfaz de usuario");
                    } else {
                        System.out.println("No hay callback configurado para onUIToggle");
                    }
                } else if (code.isDigitKey()) {
                    try {
                        // Obtener el número de la tecla presionada (1-5)
                        int formulaNumber = Integer.parseInt(code.getName()) - 1;
                        
                        if (onFormulaDetails != null) {
                            onFormulaDetails.accept(formulaNumber);
                            System.out.println("Mostrando detalles de fórmula " + (formulaNumber + 1));
                        } else {
                            System.out.println("No hay callback configurado para onFormulaDetails");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error al convertir código de tecla a número: " + e.getMessage());
                    }
                }
            });
            
            scene.setOnKeyReleased(event -> {
                if (event == null) return;
                
                KeyCode code = event.getCode();
                
                if (code == KeyCode.LEFT || code == KeyCode.A) {
                    leftKeyPressed = false;
                    if (player != null) {
                        player.moveLeft(false);
                        
                        // Si la tecla derecha sigue presionada, seguir moviendo a la derecha
                        if (rightKeyPressed) {
                            player.moveRight(true);
                        }
                    }
                } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    rightKeyPressed = false;
                    if (player != null) {
                        player.moveRight(false);
                        
                        // Si la tecla izquierda sigue presionada, seguir moviendo a la izquierda
                        if (leftKeyPressed) {
                            player.moveLeft(true);
                        }
                    }
                }
            });
            
            System.out.println("Manejadores de eventos de teclado configurados correctamente para la escena");
        } catch (Exception e) {
            System.err.println("Error al configurar manejadores de eventos de teclado para la escena: " + e.getMessage());
            e.printStackTrace();
        }
    }
      /**
     * Solicita el foco para el canvas
     */
    public void requestCanvasFocus() {
        try {
            if (gameCanvas != null) {
                gameCanvas.requestFocus();
                System.out.println("Foco solicitado para el canvas desde requestCanvasFocus()");
            } else {
                System.err.println("ERROR: No se puede solicitar foco porque gameCanvas es null en requestCanvasFocus()");
            }
        } catch (Exception e) {
            System.err.println("Error al solicitar foco para el canvas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica si la tecla izquierda está presionada
     * @return true si la tecla izquierda está presionada
     */
    public boolean isLeftKeyPressed() {
        return leftKeyPressed;
    }
    
    /**
     * Verifica si la tecla derecha está presionada
     * @return true si la tecla derecha está presionada
     */
    public boolean isRightKeyPressed() {
        return rightKeyPressed;
    }
}
