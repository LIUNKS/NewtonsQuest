package Controlador.componentes;

import Modelo.QuizDAO;
import Modelo.QuizQuestion;
import Modelo.QuizResult;
import Controlador.utils.SessionManager;
import java.util.Collections;
import java.util.List;

/**
 * Manager para gestionar la lógica del quiz.
 * Maneja las preguntas, resultados y estadísticas del quiz.
 */
public class QuizManager {
    
    private static QuizManager instance;
    
    private QuizManager() {}
    
    /**
     * Obtiene la instancia única del QuizManager (Singleton)
     * @return Instancia del QuizManager
     */
    public static QuizManager getInstance() {
        if (instance == null) {
            instance = new QuizManager();
        }
        return instance;
    }
    
    /**
     * Obtiene todas las preguntas del quiz
     * @return Lista de preguntas del quiz
     */
    public List<QuizQuestion> obtenerPreguntas() {
        return QuizDAO.obtenerTodasLasPreguntas();
    }
    
    /**
     * Obtiene las preguntas del quiz mezcladas aleatoriamente
     * @return Lista de preguntas mezcladas
     */
    public List<QuizQuestion> obtenerPreguntasMezcladas() {
        List<QuizQuestion> preguntas = QuizDAO.obtenerTodasLasPreguntas();
        Collections.shuffle(preguntas);
        return preguntas;
    }
    
    /**
     * Guarda el resultado de un quiz
     * @param totalPreguntas Número total de preguntas
     * @param correctas Número de respuestas correctas
     * @param tiempoSegundos Tiempo empleado en segundos
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean guardarResultado(int totalPreguntas, int correctas, long tiempoSegundos) {
        SessionManager sessionManager = SessionManager.getInstance();
        
        if (!sessionManager.isLoggedIn()) {
            System.err.println("No hay usuario logueado para guardar el resultado");
            return false;
        }
        
        int userId = sessionManager.getCurrentUserId();
        QuizResult resultado = new QuizResult(userId, totalPreguntas, correctas, tiempoSegundos);
        
        return QuizDAO.guardarResultadoQuiz(resultado);
    }
    
    /**
     * Obtiene el mejor resultado del usuario actual
     * @return Mejor resultado del usuario o null si no tiene resultados
     */
    public QuizResult obtenerMejorResultadoUsuarioActual() {
        SessionManager sessionManager = SessionManager.getInstance();
        
        if (!sessionManager.isLoggedIn()) {
            return null;
        }
        
        int userId = sessionManager.getCurrentUserId();
        return QuizDAO.obtenerMejorResultadoUsuario(userId);
    }
    
    /**
     * Obtiene todos los resultados del usuario actual
     * @return Lista de resultados del usuario
     */
    public List<QuizResult> obtenerResultadosUsuarioActual() {
        SessionManager sessionManager = SessionManager.getInstance();
        
        if (!sessionManager.isLoggedIn()) {
            return Collections.emptyList();
        }
        
        int userId = sessionManager.getCurrentUserId();
        return QuizDAO.obtenerResultadosUsuario(userId);
    }
    
    /**
     * Verifica si el usuario actual ha aprobado el quiz
     * @return true si el usuario ha aprobado al menos una vez
     */
    public boolean usuarioActualHaAprobado() {
        SessionManager sessionManager = SessionManager.getInstance();
        
        if (!sessionManager.isLoggedIn()) {
            return false;
        }
        
        int userId = sessionManager.getCurrentUserId();
        return QuizDAO.usuarioHaAprobado(userId);
    }
    
    /**
     * Obtiene estadísticas del quiz para el usuario actual
     * @return Estadísticas del quiz
     */
    public QuizStats obtenerEstadisticasUsuarioActual() {
        List<QuizResult> resultados = obtenerResultadosUsuarioActual();
        
        if (resultados.isEmpty()) {
            return new QuizStats(0, 0, 0, 0, false);
        }
        
        int totalQuizzes = resultados.size();
        int aprobados = 0;
        double mejorPorcentaje = 0;
        long tiempoTotal = 0;
        
        for (QuizResult resultado : resultados) {
            if (resultado.isPassed()) {
                aprobados++;
            }
            if (resultado.getPercentage() > mejorPorcentaje) {
                mejorPorcentaje = resultado.getPercentage();
            }
            tiempoTotal += resultado.getTimeSpent();
        }
        
        double tiempoPromedio = (double) tiempoTotal / totalQuizzes;
        
        return new QuizStats(totalQuizzes, aprobados, mejorPorcentaje, tiempoPromedio, aprobados > 0);
    }
    
    /**
     * Clase interna para estadísticas del quiz
     */
    public static class QuizStats {
        private final int totalQuizzes;
        private final int aprobados;
        private final double mejorPorcentaje;
        private final double tiempoPromedio;
        private final boolean haAprobado;
        
        public QuizStats(int totalQuizzes, int aprobados, double mejorPorcentaje, 
                        double tiempoPromedio, boolean haAprobado) {
            this.totalQuizzes = totalQuizzes;
            this.aprobados = aprobados;
            this.mejorPorcentaje = mejorPorcentaje;
            this.tiempoPromedio = tiempoPromedio;
            this.haAprobado = haAprobado;
        }
        
        public int getTotalQuizzes() { return totalQuizzes; }
        public int getAprobados() { return aprobados; }
        public double getMejorPorcentaje() { return mejorPorcentaje; }
        public double getTiempoPromedio() { return tiempoPromedio; }
        public boolean isHaAprobado() { return haAprobado; }
        
        public String getMejorPorcentajeFormatted() {
            return String.format("%.1f%%", mejorPorcentaje);
        }
        
        public String getTiempoPromedioFormatted() {
            long minutos = (long) tiempoPromedio / 60;
            long segundos = (long) tiempoPromedio % 60;
            return String.format("%02d:%02d", minutos, segundos);
        }
    }
}
