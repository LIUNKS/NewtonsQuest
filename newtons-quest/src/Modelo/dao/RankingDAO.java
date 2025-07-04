package Modelo.dao;

import Modelo.ConexionDB;
import Modelo.RankingEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para gestionar el ranking de jugadores que han completado los 5 niveles
 */
public class RankingDAO {
    
    /**
     * Guarda o actualiza el puntaje de un usuario que completó los 5 niveles
     * @param userId ID del usuario
     * @param score Puntaje obtenido
     * @param formulasCompleted Número de fórmulas completadas (debe ser 5)
     * @return true si se guardó exitosamente
     */
    public static boolean guardarPuntajeCompleto(int userId, int score, int formulasCompleted) {
        if (formulasCompleted < 5) {
            // El usuario no ha completado todas las fórmulas aún
            return false;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) {
                // Error: No se pudo establecer la conexión
                return false;
            }
              // Verificar si el usuario ya tiene un registro
            if (existeUsuarioEnRanking(userId)) {
                // Actualizar solo si el nuevo puntaje es mayor
                int puntajeActual = obtenerMejorPuntajeInterno(userId);
                if (score > puntajeActual) {
                    return actualizarPuntaje(userId, score);
                } else {
                    // El puntaje actual es mayor o igual al nuevo
                    return true; // No es error, simplemente no se actualiza
                }
            } else {                // Insertar nuevo registro
                String sql = "INSERT INTO ranking (usuario_id, mejor_puntaje, partidas_completadas, fecha_mejor_puntaje) VALUES (?, ?, ?, NOW())";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userId);
                stmt.setInt(2, score);
                stmt.setInt(3, formulasCompleted);
                
                int filasAfectadas = stmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    // Nuevo jugador añadido al ranking
                    return true;
                }
            }
            
        } catch (SQLException e) {
            // Error silencioso al guardar puntaje en ranking
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error silencioso al cerrar conexión
            }
        }
        
        return false;
    }
    
    /**
     * Verifica si un usuario ya existe en el ranking
     */
    private static boolean existeUsuarioEnRanking(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return false;
              String sql = "SELECT COUNT(*) FROM ranking WHERE usuario_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            // Error silencioso al verificar usuario en ranking
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error silencioso al cerrar conexión
            }
        }
        
        return false;
    }
      /**
     * Obtiene el mejor puntaje actual de un usuario (método interno)
     */
    private static int obtenerMejorPuntajeInterno(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return 0;
              String sql = "SELECT mejor_puntaje FROM ranking WHERE usuario_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("mejor_puntaje");
            }
            
        } catch (SQLException e) {
            // Error silencioso al obtener mejor puntaje
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error silencioso al cerrar conexión
            }
        }
        
        return 0;
    }
    
    /**
     * Actualiza el puntaje de un usuario existente
     */
    private static boolean actualizarPuntaje(int userId, int nuevoPuntaje) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return false;
              String sql = "UPDATE ranking SET mejor_puntaje = ?, fecha_mejor_puntaje = NOW() WHERE usuario_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, nuevoPuntaje);
            stmt.setInt(2, userId);
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Puntaje actualizado silenciosamente
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar puntaje: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    /**
     * Obtiene el ranking de los mejores jugadores que completaron las 5 fórmulas
     * @param limite Número máximo de resultados (ej: top 10)
     * @return Lista de objetos RankingEntry
     */
    public static List<RankingEntry> obtenerTopRanking(int limite) {
        List<RankingEntry> ranking = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) {
                System.err.println("Error: No se pudo establecer la conexión a la base de datos.");
                return ranking;
            }
              String sql = "SELECT r.usuario_id, u.username, r.mejor_puntaje, r.fecha_mejor_puntaje " +
                        "FROM ranking r " +
                        "INNER JOIN usuarios u ON r.usuario_id = u.id " +
                        "ORDER BY r.mejor_puntaje DESC " +
                        "LIMIT ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limite);
            
            rs = stmt.executeQuery();
            
            int posicion = 1;            while (rs.next()) {
                RankingEntry entry = new RankingEntry(
                    posicion++,
                    rs.getInt("usuario_id"),
                    rs.getString("username"),
                    rs.getInt("mejor_puntaje"),
                    rs.getTimestamp("fecha_mejor_puntaje")
                );
                ranking.add(entry);
            }
            
            // Ranking obtenido silenciosamente
            
        } catch (SQLException e) {
            System.err.println("Error al obtener ranking: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
        
        return ranking;
    }
    
    /**
     * Obtiene la posición de un usuario específico en el ranking
     * @param userId ID del usuario
     * @return La posición en el ranking (1-based) o -1 si no está en el ranking
     */
    public static int obtenerPosicionUsuario(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return -1;
            
            // Primero verificar si el usuario existe en el ranking
            String checkSql = "SELECT mejor_puntaje FROM ranking WHERE usuario_id = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                // El usuario no está en el ranking
                return -1;
            }
            
            int userScore = rs.getInt("mejor_puntaje");
            rs.close();
            stmt.close();
            
            // Ahora calcular la posición contando cuántos usuarios tienen mejor puntaje
            String positionSql = "SELECT COUNT(*) + 1 as posicion " +
                               "FROM ranking " +
                               "WHERE mejor_puntaje > ?";
            
            stmt = conn.prepareStatement(positionSql);
            stmt.setInt(1, userScore);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("posicion");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener posición del usuario: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
        
        return -1;
    }
    
    /**
     * Obtiene el total de jugadores que han completado todas las fórmulas
     */
    public static int obtenerTotalJugadoresCompletos() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return 0;
            
            String sql = "SELECT COUNT(*) FROM ranking_completo";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener total de jugadores: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
        
        return 0;
    }
    
    /**
     * Obtiene el mejor puntaje de un usuario específico
     */
    public static int obtenerMejorPuntaje(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return 0;
              String sql = "SELECT mejor_puntaje FROM ranking WHERE usuario_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("mejor_puntaje");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener mejor puntaje: " + e.getMessage());
        } finally {
            ConexionDB.cerrarRecursos(conn, stmt, rs);
        }
        
        return 0;
    }
    
    /**
     * Obtiene la fecha de completación de un usuario
     */
    public static java.sql.Timestamp obtenerFechaCompletacion(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return null;
              String sql = "SELECT fecha_mejor_puntaje FROM ranking WHERE usuario_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getTimestamp("fecha_mejor_puntaje");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener fecha de completación: " + e.getMessage());
        } finally {
            ConexionDB.cerrarRecursos(conn, stmt, rs);
        }
        
        return null;
    }
    
}
