package Controlador.componentes;

import java.io.File;
import java.io.FileInputStream;
import javafx.scene.image.Image;

/**
 * Gestor de carga de recursos gráficos.
 * 
 * Esta clase centraliza la carga de todos los recursos gráficos del juego:
 * 
 *   - Imágenes de fondo y escenarios
 *   - Sprites de interfaz de usuario (corazones, iconos)
 *   - Gestión de rutas de desarrollo vs. producción
 *   - Carga de recursos desde archivos locales
 *   - Fallbacks para recursos no encontrados
 * 
 * Separa completamente la lógica de carga de recursos del resto
 * del juego, proporcionando una interfaz limpia para acceder
 * a todos los recursos gráficos necesarios.
 */
public class ResourceManager {
    
    // ================================================================================================
    // RUTAS DE RECURSOS (ENTORNO DE DESARROLLO)
    // ================================================================================================
    
    /** Ruta de la imagen de fondo del juego */
    private final String BACKGROUND_IMAGE_PATH = "src/recursos/imagenes/fondo_juego.jpg";
    
    /** Ruta de la imagen de corazón lleno */
    private final String HEART_IMAGE_PATH = "src/recursos/sprites/corazon/corazon_lleno.png";
    
    /** Ruta de la imagen de corazón vacío */
    private final String EMPTY_HEART_IMAGE_PATH = "src/recursos/sprites/corazon/corazon_vacio.png";
    
    // ================================================================================================
    // RECURSOS CARGADOS
    // ================================================================================================
    
    /** Imagen de fondo cargada */
    private Image backgroundImage;
    
    /** Imagen de corazón lleno cargada */
    private Image heartImage;
    
    /** Imagen de corazón vacío cargada */
    private Image emptyHeartImage;
    
    // ================================================================================================
    // CONSTRUCTORES
    // ================================================================================================
    
    /**
     * Constructor principal del gestor de recursos.
     */
    public ResourceManager() {
    }
    
    // ================================================================================================
    // CARGA DE RECURSOS
    // ================================================================================================
    
    /**
     * Carga todos los recursos necesarios para el juego.
     */
    public void loadAllResources() {
        loadBackgroundImage();
        loadHeartImages();
    }
    
    /**
     * Carga la imagen de fondo del juego.
     */
    private void loadBackgroundImage() {
        File backgroundFile = new File(BACKGROUND_IMAGE_PATH);
        
        if (backgroundFile.exists()) {
            try {
                backgroundImage = new Image(new FileInputStream(backgroundFile));
            } catch (Exception e) {
                backgroundImage = null;
            }
        } else {
            try {
                backgroundImage = new Image(getClass().getResourceAsStream("/recursos/imagenes/fondo_juego.jpg"));
            } catch (Exception e) {
                backgroundImage = null;
            }
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
    
    // ================================================================================================
    // MÉTODOS DE ACCESO - GETTERS
    // ================================================================================================
    
    /**
     * Obtiene la imagen de fondo cargada.
     * @return Imagen de fondo o null si no se pudo cargar
     */
    public Image getBackgroundImage() {
        return backgroundImage;
    }
    
    /**
     * Obtiene la imagen de corazón lleno.
     * @return Imagen de corazón lleno o imagen por defecto
     */
    public Image getHeartImage() {
        return heartImage;
    }
    
    /**
     * Obtiene la imagen de corazón vacío.
     * @return Imagen de corazón vacío o imagen por defecto
     */
    public Image getEmptyHeartImage() {
        return emptyHeartImage;
    }
}
