package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para manejar operaciones relacionadas con el quiz.
 * Maneja las preguntas del quiz y los resultados de los usuarios.
 */
public class QuizDAO {
    
    /**
     * Obtiene todas las preguntas del quiz
     * @return Lista de preguntas del quiz
     */
    public static List<QuizQuestion> obtenerTodasLasPreguntas() {
        List<QuizQuestion> preguntas = new ArrayList<>();
        
        // Por ahora, las preguntas están hardcodeadas
        // En el futuro se podrían almacenar en la base de datos
        preguntas.add(new QuizQuestion(1, 
            "¿Qué es la energía cinética?",
            new String[]{
                "Es la energía que un objeto tiene cuando está en reposo.",
                "Es la energía que un objeto tiene debido a su movimiento.",
                "Es la energía que se libera cuando algo explota.",
                "Es la energía almacenada en una batería."
            },
            1, // Respuesta correcta: B
            "Piensa en lo que hace que una bicicleta se mueva.",
            "La energía cinética es la energía asociada al movimiento de un objeto."
        ));
        
        preguntas.add(new QuizQuestion(2,
            "¿Qué es la energía potencial?",
            new String[]{
                "Es la energía que un objeto tiene por estar caliente.",
                "Es la energía que un objeto tiene debido a su movimiento.",
                "Es la energía que un objeto tiene debido a su posición o altura.",
                "Es la energía que hace que las luces se enciendan."
            },
            2, // Respuesta correcta: C
            "Imagina una pelota en lo alto de una colina.",
            "La energía potencial es energía almacenada debido a la posición de un objeto en un campo de fuerza, como la gravedad."
        ));
        
        preguntas.add(new QuizQuestion(3,
            "¿Qué significa la aceleración en física?",
            new String[]{
                "Moverse a una velocidad constante.",
                "Cambiar la velocidad o la dirección de un objeto.",
                "Estar completamente quieto.",
                "Ser muy rápido."
            },
            1, // Respuesta correcta: B
            "Piensa en lo que hace un coche cuando arranca o frena.",
            "Aceleración es cualquier cambio en la velocidad o dirección del movimiento."
        ));
        
        preguntas.add(new QuizQuestion(4,
            "¿Cuál es la diferencia principal entre masa y peso?",
            new String[]{
                "La masa es lo mismo que el peso.",
                "La masa cambia si vas a otro planeta, pero el peso no.",
                "La masa es la cantidad de 'materia' en algo, y el peso es la fuerza de la gravedad sobre esa masa.",
                "El peso es la cantidad de 'materia' en algo, y la masa es la fuerza de la gravedad."
            },
            2, // Respuesta correcta: C
            "Piensa en lo que te haría pesar menos en la Luna.",
            "La masa es constante; el peso varía según la gravedad."
        ));
        
        preguntas.add(new QuizQuestion(5,
            "¿Cómo calculas la velocidad media de un objeto?",
            new String[]{
                "Multiplicando la distancia por el tiempo.",
                "Dividiendo el tiempo por la distancia.",
                "Dividiendo la distancia recorrida por el tiempo que tardó.",
                "Restando el tiempo de la distancia."
            },
            2, // Respuesta correcta: C
            "Recuerda la fórmula 'v = d / t'.",
            "La velocidad media se obtiene dividiendo la distancia entre el tiempo."
        ));
        
        preguntas.add(new QuizQuestion(6,
            "¿Cuál es la unidad de medida para la energía cinética y la energía potencial?",
            new String[]{
                "Metros por segundo (m/s).",
                "Kilogramos (kg).",
                "Julios (Joules).",
                "Newtons (N)."
            },
            2, // Respuesta correcta: C
            "Es una unidad que también se usa para medir trabajo.",
            "El julio (J) es la unidad estándar para medir energía."
        ));
        
        preguntas.add(new QuizQuestion(7,
            "¿Qué le sucede a la energía potencial de un objeto cuando lo levantas más alto?",
            new String[]{
                "Disminuye.",
                "Se mantiene igual.",
                "Aumenta.",
                "Se convierte en energía cinética."
            },
            2, // Respuesta correcta: C
            "Piensa en la energía de una pelota antes de caer.",
            "Cuanta más altura, mayor energía potencial gravitatoria."
        ));
        
        preguntas.add(new QuizQuestion(8,
            "Si un objeto se mueve a velocidad constante y en línea recta, ¿está acelerando?",
            new String[]{
                "Sí, porque siempre hay una fuerza actuando sobre él.",
                "No, porque su velocidad y dirección no cambian.",
                "Solo si se mueve muy rápido.",
                "Solo si está cayendo."
            },
            1, // Respuesta correcta: B
            "La aceleración implica cambio, ¿lo hay?",
            "Sin cambio de velocidad ni dirección, no hay aceleración."
        ));
        
        preguntas.add(new QuizQuestion(9,
            "¿Qué fuerza nos mantiene 'pegados' al suelo?",
            new String[]{
                "La fuerza del viento.",
                "La fuerza magnética.",
                "La fuerza de gravedad.",
                "La fuerza de fricción."
            },
            2, // Respuesta correcta: C
            "Es la fuerza que hace que las cosas caigan.",
            "La gravedad es la atracción entre masas, como tú y la Tierra."
        ));
        
        preguntas.add(new QuizQuestion(10,
            "¿Cuál es la energía que tiene un objeto por estar en movimiento?",
            new String[]{
                "Energía potencial.",
                "Energía térmica.",
                "Energía cinética.",
                "Energía química."
            },
            2, // Respuesta correcta: C
            "Piensa en el opuesto de la energía almacenada.",
            "La energía cinética depende del movimiento."
        ));
        
        preguntas.add(new QuizQuestion(11,
            "Si una mochila tiene una masa de 2 kg y la gravedad en la Tierra es 10 m/s², ¿cuál es su peso?",
            new String[]{
                "2 Newtons",
                "5 Newtons",
                "20 Newtons",
                "10 Newtons"
            },
            2, // Respuesta correcta: C
            "Usa la fórmula: F = m × g",
            "F = 2 kg × 10 m/s² = 20 N"
        ));
        
        preguntas.add(new QuizQuestion(12,
            "Si un caracol recorre 5 metros en 10 segundos, ¿cuál es su velocidad media?",
            new String[]{
                "0.5 m/s",
                "2 m/s",
                "50 m/s",
                "10 m/s"
            },
            0, // Respuesta correcta: A
            "v = d / t",
            "v = 5 m / 10 s = 0.5 m/s"
        ));
        
        preguntas.add(new QuizQuestion(13,
            "Un pájaro de 0.5 kg está en una rama a 4 m de altura. ¿Cuánta energía potencial tiene?",
            new String[]{
                "20 Julios",
                "2 Julios",
                "4 Julios",
                "50 Julios"
            },
            0, // Respuesta correcta: A
            "U = m × g × h",
            "U = 0.5 × 10 × 4 = 20 J"
        ));
        
        preguntas.add(new QuizQuestion(14,
            "Una pelota de 2 kg rueda a 2 m/s. ¿Cuánta energía cinética tiene?",
            new String[]{
                "2 Julios",
                "4 Julios",
                "8 Julios",
                "16 Julios"
            },
            1, // Respuesta correcta: B
            "K = ½ × m × v²",
            "K = ½ × 2 × (2)² = 4 J"
        ));
        
        preguntas.add(new QuizQuestion(15,
            "Un coche de juguete pasa de 0 m/s a 10 m/s en 5 segundos. ¿Cuál fue su aceleración?",
            new String[]{
                "0.5 m/s²",
                "2 m/s²",
                "10 m/s²",
                "50 m/s²"
            },
            1, // Respuesta correcta: B
            "a = (vf - vi) / t",
            "a = (10 - 0) / 5 = 2 m/s²"
        ));
        
        return preguntas;
    }
    
