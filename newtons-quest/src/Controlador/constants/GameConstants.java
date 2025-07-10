package Controlador.constants;

/**
 * Constantes del juego.
 * 
 * Esta clase centraliza todos los valores constantes utilizados en la aplicación,
 * facilitando el mantenimiento y la configuración global del juego. Incluye
 * constantes para puntuación, dimensiones, rutas de recursos, fórmulas de Newton,
 * validación de datos y configuración del quiz.
 * 
 * Las constantes están organizadas por categorías para facilitar su localización
 * y mantenimiento futuro.
 */
public final class GameConstants {
    
    /**
     * Constructor privado para prevenir la instanciación de esta clase de utilidades.
     */
    private GameConstants() {
        throw new UnsupportedOperationException("Esta es una clase de constantes y no debe ser instanciada");
    }
    
    // ===============================================
    // === SISTEMA DE PUNTUACIÓN Y MECÁNICAS ===
    // ===============================================
    
    /** Puntos otorgados por recoger una manzana roja */
    public static final int RED_APPLE_POINTS = 10;
    
    /** Puntos perdidos por recoger una manzana verde */
    public static final int GREEN_APPLE_POINTS = -5;
    
    /** Puntos perdidos por no recoger una manzana roja */
    public static final int MISSED_APPLE_POINTS = -3;
    
    /** Número inicial de vidas del jugador */
    public static final int INITIAL_LIVES = 5;
    
    // ===============================================
    // === PROGRESIÓN DE FÓRMULAS FÍSICAS ===
    // ===============================================

    /** Puntos necesarios para desbloquear la Fórmula 1: F = m × g */
    public static final int FORMULA_1_THRESHOLD = 100;
    
    /** Puntos necesarios para desbloquear la Fórmula 2: v = d / t */
    public static final int FORMULA_2_THRESHOLD = 250;
    
    /** Puntos necesarios para desbloquear la Fórmula 3: U = m × g × h */
    public static final int FORMULA_3_THRESHOLD = 450;
    
    /** Puntos necesarios para desbloquear la Fórmula 4: K = ½ × m × v² */
    public static final int FORMULA_4_THRESHOLD = 700;
    
    /** Puntos necesarios para desbloquear la Fórmula 5: a = (vf - vi) / t */
    public static final int FORMULA_5_THRESHOLD = 1000;
    
    // ===============================================
    // === PROBABILIDADES DE APARICIÓN ===
    // ===============================================
    
    /** Probabilidad de aparición de manzanas rojas (70%) */
    public static final double RED_APPLE_PROBABILITY = 0.7;
    
    /** Probabilidad de aparición de manzanas verdes (30%) */
    public static final double GREEN_APPLE_PROBABILITY = 0.3;
    
    // ===============================================
    // === DIMENSIONES DE VENTANAS ===
    // ===============================================
    
    /** Ancho de la ventana principal de la aplicación */
    public static final int MAIN_WINDOW_WIDTH = 900;
    
    /** Alto de la ventana principal de la aplicación */
    public static final int MAIN_WINDOW_HEIGHT = 700;
    
    /** Ancho del diálogo de reglas del juego */
    public static final int RULES_DIALOG_WIDTH = 700;
    
    /** Alto del diálogo de reglas del juego */
    public static final int RULES_DIALOG_HEIGHT = 600;
    
    /** Ancho del diálogo de configuración */
    public static final int SETTINGS_DIALOG_WIDTH = 500;
    
    /** Alto del diálogo de configuración */
    public static final int SETTINGS_DIALOG_HEIGHT = 600;
    
    /** Ancho de la ventana de reproducción de videos */
    public static final int VIDEO_WINDOW_WIDTH = 800;
    
    /** Alto de la ventana de reproducción de videos */
    public static final int VIDEO_WINDOW_HEIGHT = 600;
    
    // ===============================================
    // === DISEÑO Y COLORES TEMÁTICOS ===
    // ===============================================
    
    /** Color primario de la interfaz (rojo vibrante) */
    public static final String PRIMARY_COLOR = "#e94560";
    
    /** Color secundario de la interfaz (rojo coral) */
    public static final String SECONDARY_COLOR = "#ff6b6b";
    
