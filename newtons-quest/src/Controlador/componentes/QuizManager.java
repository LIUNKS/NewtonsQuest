package Controlador.componentes;

import Modelo.dao.QuizDAO;
import Modelo.dto.QuizQuestion;
import Modelo.dto.QuizResult;
import Controlador.utils.SessionManager;
import java.util.Collections;
import java.util.List;

/**
 * Gestor del sistema de quiz y evaluaciones.
 * 
 * Esta clase controla toda la lógica relacionada con los quizzes educativos:
 * 
 *   - Obtención y mezcla aleatoria de preguntas
 *   - Guardado y recuperación de resultados de quiz
 *   - Cálculo de estadísticas y logros del usuario
 *   - Verificación de aprobación y progreso
 *   - Formateo de datos para la interfaz de usuario
 * 
 * Implementa el patrón Singleton para garantizar una única instancia
 * y mantener la consistencia de los datos del quiz durante la sesión.
 */

public class QuizManager {
    
    // ================================================================================================
    // INSTANCIA SINGLETON
    // ================================================================================================
    
    /** Instancia única del gestor de quiz */
    private static QuizManager instance;
    
    // ================================================================================================
    // CONSTRUCTORES Y PATRÓN SINGLETON
    // ================================================================================================
    
    /** Constructor privado para implementar patrón Singleton */
    private QuizManager() {}
    
    /**
     * Obtiene la instancia única del gestor de quiz.
     * @return Instancia única del QuizManager
     */
    public static QuizManager getInstance() {
        if (instance == null) {
            instance = new QuizManager();
        }
        return instance;
    }
    
    // ================================================================================================
    // GESTIÓN DE PREGUNTAS
    // ================================================================================================
    
    /**
     * Obtiene todas las preguntas del quiz.
     * @return Lista completa de preguntas del quiz
     */
    public List<QuizQuestion> obtenerPreguntas() {
        return QuizDAO.obtenerTodasLasPreguntas();
    }
    
    /**
     * Obtiene las preguntas del quiz mezcladas aleatoriamente.
     * @return Lista de preguntas en orden aleatorio
     */
    public List<QuizQuestion> obtenerPreguntasMezcladas() {
        List<QuizQuestion> preguntas = QuizDAO.obtenerTodasLasPreguntas();
        Collections.shuffle(preguntas);
        return preguntas;
    }
    
    // ================================================================================================
    // GESTIÓN DE RESULTADOS
    // ================================================================================================
    
    /**
     * Guarda el resultado de un quiz completado.
     * @param totalPreguntas Número total de preguntas del quiz
     * @param correctas Número de respuestas correctas
     * @param tiempoSegundos Tiempo empleado en segundos
     * @return true si se guardó correctamente, false si no hay usuario logueado
     */
    public boolean guardarResultado(int totalPreguntas, int correctas, long tiempoSegundos) {
        SessionManager sessionManager = SessionManager.getInstance();
        
        if (!sessionManager.isLoggedIn()) {
            return false;
        }
        
        int userId = sessionManager.getCurrentUserId();
        QuizResult resultado = new QuizResult(userId, totalPreguntas, correctas, tiempoSegundos);
        
        return QuizDAO.guardarResultadoQuiz(resultado);
    }
    
    /**
     * Obtiene el mejor resultado del usuario actual.
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
     * Obtiene todos los resultados del usuario actual.
     * @return Lista de resultados del usuario o lista vacía si no está logueado
     */
    public List<QuizResult> obtenerResultadosUsuarioActual() {
        SessionManager sessionManager = SessionManager.getInstance();
        
        if (!sessionManager.isLoggedIn()) {
            return Collections.emptyList();
        }
        
        int userId = sessionManager.getCurrentUserId();
        return QuizDAO.obtenerResultadosUsuario(userId);
    }
    
    // ================================================================================================
    // VERIFICACIÓN DE LOGROS
    // ================================================================================================
    
    /**
     * Verifica si el usuario actual ha aprobado el quiz.
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
    
    // ================================================================================================
    // ESTADÍSTICAS DEL USUARIO
    // ================================================================================================
    
    /**
     * Obtiene estadísticas completas del quiz para el usuario actual.
     * @return Estadísticas del quiz o estadísticas vacías si no hay resultados
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
    
    // ================================================================================================
    // CLASE INTERNA - ESTADÍSTICAS DEL QUIZ
    // ================================================================================================
    
    /**
     * Clase que encapsula las estadísticas del quiz de un usuario.
     * Proporciona métodos de acceso y formateo de datos estadísticos.
     */
    public static class QuizStats {
        private final int totalQuizzes;
        private final int aprobados;
        private final double mejorPorcentaje;
        private final double tiempoPromedio;
        private final boolean haAprobado;
        
        /**
         * Constructor de estadísticas del quiz.
         * @param totalQuizzes Total de quizzes realizados
         * @param aprobados Número de quizzes aprobados
         * @param mejorPorcentaje Mejor porcentaje obtenido
         * @param tiempoPromedio Tiempo promedio por quiz
         * @param haAprobado Si ha aprobado al menos una vez
         */
        public QuizStats(int totalQuizzes, int aprobados, double mejorPorcentaje, 
                        double tiempoPromedio, boolean haAprobado) {
            this.totalQuizzes = totalQuizzes;
            this.aprobados = aprobados;
            this.mejorPorcentaje = mejorPorcentaje;
            this.tiempoPromedio = tiempoPromedio;
            this.haAprobado = haAprobado;
        }
        
        /** @return Total de quizzes realizados */
        public int getTotalQuizzes() { return totalQuizzes; }
        
        /** @return Número de quizzes aprobados */
        public int getAprobados() { return aprobados; }
        
        /** @return Mejor porcentaje obtenido */
        public double getMejorPorcentaje() { return mejorPorcentaje; }
        
        /** @return Tiempo promedio por quiz */
        public double getTiempoPromedio() { return tiempoPromedio; }
        
        /** @return true si ha aprobado al menos una vez */
        public boolean isHaAprobado() { return haAprobado; }
        
        /** @return Mejor porcentaje formateado como string */
        public String getMejorPorcentajeFormatted() {
            return String.format("%.1f%%", mejorPorcentaje);
        }
        
        /** @return Tiempo promedio formateado como MM:SS */
        public String getTiempoPromedioFormatted() {
            long minutos = (long) tiempoPromedio / 60;
            long segundos = (long) tiempoPromedio % 60;
            return String.format("%02d:%02d", minutos, segundos);
        }
    }
}
