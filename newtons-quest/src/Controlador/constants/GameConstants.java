package Controlador.constants;

/**
 * Constantes del juego Newton's Apple Quest.
 * Centraliza todos los valores constantes para facilitar el mantenimiento.
 */
public final class GameConstants {
    
    // Prevenir instanciación
    private GameConstants() {}
    
    // === PUNTUACIÓN ===
    public static final int RED_APPLE_POINTS = 10;
    public static final int GREEN_APPLE_POINTS = -5;
    public static final int MISSED_APPLE_POINTS = -3;
    
    // === VIDAS ===
    public static final int INITIAL_LIVES = 5;
    
    // === FÓRMULAS Y PROGRESIÓN ===
    public static final int FORMULA_1_THRESHOLD = 100;  // F = m × g
    public static final int FORMULA_2_THRESHOLD = 250;  // v = d / t
    public static final int FORMULA_3_THRESHOLD = 450;  // U = m × g × h
    public static final int FORMULA_4_THRESHOLD = 700;  // K = ½ × m × v²
    public static final int FORMULA_5_THRESHOLD = 1000; // a = (vf - vi) / t
    
    // === PROBABILIDADES ===
    public static final double RED_APPLE_PROBABILITY = 0.7;
    public static final double GREEN_APPLE_PROBABILITY = 0.3;
      // === DIMENSIONES DE VENTANA ===
    public static final int MAIN_WINDOW_WIDTH = 900;
    public static final int MAIN_WINDOW_HEIGHT = 700;
    public static final int RULES_DIALOG_WIDTH = 700;
    public static final int RULES_DIALOG_HEIGHT = 600;
    public static final int SETTINGS_DIALOG_WIDTH = 500;
    public static final int SETTINGS_DIALOG_HEIGHT = 600;
    
    // === COLORES TEMÁTICOS ===
    public static final String PRIMARY_COLOR = "#e94560";
    public static final String SECONDARY_COLOR = "#ff6b6b";
    public static final String BACKGROUND_GRADIENT = "linear-gradient(to bottom, #0f0c29, #1a1a2e, #24243e)";    // === PATHS DE RECURSOS ===
    public static final String FXML_PATH = "src/Vista/";
    public static final String CSS_PATH = "src/Vista/resources/";
    public static final String IMAGES_PATH = "src/recursos/imagenes/";
    public static final String MUSIC_PATH = "src/recursos/musica/";
    public static final String SOUNDS_PATH = "src/recursos/sonidos/";
    
    // === ARCHIVOS ESPECÍFICOS ===
    public static final String LOGIN_FXML = "Login.fxml";
    public static final String MAIN_FXML = "Main.fxml";
    public static final String MAP_FXML = "Map.fxml";
    public static final String GAME_FXML = "Game.fxml";
    
    public static final String LOGIN_CSS = "login.css";
    public static final String MAIN_CSS = "main.css";
    public static final String MAP_CSS = "map.css";
    public static final String GAME_CSS = "game.css";
    
    // === TÍTULOS DE VENTANA ===
    public static final String APP_NAME = "Newton's Apple Quest";
    public static final String LOGIN_TITLE = APP_NAME + " - Inicio de Sesión";
    public static final String MAIN_TITLE = APP_NAME + " - Menú Principal";
    public static final String MAP_TITLE = APP_NAME + " - Mapa de Aventuras";
    public static final String GAME_TITLE = APP_NAME + " - Juego";
    public static final String RULES_TITLE = APP_NAME + " - Reglas del Juego";
    public static final String SETTINGS_TITLE = APP_NAME + " - Configuración";
    
    // === MENSAJES ===
    public static final String WELCOME_MESSAGE_FORMAT = "¡Bienvenido, %s!";
    public static final String LOADING_MAP_MESSAGE = "Cargando la pantalla del mapa...";
    public static final String MAP_LOADED_MESSAGE = "Pantalla del mapa cargada correctamente";
    public static final String LOGOUT_MESSAGE = "Cerrando sesión...";
    public static final String LOGOUT_SUCCESS_MESSAGE = "Sesión cerrada correctamente";
    
    // === FÓRMULAS DE NEWTON ===
    public static final String[] FORMULA_NAMES = {
        "F = m × g (Fuerza de gravedad)",
        "v = d / t (Velocidad media)",
        "U = m × g × h (Energía potencial)",
        "K = ½ × m × v² (Energía cinética)",
        "a = (vf - vi) / t (Aceleración)"
    };
    
    public static final String[] FORMULA_DESCRIPTIONS = {
        "La fuerza de gravedad que actúa sobre un objeto",
        "La velocidad promedio de un objeto en movimiento",
        "La energía almacenada por la posición de un objeto",
        "La energía del movimiento de un objeto",
        "El cambio de velocidad por unidad de tiempo"
    };
    
    // === VALIDACIÓN ===
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MIN_PASSWORD_LENGTH = 6;
}
