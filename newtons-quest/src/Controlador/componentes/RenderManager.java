package Controlador.componentes;

import Modelo.Apple;
import Modelo.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Clase encargada de todas las operaciones de renderizado del juego.
 * Se separa la lógica de visualización de la lógica de juego.
 */
public class RenderManager {
    
    private final GraphicsContext gc;
    private final int GAME_WIDTH;
    private final int GAME_HEIGHT;
    private final int FLOOR_Y;
    
    // Referencias a imágenes
    private Image backgroundImage;
    private Image heartImage;
    private Image emptyHeartImage;
      // Estado para mostrar detalles de fórmula
    private boolean showingFormulaDetails = false;
    private int currentFormulaIndex = -1;
    private String currentFormulaDetails = "";
    private String currentFormulaName = "";
    
    // Información de fórmulas para el desbloqueo
    private String[] FORMULAS_SHORT;
    private String[] FORMULAS_DESCRIPTIONS;
    
    /**
     * Constructor del RenderManager
     * @param gc Contexto gráfico donde se dibujará
     * @param gameWidth Ancho del área de juego
     * @param gameHeight Alto del área de juego
     * @param floorY Posición Y del suelo
     */
    public RenderManager(GraphicsContext gc, int gameWidth, int gameHeight, int floorY) {
        this.gc = gc;
        this.GAME_WIDTH = gameWidth;
        this.GAME_HEIGHT = gameHeight;
        this.FLOOR_Y = floorY;
        
        System.out.println("RenderManager inicializado");
    }
      /**
     * Establece las imágenes utilizadas para el renderizado
     * @param backgroundImage Imagen de fondo
     * @param heartImage Imagen de corazón lleno
     * @param emptyHeartImage Imagen de corazón vacío
     */
    public void setImages(Image backgroundImage, Image heartImage, Image emptyHeartImage) {
        this.backgroundImage = backgroundImage;
        this.heartImage = heartImage;
        this.emptyHeartImage = emptyHeartImage;
    }
    
    /**
     * Establece la información de las fórmulas para mostrar en desbloqueos y detalles
     * @param formulasShort Versiones cortas de las fórmulas
     * @param formulasDescriptions Descripciones detalladas de las fórmulas
     */
    public void setFormulasInfo(String[] formulasShort, String[] formulasDescriptions) {
        this.FORMULAS_SHORT = formulasShort;
        this.FORMULAS_DESCRIPTIONS = formulasDescriptions;
    }
    
    /**
     * Renderiza el fondo del juego
     */
    public void renderBackground() {
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
    }
    
    /**
     * Renderiza todas las manzanas activas
     * @param apples Lista de manzanas a renderizar
     */
    public void renderApples(java.util.List<Apple> apples) {
        for (Apple apple : apples) {
            if (apple.isActive()) {
                apple.render(gc);
            }
        }
    }
    
    /**
     * Renderiza al jugador
     * @param player Objeto jugador a renderizar
     */
    public void renderPlayer(Player player) {
        if (player != null) {
            player.render(gc);
        } else {
            System.err.println("ERROR: Player es null en RenderManager.renderPlayer()");
        }
    }
    