    /** Gradiente de fondo principal de la aplicación */
    public static final String BACKGROUND_GRADIENT = "linear-gradient(to bottom, #0f0c29, #1a1a2e, #24243e)";    
    
    // ===============================================
    // === RUTAS DE RECURSOS DEL PROYECTO ===
    // ===============================================
    
    /** Ruta base para archivos FXML de vistas */
    public static final String FXML_PATH = "src/Vista/";
    
    /** Ruta base para archivos CSS de estilos */
    public static final String CSS_PATH = "src/Vista/resources/";
    
    /** Ruta base para imágenes del juego */
    public static final String IMAGES_PATH = "src/recursos/imagenes/";
    
    /** Ruta base para archivos de música */
    public static final String MUSIC_PATH = "src/recursos/musica/";
    
    /** Ruta base para efectos de sonido */
    public static final String SOUNDS_PATH = "src/recursos/sonidos/";
    
    /** Ruta base para archivos de video educativos */
    public static final String VIDEOS_PATH = "src/recursos/videos/";
    
    // ===============================================
    // === NOMBRES DE ARCHIVOS FXML ===
    // ===============================================
    
    /** Archivo FXML para la pantalla de inicio de sesión */
    public static final String LOGIN_FXML = "Login.fxml";
    
    /** Archivo FXML para el menú principal */
    public static final String MAIN_FXML = "Main.fxml";
    
    /** Archivo FXML para el mapa de aventuras */
    public static final String MAP_FXML = "Map.fxml";
    
    /** Archivo FXML para la pantalla de juego */
    public static final String GAME_FXML = "Game.fxml";
    
    /** Archivo FXML para el quiz de conocimientos */
    public static final String QUIZ_FXML = "Quiz.fxml";
    
    /** Archivo FXML para los resultados del quiz */
    public static final String QUIZ_RESULT_FXML = "QuizResult.fxml";
    
    // ===============================================
    // === NOMBRES DE ARCHIVOS CSS ===
    // ===============================================
    
    /** Archivo CSS para la pantalla de login */
    public static final String LOGIN_CSS = "login.css";
    
    /** Archivo CSS para el menú principal */
    public static final String MAIN_CSS = "main.css";
    
    /** Archivo CSS para el mapa de aventuras */
    public static final String MAP_CSS = "map.css";
    
    /** Archivo CSS para la pantalla de juego */
    public static final String GAME_CSS = "game.css";
    
    /** Archivo CSS para el quiz */
    public static final String QUIZ_CSS = "quiz.css";
    
    // ===============================================
    // === TÍTULOS DE VENTANAS ===
    // ===============================================
    
    /** Nombre principal de la aplicación */
    public static final String APP_NAME = "Newton's Apple Quest";
    
    /** Título de la ventana de inicio de sesión */
    public static final String LOGIN_TITLE = APP_NAME + " - Inicio de Sesión";
    
    /** Título de la ventana del menú principal */
    public static final String MAIN_TITLE = APP_NAME + " - Menú Principal";
    
    /** Título de la ventana del mapa de aventuras */
    public static final String MAP_TITLE = APP_NAME + " - Mapa de Aventuras";
    
    /** Título de la ventana de juego */
    public static final String GAME_TITLE = APP_NAME + " - Juego";
    
    /** Título de la ventana del quiz */
    public static final String QUIZ_TITLE = APP_NAME + " - Quiz";
    
    /** Título de la ventana de resultados del quiz */
    public static final String QUIZ_RESULT_TITLE = APP_NAME + " - Resultados del Quiz";
    
    /** Título del diálogo de reglas */
    public static final String RULES_TITLE = APP_NAME + " - Reglas del Juego";
    
    /** Título del diálogo de configuración */
    public static final String SETTINGS_TITLE = APP_NAME + " - Configuración";
    
    // ===============================================
    // === MENSAJES DE LA APLICACIÓN ===
    // ===============================================
    
    /** Formato del mensaje de bienvenida personalizado */
    public static final String WELCOME_MESSAGE_FORMAT = "¡Bienvenido, %s!";
    
