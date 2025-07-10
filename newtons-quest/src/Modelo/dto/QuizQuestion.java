package Modelo.dto;

/**
 * Clase que representa una pregunta del quiz.
 * Contiene toda la información necesaria para una pregunta de opción múltiple.
 */
public class QuizQuestion {
    
    private int id;
    private String question;
    private String[] options;
    private int correctAnswerIndex; // Índice de la respuesta correcta (0-3)
    private String hint;
    private String explanation;
    
    /**
     * Constructor completo para una pregunta del quiz
     * @param id Identificador único de la pregunta
     * @param question Enunciado de la pregunta
     * @param options Array de 4 opciones (A, B, C, D)
     * @param correctAnswerIndex Índice de la respuesta correcta (0-3)
     * @param hint Pista para ayudar al usuario
     * @param explanation Explicación de la respuesta correcta
     */
    public QuizQuestion(int id, String question, String[] options, int correctAnswerIndex, String hint, String explanation) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.hint = hint;
        this.explanation = explanation;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public String[] getOptions() {
        return options;
    }
    
    public String getOption(int index) {
        if (index >= 0 && index < options.length) {
            return options[index];
        }
        return "";
    }
    
    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
    
    public String getCorrectAnswer() {
        return options[correctAnswerIndex];
    }
    
    public String getHint() {
        return hint;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    /**
     * Verifica si una respuesta es correcta
     * @param selectedIndex Índice de la opción seleccionada
     * @return true si la respuesta es correcta, false en caso contrario
     */
    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctAnswerIndex;
    }
    
    /**
     * Obtiene la letra de la opción (A, B, C, D)
     * @param index Índice de la opción
     * @return Letra correspondiente a la opción
     */
    public static String getOptionLetter(int index) {
        switch (index) {
            case 0: return "A";
            case 1: return "B";
            case 2: return "C";
            case 3: return "D";
            default: return "";
        }
    }
    
    @Override
    public String toString() {
        return "QuizQuestion{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", correctAnswerIndex=" + correctAnswerIndex +
                '}';
    }
}
