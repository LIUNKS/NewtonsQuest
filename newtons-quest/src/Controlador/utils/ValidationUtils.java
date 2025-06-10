package Controlador.utils;

import Controlador.constants.GameConstants;
import java.util.regex.Pattern;

/**
 * Utilidades para validación de datos de entrada del usuario.
 * Centraliza todas las reglas de validación del juego.
 */
public class ValidationUtils {
    
    // Patrones de validación
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    // Prevenir instanciación
    private ValidationUtils() {}
    
    /**
     * Valida un nombre de usuario
     */
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.error("El nombre de usuario no puede estar vacío");
        }
        
        String trimmedUsername = username.trim();
        
        if (trimmedUsername.length() < GameConstants.MIN_USERNAME_LENGTH) {
            return ValidationResult.error(
                String.format("El nombre de usuario debe tener al menos %d caracteres", 
                GameConstants.MIN_USERNAME_LENGTH)
            );
        }
        
        if (trimmedUsername.length() > GameConstants.MAX_USERNAME_LENGTH) {
            return ValidationResult.error(
                String.format("El nombre de usuario no puede tener más de %d caracteres", 
                GameConstants.MAX_USERNAME_LENGTH)
            );
        }
        
        if (!USERNAME_PATTERN.matcher(trimmedUsername).matches()) {
            return ValidationResult.error(
                "El nombre de usuario solo puede contener letras, números y guiones bajos"
            );
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Valida una contraseña
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.error("La contraseña no puede estar vacía");
        }
        
        if (password.length() < GameConstants.MIN_PASSWORD_LENGTH) {
            return ValidationResult.error(
                String.format("La contraseña debe tener al menos %d caracteres", 
                GameConstants.MIN_PASSWORD_LENGTH)
            );
        }
        
        // Verificar que tenga al menos una letra y un número
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        
        if (!hasLetter || !hasDigit) {
            return ValidationResult.error(
                "La contraseña debe contener al menos una letra y un número"
            );
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Valida un email (opcional para registro completo)
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return ValidationResult.error("El email no puede estar vacío");
        }
        
        String trimmedEmail = email.trim();
        
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            return ValidationResult.error("El formato del email no es válido");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Valida que dos contraseñas coincidan
     */
    public static ValidationResult validatePasswordMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return ValidationResult.error("Las contraseñas no pueden estar vacías");
        }
        
        if (!password.equals(confirmPassword)) {
            return ValidationResult.error("Las contraseñas no coinciden");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Valida un puntaje del juego
     */
    public static ValidationResult validateScore(int score) {
        if (score < 0) {
            return ValidationResult.error("El puntaje no puede ser negativo");
        }
        
        // Límite razonable para detectar posibles hacks
        if (score > 100000) {
            return ValidationResult.error("Puntaje sospechosamente alto");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Valida el nivel del juego
     */
    public static ValidationResult validateLevel(int level) {
        if (level < 1) {
            return ValidationResult.error("El nivel debe ser mayor a 0");
        }
        
        if (level > 100) {
            return ValidationResult.error("Nivel máximo excedido");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Clase interna para resultados de validación
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        @Override
        public String toString() {
            return valid ? "VALID" : "INVALID: " + errorMessage;
        }
    }
}
