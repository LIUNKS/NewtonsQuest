package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    
    public static Connection getConnection() {
        Connection cnx = null;

        String url = "jdbc:mysql://localhost:3306/NewtonQuest?useTimeZone=true&"
                   + "serverTimezone=UTC&autoReconnect=true";
        String user = "root";
        String clave = "Johan12315912";
        String driver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driver);
            cnx = DriverManager.getConnection(url, user, clave);
            //System.out.println("Conexión a la base de datos establecida con éxito.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver de MySQL. " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error: No se pudo conectar a la base de datos. " + e.getMessage());
            e.printStackTrace();
        }

        return cnx;
    }
    
    // Método para probar la conexión
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null) {
                System.out.println("Conexión exitosa a la base de datos: " + conn.getCatalog());
                return true;
            } else {
                System.err.println("No se pudo establecer la conexión a la base de datos.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error al probar la conexión: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }
}