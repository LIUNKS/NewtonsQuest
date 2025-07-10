package Controlador.componentes;

import Controlador.constants.GameConstants;
import Controlador.utils.ErrorHandler;
import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Clase encargada de la reproducción de videos educativos.
 * 
 * Esta clase implementa un reproductor de video personalizado que permite:
 * - Reproducir videos educativos sobre física y las leyes de Newton
 * - Controlar la reproducción (reproducir, pausar, detener)
 * - Gestionar recursos multimedia de manera eficiente
 * - Manejar errores de reproducción y recursos no encontrados
 * 
 * El reproductor se muestra en una ventana modal independiente y
 * ofrece una interfaz intuitiva con controles básicos.
 */

public class VideoPlayer {
    
    private Stage videoStage;      // Ventana donde se reproduce el video
    private MediaPlayer mediaPlayer; // Reproductor multimedia
    private MediaView mediaView;     // Componente visual que muestra el video
    
    /**
     * Reproduce un video educativo en una nueva ventana modal.
     * 
     * Este método crea una nueva ventana para reproducir el video especificado,
     * configurando todos los controles necesarios y manejando posibles errores.
     * 
     * @param videoPath Ruta del archivo de video a reproducir
     * @param title Título descriptivo que se mostrará en la ventana
     * @param parentStage Ventana padre para establecer la modalidad
     */
    public void playVideo(String videoPath, String title, Stage parentStage) {
        try {
            // Verificar que el archivo existe
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                showErrorDialog("Video no encontrado", 
                              "El archivo de video no se encuentra en la ruta especificada:\n" + videoPath);
                ErrorHandler.logWarning("Video no encontrado: " + videoPath);
                return;
            }
            
            // Verificar tamaño del archivo para asegurarse de que no está vacío o corrupto
            if (videoFile.length() < 1024) { // Menos de 1KB probablemente no es un video válido
                showErrorDialog("Video inválido", 
                              "El archivo de video parece estar corrupto o vacío:\n" + videoPath);
                ErrorHandler.logWarning("Archivo de video inválido o vacío: " + videoPath + " (Tamaño: " + videoFile.length() + " bytes)");
                return;
            }
            
            // Verificar que la extensión del archivo es adecuada
            String fileName = videoFile.getName().toLowerCase();
            if (!fileName.endsWith(".mp4") && !fileName.endsWith(".m4v") && 
                !fileName.endsWith(".flv") && !fileName.endsWith(".fxm")) {
                showErrorDialog("Formato de video no compatible", 
                              "El formato del archivo no es compatible con el reproductor.\n" +
                              "Formatos soportados: MP4, M4V, FLV, FXM\n" +
                              "Archivo: " + videoFile.getName());
                ErrorHandler.logWarning("Formato de video potencialmente no compatible: " + fileName);
                // Continuamos a pesar del aviso, ya que podría ser un problema solo de extensión
            }
            
            ErrorHandler.logInfo("Intentando reproducir video: " + title + " desde: " + videoPath);
            
            try {
                // Crear el Media y MediaPlayer
                Media media = new Media(videoFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaView = new MediaView(mediaPlayer);
            
            // Configurar MediaView
            mediaView.setFitWidth(GameConstants.VIDEO_WINDOW_WIDTH - 40);
            mediaView.setFitHeight(GameConstants.VIDEO_WINDOW_HEIGHT - 100);
            mediaView.setPreserveRatio(true);
            
            // Crear controles
            VBox controls = createVideoControls();
            
            // Crear layout principal
            VBox root = new VBox(10);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #1a1a2e;");
            
            // Título del video
            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
            
            root.getChildren().addAll(titleLabel, mediaView, controls);
            
            // Crear la escena
            Scene scene = new Scene(root, GameConstants.VIDEO_WINDOW_WIDTH, GameConstants.VIDEO_WINDOW_HEIGHT);
            
            // Crear y configurar la ventana
            videoStage = new Stage();
            videoStage.setTitle(title);
            videoStage.setScene(scene);
            videoStage.initModality(Modality.APPLICATION_MODAL);
            videoStage.initOwner(parentStage);
            videoStage.setResizable(false);
            
            // Configurar eventos del MediaPlayer
            setupMediaPlayerEvents();
            
            // Mostrar la ventana y reproducir el video
            videoStage.show();
            mediaPlayer.play();
            
            ErrorHandler.logInfo("Reproduciendo video: " + title);
                
            } catch (MediaException me) {
                ErrorHandler.logError("Error de media al reproducir video: " + me.getMessage());
                showErrorDialog("Error de formato de video", 
                              "El formato del archivo de video no es compatible con el reproductor.\n" +
                              "Error: " + me.getMessage());
            }
            
        } catch (Exception e) {
            ErrorHandler.logError("Error al reproducir video: " + e.getMessage());
            showErrorDialog("Error de reproducción", 
                          "No se pudo reproducir el video:\n" + e.getMessage());
        }
    }
    