    /** Mensaje mostrado al cargar el mapa */
    public static final String LOADING_MAP_MESSAGE = "Cargando la pantalla del mapa...";
    
    /** Mensaje de confirmación de carga del mapa */
    public static final String MAP_LOADED_MESSAGE = "Pantalla del mapa cargada correctamente";
    
    /** Mensaje al cerrar sesión */
    public static final String LOGOUT_MESSAGE = "Cerrando sesión...";
    
    /** Mensaje de confirmación de cierre de sesión */
    public static final String LOGOUT_SUCCESS_MESSAGE = "Sesión cerrada correctamente";
    
    // ===============================================
    // === FÓRMULAS FÍSICAS DE NEWTON ===
    // ===============================================
    
    /** 
     * Nombres descriptivos de las fórmulas físicas del juego.
     * Incluye tanto la notación matemática como una breve descripción.
     */
    public static final String[] FORMULA_NAMES = {
        "F = m × g (Fuerza de gravedad)",
        "v = d / t (Velocidad media)",
        "U = m × g × h (Energía potencial)",
        "K = ½ × m × v² (Energía cinética)",
        "a = (vf - vi) / t (Aceleración)"
    };
    
    /** 
     * Descripciones educativas de cada fórmula física.
     * Utilizadas en los diálogos informativos y videos educativos.
     */
    public static final String[] FORMULA_DESCRIPTIONS = {
        "La fuerza de gravedad que actúa sobre un objeto",
        "La velocidad promedio de un objeto en movimiento",
        "La energía almacenada por la posición de un objeto",
        "La energía del movimiento de un objeto",
        "El cambio de velocidad por unidad de tiempo"
    };
    
    // ===============================================
    // === VALIDACIÓN DE DATOS DE USUARIO ===
    // ===============================================
    
    /** Longitud mínima permitida para nombres de usuario */
    public static final int MIN_USERNAME_LENGTH = 3;
    
    /** Longitud máxima permitida para nombres de usuario */
    public static final int MAX_USERNAME_LENGTH = 20;
    
    /** Longitud mínima requerida para contraseñas */
    public static final int MIN_PASSWORD_LENGTH = 6;
    
    // ===============================================
    // === CONFIGURACIÓN DEL QUIZ ===
    // ===============================================
    
    /** Número total de preguntas en cada quiz */
    public static final int QUIZ_TOTAL_QUESTIONS = 15;
    
    /** Porcentaje mínimo requerido para aprobar el quiz */
    public static final double QUIZ_PASS_PERCENTAGE = 85.0;
    
    /** Mensaje mostrado cuando el usuario aprueba el quiz */
    public static final String QUIZ_PASS_MESSAGE = "¡Felicidades! Has aprobado el quiz.";
    
    /** Mensaje mostrado cuando el usuario no aprueba el quiz */
    public static final String QUIZ_FAIL_MESSAGE = "¡Sigue intentando! Puedes repetir el quiz.";
    
    // ===============================================
    // === RECURSOS DE VIDEO EDUCATIVOS ===
    // ===============================================
    
    /** Archivo de video con la biografía de Isaac Newton */
    public static final String VIDEO_BIOGRAFIA = "video_biografia.mp4";
    
    /** 
     * Archivos de video para cada fórmula física.
     * El índice del array corresponde al número de fórmula (0-4).
     */
    public static final String[] VIDEO_FORMULA_FILES = {
        "video_formula_1.mp4",
        "video_formula_2.mp4", 
        "video_formula_3.mp4",
        "video_formula_4.mp4",
        "video_formula_5.mp4"
    };
    
    /** 
     * Títulos descriptivos para cada video de fórmula.
     * Utilizados en la interfaz de selección de videos.
     */
    public static final String[] VIDEO_FORMULA_TITLES = {
        "Fórmula 1: Fuerza de Gravedad (F = m × g)",
        "Fórmula 2: Velocidad Media (v = d / t)",
        "Fórmula 3: Energía Potencial (U = m × g × h)",
        "Fórmula 4: Energía Cinética (K = ½ × m × v²)",
        "Fórmula 5: Aceleración (a = (vf - vi) / t)"
    };
}
