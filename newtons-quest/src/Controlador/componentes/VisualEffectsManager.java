package Controlador.componentes;

import Controlador.utils.GameSettings;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;

/**
 * Gestor de efectos visuales del juego como brillo, contraste, etc.
 */
public class VisualEffectsManager {
    
    private Scene gameScene;
    private ColorAdjust colorAdjust;
    private GameSettings gameSettings;
    
    /**
     * Constructor del VisualEffectsManager
     * @param gameScene La escena del juego a la que aplicar efectos
     */
    public VisualEffectsManager(Scene gameScene) {
        this.gameScene = gameScene;
        this.gameSettings = GameSettings.getInstance();
        initializeEffects();
    }
    
    /**
     * Inicializa los efectos visuales
     */
    private void initializeEffects() {
        colorAdjust = new ColorAdjust();
        updateBrightness();
        
        if (gameScene != null && gameScene.getRoot() != null) {
            gameScene.getRoot().setEffect(colorAdjust);
        }
        
        // VisualEffectsManager inicializado
    }
    
    /**
     * Actualiza el brillo basado en la configuración actual
     */
    public void updateBrightness() {
        if (colorAdjust != null) {
            double brightness = gameSettings.getBrightness();
            // Convertir de escala 0.1-2.0 a escala -1.0 a 1.0 para JavaFX
            double fxBrightness = (brightness - 1.0);
            fxBrightness = Math.max(-0.9, Math.min(1.0, fxBrightness));
            
            colorAdjust.setBrightness(fxBrightness);
            // Brillo actualizado
        }
    }
    
    /**
     * Establece el brillo manualmente
     * @param brightness Valor entre 0.1 y 2.0
     */
    public void setBrightness(double brightness) {
        gameSettings.setBrightness(brightness);
        updateBrightness();
    }
    
    /**
     * Obtiene el brillo actual
     */
    public double getBrightness() {
        return gameSettings.getBrightness();
    }
    
    /**
     * Actualiza la escena asociada (útil si la escena cambia)
     */
    public void updateScene(Scene newScene) {
        this.gameScene = newScene;
        if (gameScene != null && gameScene.getRoot() != null && colorAdjust != null) {
            gameScene.getRoot().setEffect(colorAdjust);
        }
    }
    
    /**
     * Restaura todos los efectos visuales a valores por defecto
     */
    public void resetEffects() {
        if (colorAdjust != null) {
            colorAdjust.setBrightness(0.0);
            colorAdjust.setContrast(0.0);
            colorAdjust.setSaturation(0.0);
            colorAdjust.setHue(0.0);
        }
        gameSettings.setBrightness(1.0);
        // Efectos visuales restaurados a valores por defecto
    }
}