    /**
     * Guarda el resultado de un quiz en la base de datos
     * @param result Resultado del quiz a guardar
     * @return true si se guardó correctamente, false en caso contrario
     */
    public static boolean guardarResultadoQuiz(QuizResult result) {
        String sql = "INSERT INTO quiz_results (user_id, total_questions, correct_answers, " +
                    "incorrect_answers, percentage, time_spent, passed, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, result.getUserId());
            stmt.setInt(2, result.getTotalQuestions());
            stmt.setInt(3, result.getCorrectAnswers());
            stmt.setInt(4, result.getIncorrectAnswers());
            stmt.setDouble(5, result.getPercentage());
            stmt.setLong(6, result.getTimeSpent());
            stmt.setBoolean(7, result.isPassed());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar resultado del quiz: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene el mejor resultado de quiz de un usuario
     * @param userId ID del usuario
     * @return Mejor resultado del usuario o null si no tiene resultados
     */
    public static QuizResult obtenerMejorResultadoUsuario(int userId) {
        String sql = "SELECT * FROM quiz_results WHERE user_id = ? ORDER BY percentage DESC, created_at DESC LIMIT 1";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new QuizResult(
                    rs.getInt("user_id"),
                    rs.getInt("total_questions"),
                    rs.getInt("correct_answers"),
                    rs.getLong("time_spent")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener mejor resultado del quiz: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene todos los resultados de quiz de un usuario
     * @param userId ID del usuario
     * @return Lista de resultados del usuario
     */
    public static List<QuizResult> obtenerResultadosUsuario(int userId) {
        List<QuizResult> resultados = new ArrayList<>();
        String sql = "SELECT * FROM quiz_results WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                QuizResult result = new QuizResult(
                    rs.getInt("user_id"),
                    rs.getInt("total_questions"),
                    rs.getInt("correct_answers"),
                    rs.getLong("time_spent")
                );
                resultados.add(result);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener resultados del quiz: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultados;
    }
    
    /**
     * Verifica si un usuario ha aprobado el quiz (>= 85%)
     * @param userId ID del usuario
     * @return true si el usuario ha aprobado al menos una vez
     */
    public static boolean usuarioHaAprobado(int userId) {
        String sql = "SELECT COUNT(*) FROM quiz_results WHERE user_id = ? AND passed = true";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar aprobación del quiz: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
