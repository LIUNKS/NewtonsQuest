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
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Clase encargada de la reproducción de videos del juego.
 * Maneja la creación de ventanas de video y la reproducción de medios.
 */
public class VideoPlayer {
    
    private Stage videoStage;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    
    /**
     * Reproduce un video en una nueva ventana
     * @param videoPath Ruta del archivo de video
     * @param title Título de la ventana
     * @param parentStage Ventana padre (para modalidad)
     */
    public void playVideo(String videoPath, String title, Stage parentStage) {
        try {
            // Verificar que el archivo existe
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                showErrorDialog("Video no encontrado", 
                              "El archivo de video no se encuentra en la ruta especificada:\n" + videoPath);
                return;
            }
            
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
            
            ErrorHandler.logInfo("Video iniciado: " + title);
            
        } catch (Exception e) {
            ErrorHandler.logError("Error al reproducir video: " + e.getMessage());
            showErrorDialog("Error de reproducción", 
                          "No se pudo reproducir el video:\n" + e.getMessage());
        }
    }
    
    /**
     * Crea los controles del reproductor de video
     * @return VBox con los controles
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
        
        // Eventos de botones
        playPauseButton.setOnAction(e -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                    playPauseButton.setText("▶️ Reproducir");
                } else {
                    mediaPlayer.play();
                    playPauseButton.setText("⏸️ Pausar");
                }
            }
        });
        
        stopButton.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                playPauseButton.setText("▶️ Reproducir");
            }
        });
        
        closeButton.setOnAction(e -> closeVideo());
        
        controlsBox.getChildren().addAll(playPauseButton, stopButton, closeButton);
        
        return controlsBox;
    }
    
    /**
     * Configura los eventos del MediaPlayer
     */
    private void setupMediaPlayerEvents() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Video terminado");
                mediaPlayer.stop();
            });
            
            mediaPlayer.setOnError(() -> {
                ErrorHandler.logError("Error en la reproducción del video: " + mediaPlayer.getError());
                showErrorDialog("Error de reproducción", 
                              "Ocurrió un error durante la reproducción del video.");
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
     * Cierra el reproductor de video y libera recursos
     */
    public void closeVideo() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
            }
            
            if (videoStage != null) {
                videoStage.close();
                videoStage = null;
            }
            
            ErrorHandler.logInfo("Reproductor de video cerrado");
            
        } catch (Exception e) {
            ErrorHandler.logError("Error al cerrar el reproductor: " + e.getMessage());
        }
    }
    
    /**
     * Muestra un diálogo de error
     * @param title Título del error
     * @param message Mensaje del error
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Verifica si hay un video reproduciéndose actualmente
     * @return true si hay un video activo
     */
    public boolean isVideoPlaying() {
        return mediaPlayer != null && 
               (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING || 
                mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED);
    }
}
