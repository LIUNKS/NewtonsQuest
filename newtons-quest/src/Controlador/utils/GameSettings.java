package Controlador.utils;

import java.io.*;
import java.util.Properties;

/**
 * Clase para manejar las configuraciones del juego.
 * Guarda y carga configuraciones como volumen, brillo, etc.
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
                    
                    System.out.println("Configuraciones cargadas desde " + SETTINGS_FILE);
                }
            } else {
                System.out.println("Archivo de configuración no encontrado. Usando valores por defecto.");
                saveSettings(); // Crear archivo con valores por defecto
            }
        } catch (Exception e) {
            System.err.println("Error al cargar configuraciones: " + e.getMessage());
            // Usar valores por defecto en caso de error
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
                System.out.println("Configuraciones guardadas en " + SETTINGS_FILE);
            }
        } catch (Exception e) {
            System.err.println("Error al guardar configuraciones: " + e.getMessage());
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
        System.out.println("Configuraciones restauradas a valores por defecto");
    }
}
