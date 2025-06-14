package Modelo;

import java.sql.Timestamp;

/**
 * Clase que representa una entrada en el ranking de jugadores
 */
public class RankingEntry {
    private int posicion;
    private int userId;
    private String username;
    private int puntaje;
    private Timestamp fechaCompletado;
    
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
    
    public Timestamp getFechaCompletado() {
        return fechaCompletado;
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
