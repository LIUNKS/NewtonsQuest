package Controlador;

import Modelo.Apple;
import Modelo.Player;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameController {
    
    @FXML private Canvas gameCanvas;
    
    private GraphicsContext gc;
    private Player player;
    private AnimationTimer gameLoop;
    private MediaPlayer musicPlayer;
    private Image backgroundImage;
    
    // Dimensiones del juego
    private final int GAME_WIDTH = 900;
    private final int GAME_HEIGHT = 700;
    
    // Posición del suelo
    private final int FLOOR_Y = 600;
    
    // Estado del juego
    private boolean isPaused = false;
    private boolean gameOver = false;
    
    // Teclas presionadas
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    
    // Sistema de manzanas
    private List<Apple> apples = new ArrayList<>();
    private Random random = new Random();
    private long lastAppleSpawnTime = 0;
    private final long APPLE_SPAWN_INTERVAL = 1500; // Intervalo entre manzanas en milisegundos
    private final double MIN_APPLE_SPEED = 2.0;
    private final double MAX_APPLE_SPEED = 5.0;
    
    // Sistema de puntuación
    private int score = 0;
    private final int RED_APPLE_POINTS = 10;
    private final int GREEN_APPLE_POINTS = -5;
    private final int MISSED_APPLE_POINTS = -3;
    
    // Sistema de vidas
    private int lives = 5;
    private final int MAX_LIVES = 5;
    private Image heartImage;
    private Image emptyHeartImage;
    
    // Sistema de niveles y fórmulas
    private int level = 0;
    private final int MAX_LEVEL = 5;
    private final String[] FORMULAS = {
        "F = m × g → Peso o fuerza gravitacional",
        "v = g × t → Velocidad en caída libre",
        "d = ½ × g × t² → Distancia en caída libre",
        "U = m × g × h → Energía potencial",
        "K = ½ × m × v² → Energía cinética"
    };
    private final String[] FORMULAS_SHORT = {
        "F = m × g",
        "v = g × t",
        "d = ½ × g × t²",
        "U = m × g × h",
        "K = ½ × m × v²"
    };
    
    // Descripciones extendidas para cada fórmula
    private final String[] FORMULAS_DESCRIPTIONS = {
        "La fuerza de gravedad (peso) es igual a la masa del objeto multiplicada por la aceleración gravitacional. En la Tierra, g ≈ 9.8 m/s².",
        "La velocidad de un objeto en caída libre es igual a la aceleración gravitacional multiplicada por el tiempo transcurrido (ignorando la resistencia del aire).",
        "La distancia recorrida por un objeto en caída libre es igual a la mitad de la aceleración gravitacional multiplicada por el tiempo al cuadrado.",
        "La energía potencial gravitacional es igual a la masa del objeto multiplicada por la aceleración gravitacional y por la altura.",
        "La energía cinética es igual a la mitad de la masa del objeto multiplicada por la velocidad al cuadrado."
    };
    
    // Variables para efectos visuales de desbloqueo
    private boolean showingUnlockEffect = false;
    private int unlockedFormulaIndex = -1;
    private long unlockEffectStartTime = 0;
    private final long UNLOCK_EFFECT_DURATION = 3000; // Duración del efecto en milisegundos
    
    private final int[] LEVEL_THRESHOLDS = {100, 250, 450, 700, 1000};
    private boolean[] unlockedFormulas = new boolean[MAX_LEVEL];
    
      public void initialize() {
        try {
            System.out.println("========== INICIALIZANDO GAMECONTROLLER ==========");
            
            // Obtener el contexto gráfico del canvas
            gc = gameCanvas.getGraphicsContext2D();
            System.out.println("Contexto gráfico obtenido: " + (gc != null ? "OK" : "NULL"));
            
            // Hacer que el canvas pueda recibir el foco
            gameCanvas.setFocusTraversable(true);
            System.out.println("Canvas configurado como focusTraversable");
              // Cargar la imagen de fondo
            loadBackgroundImage();
            
            // Cargar imágenes de corazones
            loadHeartImages();
            
            // Iniciar la música de fondo
            playBackgroundMusic();
              // Inicializar el jugador en el centro de la pantalla
            player = new Player(GAME_WIDTH / 2 - 32, FLOOR_Y - 96);
            System.out.println("Jugador inicializado en (" + player.getX() + ", " + player.getY() + ")");
            
            // Inicializar el sistema de niveles y fórmulas
            for (int i = 0; i < MAX_LEVEL; i++) {
                unlockedFormulas[i] = false;
            }
            level = 0;
            
            // Configurar los eventos de teclado
            setupKeyHandlers();
            
            // Iniciar el bucle del juego
            startGameLoop();
            
            // Solicitar el foco nuevamente después de la inicialización
            gameCanvas.requestFocus();
            System.out.println("Foco solicitado para el canvas después de inicialización");
            
            System.out.println("GameController inicializado correctamente");            System.out.println("=================================================");
        } catch (Exception e) {
            System.err.println("Error al inicializar GameController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupKeyHandlers() {
        try {
            System.out.println("Configurando manejadores de teclas...");
            System.out.println("Canvas: " + (gameCanvas != null ? "OK" : "NULL"));
            
            // Agregar un listener directo al canvas para probar si recibe eventos
            gameCanvas.setOnKeyPressed(event -> {
                System.out.println("Tecla presionada directamente en el canvas: " + event.getCode());
            });
            
            // Obtener la escena
            gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
                System.out.println("Escena cambiada: " + (newScene != null ? "Nueva escena disponible" : "Escena nula"));
                
                if (newScene != null) {
                    // Solicitar el foco para el canvas cuando se carga la escena
                    gameCanvas.requestFocus();
                    System.out.println("Foco solicitado para el canvas");                    // Configurar los eventos de teclado para la escena
                    newScene.setOnKeyPressed(event -> {
                        KeyCode code = event.getCode();
                        System.out.println("Tecla presionada: " + code);
                        
                        if (code == KeyCode.LEFT || code == KeyCode.A) {
                            leftKeyPressed = true;
                            player.moveLeft(true);
                            System.out.println("Moviendo a la izquierda");
                        } else if (code == KeyCode.RIGHT || code == KeyCode.D) {                            rightKeyPressed = true;
                            player.moveRight(true);
                            System.out.println("Moviendo a la derecha");
                        } else if (code == KeyCode.ESCAPE) {
                            togglePause();
                        } else if (code == KeyCode.BACK_SPACE) {
                            returnToMainMenu();
                        } else if (code.isDigitKey()) {
                            // Obtener el número de la tecla presionada (1-5)
                            int formulaNumber = Integer.parseInt(code.getName()) - 1;
                            
                            // Verificar si esa fórmula está desbloqueada
                            if (formulaNumber >= 0 && formulaNumber < MAX_LEVEL && unlockedFormulas[formulaNumber]) {
                                showFormulaDetails(formulaNumber);
                            }
                        }
                    });
                    
                    newScene.setOnKeyReleased(event -> {
                        KeyCode code = event.getCode();
                        
                        if (code == KeyCode.LEFT || code == KeyCode.A) {
                            leftKeyPressed = false;
                            player.moveLeft(false);
                            
                            // Si la tecla derecha sigue presionada, seguir moviendo a la derecha
                            if (rightKeyPressed) {
                                player.moveRight(true);
                            }
                        } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                            rightKeyPressed = false;
                            player.moveRight(false);
                            
                            // Si la tecla izquierda sigue presionada, seguir moviendo a la izquierda
                            if (leftKeyPressed) {
                                player.moveLeft(true);
                            }
                        }
                    });
                    
                    // También añadir el foco al canvas cuando se hace clic en él
                    gameCanvas.setOnMouseClicked(event -> gameCanvas.requestFocus());
                    
                    System.out.println("Eventos de teclado configurados correctamente");
                }
            });
        } catch (Exception e) {
            System.err.println("Error al configurar los eventos de teclado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void startGameLoop() {
        try {
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    try {
                        if (!isPaused && !gameOver) {
                            update();
                            render();
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
            e.printStackTrace();        }
    }
    
    private void update() {
        try {
            // Actualizar el jugador
            player.update(FLOOR_Y);
            
            // Mantener al jugador dentro de los límites de la pantalla
            if (player.getX() < 0) {
                player.setPosition(0, player.getY());
            } else if (player.getX() > GAME_WIDTH - player.getWidth()) {
                player.setPosition(GAME_WIDTH - player.getWidth(), player.getY());
            }
            
            // Generar nuevas manzanas
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAppleSpawnTime > APPLE_SPAWN_INTERVAL) {
                spawnApple();
                lastAppleSpawnTime = currentTime;
            }
            
            // Actualizar manzanas y comprobar colisiones
            updateApples();
            
            // Verificar progreso de nivel
            checkLevelProgress();
            
            // Actualizar efecto de desbloqueo de fórmulas
            updateUnlockEffect();
        } catch (Exception e) {
            System.err.println("Error al actualizar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Actualiza el efecto visual de desbloqueo de fórmulas
     */
    private void updateUnlockEffect() {
        if (showingUnlockEffect) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - unlockEffectStartTime > UNLOCK_EFFECT_DURATION) {
                // Terminar el efecto
                showingUnlockEffect = false;
            }        }
    }
    
    private void render() {
        try {
            // Limpiar el canvas
            gc.clearRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            
            // Dibujar la imagen de fondo
            if (backgroundImage != null) {
                gc.drawImage(backgroundImage, 0, 0, GAME_WIDTH, GAME_HEIGHT);
            } else {
                // Dibujar el fondo (cielo) como respaldo si la imagen no se cargó
                gc.setFill(Color.SKYBLUE);
                gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            }
            
            // Dibujar el suelo
            gc.setFill(Color.SADDLEBROWN);
            gc.fillRect(0, FLOOR_Y, GAME_WIDTH, GAME_HEIGHT - FLOOR_Y);            // Dibujar las manzanas
            for (Apple apple : apples) {
                if (apple.isActive()) {
                    apple.render(gc);
                }
            }
            
            // Dibujar el jugador
            if (player != null) {
                player.render(gc);
            } else {
                System.err.println("ERROR: Player es null en GameController.render()");
            }
            
            // Dibujar barra de puntuación
            drawScoreBar();
            
            // Si el juego está pausado, mostrar mensaje
            if (isPaused) {
                gc.setFill(new Color(0, 0, 0, 0.5));
                gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
                
                gc.setFill(Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font(48));
                gc.fillText("PAUSA", GAME_WIDTH / 2 - 80, GAME_HEIGHT / 2);
                
                gc.setFont(javafx.scene.text.Font.font(24));
                gc.fillText("Presiona ESC para continuar", GAME_WIDTH / 2 - 150, GAME_HEIGHT / 2 + 50);
                gc.fillText("Presiona BACKSPACE para volver al menú", GAME_WIDTH / 2 - 200, GAME_HEIGHT / 2 + 90);
            }            // Si el juego terminó, mostrar mensaje
            if (gameOver) {
                gc.setFill(new Color(0, 0, 0, 0.85));
                gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
                
                gc.setFill(Color.RED);
                gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 48));
                gc.fillText("GAME OVER", GAME_WIDTH / 2 - 140, 120);
                
                // Mostrar puntuación final
                gc.setFill(Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font("Arial", 32));
                gc.fillText("Puntuación final: " + score, GAME_WIDTH / 2 - 150, 180);
                
                // Mostrar nivel alcanzado
                gc.setFill(Color.YELLOW);
                gc.fillText("Nivel alcanzado: " + level + " de " + MAX_LEVEL, GAME_WIDTH / 2 - 150, 220);
                
                // Mostrar fórmulas desbloqueadas
                gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 24));
                gc.setFill(Color.WHITE);
                gc.fillText("Fórmulas de física desbloqueadas:", 100, 280);
                
                // Dibujar un separador
                gc.setStroke(Color.GRAY);
                gc.setLineWidth(2);
                gc.strokeLine(100, 290, GAME_WIDTH - 100, 290);
                
                int formulasShown = 0;
                for (int i = 0; i < MAX_LEVEL; i++) {
                    if (unlockedFormulas[i]) {
                        formulasShown++;
                        
                        // Dibujar rectángulo de fondo para cada fórmula
                        gc.setFill(new Color(0.2, 0.2, 0.2, 0.7));
                        gc.fillRect(100, 300 + ((formulasShown - 1) * 75), GAME_WIDTH - 200, 65);
                        gc.setStroke(Color.YELLOW);
                        gc.setLineWidth(1);
                        gc.strokeRect(100, 300 + ((formulasShown - 1) * 75), GAME_WIDTH - 200, 65);
                        
                        // Fórmula
                        gc.setFill(Color.YELLOW);
                        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 22));
                        gc.fillText((i+1) + ". " + FORMULAS_SHORT[i], 120, 325 + ((formulasShown - 1) * 75));
                        
                        // Descripción
                        gc.setFill(Color.LIGHTGRAY);
                        gc.setFont(javafx.scene.text.Font.font("Arial", 16));
                        
                        // Mostrar una versión recortada de la descripción para que quepa
                        String desc = FORMULAS_DESCRIPTIONS[i];
                        if (desc.length() > 85) {
                            desc = desc.substring(0, 82) + "...";
                        }
                        gc.fillText(desc, 120, 350 + ((formulasShown - 1) * 75));
                    }
                }
                
                if (formulasShown == 0) {
                    gc.setFill(Color.GRAY);
                    gc.setFont(javafx.scene.text.Font.font("Arial", 20));
                    gc.fillText("Ninguna fórmula desbloqueada", GAME_WIDTH / 2 - 150, 330);
                    gc.fillText("¡Intenta conseguir al menos " + LEVEL_THRESHOLDS[0] + " puntos!", GAME_WIDTH / 2 - 200, 360);
                } else if (formulasShown < MAX_LEVEL) {
                    // Mostrar mensaje motivador
                    gc.setFill(Color.WHITE);
                    gc.setFont(javafx.scene.text.Font.font("Arial", 18));
                    gc.fillText("¡Sigue jugando para desbloquear más fórmulas de física!", 
                               GAME_WIDTH / 2 - 240, 300 + (formulasShown * 75) + 30);
                } else {
                    // Mensaje de felicitación por desbloquear todas las fórmulas
                    gc.setFill(Color.GOLD);
                    gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 24));
                    gc.fillText("¡FELICIDADES! ¡Has desbloqueado todas las fórmulas de física!", 
                               GAME_WIDTH / 2 - 300, 300 + (formulasShown * 75) + 30);
                }
                
                // Instrucciones para volver a jugar
                gc.setFill(Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font(24));
                gc.fillText("Presiona BACKSPACE para volver al menú", GAME_WIDTH / 2 - 200, GAME_HEIGHT - 50);
            }            // Mostrar instrucciones
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(14));
            gc.fillText("Controles: Flechas o WASD para mover, ESC para pausar", 20, 30);
            gc.fillText("Atrapa manzanas rojas (+10 pts) y evita las verdes (-5 pts)", 20, 50);
            gc.fillText("¡Cuidado! Perderás una vida si:", 20, 70);
            gc.fillText("- Dejas caer una manzana roja", 40, 90);
            gc.fillText("- Atrapas una manzana verde", 40, 110);
            
            // Dibujar sistema de vidas
            drawLives();
            
            // Dibujar nivel y fórmula actual
            drawLevelAndFormula();
            
            // Dibujar panel de fórmulas
            drawFormulasPanel();
            
        } catch (Exception e) {
            System.err.println("Error al renderizar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void togglePause() {
        isPaused = !isPaused;
        System.out.println("Juego " + (isPaused ? "pausado" : "reanudado"));
    }
    
    // Método para volver al menú principal
    public void returnToMainMenu() {
        try {
            // Detener el bucle del juego
            if (gameLoop != null) {
                gameLoop.stop();
            }
            
            // Detener la música
            if (musicPlayer != null) {
                musicPlayer.stop();
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
    
    /**
     * Carga la imagen de fondo del juego
     */
    private void loadBackgroundImage() {
        try {
            String backgroundPath = "src/recursos/imagenes/fondo_juego.jpg";
            File backgroundFile = new File(backgroundPath);
            
            if (backgroundFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                backgroundImage = new Image(new FileInputStream(backgroundFile));
                System.out.println("Imagen de fondo cargada correctamente");
            } else {
                // Estamos en producción, usar getResource
                backgroundImage = new Image(getClass().getResourceAsStream("/recursos/imagenes/fondo_juego.jpg"));
                System.out.println("Imagen de fondo cargada desde recursos");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de fondo: " + e.getMessage());
            e.printStackTrace();
            backgroundImage = null;        }
    }
    
    /**
     * Reproduce la música de fondo del juego
     */
    private void playBackgroundMusic() {
        try {
            String musicPath = "src/recursos/musica/musica_juego.mp3";
            File musicFile = new File(musicPath);
            
            Media media;
            if (musicFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                media = new Media(musicFile.toURI().toString());
                System.out.println("Música cargada desde archivo: " + musicPath);
            } else {
                // Estamos en producción, usar getResource
                String resourcePath = getClass().getResource("/recursos/musica/musica_juego.mp3").toString();
                media = new Media(resourcePath);
                System.out.println("Música cargada desde recursos");
            }
            
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Reproducir en bucle
            musicPlayer.setVolume(0.5); // Volumen al 50%
            musicPlayer.play();
            
            System.out.println("Música de fondo iniciada");
        } catch (Exception e) {
            System.err.println("Error al reproducir la música: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Genera una nueva manzana en una posición aleatoria en la parte superior de la pantalla
     */
    private void spawnApple() {
        try {
            // Posición X aleatoria (dejando margen en los bordes)
            double x = 50 + random.nextDouble() * (GAME_WIDTH - 100);
            
            // Posición Y en la parte superior
            double y = -50;
            
            // Velocidad aleatoria
            double speed = MIN_APPLE_SPEED + random.nextDouble() * (MAX_APPLE_SPEED - MIN_APPLE_SPEED);
            
            // 70% de probabilidad de que sea manzana roja, 30% de que sea verde
            boolean isRed = random.nextDouble() < 0.7;
            
            // Crear la manzana y añadirla a la lista
            Apple apple = new Apple(x, y, isRed, speed);
            apples.add(apple);
            
            System.out.println("Generada manzana " + (isRed ? "roja" : "verde") + " en (" + x + ", " + y + ") con velocidad " + speed);
        } catch (Exception e) {
            System.err.println("Error al generar manzana: " + e.getMessage());
            e.printStackTrace();
        }
    }
      /**
     * Actualiza todas las manzanas, comprobando colisiones y si han llegado al suelo
     */
    private void updateApples() {
        try {
            // Lista para guardar las manzanas que hay que eliminar
            List<Apple> applesToRemove = new ArrayList<>();
            
            // Actualizar cada manzana
            for (Apple apple : apples) {
                if (!apple.isActive()) {
                    applesToRemove.add(apple);
                    continue;
                }
                
                // Actualizar posición
                apple.update();
                
                // Comprobar si ha colisionado con el jugador
                if (apple.checkCollision(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
                    // Actualizar puntuación según el tipo de manzana
                    if (apple.isRed()) {
                        score += RED_APPLE_POINTS;
                        System.out.println("¡Manzana roja recogida! Puntos: +" + RED_APPLE_POINTS);
                        // Verificar progreso de nivel
                        checkLevelProgress();
                    } else {
                        score += GREEN_APPLE_POINTS;
                        System.out.println("¡Manzana verde recogida! Puntos: " + GREEN_APPLE_POINTS);
                        // Activar el sprite de manzana verde
                        player.setGreenAppleEffect();
                        // Perder una vida por atrapar manzana verde
                        loseLife("Has atrapado una manzana verde");
                    }
                    
                    // Desactivar la manzana para que se elimine
                    apple.deactivate();
                    applesToRemove.add(apple);
                }
                // Comprobar si ha llegado al suelo
                else if (apple.hasReachedFloor(FLOOR_Y)) {
                    // Si era roja y cayó al suelo, penalizar
                    if (apple.isRed()) {
                        score += MISSED_APPLE_POINTS;
                        System.out.println("Manzana roja perdida. Puntos: " + MISSED_APPLE_POINTS);
                        // Perder una vida por dejar caer una manzana roja
                        loseLife("Has dejado caer una manzana roja");
                    }
                    
                    // Desactivar la manzana para que se elimine
                    apple.deactivate();
                    applesToRemove.add(apple);
                }
            }
            
            // Eliminar las manzanas que ya no están activas
            apples.removeAll(applesToRemove);
              } catch (Exception e) {
            System.err.println("Error al actualizar manzanas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Dibuja la barra de puntuación en la parte superior de la pantalla
     */
    private void drawScoreBar() {
        try {
            // Dibujar un fondo semitransparente para la barra de puntuación
            gc.setFill(new Color(0, 0, 0, 0.5));
            gc.fillRect(GAME_WIDTH - 250, 10, 230, 70);
            
            // Dibujar el texto de la puntuación
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(24));
            gc.fillText("PUNTUACIÓN: " + score, GAME_WIDTH - 230, 40);
            
            // Texto explicativo
            gc.setFont(javafx.scene.text.Font.font(12));
            gc.fillText("Manzana roja: +" + RED_APPLE_POINTS + " pts", GAME_WIDTH - 230, 60);
            gc.fillText("Manzana verde: " + GREEN_APPLE_POINTS + " pts", GAME_WIDTH - 230, 75);
            
        } catch (Exception e) {
            System.err.println("Error al dibujar la barra de puntuación: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Dibuja los corazones que representan las vidas del jugador
     */
    private void drawLives() {
        try {
            if (heartImage == null || emptyHeartImage == null) {
                return;
            }
            
            // Tamaño de cada corazón
            int heartSize = 30;
            // Espacio entre corazones
            int heartSpacing = 10;
            // Posición inicial X
            int startX = 20;
            // Posición Y
            int heartY = 100;
            
            // Dibujar fondo para los corazones
            gc.setFill(new Color(0, 0, 0, 0.5));
            gc.fillRect(startX - 10, heartY - 10, (MAX_LIVES * (heartSize + heartSpacing)) + 10, heartSize + 20);
            
            // Dibujar texto "VIDAS"
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(16));
            gc.fillText("VIDAS:", startX, heartY - 15);
            
            // Dibujar cada corazón
            for (int i = 0; i < MAX_LIVES; i++) {
                int x = startX + (i * (heartSize + heartSpacing));
                
                if (i < lives) {
                    // Dibujar corazón lleno
                    gc.drawImage(heartImage, x, heartY, heartSize, heartSize);
                } else {
                    // Dibujar corazón vacío
                    gc.drawImage(emptyHeartImage, x, heartY, heartSize, heartSize);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al dibujar vidas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga las imágenes de corazones para el sistema de vidas
     */
    private void loadHeartImages() {
        try {
            // Intentar cargar las imágenes de corazones
            String heartPath = "src/recursos/sprites/corazon_lleno.png";
            String emptyHeartPath = "src/recursos/sprites/corazon_vacio.png";
            
            File heartFile = new File(heartPath);
            File emptyHeartFile = new File(emptyHeartPath);
            
            if (heartFile.exists() && emptyHeartFile.exists()) {
                // Cargar desde archivos
                heartImage = new Image(new FileInputStream(heartFile));
                emptyHeartImage = new Image(new FileInputStream(emptyHeartFile));
                System.out.println("Imágenes de corazones cargadas correctamente");
            } else {
                // Crear imágenes predeterminadas en caso de que no existan los archivos
                // Corazón lleno (rojo)
                heartImage = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAwklEQVR42mNgGAWDFDAwEAv+E4WB6kkCxFnw/z9RGJNF/0nCJFvwnygMNXAUjFpAClDdAnAMgxNTCkiyADkpiY4jKAApAIrDxQmJw8nRLPhPKIHBxeHkqAXYYkKyRwSRYsR6TGwgLYvIPgkSKptJLkpItoC8ooQUD0jyALGAJA8QC4j2AKmAaA+QCoj2AKmAaA+QCoj2AKmAaA+QCoj2AKkAJOc/kZhoC5ATNrUt+A8WIw6Ta8F/sC7iMNkW/EcOjILBAwAZMkztv9sLSwAAAABJRU5ErkJggg==");
                // Corazón vacío (gris)
                emptyHeartImage = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAwElEQVR42mNgGAWDFDAwEAv+E4WB6kkCxFnw/z9RGJNF/0nCJFvwnygMNXAUjFpAClDdAnAMgxNTCkiyADkpiY4jKAApAIrDxQmJw8nRLPhPKIHBxeHkqAXYYkKyRwSRYsR6TGwgLYvIPgkSKptJLkpItoC8ooQUD0jyALGAJA8QC4j2AKmAaA+QCoj2AKmAaA+QCoj2AKmAaA+QCoj2AKkAJOc/kZhoC5ATNrUt+A8WIw6Ta8F/sC7iMNkW/EcOjILBAwAZMkztv9sLSwAAAABJRU5ErkJggg==");
                System.out.println("Utilizando imágenes de corazones predeterminadas");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imágenes de corazones: " + e.getMessage());
            e.printStackTrace();
            
            // Crear imágenes predeterminadas en caso de error
            heartImage = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAwklEQVR42mNgGAWDFDAwEAv+E4WB6kkCxFnw/z9RGJNF/0nCJFvwnygMNXAUjFpAClDdAnAMgxNTCkiyADkpiY4jKAApAIrDxQmJw8nRLPhPKIHBxeHkqAXYYkKyRwSRYsR6TGwgLYvIPgkSKptJLkpItoC8ooQUD0jyALGAJA8QC4j2AKmAaA+QCoj2AKmAaA+QCoj2AKmAaA+QCoj2AKkAJOc/kZhoC5ATNrUt+A8WIw6Ta8F/sC7iMNkW/EcOjILBAwAZMkztv9sLSwAAAABJRU5ErkJggg==");
            emptyHeartImage = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAwElEQVR42mNgGAWDFDAwEAv+E4WB6kkCxFnw/z9RGJNF/0nCJFvwnygMNXAUjFpAClDdAnAMgxNTCkiyADkpiY4jKAApAIrDxQmJw8nRLPhPKIHBxeHkqAXYYkKyRwSRYsR6TGwgLYvIPgkSKptJLkpItoC8ooQUD0jyALGAJA8QC4j2AKmAaA+QCoj2AKmAaA+QCoj2AKmAaA+QCoj2AKkAJOc/kZhoC5ATNrUt+A8WIw6Ta8F/sC7iMNkW/EcOjILBAwAZMkztv9sLSwAAAABJRU5ErkJggg==");
        }
    }
      /**
     * Reduce una vida del jugador y verifica si el juego ha terminado
     * @param reason Motivo por el que perdió la vida
     */    private void loseLife(String reason) {
        if (lives > 0) {
            lives--;
            System.out.println("¡Has perdido una vida! Motivo: " + reason + ". Vidas restantes: " + lives);
            
            // Si se quedó sin vidas, terminar el juego
            if (lives <= 0) {
                gameOver = true;
                // Activar el sprite de game over
                player.setDead(true);
                // Centrar al jugador en la pantalla para la escena de game over
                player.setPosition(GAME_WIDTH / 2 - 48, player.getY());
                System.out.println("GAME OVER: Te has quedado sin vidas");
            }
        }
    }
      /**
     * Dibuja el nivel actual y la fórmula correspondiente en la parte superior derecha de la pantalla
     */
    private void drawLevelAndFormula() {
        try {
            // Posición y tamaño del panel del nivel
            int levelPanelX = GAME_WIDTH - 300;
            int levelPanelY = 10;
            int levelPanelWidth = 290;
            int levelPanelHeight = 100;
            
            // Dibujar fondo semitransparente para el panel del nivel
            gc.setFill(new Color(0, 0, 0, 0.7));
            gc.fillRect(levelPanelX, levelPanelY, levelPanelWidth, levelPanelHeight);
            
            // Dibujar borde del panel
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRect(levelPanelX, levelPanelY, levelPanelWidth, levelPanelHeight);
            
            // Dibujar el nivel actual
            gc.setFill(Color.YELLOW);
            gc.setFont(javafx.scene.text.Font.font(24));
            gc.fillText("Nivel: " + (level + 1), levelPanelX + 10, levelPanelY + 30);
            
            // Dibujar la fórmula correspondiente al nivel
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(18));
            String formula = FORMULAS_SHORT[level];
            gc.fillText("Fórmula: " + formula, levelPanelX + 10, levelPanelY + 60);
            
            // Si el nivel está bloqueado, mostrar mensaje de bloqueo
            if (!unlockedFormulas[level]) {
                gc.setFill(Color.RED);
                gc.setFont(javafx.scene.text.Font.font(16));
                gc.fillText("¡Desbloqueado al alcanzar " + LEVEL_THRESHOLDS[level] + " pts!", levelPanelX + 10, levelPanelY + 90);
            } else {
                // Si hay fórmulas desbloqueadas, mostrar instrucción para ver más detalles
                gc.setFill(Color.LIGHTGRAY);
                gc.setFont(javafx.scene.text.Font.font(14));
                gc.fillText("Presiona 1-" + (level + 1) + " para ver detalles de las fórmulas", levelPanelX + 10, levelPanelY + 90);
            }
        } catch (Exception e) {
            System.err.println("Error al dibujar el nivel y fórmula: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica si el jugador ha alcanzado el puntaje necesario para desbloquear una nueva fórmula
     */
    private void checkLevelProgress() {
        // Si ya estamos en el nivel máximo, no hay nada que hacer
        if (level >= MAX_LEVEL) {
            return;
        }
        
        // Verificar si el puntaje actual es suficiente para desbloquear el siguiente nivel
        if (score >= LEVEL_THRESHOLDS[level]) {
            unlockFormula(level);
        }
    }
      /**
     * Desbloquea una fórmula y aumenta el nivel del jugador
     * @param formulaIndex Índice de la fórmula a desbloquear
     */
    private void unlockFormula(int formulaIndex) {
        if (formulaIndex < 0 || formulaIndex >= MAX_LEVEL || unlockedFormulas[formulaIndex]) {
            return;
        }
        
        unlockedFormulas[formulaIndex] = true;
        level++;
        
        // Iniciar efecto visual de desbloqueo
        showingUnlockEffect = true;
        unlockedFormulaIndex = formulaIndex;
        unlockEffectStartTime = System.currentTimeMillis();
        
        // Mostrar mensaje de felicitación
        System.out.println("¡FELICIDADES! Has desbloqueado la fórmula: " + FORMULAS[formulaIndex]);
        System.out.println("Has alcanzado el nivel " + level);
        
        // Reproducir sonido de desbloqueo
        playUnlockSound();
        
        // Si ha desbloqueado todas las fórmulas, ha completado el juego
        if (level >= MAX_LEVEL) {
            System.out.println("¡FELICIDADES! ¡Has completado el juego y aprendido todas las fórmulas!");
        }
    }
      /**
     * Reproduce un sonido cuando se desbloquea una fórmula
     */
    private void playUnlockSound() {
        try {
            String soundPath = "src/recursos/sonidos/unlock.mp3";
            File soundFile = new File(soundPath);
            
            // Verificar si existe el archivo de sonido
            if (!soundFile.exists()) {
                System.out.println("Archivo de sonido de desbloqueo no encontrado: " + soundPath);
                return;
            }
            
            Media media = new Media(soundFile.toURI().toString());
            MediaPlayer soundPlayer = new MediaPlayer(media);
            soundPlayer.setVolume(0.7);
            soundPlayer.play();
            
            // Configurar para liberar recursos cuando termine de reproducirse
            soundPlayer.setOnEndOfMedia(() -> {
                soundPlayer.dispose();
            });
            
            System.out.println("Reproduciendo sonido de desbloqueo");
        } catch (Exception e) {
            System.err.println("Error al reproducir sonido de desbloqueo: " + e.getMessage());
            // No interrumpir el juego si hay un error con el sonido
        }
    }
      /**
     * Dibuja el panel de fórmulas en la pantalla
     */
    private void drawFormulasPanel() {
        try {
            // Dibujar un fondo semitransparente para el panel de fórmulas
            gc.setFill(new Color(0, 0, 0, 0.7));
            gc.fillRect(GAME_WIDTH - 350, 150, 330, 250); // Aumentado el alto para mostrar más información
            
            // Borde del panel
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(2);
            gc.strokeRect(GAME_WIDTH - 350, 150, 330, 250);
            
            // Título del panel
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 18));
            gc.fillText("FÓRMULAS DE FÍSICA", GAME_WIDTH - 330, 175);
            
            // Dibujar cada fórmula (o placeholders para las que no están desbloqueadas)
            gc.setFont(javafx.scene.text.Font.font("Monospace", 16));
            for (int i = 0; i < MAX_LEVEL; i++) {
                if (unlockedFormulas[i]) {
                    // Fórmula desbloqueada - mostrarla completa
                    if (showingUnlockEffect && i == unlockedFormulaIndex) {
                        // Efecto pulsante para la fórmula recién desbloqueada
                        double pulse = Math.sin((System.currentTimeMillis() - unlockEffectStartTime) / 100.0) * 0.5 + 0.5;
                        gc.setFill(new Color(1.0, 1.0, 0.0, 0.5 + pulse * 0.5)); // Amarillo pulsante
                        
                        // Rectángulo de fondo para destacar la fórmula
                        gc.fillRect(GAME_WIDTH - 345, 190 + (i * 45), 320, 40);
                        gc.setFill(Color.WHITE);
                    } else {
                        gc.setFill(Color.YELLOW);
                    }
                    
                    // Dibujar la fórmula
                    gc.fillText((i+1) + ". " + FORMULAS[i], GAME_WIDTH - 340, 205 + (i * 45));
                    
                    // Si está desbloqueada y se selecciona, mostrar la descripción
                    if (i == level - 1) {
                        gc.setFill(Color.LIGHTGRAY);
                        gc.setFont(javafx.scene.text.Font.font("Arial", 12));
                        
                        // Dividir la descripción en múltiples líneas si es necesario
                        String description = FORMULAS_DESCRIPTIONS[i];
                        List<String> lines = splitDescription(description, 45); // 45 caracteres por línea aproximadamente
                        
                        for (int j = 0; j < lines.size(); j++) {
                            gc.fillText(lines.get(j), GAME_WIDTH - 340, 222 + (i * 45) + (j * 15));
                        }
                    }
                } else if (i == level) {
                    // Próxima fórmula a desbloquear - mostrar pistas
                    gc.setFill(Color.GRAY);
                    gc.fillText((i+1) + ". " + FORMULAS_SHORT[i] + " (Puntos: " + LEVEL_THRESHOLDS[i] + ")", 
                               GAME_WIDTH - 340, 205 + (i * 45));
                } else {
                    // Fórmula bloqueada - mostrar solo "???"
                    gc.setFill(Color.GRAY);
                    gc.fillText((i+1) + ". ??? (Nivel " + (i+1) + ")", GAME_WIDTH - 340, 205 + (i * 45));
                }
            }
            
            // Mostrar progreso actual
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(14));
            
            // Si no hemos completado todos los niveles, mostrar el progreso hacia el siguiente nivel
            if (level < MAX_LEVEL) {
                int nextLevelPoints = LEVEL_THRESHOLDS[level];
                int percentage = (int)((score * 100.0) / nextLevelPoints);
                percentage = Math.min(percentage, 100);
                
                gc.fillText("Progreso al nivel " + (level+1) + ": " + percentage + "%", 
                           GAME_WIDTH - 330, 375);
                
                // Dibujar barra de progreso
                gc.setFill(Color.DARKGRAY);
                gc.fillRect(GAME_WIDTH - 330, 385, 300, 10);
                
                gc.setFill(Color.GREEN);
                gc.fillRect(GAME_WIDTH - 330, 385, 300 * percentage / 100, 10);
            } else {
                // Si ya completó todos los niveles
                gc.setFill(Color.GOLD);
                gc.fillText("¡JUEGO COMPLETADO! ¡Has aprendido todas las fórmulas!", 
                           GAME_WIDTH - 330, 375);
            }
            
            // Dibujar popup de nueva fórmula desbloqueada
            if (showingUnlockEffect) {
                drawUnlockPopup();
            }
            
        } catch (Exception e) {
            System.err.println("Error al dibujar panel de fórmulas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Divide una descripción larga en múltiples líneas
     * @param text Texto a dividir
     * @param maxCharsPerLine Máximo número de caracteres por línea
     * @return Lista de líneas
     */
    private List<String> splitDescription(String text, int maxCharsPerLine) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 <= maxCharsPerLine) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines;
    }
    
    /**
     * Dibuja un popup cuando se desbloquea una nueva fórmula
     */
    private void drawUnlockPopup() {
        try {
            if (unlockedFormulaIndex < 0 || unlockedFormulaIndex >= MAX_LEVEL) {
                return;
            }
            
            // Calcular tiempo transcurrido y opacidad (para efecto de fade)
            long timeElapsed = System.currentTimeMillis() - unlockEffectStartTime;
            double opacity = 1.0;
            
            // Hacer que el popup se desvanezca al final
            if (timeElapsed > UNLOCK_EFFECT_DURATION * 0.7) {
                opacity = 1.0 - ((timeElapsed - (UNLOCK_EFFECT_DURATION * 0.7)) / (UNLOCK_EFFECT_DURATION * 0.3));
                opacity = Math.max(0.0, Math.min(1.0, opacity));
            }
            
            // Dimensiones y posición del popup
            int popupWidth = 500;
            int popupHeight = 200;
            int popupX = (GAME_WIDTH - popupWidth) / 2;
            int popupY = (GAME_HEIGHT - popupHeight) / 2;
            
            // Dibujar fondo del popup
            gc.setFill(new Color(0.0, 0.0, 0.0, 0.85 * opacity));
            gc.fillRect(popupX - 5, popupY - 5, popupWidth + 10, popupHeight + 10);
            
            // Borde dorado
            gc.setStroke(new Color(1.0, 0.84, 0.0, opacity));
            gc.setLineWidth(5);
            gc.strokeRect(popupX, popupY, popupWidth, popupHeight);
            
            // Título del popup
            gc.setFill(new Color(1.0, 1.0, 0.0, opacity));
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 28));
            gc.fillText("¡NUEVA FÓRMULA DESBLOQUEADA!", popupX + 25, popupY + 40);
            
            // Dibujar la fórmula
            gc.setFill(new Color(1.0, 1.0, 1.0, opacity));
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 24));
            gc.fillText(FORMULAS[unlockedFormulaIndex], popupX + 25, popupY + 85);
            
            // Dibujar la descripción
            gc.setFill(new Color(0.9, 0.9, 0.9, opacity));
            gc.setFont(javafx.scene.text.Font.font("Arial", 16));
            
            // Dividir la descripción en múltiples líneas
            String description = FORMULAS_DESCRIPTIONS[unlockedFormulaIndex];
            List<String> lines = splitDescription(description, 60);
            
            for (int i = 0; i < lines.size(); i++) {
                gc.fillText(lines.get(i), popupX + 25, popupY + 120 + (i * 20));
            }
            
        } catch (Exception e) {
            System.err.println("Error al dibujar popup de desbloqueo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra una ventana de información detallada sobre una fórmula específica
     * @param formulaIndex Índice de la fórmula a mostrar
     */
    private void showFormulaDetails(int formulaIndex) {
        try {
            // Pausar el juego mientras se muestra la información
            boolean wasPaused = isPaused;
            isPaused = true;
            
            // Información extendida sobre cada fórmula
            final String[] EXTENDED_INFO = {
                "La ley de gravedad de Newton establece que la fuerza de gravedad entre dos objetos es directamente proporcional al producto de sus masas e inversamente proporcional al cuadrado de la distancia entre ellos. Para un objeto cerca de la superficie terrestre, esta fuerza se simplifica a F = m × g, donde g ≈ 9.8 m/s² en la Tierra.\n\nEsta fórmula explica por qué objetos con diferentes masas caen a la misma velocidad en ausencia de resistencia del aire, ya que la aceleración gravitacional g es constante independientemente de la masa.",
                
                "Cuando un objeto cae libremente bajo la influencia de la gravedad (ignorando la resistencia del aire), su velocidad aumenta constantemente con el tiempo. La tasa de este aumento es determinada por la aceleración gravitacional g. Después de t segundos, un objeto inicialmente en reposo alcanzará una velocidad de v = g × t.\n\nEsta ecuación nos permite calcular la velocidad de un objeto en cualquier momento durante su caída.",
                
                "Esta fórmula permite calcular la distancia que recorre un objeto en caída libre después de un tiempo t. La distancia es proporcional al cuadrado del tiempo, lo que significa que un objeto recorre distancias cada vez mayores en intervalos de tiempo iguales.\n\nPor ejemplo, en los primeros 1, 2 y 3 segundos, un objeto caerá aproximadamente 4.9 m, 19.6 m y 44.1 m respectivamente, demostrando cómo la distancia aumenta cuadráticamente.",
                
                "La energía potencial gravitacional es la energía almacenada en un objeto debido a su posición en un campo gravitacional. Depende de la masa del objeto, la aceleración gravitacional y la altura sobre un nivel de referencia.\n\nEsta energía potencial se convierte en energía cinética a medida que el objeto cae, siguiendo el principio de conservación de la energía.",
                
                "La energía cinética es la energía que posee un objeto debido a su movimiento. Es proporcional a la masa del objeto y al cuadrado de su velocidad.\n\nA medida que un objeto cae, su energía potencial gravitacional se convierte en energía cinética, haciendo que se mueva cada vez más rápido. En ausencia de resistencia del aire, la suma de las energías potencial y cinética permanece constante durante la caída."
            };
            
            // Aplicaciones prácticas de cada fórmula
            final String[] PRACTICAL_APPLICATIONS = {
                "• Cálculo del peso de objetos en diferentes planetas\n• Diseño de estructuras y edificios\n• Desarrollo de sistemas de elevación y grúas\n• Planificación de misiones espaciales",
                
                "• Predicción del tiempo de caída de objetos\n• Diseño de paracaídas y sistemas de frenado\n• Análisis de impactos y colisiones\n• Programación de simuladores de física",
                
                "• Cálculo de la altura de estructuras por tiempo de caída\n• Diseño de experimentos de caída libre\n• Estudio de rebotes en diferentes superficies\n• Análisis forense de caídas",
                
                "• Diseño de presas hidroeléctricas\n• Cálculo de almacenamiento de energía\n• Análisis de montañas rusas y parques de atracciones\n• Estudio de sistemas de resortes y péndulos",
                
                "• Diseño de sistemas de protección contra impactos\n• Análisis de eficiencia en vehículos\n• Cálculo de fuerzas en deportes y juegos\n• Desarrollo de armas de proyectiles"
            };
            
            // Dibujar el fondo semi-transparente para toda la pantalla
            gc.setFill(new Color(0, 0, 0, 0.9));
            gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            
            // Dibujar el panel de información
            int panelX = 50;
            int panelY = 50;
            int panelWidth = GAME_WIDTH - 100;
            int panelHeight = GAME_HEIGHT - 100;
            
            // Fondo del panel con degradado
            gc.setFill(new Color(0.1, 0.1, 0.2, 0.95));
            gc.fillRect(panelX, panelY, panelWidth, panelHeight);
            
            // Borde del panel
            gc.setStroke(Color.GOLD);
            gc.setLineWidth(3);
            gc.strokeRect(panelX, panelY, panelWidth, panelHeight);
            
            // Título - Nombre de la fórmula
            gc.setFill(Color.GOLD);
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 32));
            gc.fillText(FORMULAS_SHORT[formulaIndex], panelX + 30, panelY + 50);
            
            // Descripción completa
            gc.setFont(javafx.scene.text.Font.font("Arial", 18));
            gc.setFill(Color.WHITE);
            gc.fillText(FORMULAS[formulaIndex], panelX + 30, panelY + 90);
            
            // Separador
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(1);
            gc.strokeLine(panelX + 30, panelY + 110, panelX + panelWidth - 30, panelY + 110);
            
            // Información extendida
            gc.setFill(Color.LIGHTGRAY);
            gc.setFont(javafx.scene.text.Font.font("Arial", 16));
            
            // Dividir la información extendida en líneas
            List<String> infoLines = splitDescription(EXTENDED_INFO[formulaIndex], 85);
            for (int i = 0; i < infoLines.size(); i++) {
                gc.fillText(infoLines.get(i), panelX + 30, panelY + 140 + (i * 22));
            }
            
            // Título para aplicaciones prácticas
            int applicationsY = panelY + 140 + (infoLines.size() * 22) + 20;
            gc.setFill(Color.GOLD);
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 20));
            gc.fillText("Aplicaciones prácticas:", panelX + 30, applicationsY);
            
            // Listar aplicaciones prácticas
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 16));
            
            // Dividir las aplicaciones prácticas en líneas
            List<String> appLines = splitDescription(PRACTICAL_APPLICATIONS[formulaIndex], 85);
            for (int i = 0; i < appLines.size(); i++) {
                gc.fillText(appLines.get(i), panelX + 30, applicationsY + 30 + (i * 22));
            }
            
            // Instrucciones para cerrar
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 18));
            gc.fillText("Presiona ESCAPE para volver al juego", panelX + (panelWidth / 2) - 150, panelY + panelHeight - 30);
            
            // Esperar a que el usuario presione ESC para cerrar
            gameCanvas.getScene().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    // Restaurar los controles del juego
                    setupKeyHandlers();
                    
                    // Restaurar el estado de pausa anterior
                    isPaused = wasPaused;
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error al mostrar detalles de fórmula: " + e.getMessage());
            e.printStackTrace();
            
            // Asegurarse de restaurar los controles del juego en caso de error
            setupKeyHandlers();
        }
    }
}