    /**
     * Dibuja solo los elementos esenciales de la interfaz (modo minimalista)
     * @param score Puntuación actual
     * @param lives Vidas actuales
     * @param maxLives Máximo de vidas
     * @param level Nivel actual
     * @param showingUnlockEffect Si se está mostrando el efecto de desbloqueo
     */
    public void renderMinimalUI(int score, int lives, int maxLives, int level, boolean showingUnlockEffect) {
        try {            // Crear un fondo semi-transparente para el puntaje
            gc.setFill(new Color(0, 0, 0, 0.5));
            gc.fillRoundRect(GAME_WIDTH - 135, 15, 120, 45, 10, 10);
            
            // Añadir borde para resaltar el área de puntaje
            gc.setStroke(new Color(1, 0.8, 0, 0.8)); // Borde dorado
            gc.setLineWidth(2);
            gc.strokeRoundRect(GAME_WIDTH - 135, 15, 120, 45, 10, 10);
            
            // Mostrar etiqueta y puntuación con estilo mejorado
            gc.setFill(new Color(1, 0.8, 0, 1)); // Texto dorado para la etiqueta
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            gc.fillText("PUNTOS", GAME_WIDTH - 115, 35);
            
            // Mostrar valor del puntaje con mayor tamaño
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            gc.fillText(score + "", GAME_WIDTH - 90, 55);
            
            // Dibujar vidas como corazones en la esquina superior izquierda con fondo mejorado
            if (heartImage != null && emptyHeartImage != null) {
                int heartSize = 24; // Tamaño aumentado
                int heartSpacing = 4; // Espacio ajustado
                  // Calcular el ancho total que ocuparían todos los corazones
                int totalWidth = (maxLives * (heartSize + heartSpacing));
                
                // Si el ancho es mayor que un tercio de la pantalla, redimensionar
                if (totalWidth > GAME_WIDTH / 3) {
                    heartSize = Math.max(15, (GAME_WIDTH / 3) / maxLives - heartSpacing);
                    totalWidth = (maxLives * (heartSize + heartSpacing));
                }
                
                // Crear fondo para los corazones con más margen
                gc.setFill(new Color(0, 0, 0, 0.5));
                gc.fillRoundRect(15, 15, totalWidth + 25, heartSize + 25, 10, 10);
                
                // Añadir borde al área de vidas
                gc.setStroke(new Color(0.8, 0, 0, 0.8)); // Borde rojo para las vidas
                gc.setLineWidth(2);
                gc.strokeRoundRect(15, 15, totalWidth + 25, heartSize + 25, 10, 10);
                
                // Añadir etiqueta para las vidas
                gc.setFill(new Color(1, 0.3, 0.3, 1)); // Texto rojizo
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                gc.fillText("VIDAS", 25, 32);
                
                // Asegurarse de que los corazones no se salgan de la pantalla
                int startX = 25;
                  for (int i = 0; i < maxLives; i++) {
                    int x = startX + (i * (heartSize + heartSpacing));
                    
                    if (i < lives) {
                        // Corazón lleno con efecto de brillo
                        gc.drawImage(heartImage, x, 37, heartSize, heartSize);
                    } else {
                        // Corazón vacío
                        gc.drawImage(emptyHeartImage, x, 37, heartSize, heartSize);
                    }
                }
            }
              // Si se acaba de desbloquear una fórmula, mostrar el efecto
            if (showingUnlockEffect) {
                renderUnlockPopup();
            }            // Indicador de nivel mejorado en la esquina inferior izquierda
            gc.setFill(new Color(0, 0, 0, 0.6));
            gc.fillRoundRect(15, GAME_HEIGHT - 45, 110, 30, 8, 8);
            
            gc.setStroke(new Color(0.2, 0.6, 1, 0.8)); // Borde azul
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(15, GAME_HEIGHT - 45, 110, 30, 8, 8);
            
            gc.setFill(new Color(0.4, 0.8, 1, 1)); // Azul claro
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.fillText("Nivel " + (level + 1), 35, GAME_HEIGHT - 25);
            
            // Ya no mostramos el indicador de modo minimalista
            
        } catch (Exception e) {
            System.err.println("Error al dibujar UI minimalista: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Dibuja todos los elementos de la interfaz (modo completo)
     * @param score Puntuación actual
     * @param lives Vidas actuales
     * @param maxLives Máximo de vidas
     * @param level Nivel actual
     * @param isPaused Si el juego está pausado
     * @param gameOver Si el juego ha terminado
     * @param unlockedFormulas Estado de las fórmulas desbloqueadas
     * @param formulas Textos de las fórmulas
     * @param formulasShort Versiones cortas de las fórmulas
     * @param formulasDescriptions Descripciones de las fórmulas
     * @param maxLevel Nivel máximo
     * @param levelThresholds Umbrales de puntuación para subir de nivel
     */
    public void renderCompleteUI(int score, int lives, int maxLives, int level, boolean isPaused, 
                                boolean gameOver, boolean[] unlockedFormulas, String[] formulas, 
                                String[] formulasShort, String[] formulasDescriptions, 
                                int maxLevel, int[] levelThresholds) {
        try {
            // Dibujar barra de puntuación
            renderScoreBar(score);
            
            // Si el juego está pausado, mostrar mensaje
            if (isPaused) {
                renderPauseScreen();
            }
            
            // Si el juego terminó, mostrar mensaje
            if (gameOver) {
                renderGameOverScreen(score, level, maxLevel, unlockedFormulas, formulasShort, formulasDescriptions);
            } else {
                // Mostrar instrucciones en modo no minimalista
                renderInstructions();
                
                // Dibujar sistema de vidas
                renderLives(lives, maxLives);
                
                // Dibujar nivel y fórmula actual
                renderLevelAndFormula(level, unlockedFormulas, formulas);
                
                // Dibujar panel de fórmulas
                renderFormulasPanel(unlockedFormulas, formulasShort, level);
            }
        } catch (Exception e) {
            System.err.println("Error al dibujar UI completa: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Dibuja la barra de puntuación en la parte superior de la pantalla
     * @param score Puntuación actual
     */
    private void renderScoreBar(int score) {
        try {
            // Dibujar un fondo semitransparente para la barra de puntuación
            gc.setFill(new Color(0, 0, 0, 0.7)); 
            gc.fillRect(GAME_WIDTH - 250, 70, 240, 80);
            
            // Borde para mejorar visibilidad
            gc.setStroke(new Color(1, 1, 1, 0.4));
            gc.setLineWidth(2);
            gc.strokeRect(GAME_WIDTH - 250, 70, 240, 80);
            
            // Dibujar el texto de la puntuación
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            gc.fillText("PUNTUACIÓN: " + score, GAME_WIDTH - 230, 105);
            
            // Texto explicativo con mejor espaciado
            gc.setFont(Font.font("Arial", 14));
            gc.fillText("Manzana roja: +10 pts", GAME_WIDTH - 230, 130);
            gc.fillText("Manzana verde: -5 pts", GAME_WIDTH - 230, 145);
            
        } catch (Exception e) {
            System.err.println("Error al dibujar la barra de puntuación: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Dibuja la pantalla de pausa
     */    public void renderPauseScreen() {
        // Fondo oscuro más opaco para ocultar completamente elementos de la interfaz
        gc.setFill(new Color(0, 0, 0, 0.85));
        gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        
        // Panel de pausa semi-transparente
        int panelWidth = 500;
        int panelHeight = 200;
        int panelX = (GAME_WIDTH - panelWidth) / 2;
        int panelY = (GAME_HEIGHT - panelHeight) / 2;
        
        // Fondo del panel
        gc.setFill(new Color(0.15, 0.15, 0.2, 0.8));
        gc.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);
        
        // Borde del panel
        gc.setStroke(new Color(0.7, 0.7, 0.7, 0.6));
        gc.setLineWidth(2);
        gc.strokeRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);
        
        // Título
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        gc.fillText("PAUSA", GAME_WIDTH / 2 - 80, GAME_HEIGHT / 2 - 20);
        
        // Instrucciones
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        gc.fillText("Presiona ESC para continuar", GAME_WIDTH / 2 - 150, GAME_HEIGHT / 2 + 30);
        gc.fillText("Presiona BACKSPACE para volver al menú", GAME_WIDTH / 2 - 200, GAME_HEIGHT / 2 + 70);
    }    /**
     * Renderiza la pantalla de Game Over con el resumen del juego
     * @param score Puntuación final
     * @param unlockedFormulas Array de fórmulas desbloqueadas
     * @param maxLevel Nivel máximo del juego
     */
    public void renderGameOverScreen(int score, boolean[] unlockedFormulas, int maxLevel) {
        renderGameOverScreen(score, 0, maxLevel, unlockedFormulas, FORMULAS_SHORT, FORMULAS_DESCRIPTIONS);
    }
    
    /**
     * Dibuja la pantalla de fin de juego
     */
    private void renderGameOverScreen(int score, int level, int maxLevel, boolean[] unlockedFormulas,
                                     String[] formulasShort, String[] formulasDescriptions) {
        gc.setFill(new Color(0, 0, 0, 0.85));
        gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        
        gc.setFill(Color.RED);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        gc.fillText("GAME OVER", GAME_WIDTH / 2 - 140, 120);
        
        // Mostrar puntuación final
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 32));
        gc.fillText("Puntuación final: " + score, GAME_WIDTH / 2 - 150, 180);
        
        // Mostrar nivel alcanzado
        gc.setFill(Color.YELLOW);
        gc.fillText("Nivel alcanzado: " + level + " de " + maxLevel, GAME_WIDTH / 2 - 150, 220);
        
        // Mostrar fórmulas desbloqueadas
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.setFill(Color.WHITE);
        gc.fillText("Fórmulas de física desbloqueadas:", 100, 280);
        
        // Dibujar un separador
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);
        gc.strokeLine(100, 290, GAME_WIDTH - 100, 290);
        
        int formulasShown = 0;
        for (int i = 0; i < maxLevel; i++) {
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
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
                gc.fillText((i+1) + ". " + formulasShort[i], 120, 325 + ((formulasShown - 1) * 75));
                
                // Descripción
                gc.setFill(Color.LIGHTGRAY);
                gc.setFont(Font.font("Arial", 16));
                
                // Mostrar una versión recortada de la descripción para que quepa
                String desc = formulasDescriptions[i];
                if (desc.length() > 85) {
                    desc = desc.substring(0, 82) + "...";
                }
                gc.fillText(desc, 120, 350 + ((formulasShown - 1) * 75));
            }
        }
        
        if (formulasShown == 0) {
            gc.setFill(Color.GRAY);
            gc.setFont(Font.font("Arial", 20));
            gc.fillText("Ninguna fórmula desbloqueada", GAME_WIDTH / 2 - 150, 330);
            gc.fillText("¡Intenta conseguir al menos 100 puntos!", GAME_WIDTH / 2 - 200, 360);
        } else if (formulasShown < maxLevel) {
            // Mostrar mensaje motivador
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 18));
            gc.fillText("¡Sigue jugando para desbloquear más fórmulas de física!", 
                       GAME_WIDTH / 2 - 240, 300 + (formulasShown * 75) + 30);
        } else {
            // Mensaje de felicitación por desbloquear todas las fórmulas
            gc.setFill(Color.GOLD);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            gc.fillText("¡FELICIDADES! ¡Has desbloqueado todas las fórmulas de física!", 
                       GAME_WIDTH / 2 - 300, 300 + (formulasShown * 75) + 30);
        }
        
        // Instrucciones para volver a jugar
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(24));
        gc.fillText("Presiona BACKSPACE para volver al menú", GAME_WIDTH / 2 - 200, GAME_HEIGHT - 50);
    }
    
