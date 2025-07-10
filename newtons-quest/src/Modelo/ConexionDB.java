package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para gestionar conexiones a la base de datos MySQL.
 * 
 * Proporciona métodos para establecer conexiones y cerrar recursos de forma segura,
 * evitando memory leaks.
 * 
 * @author Johann
 * @version 1.0
 */
public class ConexionDB {
    
    // Constantes de configuración de base de datos
    /** URL de conexión a la base de datos MySQL */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/newtons_quest?useTimeZone=true&"
                                       + "serverTimezone=America/Lima&autoReconnect=true";
    
    /** Usuario de la base de datos */
    private static final String DB_USER = "root";
    
    /** Contraseña de la base de datos */
    private static final String DB_PASSWORD = "Johan12315912";
    
    /** Driver JDBC para MySQL */
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    /**
     * Establece una conexión a la base de datos MySQL.
     * @return Connection a la base de datos, o null si hay error
     */
    public static Connection getConnection() {
        Connection cnx = null;

        try {
            Class.forName(DB_DRIVER);
            cnx = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
        } catch (ClassNotFoundException e) {
            // Driver MySQL no encontrado
            
        } catch (SQLException e) {
            // Error de conexión a la base de datos
        }
        
        return cnx;
    }
    
    /**
     * Cierra recursos de base de datos de forma segura en el orden correcto.
     * Maneja excepciones individualmente para asegurar que todos los recursos se cierren.
     * 
     * @param conn Conexión a cerrar (puede ser null)
     * @param stmt Statement a cerrar (puede ser null)  
     * @param rs ResultSet a cerrar (puede ser null)
     */
    public static void cerrarRecursos(Connection conn, java.sql.Statement stmt, java.sql.ResultSet rs) {
        // Orden: ResultSet -> Statement -> Connection
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            // Error silencioso al cerrar ResultSet
        }
        
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            // Error silencioso al cerrar Statement
        }
        
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            // Error silencioso al cerrar Connection
        }
    }
    
    /**
     * Sobrecarga para cerrar solo conexión y statement (sin ResultSet).
     * Útil para operaciones INSERT, UPDATE o DELETE.
     * 
     * @param conn Conexión a cerrar
     * @param stmt Statement a cerrar
     */
    public static void cerrarRecursos(Connection conn, java.sql.Statement stmt) {
        cerrarRecursos(conn, stmt, null);
    }
}