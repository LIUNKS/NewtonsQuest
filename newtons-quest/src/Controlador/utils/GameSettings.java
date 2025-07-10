package Controlador.utils;

import java.io.*;
import java.util.Properties;

/**
 * Gestor de configuraciones del juego Newton's Apple Quest.
 * 
 * Esta clase singleton se encarga de gestionar todas las configuraciones
 * del juego, incluyendo persistencia de datos en archivo de propiedades.
 * 
 * Configuraciones gestionadas:
 * - Volumen de música de fondo (0.0 - 1.0)
 * - Volumen de efectos de sonido (0.0 - 1.0)  
 * - Brillo de pantalla (0.0 - 2.0)
 * - Modo pantalla completa (habilitado/deshabilitado)
 * 
 * Las configuraciones se guardan automáticamente en un archivo de propiedades
 * llamado "newtons_quest_settings.properties" en el directorio del juego.
 * Al iniciar la aplicación, las configuraciones se cargan desde el archivo,
 * o se utilizan valores por defecto si el archivo no existe.
 * 
 * Implementa el patrón Singleton para garantizar configuraciones únicas
 * y consistentes en toda la aplicación.
 */
public class GameSettings {
    
    private static final String SETTINGS_FILE = "newtons_quest_settings.properties";
    private static GameSettings instance;
    
    // Configuraciones por defecto
    private double musicVolume = 0.5;
    private double effectVolume = 0.7;
    private double brightness = 1.0;
    private boolean fullscreen = false;
    
    private Properties properties;
    
    /**
     * Constructor privado para el patrón Singleton
     */
    private GameSettings() {
        properties = new Properties();
        loadSettings();
    }
    
    /**
     * Obtiene la instancia única de GameSettings
     */
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    
    /**
     * Carga las configuraciones desde el archivo
     */
    private void loadSettings() {
        try {
            File settingsFile = new File(SETTINGS_FILE);
            if (settingsFile.exists()) {
                try (FileInputStream fis = new FileInputStream(settingsFile)) {
                    properties.load(fis);
                    
                    // Cargar valores del archivo
                    musicVolume = Double.parseDouble(properties.getProperty("musicVolume", "0.5"));
                    effectVolume = Double.parseDouble(properties.getProperty("effectVolume", "0.7"));
                    brightness = Double.parseDouble(properties.getProperty("brightness", "1.0"));
                    fullscreen = Boolean.parseBoolean(properties.getProperty("fullscreen", "false"));
                    
                    // Configuraciones cargadas exitosamente
                }
            } else {
                // Usar valores por defecto si no existe el archivo
                saveSettings(); // Crear archivo con valores por defecto
            }
        } catch (Exception e) {
            // Error silencioso al cargar configuraciones - usar valores por defecto
        }
    }
    
    /**
     * Guarda las configuraciones en el archivo
     */
    public void saveSettings() {
        try {
            properties.setProperty("musicVolume", String.valueOf(musicVolume));
            properties.setProperty("effectVolume", String.valueOf(effectVolume));
            properties.setProperty("brightness", String.valueOf(brightness));
            properties.setProperty("fullscreen", String.valueOf(fullscreen));
            
            try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
                properties.store(fos, "Newton's Apple Quest - Configuraciones del juego");
                // Configuraciones guardadas exitosamente
            }
        } catch (Exception e) {
            // Error silencioso al guardar configuraciones
        }
    }
    
    // Getters y setters
    public double getMusicVolume() {
        return musicVolume;
    }
    
    public void setMusicVolume(double musicVolume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, musicVolume));
    }
    
    public double getEffectVolume() {
        return effectVolume;
    }
    
    public void setEffectVolume(double effectVolume) {
        this.effectVolume = Math.max(0.0, Math.min(1.0, effectVolume));
    }
    
    public double getBrightness() {
        return brightness;
    }
    
    public void setBrightness(double brightness) {
        this.brightness = Math.max(0.1, Math.min(2.0, brightness));
    }
    
    public boolean isFullscreen() {
        return fullscreen;
    }
    
    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }
    
    /**
     * Restaura todas las configuraciones a sus valores por defecto
     */
    public void resetToDefaults() {
        musicVolume = 0.5;
        effectVolume = 0.7;
        brightness = 1.0;
        fullscreen = false;
        saveSettings();
        // Configuraciones restauradas a valores por defecto
    }
}
