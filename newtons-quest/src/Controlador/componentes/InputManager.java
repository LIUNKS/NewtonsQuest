package Controlador.componentes;

import Modelo.Player;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;

/**
 * Clase encargada de gestionar la entrada del usuario en el juego.
 * Maneja eventos de teclado y mouse, proporcionando una interfaz limpia
 * para controlar el jugador y ejecutar acciones del juego.
 */
public class InputManager {
    
    private final Canvas gameCanvas;
    private Player player;
    
    // Estado de las teclas
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    
    // Callbacks para acciones del juego
    private Runnable onPauseToggle;
    private Runnable onReturnToMenu;    
    private Runnable onUIToggle;    
    private Runnable onShowSettings;
    private java.util.function.Consumer<Integer> onFormulaDetails;
    private Runnable onHideFormulaDetails;
    private Runnable onShowRanking; // Callback para mostrar ranking
    private Runnable onHideRanking; // Callback para ocultar ranking
    private Runnable onContinueAfterCompletion; // Callback para continuar después del mensaje de completación
    private java.util.function.BiConsumer<Double, Double> onMouseClick; // Callback para clics del mouse
    
    /**
     * Constructor del InputManager.
     * @param gameCanvas Canvas del juego donde se capturan los eventos
     */
    public InputManager(Canvas gameCanvas) {
        this.gameCanvas = gameCanvas;
        // Verificar si el canvas es nulo
        if (this.gameCanvas == null) {
            System.err.println("ADVERTENCIA: Se ha inicializado InputManager con un canvas nulo");
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
                // Jugador configurado exitosamente
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
            // Callbacks configurados en InputManager
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
            // Callbacks configurados en InputManager
        } catch (Exception e) {
            System.err.println("Error al configurar callbacks en InputManager: " + e.getMessage());
            e.printStackTrace();
        }
    }
      /**
     * Configura los callbacks para acciones específicas incluyendo configuración
     * @param onPauseToggle Acción para alternar pausa
     * @param onReturnToMenu Acción para volver al menú
     * @param onUIToggle Acción para alternar UI
     * @param onShowSettings Acción para mostrar configuración
     * @param onFormulaDetails Acción para mostrar detalles de fórmula
     * @param onHideFormulaDetails Acción para ocultar detalles de fórmula
     */
    public void setCallbacks(Runnable onPauseToggle, Runnable onReturnToMenu, 
                            Runnable onUIToggle, Runnable onShowSettings,
                            java.util.function.Consumer<Integer> onFormulaDetails,
                            Runnable onHideFormulaDetails) {
        try {
            this.onPauseToggle = onPauseToggle;
            this.onReturnToMenu = onReturnToMenu;
            this.onUIToggle = onUIToggle;
            this.onShowSettings = onShowSettings;
            this.onFormulaDetails = onFormulaDetails;
            this.onHideFormulaDetails = onHideFormulaDetails;
            // Callbacks configurados en InputManager
        } catch (Exception e) {
            System.err.println("Error al configurar callbacks en InputManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura los callbacks para acciones específicas incluyendo sistema de ranking
     * @param onPauseToggle Acción para alternar pausa
     * @param onReturnToMenu Acción para volver al menú
     * @param onUIToggle Acción para alternar UI
     * @param onShowSettings Acción para mostrar configuración
     * @param onFormulaDetails Acción para mostrar detalles de fórmula
     * @param onHideFormulaDetails Acción para ocultar detalles de fórmula
     * @param onShowRanking Acción para mostrar ranking
     * @param onHideRanking Acción para ocultar ranking     */    public void setCallbacks(Runnable onPauseToggle, Runnable onReturnToMenu, 
                            Runnable onUIToggle, Runnable onShowSettings,
                            java.util.function.Consumer<Integer> onFormulaDetails,
                            Runnable onHideFormulaDetails, Runnable onShowRanking,
                            Runnable onHideRanking, Runnable onContinueAfterCompletion,
                            java.util.function.BiConsumer<Double, Double> onMouseClick) {
        try {
            this.onPauseToggle = onPauseToggle;
            this.onReturnToMenu = onReturnToMenu;
            this.onUIToggle = onUIToggle;
            this.onShowSettings = onShowSettings;
            this.onFormulaDetails = onFormulaDetails;
            this.onHideFormulaDetails = onHideFormulaDetails;
            this.onShowRanking = onShowRanking;
            this.onHideRanking = onHideRanking;
            this.onContinueAfterCompletion = onContinueAfterCompletion;
            this.onMouseClick = onMouseClick;
            // Callbacks configurados en InputManager
        } catch (Exception e) {
            System.err.println("Error al configurar callbacks en InputManager: " + e.getMessage());
            e.printStackTrace();
        }
    }/**
     * Configura los manejadores de teclas para el juego
     */
    public void setupKeyHandlers() {
        try {
            if (gameCanvas == null) {
                System.err.println("ERROR: El canvas es null al intentar configurar los manejadores de teclas");
                return;
            }
            
            // Configurando manejadores de teclas
            // Canvas configurado correctamente
            
            try {
                // Agregar un listener directo al canvas para probar si recibe eventos
                gameCanvas.setOnKeyPressed(event -> {
                    if (event != null) {
                        // Tecla presionada en canvas
                    }
                });
                // Listener de teclas configurado
            } catch (Exception e) {
                System.err.println("Error al configurar listener de teclas en el canvas: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                // Obtener la escena
                gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    // Escena cambiada
                    
                    if (newScene != null) {
                        // Solicitar el foco para el canvas cuando se carga la escena
                        gameCanvas.requestFocus();
                        // Foco solicitado para el canvas
                        
                        // Configurar los eventos de teclado para la escena
                        setupSceneKeyHandlers(newScene);
                          // También añadir el foco al canvas cuando se hace clic en él
                        try {
                            gameCanvas.setOnMouseClicked(event -> {
                                gameCanvas.requestFocus();
                                // Foco solicitado para el canvas
                                
                                // Notificar sobre el clic para manejo de botones
                                if (onMouseClick != null) {
                                    onMouseClick.accept(event.getX(), event.getY());
                                }
                            });
                            // Listener de ratón configurado
                        } catch (Exception e) {
                            System.err.println("Error al configurar listener de ratón: " + e.getMessage());
                            e.printStackTrace();
                        }
                        
                        // Eventos de teclado configurados
                    } else {
                        System.err.println("ADVERTENCIA: La nueva escena es nula, no se pueden configurar los eventos");
                    }
                });
                // Listener de cambio de escena configurado
                
                // Si la escena ya está disponible, configurar los eventos ahora mismo
                Scene currentScene = gameCanvas.getScene();
                if (currentScene != null) {
                    // Escena ya disponible, configurando eventos inmediatamente
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
            // Configurando manejadores de teclas para la escena
            
            scene.setOnKeyPressed(event -> {
                if (event == null) return;
                
                KeyCode code = event.getCode();
                // Tecla presionada
                
                if (code == KeyCode.LEFT || code == KeyCode.A) {
                    leftKeyPressed = true;
                    if (player != null) {
                        player.moveLeft(true);
                        // Moviendo a la izquierda
                    } else {
                        // No se puede mover a la izquierda: player es null
                    }
                } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    rightKeyPressed = true;
                    if (player != null) {
                        player.moveRight(true);
                        // Moviendo a la derecha
                    } else {
                        // No se puede mover a la derecha: player es null
                    }                } else if (code == KeyCode.ESCAPE) {
                    // Delegar el manejo de ESCAPE al GameController
                    if (onPauseToggle != null) {
                        onPauseToggle.run();
                        // Procesando tecla ESCAPE
                    } else {
                        // No hay callback configurado para ESCAPE
                    }
                } else if (code == KeyCode.BACK_SPACE) {
                    if (onReturnToMenu != null) {
                        onReturnToMenu.run();
                        // Volviendo al menú principal
                    } else {
                        // No hay callback configurado para onReturnToMenu
                    }                } else if (code == KeyCode.M) {
                    if (onUIToggle != null) {
                        onUIToggle.run();
                        // Alternando interfaz de usuario
                    } else {
                        // No hay callback configurado para onUIToggle
                    }                } else if (code == KeyCode.S) {
                    if (onShowSettings != null) {
                        onShowSettings.run();
                        // Abriendo configuración
                    } else {
                        // No hay callback configurado para onShowSettings
                    }                } else if (code == KeyCode.R) {
                    if (onShowRanking != null) {
                        onShowRanking.run();
                        System.out.println("Mostrando ranking");
                    } else {
                        System.out.println("No hay callback configurado para onShowRanking");
                    }
                } else if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                    if (onContinueAfterCompletion != null) {
                        onContinueAfterCompletion.run();
                        System.out.println("Continuando después del mensaje de completación");
                    } else {
                        System.out.println("No hay callback configurado para onContinueAfterCompletion");
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
