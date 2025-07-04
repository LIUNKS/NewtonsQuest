package Controlador.utils;

import Modelo.dao.UsuarioDAO;

/**
 * Clase para manejar la sesión del usuario actual en la aplicación.
 * Mantiene la información del usuario logueado durante toda la sesión.
 */
public class SessionManager {
    
    private static SessionManager instance;
    private String currentUsername;
    private int currentUserId;
    
    private SessionManager() {
        this.currentUsername = null;
        this.currentUserId = -1;
    }
    
    /**
     * Obtiene la instancia única del SessionManager (Singleton)
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Inicia sesión con el usuario especificado
     */
    public void login(String username) {
        this.currentUsername = username;
        this.currentUserId = UsuarioDAO.obtenerIdUsuario(username);
        // Sesión iniciada silenciosamente
    }
    
    /**
     * Cierra la sesión actual
     */
    public void logout() {
        // Cerrando sesión para el usuario
        this.currentUsername = null;
        this.currentUserId = -1;
    }
      /**
     * Verifica si hay una sesión activa
     */
    public boolean isLoggedIn() {
        return currentUsername != null && !currentUsername.isEmpty();
    }
    
    /**
     * Obtiene el nombre de usuario actual
     */
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    /**
     * Obtiene el ID del usuario actual
     */
    public int getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * Obtiene información completa del usuario actual
     */
    public String getUserInfo() {
        if (!isLoggedIn()) {
            return "No hay sesión activa";
        }
        return "Usuario: " + currentUsername + " (ID: " + currentUserId + ")";
    }
}
