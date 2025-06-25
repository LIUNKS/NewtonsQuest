package Controlador.dialogs;

import Controlador.componentes.VideoManager;
import Controlador.componentes.VideoPlayer;
import Controlador.constants.GameConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;

/**
 * Diálogo para mostrar y seleccionar videos disponibles.
 * Muestra los videos desbloqueados y permite reproducirlos.
 */
public class VideoSelectionDialog {
    
    private Stage dialogStage;
    private VideoManager videoManager;
    private VideoPlayer videoPlayer;
    
    /**
     * Constructor del diálogo de selección de videos
     */
    public VideoSelectionDialog() {
        this.videoManager = VideoManager.getInstance();
        this.videoPlayer = new VideoPlayer();
    }
    
    /**
     * Muestra el diálogo de selección de videos
     * @param parentStage Ventana padre
     * @param unlockedFormulas Array de fórmulas desbloqueadas para actualizar acceso
     */
    public void show(Stage parentStage, boolean[] unlockedFormulas) {
        // Actualizar acceso a videos basándose en fórmulas desbloqueadas
        videoManager.updateVideoAccess(unlockedFormulas);
        
        // Crear la ventana del diálogo
        dialogStage = new Stage();
        dialogStage.setTitle("Videos Educativos - Newton's Apple Quest");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setResizable(false);
        
        // Crear el contenido
        VBox mainContent = createMainContent();
        
        // Crear la escena
        Scene scene = new Scene(mainContent, 600, 500);
        scene.getStylesheets().add("data:text/css," + getDialogCSS());
        
        dialogStage.setScene(scene);
        dialogStage.show();
    }
    
    /**
     * Crea el contenido principal del diálogo
     * @return VBox con el contenido
     */
    private VBox createMainContent() {
        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: linear-gradient(to bottom, #0f0c29, #1a1a2e, #24243e);");
        
        // Título
        Label titleLabel = new Label("📹 Videos Educativos");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Mensaje de progreso
        Label progressLabel = new Label(videoManager.getProgressMessage());
        progressLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
        
        // Área de videos
        ScrollPane scrollPane = createVideoList();
        
        // Botón cerrar
        Button closeButton = new Button("Cerrar");
        closeButton.setStyle("-fx-background-color: #666666; -fx-text-fill: white; " +
                           "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 5;");
        closeButton.setOnAction(e -> closeDialog());
        
        mainBox.getChildren().addAll(titleLabel, progressLabel, scrollPane, closeButton);
        
        return mainBox;
    }
    
    /**
     * Crea la lista de videos disponibles
     * @return ScrollPane con la lista de videos
     */
    private ScrollPane createVideoList() {
        VBox videoList = new VBox(15);
        videoList.setPadding(new Insets(10));
        
        List<VideoManager.VideoInfo> videos = videoManager.getAvailableVideos();
        
        for (VideoManager.VideoInfo video : videos) {
            HBox videoItem = createVideoItem(video);
            videoList.getChildren().add(videoItem);
        }
        
        ScrollPane scrollPane = new ScrollPane(videoList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        return scrollPane;
    }
    
    /**
     * Crea un elemento de video en la lista
     * @param video Información del video
     * @return HBox con el elemento del video
     */
    private HBox createVideoItem(VideoManager.VideoInfo video) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(15));
        
        // Estilo del contenedor basado en si está desbloqueado
        if (video.isUnlocked()) {
            item.setStyle("-fx-background-color: rgba(233, 69, 96, 0.2); " +
                         "-fx-background-radius: 8; -fx-border-color: #e94560; " +
                         "-fx-border-width: 1; -fx-border-radius: 8;");
        } else {
            item.setStyle("-fx-background-color: rgba(102, 102, 102, 0.2); " +
                         "-fx-background-radius: 8; -fx-border-color: #666666; " +
                         "-fx-border-width: 1; -fx-border-radius: 8;");
        }
        
        // Información del video
        VBox info = new VBox(5);
        
        Label titleLabel = new Label(video.getTitle());
        if (video.isUnlocked()) {
            titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        } else {
            titleLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 16px;");
        }
        
        Label statusLabel = new Label(video.isUnlocked() ? "✅ Disponible" : "🔒 Bloqueado");
        statusLabel.setStyle("-fx-text-fill: " + (video.isUnlocked() ? "#4CAF50" : "#888888") + 
                           "; -fx-font-size: 12px;");
        
        info.getChildren().addAll(titleLabel, statusLabel);
        
        // Botón de reproducción
        Button playButton = new Button(video.isUnlocked() ? "▶️ Ver Video" : "🔒 Bloqueado");
        playButton.setDisable(!video.isUnlocked());
        
        if (video.isUnlocked()) {
            playButton.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; " +
                              "-fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 5;");
            playButton.setOnAction(e -> playVideo(video));
        } else {
            playButton.setStyle("-fx-background-color: #666666; -fx-text-fill: #cccccc; " +
                              "-fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 5;");
        }
        
        item.getChildren().addAll(info, playButton);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);
        
        return item;
    }
    
    /**
     * Reproduce un video específico
     * @param video Información del video a reproducir
     */
    private void playVideo(VideoManager.VideoInfo video) {
        String videoPath = videoManager.getVideoPath(video.getIndex());
        
        if (videoPath != null && videoManager.videoFileExists(videoPath)) {
            videoPlayer.playVideo(videoPath, video.getTitle(), dialogStage);
        } else {
            // Mostrar error si el video no existe
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Video no encontrado");
            alert.setHeaderText(null);
            alert.setContentText("El archivo de video no se encuentra disponible:\n" + 
                                video.getFilename());
            alert.showAndWait();
        }
    }
    
    /**
     * Cierra el diálogo
     */
    public void closeDialog() {
        if (videoPlayer != null && videoPlayer.isVideoPlaying()) {
            videoPlayer.closeVideo();
        }
        
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
    
    /**
     * Obtiene el CSS personalizado para el diálogo
     * @return String con el CSS
     */
    private String getDialogCSS() {
        return ".scroll-pane { " +
               "    -fx-background-color: transparent; " +
               "    -fx-control-inner-background: transparent; " +
               "} " +
               ".scroll-pane .viewport { " +
               "    -fx-background-color: transparent; " +
               "} " +
               ".scroll-pane .content { " +
               "    -fx-background-color: transparent; " +
               "}";
    }
}
