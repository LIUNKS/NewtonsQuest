package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    
    public static Connection getConnection() {
        Connection cnx = null;

        String url = "jdbc:mysql://localhost:3306/newtons_quest?useTimeZone=true&"
                   + "serverTimezone=UTC&autoReconnect=true";
        String user = "root";
        String clave = "Johan12315912";
        String driver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driver);
            cnx = DriverManager.getConnection(url, user, clave);
        } catch (ClassNotFoundException | SQLException e) {
            // Error silencioso en conexión
        }
        return cnx;
    }
    
    /**
     * Método utilitario para cerrar recursos de base de datos de forma segura
     * @param conn Conexión a cerrar
     * @param stmt Statement a cerrar
     * @param rs ResultSet a cerrar
     */
    public static void cerrarRecursos(Connection conn, java.sql.Statement stmt, java.sql.ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            // Error silencioso al cerrar ResultSet
        }
        
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            // Error silencioso al cerrar Statement
        }
        
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            // Error silencioso al cerrar Connection
        }
    }
    
    /**
     * Sobrecarga del método para cerrar solo conexión y statement
     * @param conn Conexión a cerrar
     * @param stmt Statement a cerrar
     */
    public static void cerrarRecursos(Connection conn, java.sql.Statement stmt) {
        cerrarRecursos(conn, stmt, null);
    }
}