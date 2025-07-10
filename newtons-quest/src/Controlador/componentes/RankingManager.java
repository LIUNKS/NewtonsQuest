package Controlador.componentes;

import Modelo.dao.RankingDAO;
import Modelo.dto.RankingEntry;
import java.util.List;

/**
 * Gestor del sistema de rankings y logros.
 * 
 * Esta clase controla el sistema de rankings para jugadores que han
 * completado todas las fórmulas de Newton:
 * 
 *   - Verificación de completación de las 5 fórmulas
 *   - Guardado de puntajes en el ranking global
 *   - Generación de mensajes de felicitación personalizados
 *   - Cálculo de posiciones y logros especiales
 *   - Gestión de récords y hitos significativos
 * 
 * Implementa el patrón Singleton para mantener la consistencia
 * del usuario actual y los datos del ranking durante la sesión.
 */

public class RankingManager {
    
    // ================================================================================================
    // INSTANCIA SINGLETON Y DATOS DEL USUARIO
    // ================================================================================================
    
    /** Instancia única del gestor de ranking */
    private static RankingManager instance;
    
    /** ID del usuario actual */
    private int currentUserId = -1;
    
    /** Nombre de usuario actual */
    private String currentUsername = "";
    
    // ================================================================================================
    // CALLBACKS Y EVENTOS
    // ================================================================================================
    
    /** Callback ejecutado cuando se actualiza el ranking */
    private Runnable onRankingUpdated;
    
    // ================================================================================================
    // CONSTRUCTORES Y PATRÓN SINGLETON
    // ================================================================================================
    
    /** Constructor privado para implementar patrón Singleton */
    private RankingManager() {
    }
    
    /**
     * Obtiene la instancia única del gestor de ranking.
     * @return Instancia única del RankingManager
     */
    public static RankingManager getInstance() {
        if (instance == null) {
            instance = new RankingManager();
        }
        return instance;
    }
    
    // ================================================================================================
    // CONFIGURACIÓN DEL USUARIO ACTUAL
    // ================================================================================================
    
    /**
     * Establece el usuario actual para las operaciones de ranking.
     * @param userId ID único del usuario
     * @param username Nombre de usuario
     */
    public void setCurrentUser(int userId, String username) {
        this.currentUserId = userId;
        this.currentUsername = username;
    }
    
    /**
     * Establece el callback para cuando se actualiza el ranking.
     * @param callback Acción a ejecutar cuando se actualiza el ranking
     */
    public void setOnRankingUpdated(Runnable callback) {
        this.onRankingUpdated = callback;
    }
    
    // ================================================================================================
    // VERIFICACIÓN Y GUARDADO DE PROGRESO
    // ================================================================================================
    
    /**
     * Verifica si el usuario completó las 5 fórmulas y guarda su puntaje.
     * @param formulasUnlocked Array de fórmulas desbloqueadas
     * @param score Puntaje final obtenido
     * @return true si se completaron todas las fórmulas
     */
    public boolean checkAndSaveCompletedGame(boolean[] formulasUnlocked, int score) {
        if (currentUserId == -1) {
            return false;
        }
        
        // Contar fórmulas completadas
        int formulasCompletadas = 0;
        for (boolean unlocked : formulasUnlocked) {
            if (unlocked) {
                formulasCompletadas++;
            }
        }
        
        // Verificar si completó todas las fórmulas
        if (formulasCompletadas >= 5) {
            boolean saved = RankingDAO.guardarPuntajeCompleto(currentUserId, score, formulasCompletadas);
            
            if (saved && onRankingUpdated != null) {
                onRankingUpdated.run();
            }
            
            return true;
        }
        
        return false;
    }
    
    // ================================================================================================
    // CONSULTAS DE RANKING
    // ================================================================================================
    
    /**
     * Obtiene el top N del ranking de jugadores completos.
     * @param limite Número máximo de jugadores a retornar
     * @return Lista de entradas del ranking ordenadas por puntaje
     */
    public List<RankingEntry> getTopRanking(int limite) {
        return RankingDAO.obtenerTopRanking(limite);
    }
    
    /**
     * Obtiene los mejores jugadores del ranking.
     * @param limit Número máximo de jugadores a retornar
     * @return Lista de entradas del ranking o lista vacía si hay error
     */
    public List<RankingEntry> getTopPlayers(int limit) {
        return RankingDAO.obtenerTopRanking(limit);
    }
    
    /**
     * Obtiene la posición del usuario actual en el ranking.
     * @return Posición en el ranking o -1 si no hay usuario actual
     */
    public int getCurrentUserPosition() {
        if (currentUserId == -1) {
            return -1;
        }
        return RankingDAO.obtenerPosicionUsuario(currentUserId);
    }
    
