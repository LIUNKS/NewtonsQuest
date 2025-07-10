package Controlador.utils;

import Modelo.dao.UsuarioDAO;

/**
 * Gestor de sesión de usuario en Newton's Apple Quest.
 * 
 * Esta clase singleton se encarga de mantener la información
 * del usuario actualmente logueado durante toda la sesión de juego.
 * 
 * Funcionalidades principales:
 * - Almacenar datos del usuario autenticado (ID y nombre de usuario)
 * - Proporcionar acceso global a la información de sesión
 * - Gestionar inicio y cierre de sesión
 * - Validar estado de autenticación del usuario
 * 
 * La clase mantiene sincronización con la base de datos para
 * obtener información actualizada del usuario cuando es necesario.
 * 
 * Implementa el patrón Singleton para garantizar una única
 * instancia de sesión en toda la aplicación.
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
