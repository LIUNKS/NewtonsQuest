package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class UsuarioDAO {

    // Método para registrar un nuevo usuario
    public static boolean registrarUsuario(String username, String password) {
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

            // Preparar la consulta SQL
            String sql = "INSERT INTO usuarios (username, password) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

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
            }

            // Preparar la consulta SQL
            String sql = "SELECT id_usuario FROM usuarios WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            // Ejecutar la consulta
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_usuario");
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
}
