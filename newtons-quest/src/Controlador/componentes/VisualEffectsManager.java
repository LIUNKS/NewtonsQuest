package Controlador.componentes;

import Controlador.utils.GameSettings;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;

/**
 * Gestor de efectos visuales.
 * 
 * Esta clase se encarga de administrar los efectos visuales aplicados a la interfaz del juego,
 * permitiendo ajustar configuraciones como el brillo, contraste y otros efectos visuales.
 * 
 * Funcionalidades principales:
 * - Ajuste de brillo de la escena del juego
 * - Persistencia de la configuración visual entre sesiones
 * - Restauración de valores por defecto
 * - Adaptación a cambios de escena
 * 
 * Los efectos visuales se aplican a nivel de escena, afectando a todos los elementos
 * visuales mostrados en pantalla durante el juego.
 */
public class VisualEffectsManager {
    
    private Scene gameScene;      // Escena del juego a la que se aplican los efectos
    private ColorAdjust colorAdjust; // Efecto de ajuste de color (brillo, contraste, etc.)
    private GameSettings gameSettings; // Configuración del juego donde se almacenan las preferencias
    
    /**
     * Constructor del gestor de efectos visuales.
     * 
     * Inicializa el gestor con una escena específica del juego y carga
     * la configuración actual de efectos visuales desde el sistema de configuración.
     * 
     * @param gameScene La escena del juego a la que se aplicarán los efectos visuales
     */
    public VisualEffectsManager(Scene gameScene) {
        this.gameScene = gameScene;
        this.gameSettings = GameSettings.getInstance();
        initializeEffects();
    }
    
    /**
     * Inicializa los efectos visuales básicos.
     * 
     * Este método crea un efecto ColorAdjust, configura el brillo según la 
     * configuración actual y lo aplica a la escena del juego.
     */
    private void initializeEffects() {
        colorAdjust = new ColorAdjust();
        updateBrightness();
        
        if (gameScene != null && gameScene.getRoot() != null) {
            gameScene.getRoot().setEffect(colorAdjust);
        }
    }
    
    /**
     * Actualiza el nivel de brillo basado en la configuración actual.
     * 
     * Este método:
     * - Obtiene el valor de brillo de la configuración del juego
     * - Convierte el valor al rango que espera JavaFX (-1.0 a 1.0)
     * - Aplica el brillo al efecto visual
     * - Asegura que el valor está dentro de límites seguros
     */
    public void updateBrightness() {
        if (colorAdjust != null) {
            double brightness = gameSettings.getBrightness();
            // Convertir de escala 0.1-2.0 a escala -1.0 a 1.0 para JavaFX
            double fxBrightness = (brightness - 1.0);
            fxBrightness = Math.max(-0.9, Math.min(1.0, fxBrightness));
            
            colorAdjust.setBrightness(fxBrightness);
        }
    }
    
    /**
     * Establece manualmente el nivel de brillo.
     * 
     * Actualiza tanto la configuración persistente como el efecto visual aplicado.
     * 
     * @param brightness Valor de brillo entre 0.1 (muy oscuro) y 2.0 (muy brillante),
     *                  donde 1.0 representa el brillo normal
     */
    public void setBrightness(double brightness) {
        gameSettings.setBrightness(brightness);
        updateBrightness();
    }
    
    /**
     * Obtiene el nivel de brillo actual configurado.
     * 
     * @return Valor de brillo actual entre 0.1 y 2.0
     */
    public double getBrightness() {
        return gameSettings.getBrightness();
    }
    
    /**
     * Actualiza la escena a la que se aplican los efectos visuales.
     * 
     * Este método es útil cuando se cambia de una escena a otra durante el juego
     * y se desea mantener la configuración de efectos visuales.
     * 
     * @param newScene Nueva escena a la que aplicar los efectos visuales
     */
    public void updateScene(Scene newScene) {
        this.gameScene = newScene;
        if (gameScene != null && gameScene.getRoot() != null && colorAdjust != null) {
            gameScene.getRoot().setEffect(colorAdjust);
        }
    }
    
    /**
     * Restaura todos los efectos visuales a sus valores predeterminados.
     * 
     * Este método:
     * - Resetea todos los ajustes del efecto ColorAdjust a cero
     * - Establece el brillo al valor neutral (1.0) en la configuración
     * - Útil para cuando el usuario quiere restablecer la configuración visual
     */
    public void resetEffects() {
        if (colorAdjust != null) {
            colorAdjust.setBrightness(0.0);
            colorAdjust.setContrast(0.0);
            colorAdjust.setSaturation(0.0);
            colorAdjust.setHue(0.0);
        }
        gameSettings.setBrightness(1.0);
    }
}
