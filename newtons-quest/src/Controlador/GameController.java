package Controlador;

import Controlador.componentes.InputManager;
import Controlador.componentes.*;
import Controlador.componentes.PlayerManager;
import Controlador.dialogs.SettingsDialog;
import Controlador.utils.GameSettings;
import Controlador.utils.SessionManager;
import Controlador.navigation.NavigationManager;
import Modelo.dto.Player;
import java.io.IOException;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

/**
 * Controlador principal del juego.
 * 
 * Esta clase maneja toda la lógica del juego, incluyendo:
 * - Inicialización de componentes (managers de recursos, audio, input, render, etc.)
 * - Bucle principal del juego (actualización y renderizado)
 * - Manejo de eventos del usuario (teclado, mouse)
 * - Gestión de estados del juego (pausado, jugando, game over)
 * - Coordinación entre diferentes sistemas del juego
 * 
 * El juego utiliza un patrón de arquitectura basado en componentes donde cada
 * manager se encarga de una funcionalidad específica:
 * - ResourceManager: Carga y gestión de recursos (imágenes, sonidos)
 * - AudioManager: Reproducción de música y efectos de sonido
 * - InputManager: Manejo de entrada del usuario
 * - RenderManager: Renderizado de gráficos en pantalla
 * - LevelManager: Gestión de niveles y progresión
 * - ScoreManager: Sistema de puntuación y vidas
 * - AppleManager/PotionManager: Gestión de objetos coleccionables
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
    
    // ===================================
    // MANAGERS ESPECIALIZADOS
    // ===================================
    
    /** Gestor de renderizado y gráficos */
    private RenderManager renderManager;
    
    /** Gestor de entrada del usuario */
    private InputManager inputManager;
    
    /** Gestor de audio y música */
    private AudioManager audioManager;
    
    /** Gestor de recursos del juego */
    private ResourceManager resourceManager;
    
    /** Gestor de niveles y progresión */
    private LevelManager levelManager;
    
    /** Gestor de manzanas */
    private AppleManager appleManager;
    
    /** Gestor de pociones */
    private PotionManager potionManager;
    
    /** Gestor de puntuación */
    private ScoreManager scoreManager;
    
    /** Gestor de efectos visuales */
    private VisualEffectsManager visualEffectsManager;
    
    /** Gestor de ranking */
    private RankingManager rankingManager;
    
    /** Gestor de videos */
    private VideoManager videoManager;
    
    /** Gestor del jugador */
    private PlayerManager playerManager;
    
    // ===================================
    // ESTADO DEL JUEGO
    // ===================================
    
    /** Indica si el juego está pausado */
    private boolean isPaused = false;
    
    /** Indica si el juego está pausado por celebración */
    private boolean isPausedForCelebration = false;
    
    /** Indica si el juego ha terminado */
    private boolean gameOver = false;  
    
    /** Indica si se están mostrando las configuraciones */
    private boolean showingSettings = false;
    
    // ===================================
    // MÉTODOS DE INICIALIZACIÓN
    // ===================================
    
    /**
     * Inicializa el controlador y todos los componentes del juego.
     * Este método configura todos los managers, el jugador, y los callbacks necesarios.
     */
    public void initialize() {
        try {
            if (gameCanvas == null) {
                // Se creará un canvas alternativo durante la inicialización de componentes
            } else {
                gc = gameCanvas.getGraphicsContext2D();
                gameCanvas.setFocusTraversable(true);
            }
            
            initializeComponents();
            
            if (inputManager == null || renderManager == null) {
                return; // No se puede continuar sin componentes esenciales
            }
            
            // Inicializar el PlayerManager y crear un jugador
            playerManager = PlayerManager.getInstance(GAME_WIDTH, GAME_HEIGHT, FLOOR_Y);
            playerManager.initializePlayer(
                SessionManager.getInstance().getCurrentUserId(),
                SessionManager.getInstance().getCurrentUsername(),
                "", // Nombre completo 
                ""  // Email
            );
            
            player = playerManager.getPlayer();
            inputManager.setPlayerManager(playerManager); // Usar el método para configurar el PlayerManager
            setupComponentCallbacks();
            startGameLoop();
            inputManager.requestCanvasFocus();
            
        } catch (Exception e) {
            // Error crítico durante inicialización - continuar silenciosamente
        }
    }
    
    /**
     * Inicializa todos los componentes del juego.
     * Configura los managers de recursos, audio, input, render, niveles, etc.
     */
    private void initializeComponents() {
        try {
            resourceManager = new ResourceManager();
            resourceManager.loadAllResources();
            
            audioManager = new AudioManager();
            audioManager.playBackgroundMusic();
            
            if (gameCanvas == null) {
                gameCanvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
                gameCanvas.setFocusTraversable(true);
                gc = gameCanvas.getGraphicsContext2D();
            }
            
            if (gc == null && gameCanvas != null) {
                gc = gameCanvas.getGraphicsContext2D();
            }
            
            inputManager = new InputManager(gameCanvas);
            
            if (gameCanvas != null) {
                inputManager.setupKeyHandlers();
            }
            
            if (gc != null) {
                renderManager = new RenderManager(gc, GAME_WIDTH, GAME_HEIGHT, FLOOR_Y);
                renderManager.setImages(
                    resourceManager.getBackgroundImage(),
                    resourceManager.getHeartImage(),
                    resourceManager.getEmptyHeartImage()
                );
            }
            
            levelManager = new LevelManager();
            
            if (renderManager != null && levelManager != null) {
                renderManager.setFormulasInfo(
                    levelManager.getFormulasShort(),
                    levelManager.getFormulasDescriptions()
                );
            }
            
            scoreManager = new ScoreManager(5);
            appleManager = new AppleManager(GAME_WIDTH, GAME_HEIGHT, FLOOR_Y, 1500, 2.0, 5.0);
            potionManager = new PotionManager(GAME_WIDTH, GAME_HEIGHT, FLOOR_Y, 5000, 1.5, 4.0);
            
            if (gameCanvas != null && gameCanvas.getScene() != null) {
                visualEffectsManager = new VisualEffectsManager(gameCanvas.getScene());
            }
            
            rankingManager = RankingManager.getInstance();
            videoManager = VideoManager.getInstance();
            
            String currentUsername = Controlador.LoginController.getCurrentUsername();
            if (currentUsername != null && !currentUsername.isEmpty()) {
                int userId = Modelo.dao.UsuarioDAO.obtenerIdUsuario(currentUsername);
                rankingManager.setCurrentUser(userId, currentUsername);
            }
            
            if (levelManager != null && renderManager != null) {
                levelManager.setOnAllFormulasCompleted(() -> {
                    isPausedForCelebration = true;
                    renderManager.startCompletionCelebration();
                    
                    if (rankingManager != null && scoreManager != null) {
                        rankingManager.checkAndSaveCompletedGame(
                            levelManager.getUnlockedFormulas(), 
                            scoreManager.getScore()
                        );
                    }
                });
            }
            
        } catch (Exception e) {
            // Error durante inicialización de componentes - continuar silenciosamente
        }
    }
    
    /**
     * Configura los callbacks entre los diferentes componentes.
     * Establece las comunicaciones entre managers para el funcionamiento correcto del juego.
     */
    private void setupComponentCallbacks() {
        try {
            if (inputManager == null || renderManager == null || scoreManager == null || 
                levelManager == null || appleManager == null || audioManager == null) {
                return; // No se pueden configurar callbacks sin todos los componentes
            }
            inputManager.setCallbacks(
                this::handleEscapeKey,             // onPauseToggle 
                this::returnToMainMenu,            // onReturnToMenu
                this::toggleUI,                    // onUIToggle
                this::showSettings,                // onShowSettings
                this::showFormulaDetails,          // onFormulaDetails 
                this::showRanking,                 // onShowRanking
                this::continueAfterCompletion,     // onContinueAfterCompletion
                this::handleMouseClick             // onMouseClick 
            );
            
            // Configurar callbacks del ScoreManager
            scoreManager.setCallbacks(
                this::setGameOver,                 // onGameOver
                score -> levelManager.checkLevelProgress(score) // onScoreChange
            );
            
            // Configurar callbacks del LevelManager
            levelManager.setOnFormulaUnlocked(() -> {
                audioManager.playUnlockSound();
                // Actualizar acceso a videos cuando se desbloquee una fórmula
                videoManager.updateVideoAccess(levelManager.getUnlockedFormulas());
            });
            
            // Configurar callbacks del AppleManager
            appleManager.setCallbacks(
                scoreManager::addScore,            // onScoreChange
                this::loseLife                     // onLifeLost
            );
            
            // Configurar callbacks del PotionManager
            potionManager.setCallbacks(
                this::handlePotionScore,           // onScoreChange (con efecto de puntos dobles)
                this::handlePotionEffect,          // onPotionEffect (mostrar mensaje)
                scoreManager::gainLife             // onGainLife (ganar vida)
            );
            
        } catch (Exception e) {
            // Error al configurar callbacks - continuar silenciosamente
        }
    }
    
    // ===================================
    // BUCLE PRINCIPAL DEL JUEGO
    // ===================================
    
    /**
     * Inicia el bucle principal del juego
     */
    private void startGameLoop() {
        try {
            // Verificar componentes críticos antes de iniciar el bucle
            if (renderManager == null || player == null || appleManager == null || 
                potionManager == null || scoreManager == null || levelManager == null) {
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
                        // Error silencioso en el bucle del juego
                    }
                }
            };
            
            gameLoop.start();
            
        } catch (Exception e) {
            // Error silencioso al iniciar el bucle del juego
        }
    }
    
    /**
     * Actualiza el estado del juego
     */
    private void update() {
        try {
            // Verificar componentes críticos
            if (player == null || playerManager == null || appleManager == null || levelManager == null) {
                return;
            }
            
            // NO actualizar el juego si está pausado por celebración
            if (isPausedForCelebration) {
                // Solo actualizar el estado de la celebración para detectar cuándo termina
                if (renderManager != null) {
                    boolean wasShowingCelebration = renderManager.isShowingCompletionCelebration();
                    renderManager.updateCompletionCelebration();
                    
                    // Si la celebración terminó, reanudar el juego
                    if (wasShowingCelebration && !renderManager.isShowingCompletionCelebration()) {
                        isPausedForCelebration = false;
                    }
                }
                return; // No actualizar nada más durante la pausa de celebración
            }
            
            // Actualizar el jugador solo si no está pausado
            playerManager.update();
            
            // Actualizar manzanas y colisiones
            appleManager.update(player);            
            
            // Actualizar pociones y colisiones
            potionManager.update(player);
            
            // Actualizar efecto de desbloqueo de fórmulas
            levelManager.updateUnlockEffect();
            
        } catch (Exception e) {
            // Error silencioso al actualizar el juego
        }
    }
    
    /**
     * Renderiza el estado actual del juego
     */
    private void render() {
        try {
            // Verificar componentes críticos
            if (renderManager == null || scoreManager == null || levelManager == null) {
                return;
            }
            
            // Renderizar fondo
            renderManager.renderBackground();
            
            // Renderizar manzanas si están disponibles
            if (appleManager != null) {
                renderManager.renderApples(appleManager.getApples());
            }
            
            // Renderizar pociones si están disponibles
            if (potionManager != null) {
                renderManager.renderPotions(potionManager.getPotions());
            }
            
            // Renderizar jugador si está disponible
            if (player != null) {
                renderManager.renderPlayer(player);
            }
            
            // Determinar si debemos mostrar las interfaces de HUD o superposiciones modales
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
                    mostrarDesbloqueo,
                    player.hasSlownessEffect(),
                    player.hasPointsEffect(),
                    player.hasHealthEffect()
                );
            }
            
            // Mostrar el popup de desbloqueo de fórmula si corresponde
            if (mostrarDesbloqueo) {
                // Actualizar el índice de la fórmula en el RenderManager
                renderManager.setCurrentFormulaForUnlock(levelManager.getUnlockedFormulaIndex());
                // Mostrar la notificación 
                renderManager.renderUnlockPopup();
            }
            
            // Renderizar detalles de fórmula si están activos
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
                    levelManager.getLevel(),  // Pasar el nivel actual
                    levelManager.getUnlockedFormulas(),
                    levelManager.getMaxLevel()
                );
            }

            // Mostrar celebración de completación AL FINAL para que aparezca en primer plano
            if (mostrarCelebracion) {
                renderManager.renderCompletionCelebrationDuringGame(scoreManager.getScore());
            }
            
        } catch (Exception e) {
            // Error silencioso
        }
    }
    
    // ===================================
    // MÉTODOS DE CONTROL DEL JUEGO
    // ===================================
    
    /**
     * Alterna entre pausa y reanudación del juego
     */
    private void togglePause() {
        isPaused = !isPaused;
    }
    
    /**
     * Alterna entre interfaz minimalista y completa (desactivado)
     */
    private void toggleUI() {
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
        
        // Guardar progreso del usuario (siempre, no solo cuando completa todas las fórmulas)
        if (levelManager != null && scoreManager != null) {
            // Contar fórmulas desbloqueadas
            boolean[] unlockedFormulas = levelManager.getUnlockedFormulas();
            int formulasCount = 0;
            for (boolean unlocked : unlockedFormulas) {
                if (unlocked) formulasCount++;
            }
            
            // Guardar progreso en la tabla usuarios
            try {
                int currentUserId = Controlador.utils.SessionManager.getInstance().getCurrentUserId();
                if (currentUserId != -1) {
                    boolean progressSaved = Modelo.dao.UsuarioDAO.actualizarProgresoUsuario(
                        currentUserId, 
                        scoreManager.getScore(), 
                        formulasCount
                    );
                    
                    if (progressSaved) {
                        // Actualizar VideoManager con el nuevo progreso
                        if (videoManager != null) {
                            videoManager.updateUnlockedVideos(unlockedFormulas);
                        }
                    }
                }
            } catch (Exception e) {
                // Error silencioso
            }
            
            // Además, verificar si completó todas las fórmulas para el ranking global
            if (rankingManager != null) {
                rankingManager.checkAndSaveCompletedGame(
                    unlockedFormulas, 
                    scoreManager.getScore()
                );
            }
        }
    }
    
    // ===================================
    // MANEJO DE EVENTOS
    // ===================================
    
    /**
     * Maneja la pérdida de una vida
     */
    private void loseLife() {
        scoreManager.loseLife("Has perdido una vida");
    }
    
    /**
     * Maneja la puntuación de las pociones (ya con efectos aplicados en PotionManager)
     */
    private void handlePotionScore(int points) {
        // Los puntos ya vienen procesados desde PotionManager, no duplicar aquí
        scoreManager.addScore(points);
        // Puntos de poción añadidos
    }
    /**
     * Maneja los efectos visuales de las pociones
     */
    private void handlePotionEffect(String message) {
        // Los efectos visuales de las pociones se muestran en el HUD
        // a través de los indicadores en renderPotionEffects()
    }
    
    /**
     * Muestra los detalles de una fórmula específica
     * @param formulaIndex Índice de la fórmula
     */
    private void showFormulaDetails(int formulaIndex) {
        try {
            // Verificar componentes críticos
            if (levelManager == null || renderManager == null) {
                // Error silencioso
                return;
            }
            
            // Verificar si el índice está fuera de rango
            if (formulaIndex < 0 || formulaIndex >= levelManager.getMaxLevel()) {
                // Error silencioso
                return;
            }
            
            // Verificar si la fórmula está desbloqueada
            if (!levelManager.getUnlockedFormulas()[formulaIndex]) {
                return;
            }
            
            // Si ya se están mostrando los detalles de esta fórmula, ocultarlos y reanudar el juego
            if (renderManager.isShowingFormulaDetails() && formulaIndex == renderManager.getCurrentFormulaIndex()) {
                renderManager.hideFormulaDetails();
                // Reanudar el juego si estaba pausado por mostrar la fórmula
                if (isPaused) {
                    isPaused = false;
                }
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
                // Error silencioso
                return;
            }
            
            if (formulaIndex >= formulasShort.length || formulaIndex >= formulasDescriptions.length) {
                // Error silencioso
                return;
            }
            
            // Mostrar los detalles en pantalla mediante el RenderManager
            renderManager.setFormulaDetails(
                formulaIndex,
                formulasShort[formulaIndex],
                formulasDescriptions[formulaIndex]
            );
            
        } catch (Exception e) {
            // Error silencioso
        }
    }
    
    // ===================================
    // NAVEGACIÓN Y MENÚS
    // ===================================
    
    /**
     * Vuelve al menú principal
     */
    public void returnToMainMenu() {
        try {
            // Detener el bucle del juego
            if (gameLoop != null) {
                gameLoop.stop();
            }
            
            // Detener la música
            if (audioManager != null) {
                audioManager.stopBackgroundMusic();
            }
            
            // Usar el NavigationManager para volver al menú principal con el usuario configurado
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            NavigationManager.navigateToMainWithUser(stage);
            
        } catch (IOException e) {
            // Error silencioso
        } catch (Exception e) {
            // Error silencioso
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
            
        } catch (Exception e) {
            // Error silencioso
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
            // Error silencioso
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
            // Error silencioso
        }
    }
    
    /**
     * Muestra la pantalla de ranking
     */
    private void showRanking() {
        try {
            // Solo mostrar ranking si el juego está terminado y se han completado todas las fórmulas
            if (!gameOver) {
                return;
            }
            
            if (levelManager == null || !levelManager.areAllFormulasUnlocked()) {
                return;
            }
            
            Stage gameStage = (Stage) gameCanvas.getScene().getWindow();
            Controlador.dialogs.RankingDialog rankingDialog = new Controlador.dialogs.RankingDialog(gameStage);
            rankingDialog.showAndWait();
            
        } catch (Exception e) {
            // Error silencioso
        }
    }
    
    /**
     * Oculta la pantalla de ranking
     */
    private void hideRanking() {
        // La funcionalidad de ranking completo no está implementada en RenderManager
        // El ranking se muestra solo en la celebración de completitud
    }
    
    /**
     * Maneja la tecla ESCAPE con prioridades: game over > detalles de fórmula > pausa
     */
    private void handleEscapeKey() {
        try {
            // Prioridad 1: Si el juego terminó (Game Over), volver al mapa
            if (gameOver) {
                returnToMap();
                return;
            }
            
            // Prioridad 2: Si se están mostrando detalles de fórmula, cerrarlos
            if (renderManager != null && renderManager.isShowingFormulaDetails()) {
                renderManager.hideFormulaDetails();
                return;
            }
            
            // Prioridad 3: Alternar pausa
            togglePause();
        } catch (Exception e) {
            // Error silencioso
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
            }
        } catch (Exception e) {
            // Error silencioso
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
                            returnToMainMenu();
                            break;
                        case "settings":
                            showSettings();
                            break;
                    }
                }
            }
        } catch (Exception e) {
            // Error silencioso
        }
    }
    
    /**
     * Regresa al mapa cuando el juego ha terminado (Game Over)
     */
    private void returnToMap() {
        try {
            // Detener el bucle del juego
            if (gameLoop != null) {
                gameLoop.stop();
            }
            
            // Detener la música
            if (audioManager != null) {
                audioManager.stopBackgroundMusic();
            }
            
            // Obtener el stage actual y navegar al mapa con actualización del estado
            Stage currentStage = (Stage) gameCanvas.getScene().getWindow();
            NavigationManager.navigateToMapWithRefresh(currentStage);
            
        } catch (Exception e) {
            // Error silencioso
            
            // Fallback: intentar navegar al menú principal
            try {
                Stage currentStage = (Stage) gameCanvas.getScene().getWindow();
                NavigationManager.navigateToMainWithUser(currentStage);
            } catch (Exception fallbackError) {
                // Error silencioso
            }
        }
    }
    
    // ===================================
    // MÉTODOS AUXILIARES
    // ===================================
    
    /**
     * Cuenta el número de fórmulas desbloqueadas
     * @param unlockedFormulas Array de fórmulas desbloqueadas
     * @return Número de fórmulas desbloqueadas
     */
    private int countUnlockedFormulas(boolean[] unlockedFormulas) {
        int count = 0;
        if (unlockedFormulas != null) {
            for (boolean unlocked : unlockedFormulas) {
                if (unlocked) count++;
            }
        }
        return count;
    }
}