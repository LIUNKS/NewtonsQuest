package Controlador.componentes;

import Controlador.constants.GameConstants;
import Controlador.utils.SessionManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de videos educativos en Newton's Apple Quest.
 * 
 * Esta clase maneja la disponibilidad y acceso a los videos educativos del juego,
 * controlando qué contenido está disponible según el progreso del usuario.
 * 
 * Funcionalidades principales:
 * - Gestión de acceso a videos basado en fórmulas desbloqueadas
 * - Sincronización con el progreso del usuario en la base de datos
 * - Control de disponibilidad de contenido educativo
 * - Listado de videos accesibles según nivel alcanzado
 * 
 * Implementa el patrón Singleton para garantizar una única instancia
 * de gestión de videos en toda la aplicación.
 * 
 * Los videos están organizados según las fórmulas de física disponibles:
 * - Video 0: Física básica y fundamentos
 * - Video 1-4: Fórmulas específicas correspondientes a cada nivel
 */
public class VideoManager {
    
    private static VideoManager instance;
    private boolean[] unlockedVideos;
    private final int MAX_VIDEOS = 5;
    
    /**
     * Constructor privado para patrón Singleton
     */
    private VideoManager() {
        unlockedVideos = new boolean[MAX_VIDEOS];
        // Inicialmente todos los videos están bloqueados
        for (int i = 0; i < MAX_VIDEOS; i++) {
            unlockedVideos[i] = false;
        }
    }
    
    /**
     * Obtiene la instancia única del VideoManager
     */
    public static VideoManager getInstance() {
        if (instance == null) {
            instance = new VideoManager();
        }
        return instance;
    }
    
    /**
     * Actualiza el estado de los videos basándose en las fórmulas desbloqueadas
     * @param unlockedFormulas Array de fórmulas desbloqueadas
     */
    public void updateVideoAccess(boolean[] unlockedFormulas) {
        if (unlockedFormulas != null && unlockedFormulas.length >= MAX_VIDEOS) {
            for (int i = 0; i < MAX_VIDEOS; i++) {
                unlockedVideos[i] = unlockedFormulas[i];
            }
            // Acceso a videos actualizado
        }
    }
    
    /**
     * Alias para updateVideoAccess - mantener compatibilidad
     */
    public void updateUnlockedVideos(boolean[] unlockedFormulas) {
        updateVideoAccess(unlockedFormulas);
    }
    
    /**
     * Carga el progreso del usuario desde la base de datos
     * para determinar qué videos están desbloqueados
     */
    public void loadUserProgress() {
        try {
            int currentUserId = SessionManager.getInstance().getCurrentUserId();
            if (currentUserId != -1) {
                int formulasCompletadas = Modelo.dao.UsuarioDAO.obtenerFormulasCompletadasUsuario(currentUserId);
                
                // Desbloquear videos basándose en el número de fórmulas completadas
                for (int i = 0; i < MAX_VIDEOS; i++) {
                    unlockedVideos[i] = (i < formulasCompletadas);
                }
                
                // Progreso del usuario cargado
            } else {
                // No hay usuario logueado para cargar progreso
            }
        } catch (Exception e) {
            // Error silencioso al cargar progreso del usuario
            // En caso de error, mantener todos los videos bloqueados
            for (int i = 0; i < MAX_VIDEOS; i++) {
                unlockedVideos[i] = false;
            }
        }
    }
    
    /**
     * Verifica si un video específico está desbloqueado
     * @param videoIndex Índice del video (0-4)
     * @return true si el video está desbloqueado
     */
    public boolean isVideoUnlocked(int videoIndex) {
        if (videoIndex >= 0 && videoIndex < MAX_VIDEOS) {
            return unlockedVideos[videoIndex];
        }
        return false;
    }
    
    /**
     * Obtiene la cantidad de videos desbloqueados
     * @return Número de videos disponibles
     */
    public int getUnlockedVideoCount() {
        int count = 0;
        for (boolean unlocked : unlockedVideos) {
            if (unlocked) count++;
        }
        return count;
    }
    
    /**
     * Obtiene la lista de videos disponibles para mostrar en la interfaz
     * @return Lista de información de videos disponibles
     */
    public List<VideoInfo> getAvailableVideos() {
        List<VideoInfo> availableVideos = new ArrayList<>();
        
        for (int i = 0; i < MAX_VIDEOS; i++) {
            VideoInfo videoInfo = new VideoInfo(
                i,
                GameConstants.VIDEO_FORMULA_TITLES[i],
                GameConstants.VIDEO_FORMULA_FILES[i],
                unlockedVideos[i]
            );
            availableVideos.add(videoInfo);
        }
        
        return availableVideos;
    }
    
    /**
     * Obtiene la ruta completa de un video
     * @param videoIndex Índice del video
     * @return Ruta del video o null si no está disponible
     */
    public String getVideoPath(int videoIndex) {
        if (isVideoUnlocked(videoIndex)) {
            return GameConstants.VIDEOS_PATH + GameConstants.VIDEO_FORMULA_FILES[videoIndex];
        }
        return null;
    }
    
    /**
     * Obtiene la ruta del video de biografía (siempre disponible)
     * @return Ruta del video de biografía
     */
    public String getBiographyVideoPath() {
        return GameConstants.VIDEOS_PATH + GameConstants.VIDEO_BIOGRAFIA;
    }
    
    /**
     * Verifica si un archivo de video existe
     * @param videoPath Ruta del video
     * @return true si el archivo existe
     */
    public boolean videoFileExists(String videoPath) {
        File videoFile = new File(videoPath);
        return videoFile.exists() && videoFile.isFile();
    }
    
    /**
     * Obtiene información sobre el progreso de videos
     * @return Mensaje con el progreso actual
     */
    public String getProgressMessage() {
        int unlockedCount = getUnlockedVideoCount();
        if (unlockedCount == 0) {
            return "Completa los niveles del juego para desbloquear videos educativos";
        } else if (unlockedCount == MAX_VIDEOS) {
            return "¡Felicitaciones! Has desbloqueado todos los videos";
        } else {
            return String.format("Videos desbloqueados: %d/%d", unlockedCount, MAX_VIDEOS);
        }
    }
    
    /**
     * Clase interna para representar información de un video
     */
    public static class VideoInfo {
        private final int index;
        private final String title;
        private final String filename;
        private final boolean unlocked;
        
        public VideoInfo(int index, String title, String filename, boolean unlocked) {
            this.index = index;
            this.title = title;
            this.filename = filename;
            this.unlocked = unlocked;
        }
        
        public int getIndex() { return index; }
        public String getTitle() { return title; }
        public String getFilename() { return filename; }
        public boolean isUnlocked() { return unlocked; }
    }
}
