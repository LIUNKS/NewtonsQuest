package Controlador.componentes;

import Modelo.RankingDAO;
import Modelo.RankingEntry;
import java.util.List;

/**
 * Clase encargada de gestionar el sistema de ranking de jugadores
 * que han completado las 5 fórmulas del juego.
 */
public class RankingManager {
    
    private static RankingManager instance;
    private int currentUserId = -1;
    private String currentUsername = "";
    
    // Callbacks
    private Runnable onRankingUpdated;
    
    /**
     * Constructor privado para patrón Singleton
     */
    private RankingManager() {
    }
    
    /**
     * Obtiene la instancia única del RankingManager
     */
    public static RankingManager getInstance() {
        if (instance == null) {
            instance = new RankingManager();
        }
        return instance;
    }
    
    /**
     * Establece el usuario actual
     */
    public void setCurrentUser(int userId, String username) {
        this.currentUserId = userId;
        this.currentUsername = username;
        System.out.println("Usuario actual establecido en RankingManager: " + username + " (ID: " + userId + ")");
    }
    
    /**
     * Establece el callback para cuando se actualiza el ranking
     */
    public void setOnRankingUpdated(Runnable callback) {
        this.onRankingUpdated = callback;
    }
    
    /**
     * Verifica si el usuario completó las 5 fórmulas y guarda su puntaje
     * @param formulasUnlocked Array de fórmulas desbloqueadas
     * @param score Puntaje final
     * @return true si se completaron todas las fórmulas
     */
    public boolean checkAndSaveCompletedGame(boolean[] formulasUnlocked, int score) {
        if (currentUserId == -1) {
            System.out.println("No hay usuario actual establecido");
            return false;
        }
        
        // Contar fórmulas completadas
        int formulasCompletadas = 0;
        for (boolean unlocked : formulasUnlocked) {
            if (unlocked) {
                formulasCompletadas++;
            }
        }
        
        System.out.println("Usuario " + currentUsername + " completó " + formulasCompletadas + " fórmulas con " + score + " puntos");
        
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
    
    /**
     * Obtiene el top N del ranking
     */
    public List<RankingEntry> getTopRanking(int limite) {
        return RankingDAO.obtenerTopRanking(limite);
    }
    
    /**
     * Obtiene la posición del usuario actual en el ranking
     */
    public int getCurrentUserPosition() {
        if (currentUserId == -1) {
            return -1;
        }
        return RankingDAO.obtenerPosicionUsuario(currentUserId);
    }
    
    /**
     * Obtiene el total de jugadores que han completado el juego
     */
    public int getTotalCompletedPlayers() {
        return RankingDAO.obtenerTotalJugadoresCompletos();
    }
    
    /**
     * Genera un mensaje de felicitación personalizado según el ranking
     */
    public String generateCongratulationMessage(int score, boolean allFormulasCompleted) {
        if (!allFormulasCompleted) {
            return "¡Buen trabajo! Sigue jugando para desbloquear todas las fórmulas y entrar al ranking de maestros de la física.";
        }
        
        StringBuilder message = new StringBuilder();
        message.append("🎉 ¡FELICITACIONES! 🎉\n\n");
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
                message.append("🎯 ¡Sigue practicando para mejorar tu posición en el ranking!\n");
            }
        } else {
            message.append(String.format("📊 Puntaje: %d puntos\n", score));
            message.append("🎯 ¡Ya formas parte del selecto grupo de Maestros de la Física!\n");
        }
        
        message.append("\n💡 ¿Puedes conseguir una puntuación aún mejor? ¡Inténtalo de nuevo!");
        
        return message.toString();
    }
    
    /**
     * Verifica si hay nuevos récords o mejoras significativas
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
    
    /**
     * Obtiene información del usuario actual
     */
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    public int getCurrentUserId() {
        return currentUserId;
    }
}
