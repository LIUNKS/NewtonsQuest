package Controlador.componentes;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Clase encargada de gestionar todo el audio del juego.
 * Separa la lógica de reproducción de sonidos del controlador principal.
 */
public class AudioManager {
    
    private MediaPlayer musicPlayer;
    private MediaPlayer effectPlayer;
    
    // Rutas de los recursos
    private final String MUSIC_PATH = "src/recursos/musica/musica_juego.mp3";
    private final String UNLOCK_SOUND_PATH = "src/recursos/sonidos/unlock.mp3";
    
    /**
     * Constructor del AudioManager
     */
    public AudioManager() {
        System.out.println("AudioManager inicializado");
    }
    
    /**
     * Reproduce la música de fondo del juego
     */
    public void playBackgroundMusic() {
        try {
            File musicFile = new File(MUSIC_PATH);
            
            Media media;
            if (musicFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                media = new Media(musicFile.toURI().toString());
                System.out.println("Música cargada desde archivo: " + MUSIC_PATH);
            } else {
                // Estamos en producción, usar getResource
                String resourcePath = getClass().getResource("/recursos/musica/musica_juego.mp3").toString();
                media = new Media(resourcePath);
                System.out.println("Música cargada desde recursos");
            }
            
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Reproducir en bucle
            musicPlayer.setVolume(0.5); // Volumen al 50%
            musicPlayer.play();
            
            System.out.println("Música de fondo iniciada");
        } catch (Exception e) {
            System.err.println("Error al reproducir la música: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Reproduce el sonido de desbloqueo de fórmula
     */
    public void playUnlockSound() {
        try {
            File soundFile = new File(UNLOCK_SOUND_PATH);
            
            Media media;
            if (soundFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                media = new Media(soundFile.toURI().toString());
                System.out.println("Sonido cargado desde archivo: " + UNLOCK_SOUND_PATH);
            } else {
                // Estamos en producción, usar getResource
                String resourcePath = getClass().getResource("/recursos/sonidos/unlock.mp3").toString();
                media = new Media(resourcePath);
                System.out.println("Sonido cargado desde recursos");
            }
            
            // Si ya hay un efecto reproduciéndose, detenerlo
            if (effectPlayer != null) {
                effectPlayer.stop();
            }
            
            effectPlayer = new MediaPlayer(media);
            effectPlayer.setVolume(0.7); // Volumen al 70%
            effectPlayer.play();
            
            System.out.println("Sonido de desbloqueo reproducido");
        } catch (Exception e) {
            System.err.println("Error al reproducir sonido de desbloqueo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Detiene la música de fondo
     */
    public void stopBackgroundMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            System.out.println("Música de fondo detenida");
        }
    }
    
    /**
     * Pausa la música de fondo
     */
    public void pauseBackgroundMusic() {
        if (musicPlayer != null) {
            musicPlayer.pause();
            System.out.println("Música de fondo pausada");
        }
    }
    
    /**
     * Reanuda la música de fondo
     */
    public void resumeBackgroundMusic() {
        if (musicPlayer != null) {
            musicPlayer.play();
            System.out.println("Música de fondo reanudada");
        }
    }
    
    /**
     * Ajusta el volumen de la música de fondo
     * @param volume Valor entre 0.0 y 1.0
     */
    public void setMusicVolume(double volume) {
        if (musicPlayer != null) {
            musicPlayer.setVolume(Math.max(0.0, Math.min(1.0, volume)));
        }
    }
}
