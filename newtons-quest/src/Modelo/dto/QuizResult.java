package Modelo;

/**
 * Clase que representa el resultado de un quiz.
 * Almacena información sobre el desempeño del usuario en el quiz.
 */
public class QuizResult {
    
    private int userId;
    private int totalQuestions;
    private int correctAnswers;
    private int incorrectAnswers;
    private double percentage;
    private long timeSpent; // Tiempo en segundos
    private boolean passed; // true si obtuvo >= 85%
    
    /**
     * Constructor para crear un resultado de quiz
     * @param userId ID del usuario que realizó el quiz
     * @param totalQuestions Número total de preguntas
     * @param correctAnswers Número de respuestas correctas
     * @param timeSpent Tiempo total empleado en segundos
     */
    public QuizResult(int userId, int totalQuestions, int correctAnswers, long timeSpent) {
        this.userId = userId;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = totalQuestions - correctAnswers;
        this.timeSpent = timeSpent;
        this.percentage = (double) correctAnswers / totalQuestions * 100;
        this.passed = percentage >= 85.0;
    }
    
    // Getters
    public int getUserId() {
        return userId;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
    
    public int getCorrectAnswers() {
        return correctAnswers;
    }
    
    public int getIncorrectAnswers() {
        return incorrectAnswers;
    }
    
    public double getPercentage() {
        return percentage;
    }
    
    public long getTimeSpent() {
        return timeSpent;
    }
    
    public boolean isPassed() {
        return passed;
    }
    
    /**
     * Obtiene el tiempo formateado en minutos y segundos
     * @return Tiempo en formato "MM:SS"
     */
    public String getFormattedTime() {
        long minutes = timeSpent / 60;
        long seconds = timeSpent % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Obtiene el porcentaje formateado
     * @return Porcentaje con un decimal
     */
    public String getFormattedPercentage() {
        return String.format("%.1f%%", percentage);
    }
    
    /**
     * Obtiene el estado del quiz (Aprobado/Reprobado)
     * @return Estado del quiz
     */
    public String getStatus() {
        return passed ? "Aprobado" : "Reprobado";
    }
    
    /**
     * Obtiene una calificación en letras basada en el porcentaje
     * @return Calificación (A, B, C, D, F)
     */
    public String getLetterGrade() {
        if (percentage >= 90) return "A";
        else if (percentage >= 80) return "B";
        else if (percentage >= 70) return "C";
        else if (percentage >= 60) return "D";
        else return "F";
    }
    
    @Override
    public String toString() {
        return "QuizResult{" +
                "userId=" + userId +
                ", correctAnswers=" + correctAnswers +
                "/" + totalQuestions +
                ", percentage=" + getFormattedPercentage() +
                ", timeSpent=" + getFormattedTime() +
                ", passed=" + passed +
                '}';
    }
}