    /**
     * Dibuja las instrucciones del juego
     */
    private void renderInstructions() {
        gc.setFill(new Color(0, 0, 0, 0.7)); // Fondo para instrucciones
        gc.fillRect(15, 15, 400, 135);
        gc.setStroke(new Color(1, 1, 1, 0.4));
        gc.setLineWidth(2);
        gc.strokeRect(15, 15, 400, 135);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(14));
        gc.fillText("Controles: Flechas o WASD para mover, ESC para pausar", 20, 35);
        gc.fillText("Atrapa manzanas rojas (+10 pts) y evita las verdes (-5 pts)", 20, 55);
        gc.fillText("¡Cuidado! Perderás una vida si:", 20, 75);
        gc.fillText("- Dejas caer una manzana roja", 40, 95);
        gc.fillText("- Atrapas una manzana verde", 40, 115);
        gc.fillText("Presiona M para alternar entre interfaz minimalista/completa", 20, 135);
    }
    
    /**
     * Dibuja las vidas del jugador como corazones
     */
    private void renderLives(int lives, int maxLives) {
        try {
            if (heartImage == null || emptyHeartImage == null) {
                return;
            }
            // Tamaño de cada corazón
            int heartSize = 32;
            // Espacio entre corazones
            int heartSpacing = 10;
            
            // Calcular el ancho total que ocuparían todos los corazones
            int totalWidth = (maxLives * (heartSize + heartSpacing)) + 20; // +20 para margen
            
            // Si el ancho es mayor que un tercio de la pantalla, redimensionar
            if (totalWidth > GAME_WIDTH / 3) {
                heartSize = Math.max(18, (GAME_WIDTH / 3 - 20) / maxLives - heartSpacing);
                totalWidth = (maxLives * (heartSize + heartSpacing)) + 20;
            }
            
            // Posición inicial X
            int startX = 20;
            // Posición Y
            int heartY = 180;
            
            // Dibujar fondo para los corazones
            gc.setFill(new Color(0, 0, 0, 0.7));
            int backgroundWidth = Math.min((maxLives * (heartSize + heartSpacing)) + 10, GAME_WIDTH - 40);
            gc.fillRect(startX - 10, heartY - 10, backgroundWidth, heartSize + 20);
            
            // Borde para mejorar visibilidad
            gc.setStroke(new Color(1, 1, 1, 0.4));
            gc.setLineWidth(2);
            gc.strokeRect(startX - 10, heartY - 10, backgroundWidth, heartSize + 20);
            
            // Dibujar texto "VIDAS"
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            gc.fillText("VIDAS:", startX, heartY - 15);
            
            // Dibujar cada corazón
            for (int i = 0; i < maxLives; i++) {
                int x = startX + (i * (heartSize + heartSpacing));
                
                if (i < lives) {
                    // Corazón lleno
                    gc.drawImage(heartImage, x, heartY, heartSize, heartSize);
                } else {
                    // Corazón vacío
                    gc.drawImage(emptyHeartImage, x, heartY, heartSize, heartSize);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al dibujar vidas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Dibuja el nivel actual y la fórmula correspondiente
     */
    private void renderLevelAndFormula(int level, boolean[] unlockedFormulas, String[] formulas) {
        try {
            // Crear un panel para mostrar el nivel actual y la fórmula correspondiente
            gc.setFill(new Color(0, 0, 0, 0.7));
            gc.fillRect(20, 260, 400, 80);
            gc.setStroke(new Color(1, 1, 1, 0.4));
            gc.setLineWidth(2);
            gc.strokeRect(20, 260, 400, 80);
            
            // Dibujar nivel actual
            gc.setFill(Color.YELLOW);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.fillText("NIVEL " + (level + 1) + ":", 30, 285);
            
            // Dibujar fórmula correspondiente al nivel
            if (unlockedFormulas[level]) {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", 18));
                gc.fillText(formulas[level], 30, 315);
            } else {
                gc.setFill(Color.GRAY);
                gc.setFont(Font.font("Arial", 18));
                gc.fillText("(Fórmula bloqueada - alcanza " + (level + 1) * 200 + " puntos para desbloquear)", 30, 315);
            }
        } catch (Exception e) {
            System.err.println("Error al dibujar nivel y fórmula: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Dibuja el panel de fórmulas desbloqueadas
     */
    private void renderFormulasPanel(boolean[] unlockedFormulas, String[] formulasShort, int level) {
        try {
            // Crear un panel para mostrar las fórmulas desbloqueadas
            gc.setFill(new Color(0, 0, 0, 0.7));
            gc.fillRect(20, 350, 400, 220);
            gc.setStroke(new Color(1, 1, 1, 0.4));
            gc.setLineWidth(2);
            gc.strokeRect(20, 350, 400, 220);
            
            // Título del panel
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            gc.fillText("FÓRMULAS DESBLOQUEADAS:", 30, 375);
            
            // Dibujar un separador
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(1);
            gc.strokeLine(30, 385, 410, 385);
            
            // Comprobar si hay fórmulas desbloqueadas
            boolean hayFormulasDesbloqueadas = false;
            
            for (int i = 0; i < unlockedFormulas.length; i++) {
                if (unlockedFormulas[i]) {
                    hayFormulasDesbloqueadas = true;
                    
                    // Destacar la fórmula del nivel actual
                    if (i == level) {
                        gc.setFill(new Color(0.3, 0.3, 0.7, 0.5));
                        gc.fillRect(30, 395 + (i * 30), 380, 25);
                    }
                    
                    // Dibujar número de fórmula
                    gc.setFill(Color.YELLOW);
                    gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                    gc.fillText((i+1) + ".", 35, 415 + (i * 30));
                    
                    // Dibujar fórmula
                    gc.setFill(Color.WHITE);
                    gc.setFont(Font.font("Arial", 16));
                    gc.fillText(formulasShort[i], 60, 415 + (i * 30));
                    
                    // Instrucción para ver detalle
                    gc.setFill(Color.LIGHTGRAY);
                    gc.setFont(Font.font("Arial", 12));
                    gc.fillText("(Presiona " + (i+1) + " para detalles)", 250, 415 + (i * 30));
                    
                } else {
                    // Mostrar fórmula bloqueada
                    gc.setFill(Color.GRAY);
                    gc.setFont(Font.font("Arial", 16));
                    gc.fillText((i+1) + ". ???", 35, 415 + (i * 30));
                }
            }
            
            if (!hayFormulasDesbloqueadas) {
                gc.setFill(Color.GRAY);
                gc.setFont(Font.font("Arial", 16));
                gc.fillText("Ninguna fórmula desbloqueada aún", 70, 420);
                gc.fillText("¡Sigue jugando para desbloquearlas!", 70, 450);
            }
            
        } catch (Exception e) {
            System.err.println("Error al dibujar panel de fórmulas: " + e.getMessage());
            e.printStackTrace();
        }
    }    /**
     * Renderiza el popup de desbloqueo de fórmula en formato compacto y no intrusivo
     */
    public void renderUnlockPopup() {
        try {
            // Obtener datos del índice actual
            if (currentFormulaIndex < 0 || currentFormulaIndex >= FORMULAS_SHORT.length) {
                return;
            }
            
            // Crear una notificación compacta en la esquina superior derecha
            int notifWidth = 400;
            int notifHeight = 70;
            int notifX = GAME_WIDTH - notifWidth - 20; // 20px de margen derecho
            int notifY = 20; // 20px de margen superior
            
            // Calcular un efecto de pulsación para el borde basado en el tiempo
            double pulseIntensity = 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 200.0);
            
            // Fondo de la notificación con transparencia
            gc.setFill(new Color(0.15, 0.15, 0.25, 0.85));
            gc.fillRoundRect(notifX, notifY, notifWidth, notifHeight, 10, 10);
            
            // Borde con efecto brillante y pulsante
            gc.setStroke(new Color(1.0, 0.8, 0.0, 0.5 + 0.3 * pulseIntensity));
            gc.setLineWidth(2 + pulseIntensity);
            gc.strokeRoundRect(notifX, notifY, notifWidth, notifHeight, 10, 10);
            
            // Título de la notificación
            gc.setFill(Color.GOLD);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.fillText("¡NUEVA FÓRMULA DESBLOQUEADA!", notifX + 20, notifY + 25);
            
            // Mostrar la fórmula
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            gc.fillText(FORMULAS_SHORT[currentFormulaIndex], notifX + 20, notifY + 50);
            
            // Indicación para ver detalles
            gc.setFill(Color.LIGHTGRAY);
            gc.setFont(Font.font("Arial", 14));
            gc.fillText("Presiona " + (currentFormulaIndex + 1) + " para ver detalles", notifX + 200, notifY + 50);
            
        } catch (Exception e) {
            System.err.println("Error al renderizar notificación de desbloqueo: " + e.getMessage());
            e.printStackTrace();
        }
    }/**
     * Dibuja una ventana modal con los detalles de la fórmula actual
     */
    public void renderFormulaDetailsModal() {
        try {
            // Dibujar un fondo opaco para ocultar completamente el juego
            gc.setFill(new Color(0, 0, 0, 0.9));
            gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            
            // Dibujar el panel modal
            int modalWidth = 700;
            int modalHeight = 400;
            int modalX = (GAME_WIDTH - modalWidth) / 2;
            int modalY = (GAME_HEIGHT - modalHeight) / 2;
            
            // Fondo del modal con gradiente
            gc.setFill(new Color(0.12, 0.12, 0.22, 0.95));
            gc.fillRoundRect(modalX, modalY, modalWidth, modalHeight, 20, 20);
            
            // Borde del modal
            gc.setStroke(Color.GOLD);
            gc.setLineWidth(3);
            gc.strokeRoundRect(modalX, modalY, modalWidth, modalHeight, 20, 20);
            
            // Título con estilo
            gc.setFill(Color.GOLD);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            gc.fillText("Fórmula: " + currentFormulaName, modalX + 20, modalY + 45);
            
            // Separador
            gc.setStroke(new Color(0.8, 0.8, 0.8, 0.5));
            gc.setLineWidth(2);
            gc.strokeLine(modalX + 20, modalY + 60, modalX + modalWidth - 20, modalY + 60);
            
            // Descripción de la fórmula
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 20));
            
            // Dividir la descripción en líneas para que encaje en el modal
            String[] lines = wrapText(currentFormulaDetails, modalWidth - 60, Font.font("Arial", 20));
            
            for (int i = 0; i < lines.length; i++) {
                gc.fillText(lines[i], modalX + 30, modalY + 100 + i * 30);
            }
            
            // Dibujar un icono o ilustración relacionada con la fórmula
            // Aquí podríamos añadir iconos específicos para cada fórmula
            int iconSize = 60;
            gc.setFill(new Color(0.9, 0.9, 0.1, 0.3));
            gc.fillOval(modalX + modalWidth - 90, modalY + 25, iconSize, iconSize);
            
            // Sección de aplicación práctica
            gc.setFill(new Color(0.2, 0.6, 1.0, 0.8));
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.fillText("Aplicación en el juego:", modalX + 30, modalY + 280);
            
            gc.setFill(Color.LIGHTGRAY);
            gc.setFont(Font.font("Arial", 18));
            gc.fillText("Esta fórmula explica por qué las manzanas caen y se mueven como lo hacen.", modalX + 30, modalY + 310);
            
            // Instrucción para cerrar
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.fillText("Presiona [ESC] o el número " + (currentFormulaIndex + 1) + " nuevamente para cerrar y continuar jugando", 
                       modalX + 120, modalY + modalHeight - 25);
            
            // Efecto de brillo en los bordes
            gc.setStroke(new Color(1, 1, 0.5, 0.2));
            gc.setLineWidth(6);
            gc.strokeRoundRect(modalX - 3, modalY - 3, modalWidth + 6, modalHeight + 6, 22, 22);
            
        } catch (Exception e) {
            System.err.println("Error al renderizar los detalles de la fórmula: " + e.getMessage());
            e.printStackTrace();
        }
    }
      /**
     * Divide un texto en líneas para que encaje en un ancho determinado
     * @param text Texto a dividir
     * @param maxWidth Ancho máximo
     * @param font Fuente utilizada
     * @return Array de líneas
     */
    private String[] wrapText(String text, double maxWidth, Font font) {
        // Dividir el texto en palabras
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<>();
        
        // Aproximación simple: un promedio de caracteres por línea
        int charsPerLine = (int)(maxWidth / 8.5); // Aproximadamente 8.5 píxeles por carácter
        
        for (String word : words) {
            // Si añadir la palabra excede el ancho máximo aproximado, añadir la línea actual y empezar una nueva
            if (line.length() + word.length() + 1 > charsPerLine) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                // Si la línea está vacía, no añadir espacio
                if (line.length() == 0) {
                    line.append(word);
                } else {
                    line.append(" ").append(word);
                }
            }
        }
        
        // Añadir la última línea
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        
        return lines.toArray(new String[0]);
    }
    
    /**
     * Establece la fórmula actual para mostrar sus detalles
     * @param formulaIndex Índice de la fórmula
     * @param formulaName Nombre de la fórmula (versión corta)
     * @param formulaDetails Detalles o descripción de la fórmula
     */
    public void setFormulaDetails(int formulaIndex, String formulaName, String formulaDetails) {
        this.currentFormulaIndex = formulaIndex;
        this.currentFormulaName = formulaName;
        this.currentFormulaDetails = formulaDetails;
        this.showingFormulaDetails = true;
    }
    
    /**
     * Oculta los detalles de la fórmula
     */
    public void hideFormulaDetails() {
        this.showingFormulaDetails = false;
    }
    
    /**
     * Verifica si se están mostrando detalles de fórmula
     * @return true si se están mostrando detalles
     */
    public boolean isShowingFormulaDetails() {
        return showingFormulaDetails;
    }
    
    /**
     * Establece la fórmula actual para mostrar en el popup de desbloqueo
     * @param formulaIndex Índice de la fórmula desbloqueada
     */
    public void setCurrentFormulaForUnlock(int formulaIndex) {
        this.currentFormulaIndex = formulaIndex;
    }
}