    /**
     * Crea los controles de reproducción para el player de video.
     * 
     * Genera botones para pausar/reproducir, detener y cerrar el video,
     * configurando sus estilos y eventos correspondientes.
     * 
     * @return VBox conteniendo los controles organizados verticalmente
     */
    private VBox createVideoControls() {
        VBox controlsBox = new VBox(10);
        controlsBox.setAlignment(Pos.CENTER);
        
        // Botones de control
        Button playPauseButton = new Button("⏸️ Pausar");
        Button stopButton = new Button("⏹️ Detener");
        Button closeButton = new Button("❌ Cerrar");
        
        // Estilos de botones
        String buttonStyle = "-fx-background-color: #e94560; -fx-text-fill: white; " +
                           "-fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 5;";
        
        playPauseButton.setStyle(buttonStyle);
        stopButton.setStyle(buttonStyle);
        closeButton.setStyle(buttonStyle + "-fx-background-color: #666666;");
        
        // Evento de botón reproducir/pausar
        playPauseButton.setOnAction(e -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                    playPauseButton.setText("▶️ Reproducir");
                    ErrorHandler.logInfo("Video pausado");
                } else {
                    mediaPlayer.play();
                    playPauseButton.setText("⏸️ Pausar");
                    ErrorHandler.logInfo("Video reanudado");
                }
            }
        });
        
        // Evento de botón detener
        stopButton.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                playPauseButton.setText("▶️ Reproducir");
                ErrorHandler.logInfo("Video detenido");
            }
        });
        
        // Evento de botón cerrar
        closeButton.setOnAction(e -> closeVideo());
        
        controlsBox.getChildren().addAll(playPauseButton, stopButton, closeButton);
        
        return controlsBox;
    }
    
    /**
     * Configura los eventos del MediaPlayer para manejar el fin de la reproducción,
     * errores y cierre de la ventana.
     * 
     * Establece comportamientos para:
     * - Cuando el video termina de reproducirse
     * - Cuando ocurre un error durante la reproducción
     * - Cuando se cierra la ventana del reproductor
     */
    private void setupMediaPlayerEvents() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                ErrorHandler.logInfo("Video finalizado");
            });
            
            mediaPlayer.setOnError(() -> {
                try {
                    MediaException error = mediaPlayer.getError();
                    String errorDetails = error != null ? error.toString() : "Error desconocido";
                    ErrorHandler.logError("Error en la reproducción del video: " + errorDetails);
                    
                    // Identificar tipo de error para mensajes más específicos
                    String errorMessage = "Ocurrió un error durante la reproducción del video.";
                    if (errorDetails != null) {
                        if (errorDetails.contains("ERROR_MEDIA_INVALID")) {
                            errorMessage = "El formato del archivo de video no es compatible con el reproductor.\n" +
                                          "Verifique que el archivo sea un formato estándar como MP4 con codec H.264.";
                        } else if (errorDetails.contains("ERROR_MEDIA_CORRUPTED")) {
                            errorMessage = "El archivo de video parece estar corrupto.\n" +
                                          "Intente reinstalar la aplicación para restaurar los archivos.";
                        } else if (errorDetails.contains("ERROR_MEDIA_UNSUPPORTED")) {
                            errorMessage = "El formato del video no es compatible con este reproductor.\n" +
                                          "Intente convertir el video a un formato estándar como MP4 con codec H.264.";
                        }
                    }
                    
                    showErrorDialog("Error de reproducción", errorMessage);
                } catch (Exception e) {
                    ErrorHandler.logError("Error al procesar el error del MediaPlayer: " + e.getMessage());
                    showErrorDialog("Error de reproducción", "Ocurrió un error inesperado durante la reproducción.");
                } finally {
                    // Cerrar la ventana automáticamente después de un error
                    if (videoStage != null) {
                        videoStage.close();
                    }
                }
            });
            
            // Cuando se cierre la ventana, detener el video
            if (videoStage != null) {
                videoStage.setOnCloseRequest(e -> {
                    closeVideo();
                });
            }
        }
    }
    
    /**
     * Cierra el reproductor de video y libera todos los recursos asociados.
     * 
     * Este método:
     * - Detiene la reproducción del video si está activa
     * - Libera los recursos del MediaPlayer
     * - Cierra la ventana del reproductor
     */
    public void closeVideo() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
                ErrorHandler.logInfo("Reproductor de video detenido y recursos liberados");
            }
            
            if (videoStage != null) {
                videoStage.close();
                videoStage = null;
            }
            
        } catch (Exception e) {
            ErrorHandler.logError("Error al cerrar el reproductor: " + e.getMessage());
        }
    }
    
    /**
     * Muestra un diálogo de error con información sobre problemas de reproducción.
     * 
     * @param title Título del diálogo de error
     * @param message Mensaje detallado que explica el error
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Verifica si hay un video reproduciéndose o pausado actualmente.
     * 
     * Este método es útil para comprobar el estado del reproductor antes de
     * intentar iniciar una nueva reproducción o realizar otras operaciones.
     * 
     * @return true si hay un video activo (reproduciendo o pausado), false en caso contrario
     */
    public boolean isVideoPlaying() {
        return mediaPlayer != null && 
               (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING || 
                mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED);
    }
}