    /**
     * Obtiene el total de jugadores que han completado el juego.
     * @return Número total de jugadores que completaron todas las fórmulas
     */
    public int getTotalCompletedPlayers() {
        return RankingDAO.obtenerTotalJugadoresCompletos();
    }
    
    // ================================================================================================
    // GENERACIÓN DE MENSAJES Y LOGROS
    // ================================================================================================
    
    /**
     * Genera un mensaje de felicitación personalizado según el ranking.
     * @param score Puntaje obtenido por el jugador
     * @param allFormulasCompleted Si completó todas las fórmulas
     * @return Mensaje de felicitación personalizado
     */
    public String generateCongratulationMessage(int score, boolean allFormulasCompleted) {
        if (!allFormulasCompleted) {
            return "¡Buen trabajo! Sigue jugando para desbloquear todas las fórmulas y entrar al ranking de maestros de la física.";
        }
        
        StringBuilder message = new StringBuilder();
        message.append("🎉 ¡FELICITACIONES, SUPERASTE LOS 5 NIVELES! 🎉\n\n");
        message.append("¡Has desbloqueado las 5 fórmulas de Newton y te has convertido en un Maestro de la Física!\n\n");
        
        int position = getCurrentUserPosition();
        int totalPlayers = getTotalCompletedPlayers();
        
        if (position > 0) {
            message.append(String.format("🏆 Tu posición en el ranking: #%d de %d maestros\n", position, totalPlayers));
            message.append(String.format("📊 Puntaje: %d puntos\n\n", score));
            
            if (position == 1) {
                message.append("👑 ¡Eres el CAMPEÓN actual! ¡Nadie puede superarte!\n");
            } else if (position <= 3) {
                message.append("🥉 ¡Estás en el podio! ¡Excelente trabajo!\n");
            } else if (position <= 10) {
                message.append("⭐ ¡Estás en el TOP 10! ¡Sigue mejorando para llegar al podio!\n");
            } else {
                message.append("🎯 ¡Sigue buscando el mejor puntaje para entrar en el ranking de los Maestros de Física!\n");
            }
        } else {
            message.append(String.format("📊 Puntaje: %d puntos\n", score));
            message.append("🎯 ¡Ya formas parte del selecto grupo de Maestros de la Física!\n");
        }
        
        message.append("\n💡 ¡Sigue buscando el mejor puntaje para entrar en el ranking de los Maestros de Física!");
        
        return message.toString();
    }
    
    /**
     * Verifica si hay nuevos récords o mejoras significativas.
     * @param newScore Nuevo puntaje obtenido
     * @param allFormulasCompleted Si completó todas las fórmulas
     * @return Mensaje de logro especial o null si no hay logros
     */
    public String checkForAchievements(int newScore, boolean allFormulasCompleted) {
        if (!allFormulasCompleted) {
            return null;
        }
        
        List<RankingEntry> topRanking = getTopRanking(10);
        if (topRanking.isEmpty()) {
            return "🌟 ¡Eres el PRIMER jugador en completar todas las fórmulas! ¡Eres una leyenda!";
        }
        
        // Verificar si es un nuevo récord
        RankingEntry topPlayer = topRanking.get(0);
        if (newScore > topPlayer.getPuntaje()) {
            return String.format("🔥 ¡NUEVO RÉCORD MUNDIAL! Superaste el anterior récord de %d puntos de %s", 
                               topPlayer.getPuntaje(), topPlayer.getUsername());
        }
        
        // Verificar si entró al top 3
        if (topRanking.size() >= 3) {
            RankingEntry thirdPlace = topRanking.get(2);
            if (newScore > thirdPlace.getPuntaje()) {
                return "🥉 ¡Entraste al TOP 3! ¡Increíble logro!";
            }
        }
        
        // Verificar si entró al top 10
        if (topRanking.size() >= 10) {
            RankingEntry tenthPlace = topRanking.get(9);
            if (newScore > tenthPlace.getPuntaje()) {
                return "⭐ ¡Entraste al TOP 10! ¡Excelente trabajo!";
            }
        }
        
        return null;
    }
    
    // ================================================================================================
    // MÉTODOS DE ACCESO - GETTERS
    // ================================================================================================
    
    /**
     * Obtiene el nombre del usuario actual.
     * @return Nombre de usuario actual
     */
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    /**
     * Obtiene el ID del usuario actual.
     * @return ID del usuario actual
     */
    public int getCurrentUserId() {
        return currentUserId;
    }
}
