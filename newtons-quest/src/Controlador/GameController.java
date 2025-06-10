package Controlador;

import Controlador.componentes.InputManager;
import Controlador.componentes.*;
import Modelo.Player;
import java.io.File;
import java.io.IOException;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

/**
 * Controlador principal del juego.
 * Ahora actúa como coordinador entre los diferentes componentes especializados.
 */
public class GameController {
    
    @FXML private Canvas gameCanvas;
    
    // Dimensiones del juego
    private final int GAME_WIDTH = 900;
    private final int GAME_HEIGHT = 700;
    private final int FLOOR_Y = 600;
    
    // Componentes del juego
    private GraphicsContext gc;
    private Player player;
    private AnimationTimer gameLoop;
    
    // Managers especializados
    private RenderManager renderManager;
    private InputManager inputManager;
    private AudioManager audioManager;
    private ResourceManager resourceManager;
    private LevelManager levelManager;
    private AppleManager appleManager;
    private ScoreManager scoreManager;
    
    // Estado del juego
    private boolean isPaused = false;
    private boolean gameOver = false;
    private boolean minimalUI = true; // Interfaz minimalista por defecto
    
    /**
     * Inicializa el controlador y todos los componentes del juego
     */    public void initialize() {
        try {
            System.out.println("========== INICIALIZANDO GAMECONTROLLER ==========");
            
            // Verificar si el canvas existe
            if (gameCanvas == null) {
                System.err.println("ADVERTENCIA: gameCanvas es null en initialize(). Se creará uno alternativo durante la inicialización de componentes.");
                // No hacemos return aquí, seguimos adelante y crearemos el canvas en initializeComponents
            } else {
                // Obtener el contexto gráfico del canvas
                gc = gameCanvas.getGraphicsContext2D();
                System.out.println("Contexto gráfico obtenido: " + (gc != null ? "OK" : "NULL"));
                
                // Hacer que el canvas pueda recibir el foco
                gameCanvas.setFocusTraversable(true);
                System.out.println("Canvas configurado como focusTraversable");
            }
            
            // Inicializar todos los componentes
            System.out.println("Iniciando inicialización de componentes...");
            initializeComponents();
            System.out.println("Componentes inicializados: " + 
                              "inputManager=" + (inputManager != null ? "OK" : "NULL") + ", " +
                              "renderManager=" + (renderManager != null ? "OK" : "NULL") + ", " +
                              "levelManager=" + (levelManager != null ? "OK" : "NULL"));
            
            // Verificar si los componentes esenciales están inicializados
            if (inputManager == null) {
                System.err.println("ERROR CRÍTICO: InputManager no se pudo inicializar. No se puede continuar.");
                return;
            }
            
            if (renderManager == null) {
                System.err.println("ERROR CRÍTICO: RenderManager no se pudo inicializar. No se puede continuar.");
                return;
            }
            
            // Inicializar el jugador en el centro de la pantalla
            player = new Player(GAME_WIDTH / 2 - 32, FLOOR_Y - 96);
            System.out.println("Jugador inicializado en (" + player.getX() + ", " + player.getY() + ")");
            
            // Configurar el input manager con el jugador
            System.out.println("Configurando jugador en inputManager...");
            inputManager.setPlayer(player);
            
            // Configurar los callbacks entre componentes
            System.out.println("Configurando callbacks...");
            setupComponentCallbacks();
            
            // Iniciar el bucle del juego
            System.out.println("Iniciando bucle de juego...");
            startGameLoop();
            
            // Solicitar el foco para el canvas
            System.out.println("Solicitando foco para el canvas...");
            inputManager.requestCanvasFocus();
            
            System.out.println("GameController inicializado correctamente");
            System.out.println("=================================================");
        } catch (Exception e) {
            System.err.println("Error al inicializar GameController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inicializa todos los componentes del juego
     */    private void initializeComponents() {
        try {
            System.out.println("Iniciando inicialización de componentes...");
            
            // Inicializar gestores de recursos
            resourceManager = new ResourceManager();
            resourceManager.loadAllResources();
            System.out.println("ResourceManager inicializado");
            
            // Inicializar gestores de audio
            audioManager = new AudioManager();
            audioManager.playBackgroundMusic();
            System.out.println("AudioManager inicializado");
            
            // Verificar si el canvas es null y crear uno alternativo si es necesario
            if (gameCanvas == null) {
                System.err.println("ADVERTENCIA: gameCanvas es null, creando uno alternativo");
                gameCanvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
                // Hacer que el canvas pueda recibir el foco
                gameCanvas.setFocusTraversable(true);
                // Obtener el contexto gráfico del canvas
                gc = gameCanvas.getGraphicsContext2D();
                System.out.println("Canvas alternativo creado y configurado");
            }
            
            // Asegurarse de que el contexto gráfico no sea nulo
            if (gc == null && gameCanvas != null) {
                gc = gameCanvas.getGraphicsContext2D();
                System.out.println("Contexto gráfico inicializado");
            }
            
            // Inicializar gestor de entrada (IMPORTANTE: inicializar antes de usarlo)
            System.out.println("Iniciando InputManager con canvas: " + (gameCanvas != null ? "OK" : "NULL"));
            inputManager = new InputManager(gameCanvas);
            System.out.println("InputManager creado");
            
            // Solo configurar manejadores de teclas si el canvas no es nulo
            if (gameCanvas != null) {
                inputManager.setupKeyHandlers(); // Configurar los manejadores de eventos de teclado
                System.out.println("Manejadores de teclas configurados");
            } else {
                System.err.println("ERROR: No se pueden configurar manejadores de teclas porque el canvas es nulo");
            }
              // Inicializar gestor de renderizado
            if (gc != null) {
                renderManager = new RenderManager(gc, GAME_WIDTH, GAME_HEIGHT, FLOOR_Y);
                renderManager.setImages(
                    resourceManager.getBackgroundImage(),
                    resourceManager.getHeartImage(),
                    resourceManager.getEmptyHeartImage()
                );
                System.out.println("RenderManager inicializado");
            } else {
                System.err.println("ERROR: No se puede inicializar RenderManager porque gc es nulo");
            }
            
            // Inicializar gestor de niveles
            levelManager = new LevelManager();
            System.out.println("LevelManager inicializado");
            
            // Pasar la información de fórmulas al RenderManager solo si el renderManager no es nulo
            if (renderManager != null && levelManager != null) {
                renderManager.setFormulasInfo(
                    levelManager.getFormulasShort(),
                    levelManager.getFormulasDescriptions()
                );
                System.out.println("Información de fórmulas configurada en RenderManager");
            } else {
                System.err.println("ERROR: No se puede configurar información de fórmulas porque renderManager o levelManager son nulos");
            }
            
            // Inicializar gestor de puntuación (5 vidas máximo)
            scoreManager = new ScoreManager(5);
            System.out.println("ScoreManager inicializado");
            
            // Inicializar gestor de manzanas
            appleManager = new AppleManager(GAME_WIDTH, GAME_HEIGHT, FLOOR_Y, 1500, 2.0, 5.0);
            System.out.println("AppleManager inicializado");
            
            System.out.println("Todos los componentes inicializados correctamente");
        } catch (Exception e) {
            System.err.println("Error al inicializar componentes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configura los callbacks entre los diferentes componentes
     */
    private void setupComponentCallbacks() {
        try {
            // Verificar que todos los componentes estén inicializados
            if (inputManager == null) {
                System.err.println("ERROR: InputManager es null en setupComponentCallbacks");
                return;
            }
            
            if (renderManager == null) {
                System.err.println("ERROR: RenderManager es null en setupComponentCallbacks");
                return;
            }
            
            if (scoreManager == null) {
                System.err.println("ERROR: ScoreManager es null en setupComponentCallbacks");
                return;
            }
            
            if (levelManager == null) {
                System.err.println("ERROR: LevelManager es null en setupComponentCallbacks");
                return;
            }
            
            if (appleManager == null) {
                System.err.println("ERROR: AppleManager es null en setupComponentCallbacks");
                return;
            }
            
            if (audioManager == null) {
                System.err.println("ERROR: AudioManager es null en setupComponentCallbacks");
                return;
            }
            
            // Configurar callbacks del InputManager
            inputManager.setCallbacks(
                this::togglePause,                 // onPauseToggle
                this::returnToMainMenu,            // onReturnToMenu
                this::toggleUI,                    // onUIToggle
                this::showFormulaDetails,          // onFormulaDetails
                () -> renderManager.hideFormulaDetails() // onHideFormulaDetails
            );
            
            // Configurar callbacks del ScoreManager
            scoreManager.setCallbacks(
                this::setGameOver,                 // onGameOver
                score -> levelManager.checkLevelProgress(score) // onScoreChange
            );
            
            // Configurar callbacks del LevelManager
            levelManager.setOnFormulaUnlocked(
                audioManager::playUnlockSound      // onFormulaUnlocked
            );
            
            // Configurar callbacks del AppleManager
            appleManager.setCallbacks(
                scoreManager::addScore,            // onScoreChange
                this::loseLife                     // onLifeLost
            );
            
            System.out.println("Todos los callbacks configurados correctamente");
        } catch (Exception e) {
            System.err.println("Error al configurar callbacks: " + e.getMessage());
            e.printStackTrace();
        }
    }    /**
     * Inicia el bucle principal del juego
     */
    private void startGameLoop() {
        try {
            // Verificar componentes críticos antes de iniciar el bucle
            if (renderManager == null) {
                System.err.println("ERROR CRÍTICO: RenderManager es null, no se puede iniciar el bucle del juego");
                return;
            }
            
            if (player == null) {
                System.err.println("ERROR CRÍTICO: Player es null, no se puede iniciar el bucle del juego");
                return;
            }
            
            if (appleManager == null) {
                System.err.println("ERROR CRÍTICO: AppleManager es null, no se puede iniciar el bucle del juego");
                return;
            }
            
            if (scoreManager == null) {
                System.err.println("ERROR CRÍTICO: ScoreManager es null, no se puede iniciar el bucle del juego");
                return;
            }
            
            if (levelManager == null) {
                System.err.println("ERROR CRÍTICO: LevelManager es null, no se puede iniciar el bucle del juego");
                return;
            }
            
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    try {
                        // Siempre renderizar, incluso cuando el juego está pausado
                        render();
                        
                        // Solo actualizar cuando el juego no está pausado y no ha terminado
                        if (!isPaused && !gameOver) {
                            update();
                        }
                    } catch (Exception e) {
                        System.err.println("Error en el bucle del juego: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            };
            
            gameLoop.start();
            System.out.println("Bucle del juego iniciado correctamente");
        } catch (Exception e) {
            System.err.println("Error al iniciar el bucle del juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
      /**
     * Actualiza el estado del juego
     */
    private void update() {
        try {
            // Verificar componentes críticos
            if (player == null) {
                System.err.println("ERROR: Player es null en update(), no se puede actualizar");
                return;
            }
            
            if (appleManager == null) {
                System.err.println("ERROR: AppleManager es null en update(), no se puede actualizar");
                return;
            }
            
            if (levelManager == null) {
                System.err.println("ERROR: LevelManager es null en update(), no se puede actualizar");
                return;
            }
            
            // Actualizar el jugador
            player.update(FLOOR_Y);
            
            // Mantener al jugador dentro de los límites de la pantalla
            if (player.getX() < 0) {
                player.setPosition(0, player.getY());
            } else if (player.getX() > GAME_WIDTH - player.getWidth()) {
                player.setPosition(GAME_WIDTH - player.getWidth(), player.getY());
            }
            
            // Actualizar manzanas y colisiones
            appleManager.update(player);
            
            // Actualizar efecto de desbloqueo de fórmulas
            levelManager.updateUnlockEffect();
            
        } catch (Exception e) {
            System.err.println("Error al actualizar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }    /**
     * Renderiza el estado actual del juego
     */
    private void render() {
        try {
            // Verificar componentes críticos
            if (renderManager == null) {
                System.err.println("ERROR: RenderManager es null en render(), no se puede renderizar");
                return;
            }
            
            if (scoreManager == null) {
                System.err.println("ERROR: ScoreManager es null en render(), no se puede renderizar");
                return;
            }
            
            if (levelManager == null) {
                System.err.println("ERROR: LevelManager es null en render(), no se puede renderizar");
                return;
            }
            
            if (player == null) {
                System.err.println("ERROR: Player es null en render(), no se puede renderizar al jugador");
                // Continuamos para al menos mostrar el fondo y la interfaz
            }
            
            if (appleManager == null) {
                System.err.println("ERROR: AppleManager es null en render(), no se pueden renderizar las manzanas");
                // Continuamos para al menos mostrar el fondo y la interfaz
            }
            
            // Renderizar fondo
            renderManager.renderBackground();
            
            // Renderizar manzanas si están disponibles
            if (appleManager != null) {
                renderManager.renderApples(appleManager.getApples());
            }
            
            // Renderizar jugador si está disponible
            if (player != null) {
                renderManager.renderPlayer(player);
            }            // Determinar si debemos mostrar las interfaces de HUD o superposiciones modales
            boolean mostrarPausa = isPaused && !gameOver && !renderManager.isShowingFormulaDetails();
            boolean mostrarFormula = renderManager.isShowingFormulaDetails();
            boolean mostrarDesbloqueo = levelManager.isShowingUnlockEffect();
            boolean mostrarGameOver = gameOver;
            
            // Si vamos a mostrar una pantalla de pausa o detalles de fórmula, no renderizar el HUD
            if (!mostrarPausa && !mostrarFormula && !mostrarGameOver) {
                // Solo usar interfaz minimalista
                renderManager.renderMinimalUI(
                    scoreManager.getScore(), 
                    scoreManager.getLives(), 
                    scoreManager.getMaxLives(), 
                    levelManager.getLevel(),
                    mostrarDesbloqueo
                );
            }
            
            // Mostrar el popup de desbloqueo de fórmula si corresponde
            if (mostrarDesbloqueo) {
                // Actualizar el índice de la fórmula en el RenderManager
                renderManager.setCurrentFormulaForUnlock(levelManager.getUnlockedFormulaIndex());
                // Mostrar la notificación (ya no pausa el juego)
                renderManager.renderUnlockPopup();
            }
            
            // Renderizar detalles de fórmula si están activos
            if (mostrarFormula) {
                renderManager.renderFormulaDetailsModal();
            } 
            
            // Mostrar pantalla de pausa solo si corresponde
            if (mostrarPausa) {
                renderManager.renderPauseScreen();
            }
            
            // Mostrar pantalla de Game Over si el juego ha terminado
            if (mostrarGameOver) {
                renderManager.renderGameOverScreen(
                    scoreManager.getScore(),
                    levelManager.getUnlockedFormulas(),
                    levelManager.getMaxLevel()
                );
            }
            
        } catch (Exception e) {
            System.err.println("Error al renderizar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Alterna entre pausa y reanudación del juego
     */
    private void togglePause() {
        isPaused = !isPaused;
        System.out.println("Juego " + (isPaused ? "pausado" : "reanudado"));
    }
      /**
     * Alterna entre interfaz minimalista y completa (desactivado)
     */
    private void toggleUI() {
        // Siempre mantenemos la interfaz minimalista
        minimalUI = true;
        // No hacer nada más, ya que solo queremos la interfaz minimalista
        System.out.println("Interfaz se mantiene en modo minimalista");
    }
      /**
     * Marca el juego como terminado y configura el sprite de muerte del jugador
     */
    private void setGameOver() {
        gameOver = true;
        // Configurar el sprite de muerte del jugador
        if (player != null) {
            player.setDead(true);
        }
        System.out.println("Juego terminado");
    }
    
    /**
     * Maneja la pérdida de una vida
     */
    private void loseLife() {
        scoreManager.loseLife("Has perdido una vida");
    }    /**
     * Muestra los detalles de una fórmula específica
     * @param formulaIndex Índice de la fórmula
     */
    private void showFormulaDetails(int formulaIndex) {
        try {
            // Verificar componentes críticos
            if (levelManager == null) {
                System.err.println("ERROR: LevelManager es null en showFormulaDetails, no se puede continuar");
                return;
            }
            
            if (renderManager == null) {
                System.err.println("ERROR: RenderManager es null en showFormulaDetails, no se puede continuar");
                return;
            }
            
            // Verificar si el índice está fuera de rango
            if (formulaIndex < 0 || formulaIndex >= levelManager.getMaxLevel()) {
                System.err.println("ERROR: Índice de fórmula fuera de rango: " + formulaIndex);
                return;
            }
            
            // Verificar si la fórmula está desbloqueada
            if (!levelManager.getUnlockedFormulas()[formulaIndex]) {
                System.out.println("La fórmula " + (formulaIndex + 1) + " no está desbloqueada todavía");
                return;
            }
            
            // Si ya se están mostrando los detalles de esta fórmula, ocultarlos y reanudar el juego
            if (renderManager.isShowingFormulaDetails() && formulaIndex == levelManager.getUnlockedFormulaIndex()) {
                renderManager.hideFormulaDetails();
                // Reanudar el juego si estaba pausado por mostrar la fórmula
                if (isPaused) {
                    isPaused = false;
                }
                System.out.println("Ocultando detalles de la fórmula y reanudando el juego");
                return;
            }
            
            // Pausar el juego mientras se muestran los detalles de la fórmula
            isPaused = true;
            
            // Registrar en el LevelManager para tener consistencia
            levelManager.showFormulaDetails(formulaIndex);
            
            // Obtener información de la fórmula
            String[] formulasShort = levelManager.getFormulasShort();
            String[] formulasDescriptions = levelManager.getFormulasDescriptions();
            
            // Verificar que los arreglos no sean nulos y tengan el índice solicitado
            if (formulasShort == null || formulasDescriptions == null) {
                System.err.println("ERROR: Los arreglos de fórmulas son nulos");
                return;
            }
            
            if (formulaIndex >= formulasShort.length || formulaIndex >= formulasDescriptions.length) {
                System.err.println("ERROR: Índice de fórmula fuera de rango para los arreglos de información");
                return;
            }
            
            // Mostrar los detalles en pantalla mediante el RenderManager
            renderManager.setFormulaDetails(
                formulaIndex,
                formulasShort[formulaIndex],
                formulasDescriptions[formulaIndex]
            );
            
            System.out.println("Mostrando detalles de la fórmula: " + formulasShort[formulaIndex]);
            System.out.println("Descripción: " + formulasDescriptions[formulaIndex]);
            System.out.println("Juego pausado mientras se muestran detalles de la fórmula");
        } catch (Exception e) {
            System.err.println("Error al mostrar detalles de la fórmula: " + e.getMessage());
            e.printStackTrace();
        }
    }
      /**
     * Vuelve al menú principal
     */
    public void returnToMainMenu() {
        try {
            System.out.println("Preparando para volver al menú principal...");
            
            // Detener el bucle del juego
            if (gameLoop != null) {
                gameLoop.stop();
                System.out.println("Bucle del juego detenido");
            } else {
                System.err.println("ADVERTENCIA: gameLoop es null al intentar detenerlo");
            }
            
            // Detener la música
            if (audioManager != null) {
                audioManager.stopBackgroundMusic();
                System.out.println("Música de fondo detenida");
            } else {
                System.err.println("ADVERTENCIA: audioManager es null al intentar detener la música");
            }
            
            System.out.println("Volviendo al menú principal...");
            
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
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            
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