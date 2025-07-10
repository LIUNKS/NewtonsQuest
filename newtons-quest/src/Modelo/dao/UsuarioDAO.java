package Modelo.dao;

import Modelo.ConexionDB;
import Modelo.dto.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

/**
 * Data Access Object para la gestión de usuarios.
 * 
 * Proporciona operaciones CRUD completas para usuarios incluyendo registro,
 * autenticación, gestión de perfiles y sincronización con datos de ranking.
 * Todos los métodos son estáticos y manejan sus propias conexiones a BD.
 * 
 * @author Johann
 * @version 1.0
 */
public class UsuarioDAO {
    
    /**
     * Registra un nuevo usuario con username y contraseña.
     * @param username Nombre de usuario único
     * @param password Contraseña del usuario
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public static boolean registrarUsuario(String username, String password) {
        return registrarUsuario(username, password, "", "");
    }
    
    /**
     * Registra un nuevo usuario con información completa.
     * @param username Nombre de usuario único
     * @param password Contraseña del usuario
     * @param nombreCompleto Nombre completo del usuario
     * @param email Correo electrónico del usuario
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public static boolean registrarUsuario(String username, String password, String nombreCompleto, String email) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConexionDB.getConnection();
            if (conn == null) {
                return false;
            }

            if (existeUsuario(username)) {
                return false;
            }

            String hashedPassword = hashPassword(password);
            String sql = "INSERT INTO usuarios (username, password, nombre_completo, correo, fecha_registro, activo) VALUES (?, ?, ?, ?, NOW(), 1)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, nombreCompleto != null ? nombreCompleto : "");
            stmt.setString(4, email != null ? email : "");

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
    }

    /**
     * Verifica si un usuario existe en la base de datos.
     * @param username Nombre de usuario a verificar
     * @return true si el usuario existe, false en caso contrario
     */
    public static boolean existeUsuario(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            if (conn == null) {
                return false;
            }

            String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            return false;
        } finally {
            // Cerrar recursos
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
    }

