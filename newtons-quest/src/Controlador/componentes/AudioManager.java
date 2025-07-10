package Controlador.componentes;

import Controlador.utils.GameSettings;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Gestor de audio.
 * 
 * Esta clase centraliza toda la gestión de audio del juego, incluyendo:
 * 
 *   - Reproducción de música de fondo en bucle
 *   - Efectos de sonido para eventos del juego
 *   - Control de volumen independiente para música y efectos
 *   - Gestión de estados de reproducción (play, pause, stop)
 * 
 * El gestor utiliza JavaFX MediaPlayer para la reproducción de audio
 * y se integra con el sistema de configuración del juego para mantener
 * los niveles de volumen preferidos por el usuario.
 */
public class AudioManager {
    
    // =====================================
    // ATRIBUTOS DE INSTANCIA
    // =====================================
    
    /** Reproductor para música de fondo */
    private MediaPlayer musicPlayer;
    
    /** Reproductor para efectos de sonido */
    private MediaPlayer effectPlayer;
    
    // =====================================
    // RUTAS DE RECURSOS
    // =====================================
    
    /** Ruta local de la música de fondo del juego */
    private final String MUSIC_PATH = "src/recursos/musica/musica_juego.mp3";
    
    /** Ruta local del sonido de desbloqueo de fórmulas */
    private final String UNLOCK_SOUND_PATH = "src/recursos/sonidos/unlock.mp3";
    
    // =====================================
    // CONSTRUCTOR
    // =====================================
    
    /**
     * Constructor por defecto del gestor de audio.
     * 
     * Inicializa el gestor sin cargar ningún archivo de audio.
     * Los recursos se cargan bajo demanda cuando se solicita su reproducción.
     */
    public AudioManager() {
        // Gestor de audio inicializado y listo para usar
    }
    
    // =====================================
    // MÉTODOS PÚBLICOS - MÚSICA DE FONDO
    // =====================================
    
    /**
     * Inicia la reproducción de música de fondo del juego.
     * 
     * Carga y reproduce el archivo de música en bucle infinito,
     * aplicando el volumen configurado en las preferencias del usuario.
     * Si ya hay música reproduciéndose, la detiene antes de iniciar la nueva.
     *
     * Comportamiento:
     * - Reproducción en bucle continuo
     * - Volumen según configuración del usuario
     */
    public void playBackgroundMusic() {
        try {
            // Detener música anterior si existe
            if (musicPlayer != null) {
                musicPlayer.stop();
            }
            
            // Cargar archivo de música desde ruta de desarrollo
            File musicFile = new File(MUSIC_PATH);
            Media media = new Media(musicFile.toURI().toString());
            
            // Configurar reproductor
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Reproducción en bucle
            musicPlayer.setVolume(GameSettings.getInstance().getMusicVolume());
            musicPlayer.play();
            
        } catch (Exception e) {
            // Manejar errores en desarrollo
        }
    }
    
    /**
     * Reproduce el efecto de sonido de desbloqueo de fórmula.
     *
     * Se reproduce cuando el jugador alcanza suficientes puntos para
     * desbloquear una nueva fórmula de Newton. Si hay otro efecto
     * reproduciéndose, lo detiene antes de iniciar el nuevo.
     *
     * Características:
     * - Reproducción única (no en bucle)
     * - Volumen según configuración de efectos
     * - Interrupción de efectos previos
     */
    public void playUnlockSound() {
        try {
            // Detener efecto anterior si existe
            if (effectPlayer != null) {
                effectPlayer.stop();
            }
            
            // Cargar archivo de sonido desde ruta de desarrollo
            File soundFile = new File(UNLOCK_SOUND_PATH);
            Media media = new Media(soundFile.toURI().toString());
            
            // Configurar y reproducir efecto
            effectPlayer = new MediaPlayer(media);
            effectPlayer.setVolume(GameSettings.getInstance().getEffectVolume());
            effectPlayer.play();
            
        } catch (Exception e) {
            // Manejar errores silenciosamente en desarrollo
        }
    }
    
    // =====================================
    // MÉTODOS PÚBLICOS - CONTROL DE VOLUMEN
    // =====================================
    
    /**
     * Detiene completamente la reproducción de música de fondo.
     * 
     * Útil cuando el jugador sale del juego o desea silenciar
     * completamente el audio.
     */
    public void stopBackgroundMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }
    
    /**
     * Pausa temporalmente la música de fondo.
     * La música puede reanudarse desde el mismo punto usando
     * {@link #resumeBackgroundMusic()}.
     */
    public void pauseBackgroundMusic() {
        if (musicPlayer != null) {
            musicPlayer.pause();
        }
    }
    
    /**
     * Reanuda la reproducción de música pausada.
     * {@link #pauseBackgroundMusic()}.
     * Continúa la reproducción desde el punto donde fue pausada.
     */
    public void resumeBackgroundMusic() {
        if (musicPlayer != null) {
            musicPlayer.play();
        }
    }
    
    // =====================================
    // MÉTODOS PÚBLICOS - EFECTOS DE SONIDO
    // =====================================
    
    /**
     * Ajusta el volumen de la música de fondo.
     * 
     * <El valor se normaliza automáticamente al rango válido [0.0, 1.0].
     * Los cambios se aplican inmediatamente si hay música reproduciéndose.
     * 
     * @param volume Nivel de volumen entre 0.0 (silencio) y 1.0 (máximo)
     */
    public void setMusicVolume(double volume) {
        if (musicPlayer != null) {
            musicPlayer.setVolume(Math.max(0.0, Math.min(1.0, volume)));
        }
    }
    
    /**
     * Ajusta el volumen de los efectos de sonido.
     * 
     * El valor se normaliza automáticamente al rango válido [0.0, 1.0].
     * Los cambios se aplican al próximo efecto que se reproduzca.
     * 
     * @param volume Nivel de volumen entre 0.0 (silencio) y 1.0 (máximo)
     */
    public void setEffectVolume(double volume) {
        if (effectPlayer != null) {
            effectPlayer.setVolume(Math.max(0.0, Math.min(1.0, volume)));
        }
    }
    
    /**
     * Obtiene el nivel de volumen actual de la música de fondo.
     * 
     * @return Volumen actual entre 0.0 y 1.0, o 0.0 si no hay música cargada
     */
    public double getMusicVolume() {
        return musicPlayer != null ? musicPlayer.getVolume() : 0.0;
    }
    
    /**
     * Obtiene el nivel de volumen actual de los efectos de sonido.
     * 
     * @return Volumen actual entre 0.0 y 1.0, o 0.0 si no hay efectos cargados
     */
    public double getEffectVolume() {
        return effectPlayer != null ? effectPlayer.getVolume() : 0.0;
    }
}
