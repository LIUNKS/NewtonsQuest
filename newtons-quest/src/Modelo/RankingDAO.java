package Modelo;

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
            System.out.println("El usuario no ha completado todas las fórmulas aún");
            return false;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) {
                System.err.println("Error: No se pudo establecer la conexión a la base de datos.");
                return false;
            }
            
            // Verificar si el usuario ya tiene un registro
            if (existeUsuarioEnRanking(userId)) {
                // Actualizar solo si el nuevo puntaje es mayor
                int puntajeActual = obtenerMejorPuntaje(userId);
                if (score > puntajeActual) {
                    return actualizarPuntaje(userId, score);
                } else {
                    System.out.println("El puntaje actual (" + puntajeActual + ") es mayor o igual al nuevo (" + score + ")");
                    return true; // No es error, simplemente no se actualiza
                }
            } else {
                // Insertar nuevo registro
                String sql = "INSERT INTO ranking_completo (id_usuario, mejor_puntaje, formulas_completadas, fecha_completado) VALUES (?, ?, ?, NOW())";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userId);
                stmt.setInt(2, score);
                stmt.setInt(3, formulasCompleted);
                
                int filasAfectadas = stmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    System.out.println("¡Nuevo jugador añadido al ranking! Usuario: " + userId + ", Puntaje: " + score);
                    return true;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar puntaje en ranking: " + e.getMessage());
            e.printStackTrace();
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
     * Verifica si un usuario ya existe en el ranking
     */
    private static boolean existeUsuarioEnRanking(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return false;
            
            String sql = "SELECT COUNT(*) FROM ranking_completo WHERE id_usuario = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar usuario en ranking: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    /**
     * Obtiene el mejor puntaje actual de un usuario
     */
    private static int obtenerMejorPuntaje(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return 0;
            
            String sql = "SELECT mejor_puntaje FROM ranking_completo WHERE id_usuario = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("mejor_puntaje");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener mejor puntaje: " + e.getMessage());
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
     * Actualiza el puntaje de un usuario existente
     */
    private static boolean actualizarPuntaje(int userId, int nuevoPuntaje) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return false;
            
            String sql = "UPDATE ranking_completo SET mejor_puntaje = ?, fecha_completado = NOW() WHERE id_usuario = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, nuevoPuntaje);
            stmt.setInt(2, userId);
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("¡Puntaje actualizado! Usuario: " + userId + ", Nuevo puntaje: " + nuevoPuntaje);
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
            
            String sql = "SELECT r.id_usuario, u.username, r.mejor_puntaje, r.fecha_completado " +
                        "FROM ranking_completo r " +
                        "INNER JOIN usuarios u ON r.id_usuario = u.id_usuario " +
                        "ORDER BY r.mejor_puntaje DESC " +
                        "LIMIT ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limite);
            
            rs = stmt.executeQuery();
            
            int posicion = 1;
            while (rs.next()) {
                RankingEntry entry = new RankingEntry(
                    posicion++,
                    rs.getInt("id_usuario"),
                    rs.getString("username"),
                    rs.getInt("mejor_puntaje"),
                    rs.getTimestamp("fecha_completado")
                );
                ranking.add(entry);
            }
            
            System.out.println("Ranking obtenido con " + ranking.size() + " jugadores");
            
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
            
            String sql = "SELECT COUNT(*) + 1 as posicion " +
                        "FROM ranking_completo r1 " +
                        "WHERE r1.mejor_puntaje > (" +
                        "    SELECT r2.mejor_puntaje " +
                        "    FROM ranking_completo r2 " +
                        "    WHERE r2.id_usuario = ?" +
                        ")";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
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
}
