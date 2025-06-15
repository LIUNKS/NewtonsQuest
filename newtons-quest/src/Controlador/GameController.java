package Controlador;

import Controlador.componentes.InputManager;
import Controlador.componentes.*;
import Controlador.dialogs.SettingsDialog;
import Controlador.utils.GameSettings;
import Controlador.navigation.NavigationManager;
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
    private VisualEffectsManager visualEffectsManager;
    private RankingManager rankingManager; // Nuevo manager para el ranking
      // Estado del juego
    private boolean isPaused = false;
    private boolean isPausedForCelebration = false; // Nueva variable para pausa de celebración
    private boolean gameOver = false;
    private boolean minimalUI = true; // Interfaz minimalista por defecto
    private boolean showingSettings = false; // Nueva variable para controlar el estado de configuración
    
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
            
            // Inicializar gestor de efectos visuales
            if (gameCanvas != null && gameCanvas.getScene() != null) {
                visualEffectsManager = new VisualEffectsManager(gameCanvas.getScene());
                System.out.println("VisualEffectsManager inicializado");            } else {
                System.out.println("VisualEffectsManager se inicializará más tarde cuando la escena esté disponible");
            }
            
            // Inicializar gestor de ranking
            rankingManager = RankingManager.getInstance();
            
            // Configurar usuario actual desde LoginController
            String currentUsername = Controlador.LoginController.getCurrentUsername();
            if (currentUsername != null && !currentUsername.isEmpty()) {
                int userId = Modelo.UsuarioDAO.obtenerIdUsuario(currentUsername);
                rankingManager.setCurrentUser(userId, currentUsername);
                System.out.println("RankingManager configurado para usuario: " + currentUsername);
            } else {
                System.out.println("No hay usuario actual definido para el ranking");
            }              // Configurar callback para cuando se completan todas las fórmulas
            if (levelManager != null && renderManager != null) {
                levelManager.setOnAllFormulasCompleted(() -> {
                    System.out.println("¡Todas las fórmulas completadas! Iniciando celebración...");
                    // Activar pausa especial para celebración (sin mostrar interfaz de pausa)
                    isPausedForCelebration = true;
                    renderManager.startCompletionCelebration();
                    
                    // También guardar inmediatamente en el ranking
                    if (rankingManager != null && scoreManager != null) {
                        boolean saved = rankingManager.checkAndSaveCompletedGame(
                            levelManager.getUnlockedFormulas(), 
                            scoreManager.getScore()
                        );
                        if (saved) {
                            System.out.println("Completación guardada en ranking inmediatamente");
                        }
                    }
                });
                System.out.println("Callback de completación de todas las fórmulas configurado");
            }
            
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
            }            // Configurar callbacks del InputManager
            inputManager.setCallbacks(
                this::handleEscapeKey,             // onPauseToggle (ahora maneja ESCAPE inteligentemente)
                this::returnToMainMenu,            // onReturnToMenu
                this::toggleUI,                    // onUIToggle
                this::showSettings,                // onShowSettings
                this::showFormulaDetails,          // onFormulaDetails
                () -> {}, // Dummy callback ya que ESCAPE maneja esto
                this::showRanking,                 // onShowRanking
                () -> {}, // Dummy callback ya que ESCAPE maneja esto
                this::continueAfterCompletion,     // onContinueAfterCompletion
                this::handleMouseClick             // onMouseClick (nuevo)
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
    }    /**
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
            
            // NO actualizar el juego si está pausado por celebración (pausa silenciosa)
            if (isPausedForCelebration) {
                // Solo actualizar el estado de la celebración para detectar cuándo termina
                if (renderManager != null) {
                    boolean wasShowingCelebration = renderManager.isShowingCompletionCelebration();
                    renderManager.updateCompletionCelebration();
                    
                    // Si la celebración terminó, reanudar el juego
                    if (wasShowingCelebration && !renderManager.isShowingCompletionCelebration()) {
                        System.out.println("Celebración terminada, reanudando el juego...");
                        isPausedForCelebration = false;
                    }
                }
                return; // No actualizar nada más durante la pausa de celebración
            }
            
            // Actualizar el jugador solo si no está pausado
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
    }/**
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
            boolean mostrarCelebracion = renderManager.isShowingCompletionCelebration();
            boolean mostrarPausa = isPaused && !gameOver && !renderManager.isShowingFormulaDetails() && !mostrarCelebracion && !isPausedForCelebration;
            boolean mostrarFormula = renderManager.isShowingFormulaDetails();
            boolean mostrarDesbloqueo = levelManager.isShowingUnlockEffect();
            boolean mostrarGameOver = gameOver;
              // Si vamos a mostrar una pantalla de pausa, detalles de fórmula o game over, no renderizar el HUD
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
            }            // Renderizar detalles de fórmula si están activos
            if (mostrarFormula) {
                renderManager.renderFormulaDetailsModal();
            }

            // Mostrar pantalla de pausa solo si corresponde (y NO si hay celebración activa)
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

            // Mostrar celebración de completación AL FINAL para que aparezca en primer plano
            if (mostrarCelebracion) {
                renderManager.renderCompletionCelebrationDuringGame(scoreManager.getScore());
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
    }    /**
     * Marca el juego como terminado y configura el sprite de muerte del jugador
     */
    private void setGameOver() {
        gameOver = true;
        // Configurar el sprite de muerte del jugador
        if (player != null) {
            player.setDead(true);
        }
        
        // Verificar si el jugador completó todas las fórmulas y guardar en ranking
        if (levelManager != null && scoreManager != null && rankingManager != null) {
            boolean allFormulasCompleted = rankingManager.checkAndSaveCompletedGame(
                levelManager.getUnlockedFormulas(), 
                scoreManager.getScore()
            );
            
            if (allFormulasCompleted) {
                System.out.println("¡Jugador completó todas las fórmulas! Datos guardados en ranking.");
            }
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
    }    /**
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
            
            // Usar el NavigationManager para volver al menú principal con el usuario configurado
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
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
     * Abre el diálogo de configuración del juego
     */
    private void showSettings() {
        try {
            isPaused = true; // Pausar el juego mientras se muestran las configuraciones
            showingSettings = true;
            
            Stage gameStage = (Stage) gameCanvas.getScene().getWindow();
            
            // Crear el diálogo de configuración con callbacks
            SettingsDialog settingsDialog = new SettingsDialog(
                gameStage,
                this::updateBrightness,  // Callback para brillo
                this::updateVolume       // Callback para volumen
            );
            
            settingsDialog.showAndWait();
            
            // Cuando se cierra el diálogo, reanudar el juego si no estaba en game over
            showingSettings = false;
            if (!gameOver) {
                isPaused = false;
            }
            
            System.out.println("Configuración cerrada, juego reanudado");
        } catch (Exception e) {
            System.err.println("Error al mostrar configuración: " + e.getMessage());
            e.printStackTrace();
            showingSettings = false;
            if (!gameOver) {
                isPaused = false;
            }
        }
    }
    
    /**
     * Actualiza el brillo del juego
     */
    private void updateBrightness() {
        try {
            if (visualEffectsManager == null && gameCanvas != null && gameCanvas.getScene() != null) {
                // Inicializar el gestor de efectos visuales si no existe
                visualEffectsManager = new VisualEffectsManager(gameCanvas.getScene());
            }
            
            if (visualEffectsManager != null) {
                visualEffectsManager.updateBrightness();
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar brillo: " + e.getMessage());
        }
    }
      /**
     * Actualiza el volumen del juego
     */
    private void updateVolume() {
        try {
            if (audioManager != null) {
                GameSettings settings = GameSettings.getInstance();
                audioManager.setMusicVolume(settings.getMusicVolume());
                audioManager.setEffectVolume(settings.getEffectVolume());
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar volumen: " + e.getMessage());
        }
    }    /**
     * Muestra la pantalla de ranking
     */
    private void showRanking() {
        try {
            // Solo mostrar ranking si el juego está terminado y se han completado todas las fórmulas
            if (!gameOver) {
                System.out.println("El ranking solo se puede ver cuando el juego ha terminado");
                return;
            }
            
            if (levelManager == null || !levelManager.areAllFormulasUnlocked()) {
                System.out.println("El ranking solo se puede ver cuando se han completado todas las fórmulas");
                return;
            }
            
            Stage gameStage = (Stage) gameCanvas.getScene().getWindow();
            Controlador.dialogs.RankingDialog rankingDialog = new Controlador.dialogs.RankingDialog(gameStage);
            rankingDialog.showAndWait();
            
            System.out.println("Ranking mostrado exitosamente");
        } catch (Exception e) {
            System.err.println("Error al mostrar ranking: " + e.getMessage());
            e.printStackTrace();
        }
    }/**
     * Oculta la pantalla de ranking (método deshabilitado - funcionalidad no implementada)
     */
    private void hideRanking() {
        try {
            // La funcionalidad de ranking completo no está implementada en RenderManager
            // El ranking se muestra solo en la celebración de completitud
            System.out.println("Funcionalidad de ranking no implementada");
        } catch (Exception e) {
            System.err.println("Error al ocultar ranking: " + e.getMessage());
            e.printStackTrace();
        }
    }      /**
     * Maneja la tecla ESCAPE con prioridades: detalles de fórmula > pausa
     */
    private void handleEscapeKey() {
        try {
            // Prioridad 1: Si se están mostrando detalles de fórmula, cerrarlos
            if (renderManager != null && renderManager.isShowingFormulaDetails()) {
                renderManager.hideFormulaDetails();
                System.out.println("Cerrando detalles de fórmula");
                return;
            }
            
            // Prioridad 3: Alternar pausa
            togglePause();
        } catch (Exception e) {
            System.err.println("Error al manejar tecla ESCAPE: " + e.getMessage());
            e.printStackTrace();
        }
    }
      /**
     * Continúa el juego después del mensaje de completación
     */
    private void continueAfterCompletion() {
        try {
            if (renderManager != null && renderManager.isShowingCompletionCelebration()) {
                // Ocultar la celebración y reanudar el juego
                renderManager.stopCompletionCelebration();
                isPausedForCelebration = false; // Reanudar desde pausa de celebración
                System.out.println("Continuando juego después del mensaje de completación");
            } else {
                System.out.println("No hay celebración activa para continuar");
            }
        } catch (Exception e) {
            System.err.println("Error al continuar después de completación: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Maneja clics del mouse en la interfaz
     */
    private void handleMouseClick(double mouseX, double mouseY) {
        try {
            // Solo manejar clics si estamos en pausa (menu de pausa visible)
            if (isPaused && !gameOver && !renderManager.isShowingFormulaDetails() && !isPausedForCelebration) {
                String action = renderManager.handlePauseMenuClick(mouseX, mouseY);
                
                if (action != null) {
                    switch (action) {
                        case "back":
                            System.out.println("Botón 'Volver al menú' clickeado");
                            returnToMainMenu();
                            break;
                        case "settings":
                            System.out.println("Botón 'Configuración' clickeado");
                            showSettings();
                            break;
                        default:
                            System.out.println("Acción desconocida: " + action);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al manejar clic del mouse: " + e.getMessage());
            e.printStackTrace();
        }
    }
}