package Modelo.dto;

import java.sql.Timestamp;

/**
 * Representa una entrada en el ranking de jugadores.
 * 
 * Contiene información de posición, usuario, puntaje y fecha de un récord
 * para mostrar en las tablas de clasificación del juego.
 * 
 * @author Johann
 * @version 1.0
 */
public class RankingEntry {
    
    /** Posición en el ranking (1, 2, 3, etc.) */
    private int posicion;
    /** ID único del usuario en la base de datos */
    private int userId;
    /** Nombre de usuario para mostrar */
    private String username;
    /** Puntaje obtenido por el jugador */
    private int puntaje;
    /** Fecha y hora cuando se completó el juego */
    private Timestamp fechaCompletado;
    
    /**
     * Constructor para crear una entrada de ranking.
     * 
     * @param posicion Posición en el ranking
     * @param userId ID del usuario
     * @param username Nombre de usuario
     * @param puntaje Puntaje obtenido
     * @param fechaCompletado Fecha de finalización
     */
    public RankingEntry(int posicion, int userId, String username, int puntaje, Timestamp fechaCompletado) {
        this.posicion = posicion;
        this.userId = userId;
        this.username = username;
        this.puntaje = puntaje;
        this.fechaCompletado = fechaCompletado;
    }
    
    // Getters
    public int getPosicion() {
        return posicion;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public int getPuntaje() {
        return puntaje;
    }
    
    /**
     * Obtiene el puntaje
     */
    public int getScore() {
        return puntaje;
    }
    
    public Timestamp getFechaCompletado() {
        return fechaCompletado;
    }
    
    /**
     * Obtiene la fecha de completación formateada
     */
    public String getFormattedCompletionDate() {
        if (fechaCompletado == null) {
            return "N/A";
        }
        // Formatear fecha como "dd/MM/yyyy"
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(fechaCompletado);
    }
    
    // Setters
    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }
    
    public void setFechaCompletado(Timestamp fechaCompletado) {
        this.fechaCompletado = fechaCompletado;
    }
    
    @Override
    public String toString() {
        return String.format("#%d - %s: %d puntos (Completado: %s)", 
                           posicion, username, puntaje, fechaCompletado);
    }
}