    /**
     * Valida las credenciales de un usuario.
     * @param username Nombre de usuario
     * @param password Contraseña del usuario
     * @return true si las credenciales son válidas, false en caso contrario
     */
    public static boolean validarCredenciales(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            if (conn == null) {
                return false;
            }

            String sql = "SELECT password FROM usuarios WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return verificarPassword(password, hashedPassword);
            } else {
                return false;
            }

        } catch (SQLException e) {
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
    }

    /**
     * Obtiene el ID de un usuario basado en su nombre de usuario.
     * @param username Nombre de usuario
     * @return ID del usuario o -1 si no se encuentra
     */
    public static int obtenerIdUsuario(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            if (conn == null) {
                return -1;
            }
            
            String sql = "SELECT id FROM usuarios WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;

        } catch (SQLException e) {
            return -1;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
    }

    /**
     * Obtiene el nombre completo de un usuario.
     * @param userId ID del usuario
     * @return Nombre completo del usuario o username si no está disponible
     */
    public static String obtenerNombreCompleto(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return "Usuario desconocido";
            
            String sql = "SELECT nombre_completo, username FROM usuarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                String nombreCompleto = rs.getString("nombre_completo");
                return (nombreCompleto != null && !nombreCompleto.trim().isEmpty()) ? 
                       nombreCompleto : rs.getString("username");
            }
        } catch (SQLException e) {
            // Error al obtener nombre completo
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
        return "Usuario desconocido";
    }
    
    /**
     * Obtiene el correo electrónico de un usuario.
     * @param userId ID del usuario
     * @return Correo electrónico del usuario o cadena vacía si no está disponible
     */
    public static String obtenerCorreo(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return "";
            
            String sql = "SELECT correo FROM usuarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                String email = rs.getString("correo");
                return email != null ? email : "";
            }
        } catch (SQLException e) {
            // Error al obtener correo
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
        return "";
    }
    
    /**
     * Obtiene la fecha de registro de un usuario.
     * @param userId ID del usuario
     * @return Fecha de registro formateada o "Fecha no disponible"
     */
    public static String obtenerFechaRegistro(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return "Fecha no disponible";
            
            String sql = "SELECT fecha_registro FROM usuarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                java.sql.Timestamp timestamp = rs.getTimestamp("fecha_registro");
                if (timestamp != null) {
                    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                    return formatter.format(timestamp);
                }
            }
        } catch (SQLException e) {
            // Error al obtener fecha de registro
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
        return "Fecha no disponible";
    }
    
    /**
     * Actualiza el perfil de un usuario (nombre completo y correo).
     * @param userId ID del usuario
     * @param nombreCompleto Nuevo nombre completo
     * @param correo Nuevo correo electrónico
     * @return true si la actualización fue exitosa
     */
    public static boolean actualizarPerfilUsuario(int userId, String nombreCompleto, String correo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return false;
            
            String sql = "UPDATE usuarios SET nombre_completo = ?, correo = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombreCompleto.isEmpty() ? null : nombreCompleto);
            stmt.setString(2, correo.isEmpty() ? null : correo);
            stmt.setInt(3, userId);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
    }
    
    /**
     * Actualiza el progreso del usuario (mejor puntaje y fórmulas completadas)
     * @param userId ID del usuario
     * @param mejorPuntaje Mejor puntaje obtenido
     * @param formulasCompletadas Número de fórmulas completadas (0-5)
     * @return true si se actualizó exitosamente
     */
    public static boolean actualizarProgresoUsuario(int userId, int mejorPuntaje, int formulasCompletadas) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return false;
            
            int puntajeActual = obtenerMejorPuntajeUsuario(userId);
            int puntajeFinal = Math.max(puntajeActual, mejorPuntaje);
            
            int formulasActuales = obtenerFormulasCompletadasUsuario(userId);
            int formulasFinal = Math.max(formulasActuales, formulasCompletadas);
            
            String sql = "UPDATE usuarios SET mejor_puntaje = ?, formulas_completadas = ?, ultima_partida = NOW() WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, puntajeFinal);
            stmt.setInt(2, formulasFinal);
            stmt.setInt(3, userId);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
    }
    
    /**
     * Obtiene el mejor puntaje de un usuario.
     * @param userId ID del usuario
     * @return Mejor puntaje del usuario
     */
    public static int obtenerMejorPuntajeUsuario(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return 0;
            
            String sql = "SELECT mejor_puntaje FROM usuarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("mejor_puntaje");
            }
        } catch (SQLException e) {
            // Error al obtener mejor puntaje
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
        return 0;
    }
    
    /**
     * Obtiene el número de fórmulas completadas por un usuario.
     * @param userId ID del usuario
     * @return Número de fórmulas completadas
     */
    public static int obtenerFormulasCompletadasUsuario(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return 0;
            
            String sql = "SELECT formulas_completadas FROM usuarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("formulas_completadas");
            }
        } catch (SQLException e) {
            // Error al obtener fórmulas completadas
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
        return 0;
    }
    
    /**
     * Obtiene la fecha de la última partida de un usuario.
     * @param userId ID del usuario
     * @return Fecha de la última partida formateada o "Nunca"
     */
    public static String obtenerUltimaPartidaUsuario(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return "Nunca";
            
            String sql = "SELECT ultima_partida FROM usuarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                java.sql.Timestamp timestamp = rs.getTimestamp("ultima_partida");
                if (timestamp != null) {
                    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                    return formatter.format(timestamp);
                }
            }
        } catch (SQLException e) {
            // Error al obtener última partida
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
        return "Nunca";
    }

    /**
     * Genera un hash SHA-256 de la contraseña proporcionada.
     * @param password Contraseña a hashear
     * @return Hash de la contraseña o null si hay error
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Verifica si una contraseña coincide con su hash.
     * @param password Contraseña en texto plano
     * @param hashedPassword Hash almacenado
     * @return true si coinciden, false en caso contrario
     */
    private static boolean verificarPassword(String password, String hashedPassword) {
        String hashedInput = hashPassword(password);
        return hashedInput != null && hashedInput.equals(hashedPassword);
    }
    
    /**
     * Sincroniza los datos del ranking con la tabla de usuarios.
     * Este método corrige las discrepancias entre ambas tablas.
     * @param userId ID del usuario a sincronizar
     * @return true si la sincronización fue exitosa
     */
    public static boolean sincronizarDatosRankingAUsuarios(int userId) {
        Connection conn = null;
        PreparedStatement stmtRanking = null;
        PreparedStatement stmtUsuarios = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return false;
            
            String sqlRanking = "SELECT mejor_puntaje FROM ranking WHERE usuario_id = ?";
            stmtRanking = conn.prepareStatement(sqlRanking);
            stmtRanking.setInt(1, userId);
            rs = stmtRanking.executeQuery();
            
            if (rs.next()) {
                int puntajeRanking = rs.getInt("mejor_puntaje");
                
                String sqlUsuarios = "UPDATE usuarios SET mejor_puntaje = ?, formulas_completadas = 5 WHERE id = ?";
                stmtUsuarios = conn.prepareStatement(sqlUsuarios);
                stmtUsuarios.setInt(1, puntajeRanking);
                stmtUsuarios.setInt(2, userId);
                
                int filasActualizadas = stmtUsuarios.executeUpdate();
                return filasActualizadas > 0;
            }
        } catch (SQLException e) {
            // Error al sincronizar datos de ranking a usuarios
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmtRanking != null) stmtRanking.close();
                if (stmtUsuarios != null) stmtUsuarios.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
        return false;
    }
    
    /**
     * Sincroniza todos los datos del ranking con la tabla de usuarios.
     * Útil para corregir discrepancias masivas.
     * @return Número de usuarios sincronizados
     */
    public static int sincronizarTodosLosDatosRanking() {
        Connection conn = null;
        PreparedStatement stmtRanking = null;
        PreparedStatement stmtUsuarios = null;
        ResultSet rs = null;
        int usuariosSincronizados = 0;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return 0;
            
            String sqlRanking = "SELECT usuario_id, mejor_puntaje FROM ranking";
            stmtRanking = conn.prepareStatement(sqlRanking);
            rs = stmtRanking.executeQuery();
            
            String sqlUsuarios = "UPDATE usuarios SET mejor_puntaje = ?, formulas_completadas = 5 WHERE id = ? AND (mejor_puntaje < ? OR formulas_completadas < 5)";
            stmtUsuarios = conn.prepareStatement(sqlUsuarios);
            
            while (rs.next()) {
                int userId = rs.getInt("usuario_id");
                int puntajeRanking = rs.getInt("mejor_puntaje");
                
                stmtUsuarios.setInt(1, puntajeRanking);
                stmtUsuarios.setInt(2, userId);
                stmtUsuarios.setInt(3, puntajeRanking);
                
                int filasActualizadas = stmtUsuarios.executeUpdate();
                if (filasActualizadas > 0) {
                    usuariosSincronizados++;
                }
            }
        } catch (SQLException e) {
            // Error al sincronizar datos de ranking a usuarios
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmtRanking != null) stmtRanking.close();
                if (stmtUsuarios != null) stmtUsuarios.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
        return usuariosSincronizados;
    }
    
    /**
     * Obtiene los datos del jugador a partir de su ID.
     * @param userId ID del usuario
     * @return Objeto Player con los datos del usuario o null si no existe
     */
    public static Player obtenerDatosJugador(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            if (conn == null) {
                return null;
            }

            String sql = "SELECT id, username, nombre_completo, correo FROM usuarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                try {
                    Player player = new Player();
                    
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String nombreCompleto = rs.getString("nombre_completo");
                    String correo = rs.getString("correo");
                    
                    player.setId(id);
                    player.setUsername(username);
                    player.setFullName(nombreCompleto);
                    player.setEmail(correo);
                    
                    return player;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;

        } catch (SQLException e) {
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Error al cerrar recursos
            }
        }
    }
}
