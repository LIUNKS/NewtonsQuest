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
        } catch (ClassNotFoundException | SQLException e) {
            
        }

        return cnx;
    }
    
    /*
    public static void main(String[] args) throws SQLException {
        Connection cnx = ConexionDB.getConnection();
        System.out.println(cnx.getCatalog());
    }*/
}
