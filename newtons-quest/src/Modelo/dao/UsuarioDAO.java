package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class UsuarioDAO {    // Método para registrar un nuevo usuario
    public static boolean registrarUsuario(String username, String password) {
        return registrarUsuario(username, password, "", "");
    }
    
    // Método sobrecargado para registrar un nuevo usuario con información completa
    public static boolean registrarUsuario(String username, String password, String nombreCompleto, String email) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Obtener conexión
            conn = ConexionDB.getConnection();

            // Verificar si la conexión se estableció correctamente
            if (conn == null) {
                System.err.println("Error: No se pudo establecer la conexión a la base de datos.");
                return false;
            }

            // Verificar si el usuario ya existe
            if (existeUsuario(username)) {
                return false;
            }

            // Hashear la contraseña
            String hashedPassword = hashPassword(password);

            // Preparar la consulta SQL con todas las columnas
            String sql = "INSERT INTO usuarios (username, password, nombre_completo, correo, fecha_registro, activo) VALUES (?, ?, ?, ?, NOW(), 1)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, nombreCompleto != null ? nombreCompleto : "");
            stmt.setString(4, email != null ? email : "");

            // Ejecutar la consulta
            int filasAfectadas = stmt.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar recursos
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    // Método para verificar si un usuario existe
    public static boolean existeUsuario(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Obtener conexión
            conn = ConexionDB.getConnection();

            // Verificar si la conexión se estableció correctamente
            if (conn == null) {
                System.err.println("Error: No se pudo establecer la conexión a la base de datos.");
                return false;
            }

            // Preparar la consulta SQL
            String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            // Ejecutar la consulta
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
            e.printStackTrace();
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
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    // Método para validar credenciales
    public static boolean validarCredenciales(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Obtener conexión
            conn = ConexionDB.getConnection();

            // Verificar si la conexión se estableció correctamente
            if (conn == null) {
                System.err.println("Error: No se pudo establecer la conexión a la base de datos.");
                return false;
            }

            // Preparar la consulta SQL
            String sql = "SELECT password FROM usuarios WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            // Ejecutar la consulta
            rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                // Verificar si la contraseña coincide
                boolean passwordMatch = verificarPassword(password, hashedPassword);

                // Imprimir información de depuración
                //System.out.println("Verificando contraseña para usuario: " + username);
                //System.out.println("Contraseña proporcionada hash: " + hashPassword(password));
                //System.out.println("Contraseña almacenada hash: " + hashedPassword);
                //System.out.println("¿Coinciden? " + passwordMatch);

                return passwordMatch;
            } else {
                System.out.println("No se encontró el usuario en la base de datos: " + username);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
            e.printStackTrace();
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
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    // Método para obtener el ID de un usuario
    public static int obtenerIdUsuario(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Obtener conexión
            conn = ConexionDB.getConnection();

            // Verificar si la conexión se estableció correctamente
            if (conn == null) {
                System.err.println("Error: No se pudo establecer la conexión a la base de datos.");
                return -1;
            }            // Preparar la consulta SQL
            String sql = "SELECT id FROM usuarios WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            // Ejecutar la consulta
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

            return -1; // Usuario no encontrado

        } catch (SQLException e) {
            System.err.println("Error al obtener ID de usuario: " + e.getMessage());
            e.printStackTrace();
            return -1;
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
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Obtiene el nombre completo de un usuario
     */    public static String obtenerNombreCompleto(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return null;
            
            String sql = "SELECT nombre_completo, username FROM usuarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                String nombreCompleto = rs.getString("nombre_completo");
                // Si no hay nombre completo, devolver el username
                return (nombreCompleto != null && !nombreCompleto.trim().isEmpty()) ? 
                       nombreCompleto : rs.getString("username");
            }
              } catch (SQLException e) {
            System.err.println("Error al obtener nombre completo: " + e.getMessage());
        } finally {
            ConexionDB.cerrarRecursos(conn, stmt, rs);
        }
        
        // Si no se pudo obtener de la base de datos, devolver "Usuario desconocido"
        return "Usuario desconocido";
    }
    
    /**
     * Obtiene el correo electrónico de un usuario
     */
    public static String obtenerCorreo(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return null;
            
            String sql = "SELECT correo FROM usuarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
              if (rs.next()) {
                String email = rs.getString("correo");
                return email != null ? email : "";
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener correo: " + e.getMessage());
        } finally {
            ConexionDB.cerrarRecursos(conn, stmt, rs);
        }
        
        return "";
    }
    
    /**
     * Obtiene la fecha de registro de un usuario
     */
    public static String obtenerFechaRegistro(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {            conn = ConexionDB.getConnection();
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
            System.err.println("Error al obtener fecha de registro: " + e.getMessage());
        } finally {
            ConexionDB.cerrarRecursos(conn, stmt, rs);
        }
        
        return "Fecha no disponible";
    }
    
    /**
     * Actualiza el perfil de un usuario (nombre completo y correo)
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
            System.err.println("Error al actualizar perfil de usuario: " + e.getMessage());
            return false;
        } finally {
            ConexionDB.cerrarRecursos(conn, stmt, null);
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
            
            // Primero verificar el puntaje actual para no sobreescribir con uno menor
            int puntajeActual = obtenerMejorPuntajeUsuario(userId);
            int puntajeFinal = Math.max(puntajeActual, mejorPuntaje);
            
            // También verificar fórmulas actuales para no retroceder
            int formulasActuales = obtenerFormulasCompletadasUsuario(userId);
            int formulasFinal = Math.max(formulasActuales, formulasCompletadas);
            
            System.out.println("=== ACTUALIZANDO PROGRESO USUARIO " + userId + " ===");
            System.out.println("Puntaje actual: " + puntajeActual + " -> Nuevo puntaje: " + mejorPuntaje + " -> Final: " + puntajeFinal);
            System.out.println("Fórmulas actuales: " + formulasActuales + " -> Nuevas fórmulas: " + formulasCompletadas + " -> Final: " + formulasFinal);
            
            String sql = "UPDATE usuarios SET mejor_puntaje = ?, formulas_completadas = ?, ultima_partida = NOW() WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, puntajeFinal);
            stmt.setInt(2, formulasFinal);
            stmt.setInt(3, userId);
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Progreso actualizado para usuario " + userId + ": Puntaje=" + puntajeFinal + ", Fórmulas=" + formulasFinal);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar progreso del usuario: " + e.getMessage());
            return false;
        } finally {
            ConexionDB.cerrarRecursos(conn, stmt, null);
        }
        
        return false;
    }
    
    /**
     * Obtiene el mejor puntaje de un usuario
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
            System.err.println("Error al obtener mejor puntaje del usuario: " + e.getMessage());
        } finally {
            ConexionDB.cerrarRecursos(conn, stmt, rs);
        }
        
        return 0;
    }
    
    /**
     * Obtiene el número de fórmulas completadas por un usuario
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
            System.err.println("Error al obtener fórmulas completadas del usuario: " + e.getMessage());
        } finally {
            ConexionDB.cerrarRecursos(conn, stmt, rs);
        }
        
        return 0;
    }
    
    /**
     * Obtiene la fecha de la última partida de un usuario
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
            System.err.println("Error al obtener última partida del usuario: " + e.getMessage());
        } finally {
            ConexionDB.cerrarRecursos(conn, stmt, rs);
        }
        
        return "Nunca";
    }

    // Método para hashear contraseñas usando SHA-256
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            // Añadir ceros a la izquierda si es necesario
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error al hashear contraseña: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Método para verificar si una contraseña coincide con su hash
    private static boolean verificarPassword(String password, String hashedPassword) {
        String hashedInput = hashPassword(password);
        return hashedInput != null && hashedInput.equals(hashedPassword);
    }
    
    /**
     * Sincroniza los datos del ranking con la tabla de usuarios
     * Este método corrige las discrepancias entre ambas tablas
     */
    public static boolean sincronizarDatosRankingAUsuarios(int userId) {
        Connection conn = null;
        PreparedStatement stmtRanking = null;
        PreparedStatement stmtUsuarios = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return false;
            
            // Obtener datos del ranking
            String sqlRanking = "SELECT mejor_puntaje FROM ranking WHERE usuario_id = ?";
            stmtRanking = conn.prepareStatement(sqlRanking);
            stmtRanking.setInt(1, userId);
            rs = stmtRanking.executeQuery();
            
            if (rs.next()) {
                int puntajeRanking = rs.getInt("mejor_puntaje");
                
                // Actualizar tabla usuarios con los datos del ranking
                // Si el usuario está en ranking, significa que completó las 5 fórmulas
                String sqlUsuarios = "UPDATE usuarios SET mejor_puntaje = ?, formulas_completadas = 5 WHERE id = ?";
                stmtUsuarios = conn.prepareStatement(sqlUsuarios);
                stmtUsuarios.setInt(1, puntajeRanking);
                stmtUsuarios.setInt(2, userId);
                
                int filasActualizadas = stmtUsuarios.executeUpdate();
                
                if (filasActualizadas > 0) {
                    System.out.println("Datos sincronizados para usuario " + userId + ": puntaje=" + puntajeRanking + ", fórmulas=5");
                    return true;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al sincronizar datos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmtRanking != null) stmtRanking.close();
                if (stmtUsuarios != null) stmtUsuarios.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    /**
     * Sincroniza todos los datos del ranking con la tabla de usuarios
     * Útil para corregir discrepancias masivas
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
            
            // Obtener todos los usuarios del ranking
            String sqlRanking = "SELECT usuario_id, mejor_puntaje FROM ranking";
            stmtRanking = conn.prepareStatement(sqlRanking);
            rs = stmtRanking.executeQuery();
            
            // Preparar statement para actualizar usuarios
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
                    System.out.println("Usuario " + userId + " sincronizado: puntaje=" + puntajeRanking + ", fórmulas=5");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al sincronizar todos los datos: " + e.getMessage());
        } finally {
            ConexionDB.cerrarRecursos(conn, stmtRanking, rs);
            if (stmtUsuarios != null) {
                try {
                    stmtUsuarios.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar statement: " + e.getMessage());
                }
            }
        }
        
        return usuariosSincronizados;
    }
}
