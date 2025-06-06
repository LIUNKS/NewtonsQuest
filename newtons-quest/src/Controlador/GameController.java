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
            
            // Configurar los eventos de teclado
            setupKeyHandlers();
            
            // Iniciar el bucle del juego
            startGameLoop();
            
            // Solicitar el foco nuevamente después de la inicialización
            gameCanvas.requestFocus();
            System.out.println("Foco solicitado para el canvas después de inicialización");
            
            System.out.println("GameController inicializado correctamente");
            System.out.println("=================================================");
        } catch (Exception e) {
            System.err.println("Error al inicializar GameController: " + e.getMessage());
            e.printStackTrace();
        }
    }private void setupKeyHandlers() {
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
                        } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                            rightKeyPressed = true;
                            player.moveRight(true);
                            System.out.println("Moviendo a la derecha");
                        } else if (code == KeyCode.ESCAPE) {
                            togglePause();
                        } else if (code == KeyCode.BACK_SPACE) {
                            returnToMainMenu();
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
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            System.err.println("Error al actualizar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }    private void render() {
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
            gc.fillRect(0, FLOOR_Y, GAME_WIDTH, GAME_HEIGHT - FLOOR_Y);
            
            // Dibujar las manzanas
            for (Apple apple : apples) {
                if (apple.isActive()) {
                    apple.render(gc);
                }
            }
            
            // Dibujar el jugador
            player.render(gc);
              // Dibujar barra de puntuación
            drawScoreBar();
            
            // Dibujar vidas
            drawLives();
            
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
            }
              // Si el juego terminó, mostrar mensaje
            if (gameOver) {
                gc.setFill(new Color(0, 0, 0, 0.7));
                gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
                
                gc.setFill(Color.RED);
                gc.setFont(javafx.scene.text.Font.font(48));
                gc.fillText("GAME OVER", GAME_WIDTH / 2 - 120, GAME_HEIGHT / 2 - 50);
                
                // Mostrar puntuación final
                gc.setFill(Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font(32));
                gc.fillText("Puntuación final: " + score, GAME_WIDTH / 2 - 150, GAME_HEIGHT / 2 + 20);
                
                // Instrucciones para volver a jugar
                gc.setFont(javafx.scene.text.Font.font(24));
                gc.fillText("Presiona BACKSPACE para volver al menú", GAME_WIDTH / 2 - 200, GAME_HEIGHT / 2 + 80);
            }              // Mostrar instrucciones
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(14));
            gc.fillText("Controles: Flechas o WASD para mover, ESC para pausar", 20, 30);
            gc.fillText("Atrapa manzanas rojas (+10 pts) y evita las verdes (-5 pts)", 20, 50);
            gc.fillText("¡Cuidado! Perderás una vida si:", 20, 70);
            gc.fillText("- Dejas caer una manzana roja", 40, 90);
            gc.fillText("- Atrapas una manzana verde", 40, 110);
            
            // Dibujar sistema de vidas
            drawLives();
            
        } catch (Exception e) {
            System.err.println("Error al renderizar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void togglePause() {
        isPaused = !isPaused;
        System.out.println("Juego " + (isPaused ? "pausado" : "reanudado"));
    }    // Método para volver al menú principal
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
            backgroundImage = null;
        }
    }    /**
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
                if (apple.checkCollision(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {                    // Actualizar puntuación según el tipo de manzana
                    if (apple.isRed()) {
                        score += RED_APPLE_POINTS;
                        System.out.println("¡Manzana roja recogida! Puntos: +" + RED_APPLE_POINTS);
                    } else {
                        score += GREEN_APPLE_POINTS;
                        System.out.println("¡Manzana verde recogida! Puntos: " + GREEN_APPLE_POINTS);
                        // Perder una vida por atrapar manzana verde
                        loseLife("Has atrapado una manzana verde");
                    }
                    
                    // Desactivar la manzana para que se elimine
                    apple.deactivate();
                    applesToRemove.add(apple);
                }
                // Comprobar si ha llegado al suelo
                else if (apple.hasReachedFloor(FLOOR_Y)) {                    // Si era roja y cayó al suelo, penalizar
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
     */
    private void loseLife(String reason) {
        if (lives > 0) {
            lives--;
            System.out.println("¡Has perdido una vida! Motivo: " + reason + ". Vidas restantes: " + lives);
            
            // Si se quedó sin vidas, terminar el juego
            if (lives <= 0) {
                gameOver = true;
                System.out.println("GAME OVER: Te has quedado sin vidas");
            }
        }
    }
}