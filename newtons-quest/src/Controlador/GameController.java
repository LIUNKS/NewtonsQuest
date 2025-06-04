package Controlador;

import Modelo.Player;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    private boolean rightKeyPressed = false;      public void initialize() {
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
        } catch (Exception e) {
            System.err.println("Error al actualizar el juego: " + e.getMessage());
            e.printStackTrace();
        }
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
            gc.fillRect(0, FLOOR_Y, GAME_WIDTH, GAME_HEIGHT - FLOOR_Y);
            
            // Dibujar el jugador
            player.render(gc);
            
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
                gc.setFill(new Color(0, 0, 0, 0.5));
                gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
                
                gc.setFill(Color.RED);
                gc.setFont(javafx.scene.text.Font.font(48));
                gc.fillText("GAME OVER", GAME_WIDTH / 2 - 120, GAME_HEIGHT / 2);
            }
              // Mostrar instrucciones
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(14));
            gc.fillText("Controles: Flechas o WASD para mover, ESC para pausar", 20, 30);
            gc.fillText("BACKSPACE para volver al menú principal", 20, 50);
            
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
}