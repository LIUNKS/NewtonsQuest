package Controlador.componentes;

import java.io.File;
import java.io.FileInputStream;
import javafx.scene.image.Image;

/**
 * Clase encargada de gestionar la carga de recursos del juego.
 * Separa la lógica de carga de recursos del controlador principal.
 */
public class ResourceManager {
    
    // Rutas de los recursos
    private final String BACKGROUND_IMAGE_PATH = "src/recursos/imagenes/fondo_juego.jpg";
    private final String HEART_IMAGE_PATH = "src/recursos/sprites/corazon/corazon_lleno.png";
    private final String EMPTY_HEART_IMAGE_PATH = "src/recursos/sprites/corazon/corazon_vacio.png";
    
    // Recursos cargados
    private Image backgroundImage;
    private Image heartImage;
    private Image emptyHeartImage;
    
    /**
     * Constructor del ResourceManager
     */
    public ResourceManager() {
        // ResourceManager inicializado
    }
    
    /**
     * Carga todos los recursos necesarios para el juego
     */
    public void loadAllResources() {
        loadBackgroundImage();
        loadHeartImages();
    }
    
    /**
     * Carga la imagen de fondo del juego
     */
    private void loadBackgroundImage() {
        try {
            File backgroundFile = new File(BACKGROUND_IMAGE_PATH);
            
            if (backgroundFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                backgroundImage = new Image(new FileInputStream(backgroundFile));
                // Imagen de fondo cargada correctamente
            } else {
                // Estamos en producción, usar getResource
                backgroundImage = new Image(getClass().getResourceAsStream("/recursos/imagenes/fondo_juego.jpg"));
                // Imagen de fondo cargada desde recursos
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de fondo: " + e.getMessage());
            e.printStackTrace();
            backgroundImage = null;
        }
    }
    
    /**
     * Carga las imágenes de corazones para el sistema de vidas
     */
    private void loadHeartImages() {
        try {
            // Intentar cargar las imágenes de corazones
            File heartFile = new File(HEART_IMAGE_PATH);
            File emptyHeartFile = new File(EMPTY_HEART_IMAGE_PATH);
            
            if (heartFile.exists() && emptyHeartFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                heartImage = new Image(new FileInputStream(heartFile));
                emptyHeartImage = new Image(new FileInputStream(emptyHeartFile));
                // Imágenes de corazones cargadas correctamente
            } else {
                // Estamos en producción, usar getResource
                heartImage = new Image(getClass().getResourceAsStream("/recursos/sprites/corazon/corazon_lleno.png"));
                emptyHeartImage = new Image(getClass().getResourceAsStream("/recursos/sprites/corazon/corazon_vacio.png"));
                // Imágenes de corazones cargadas desde recursos
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imágenes de corazones: " + e.getMessage());
            e.printStackTrace();
            
            // Crear imágenes predeterminadas en caso de error
            heartImage = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAwklEQVR42mNgGAWDFDAwEAv+E4WB6kkCxFnw/z9RGJNF/0nCJFvwnygMNXAUjFpAClDdAnAMgxNTCkiyADkpiY4jKAApAIrDxQmJw8nRLPhPKIHBxeHkqAXYYkKyRwSRYsR6TGwgLYvIPgkSKptJLkpItoC8ooQUD0jyALGAJA8QC4j2AKmAaA+QCoj2AKmAaA+QCoj2AKmAaA+QCoj2AKkAJOc/kZhoC5ATNrUt+A8WIw6Ta8F/sC7iMNkW/EcOjILBAwAZMkztv9sLSwAAAABJRU5ErkJggg==");
            emptyHeartImage = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAzElEQVR42mNgGAWDFDAwEAv+E4WB6kkCxFnw/z9RGJNF/0nCJFvwnygMNXAUjFpAClDdAnAMgxNTCkiyADkpiY4jKAApAIrDxQmJw8nRLPhPKIHBxeHkqAXYYkKyRwSRYsR6TGwgLYvIPgkSKptJLkpItoC8ooQUD0jyALGAJA8QC4j2AKmAaA+QCoj2AKmAaA+QCoj2AKkAJOc/kRhZnkQsOUgWEIrN4VtAKCZkAS6LcFqAK01SbAExGcRoQEpG1M+qNEsBg2/VKBjEAABlVEzsxoKa5AAAAABJRU5ErkJggg==");
        }
    }
    
    // Getters para los recursos cargados
    
    public Image getBackgroundImage() {
        return backgroundImage;
    }
    
    public Image getHeartImage() {
        return heartImage;
    }
    
    public Image getEmptyHeartImage() {
        return emptyHeartImage;
    }
}
