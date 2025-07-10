package Controlador.dialogs;

import Controlador.componentes.VideoManager;
import Controlador.componentes.VideoPlayer;
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
 * Diálogo modal para la selección y reproducción de videos educativos.
 * 
 * Esta clase proporciona una interfaz gráfica interactiva que permite a los jugadores
 * explorar y visualizar contenido educativo sobre las leyes de Newton y principios físicos.
 * El acceso a los videos está vinculado al progreso del jugador en el juego.
 * 
 * Características principales:
 * - Catálogo organizado de videos educativos sobre física
 * - Sistema de desbloqueo progresivo basado en el avance del jugador
 * - Reproductor de video integrado con controles intuitivos
 * - Interfaz visual moderna con indicadores de estado claros
 * - Gestión eficiente de recursos multimedia
 * 
 * Elementos visuales:
 * - Lista scrolleable de videos con información detallada
 * - Indicadores visuales para videos bloqueados/desbloqueados
 * - Controles de reproducción accesibles
 * - Mensajes informativos sobre el progreso del jugador
 * - Estilo visual coherente con la identidad del juego
 */

public class VideoSelectionDialog {
    // =====================================
    // ATRIBUTOS DE INSTANCIA
    // =====================================
    
    /** Ventana principal del diálogo de selección de videos */
    private Stage dialogStage;
    
    /** Gestor de videos que proporciona acceso al catálogo de contenido educativo */
    private VideoManager videoManager;
    
    /** Componente encargado de la reproducción de videos seleccionados */
    private VideoPlayer videoPlayer;
    
    // =====================================
    // CONSTRUCTOR
    // =====================================
    
    /**
     * Constructor por defecto del diálogo de selección de videos.
     * 
     * Inicializa los componentes necesarios para la gestión y reproducción
     * de videos educativos. Establece las conexiones con el sistema de gestión
     * de contenido multimedia del juego y prepara el reproductor de videos.
     * 
     * Este constructor no crea la interfaz gráfica inmediatamente, sino que
     * prepara los componentes que se utilizarán cuando se llame al método show().
     */
    public VideoSelectionDialog() {
        this.videoManager = VideoManager.getInstance();
        this.videoPlayer = new VideoPlayer();
    }
    
    // =====================================
    // MÉTODOS PÚBLICOS
    // =====================================
    
    /**
     * Muestra el diálogo modal de selección de videos educativos.
     * 
     * Este método crea y configura una ventana modal que presenta la lista completa
     * de videos disponibles. El acceso a cada video se determina dinámicamente
     * según las fórmulas que el jugador ha desbloqueado durante el juego.
     * 
     * El diálogo incluye:
     * - Título descriptivo y mensaje de progreso personalizado
     * - Lista de videos con indicadores de estado (bloqueado/desbloqueado)
     * - Botones de reproducción para videos disponibles
     * - Botón de cierre para volver al juego
     * 
     * @param parentStage Ventana padre sobre la cual se mostrará el diálogo modal
     * @param unlockedFormulas Array booleano que indica qué fórmulas ha desbloqueado
     *                        el jugador (true = desbloqueada, false = bloqueada)
     * 
     * @throws IllegalArgumentException si parentStage es null
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
     * Cierra el diálogo y libera los recursos asociados.
     * 
     * Este método realiza una limpieza completa de los recursos utilizados:
     * - Detiene cualquier reproducción de video en curso
     * - Libera los recursos multimedia utilizados por el reproductor
     * - Cierra la ventana del diálogo de manera segura
     * 
     * Es importante llamar a este método cuando el diálogo ya no es necesario
     * para evitar problemas de rendimiento o fugas de memoria, especialmente
     * con los recursos multimedia.
     */
    public void closeDialog() {
        if (videoPlayer != null && videoPlayer.isVideoPlaying()) {
            videoPlayer.closeVideo();
        }
        
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
    
    // =====================================
    // MÉTODOS PRIVADOS - CONSTRUCCIÓN DE UI
    // =====================================
    
    /**
     * Construye el contenido principal del diálogo.
     * 
     * Este método crea la estructura visual completa del diálogo, organizando
     * todos los elementos en un layout vertical (VBox) con estilo coherente.
     * 
     * Componentes principales:
     * - Título principal con ícono representativo
     * - Mensaje informativo sobre el progreso de desbloqueo
     * - Lista scrolleable de videos disponibles
     * - Botón de cierre para volver al juego
     * 
     * El diseño visual incluye un fondo con degradado que sigue la paleta
     * cromática del juego y espaciado consistente entre elementos.
     * 
     * @return VBox contenedor principal con todos los elementos del diálogo
     */
    private VBox createMainContent() {
        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: linear-gradient(to bottom, #0f0c29, #1a1a2e, #24243e);");
        
        // Título principal
        Label titleLabel = new Label("📹 Videos Educativos");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Mensaje de progreso del jugador
        Label progressLabel = new Label(videoManager.getProgressMessage());
        progressLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
        
        // Lista scrolleable de videos
        ScrollPane scrollPane = createVideoList();
        
        // Botón de cierre
        Button closeButton = new Button("Cerrar");
        closeButton.setStyle("-fx-background-color: #666666; -fx-text-fill: white; " +
                           "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 5;");
        closeButton.setOnAction(e -> closeDialog());
        
        mainBox.getChildren().addAll(titleLabel, progressLabel, scrollPane, closeButton);
        
        return mainBox;
    }
    
    /**
     * Construye la lista scrolleable de videos disponibles.
     * 
     * Este método crea un componente ScrollPane que contiene la lista completa
     * de videos educativos, obteniendo la información desde el VideoManager.
     * 
     * Características del componente:
     * - Scroll vertical para navegar por la lista de videos
     * - Ancho adaptable al contenedor principal
     * - Fondo transparente para integración visual
     * - Espaciado uniforme entre elementos
     * - Estilos visuales consistentes con el tema del juego
     * 
     * Cada video se representa como un elemento individual creado
     * por el método createVideoItem().
     * 
     * @return ScrollPane conteniendo la lista completa de videos educativos
     */
    private ScrollPane createVideoList() {
        VBox videoList = new VBox(15);
        videoList.setPadding(new Insets(10));
        
        // Obtener lista de videos disponibles
        List<VideoManager.VideoInfo> videos = videoManager.getAvailableVideos();
        
        // Crear elemento visual para cada video
        for (VideoManager.VideoInfo video : videos) {
            HBox videoItem = createVideoItem(video);
            videoList.getChildren().add(videoItem);
        }
        
        // Configurar el scroll pane
        ScrollPane scrollPane = new ScrollPane(videoList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        return scrollPane;
    }
    
    /**
     * Crea un elemento visual individual para un video en la lista.
     * 
     * Este método genera una representación visual completa para cada video
     * del catálogo, adaptando su apariencia según el estado de desbloqueo.
     * 
     * Características del elemento:
     * - Contenedor horizontal (HBox) con bordes redondeados
     * - Color de fondo y bordes que indican el estado (desbloqueado/bloqueado)
     * - Información del video (título y estado de disponibilidad)
     * - Botón de reproducción (habilitado solo si está desbloqueado)
     * - Distribución de espacio optimizada para legibilidad
     * 
     * Videos desbloqueados:
     * - Fondo y bordes en color destacado (#e94560)
     * - Título en blanco y con negrita
     * - Indicador "✅ Disponible" en verde
     * - Botón de reproducción activo
     * 
     * Videos bloqueados:
     * - Fondo y bordes en gris (#666666)
     * - Título en gris
     * - Indicador "🔒 Bloqueado" en gris
     * - Botón de reproducción deshabilitado
     * 
     * @param video Información del video para crear el elemento visual
     * @return HBox conteniendo la representación visual completa del video
     */
    private HBox createVideoItem(VideoManager.VideoInfo video) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(15));
        
        // Aplicar estilo según disponibilidad
        if (video.isUnlocked()) {
            item.setStyle("-fx-background-color: rgba(233, 69, 96, 0.2); " +
                         "-fx-background-radius: 8; -fx-border-color: #e94560; " +
                         "-fx-border-width: 1; -fx-border-radius: 8;");
        } else {
            item.setStyle("-fx-background-color: rgba(102, 102, 102, 0.2); " +
                         "-fx-background-radius: 8; -fx-border-color: #666666; " +
                         "-fx-border-width: 1; -fx-border-radius: 8;");
        }
        
        // Crear sección de información del video
        VBox info = new VBox(5);
        
        // Título del video
        Label titleLabel = new Label(video.getTitle());
        if (video.isUnlocked()) {
            titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        } else {
            titleLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 16px;");
        }
        
        // Estado de disponibilidad
        Label statusLabel = new Label(video.isUnlocked() ? "✅ Disponible" : "🔒 Bloqueado");
        statusLabel.setStyle("-fx-text-fill: " + (video.isUnlocked() ? "#4CAF50" : "#888888") + 
                           "; -fx-font-size: 12px;");
        
        info.getChildren().addAll(titleLabel, statusLabel);
        
        // Crear botón de reproducción
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
        
        // Ensamblar el elemento
        item.getChildren().addAll(info, playButton);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);
        
        return item;
    }
    
    // =====================================
    // MÉTODOS PRIVADOS - FUNCIONALIDAD
    // =====================================
    
    /**
     * Inicia la reproducción de un video educativo específico.
     * 
     * Este método gestiona el proceso completo de reproducción:
     * 1. Obtiene la ruta del archivo de video seleccionado
     * 2. Verifica que el archivo exista físicamente
     * 3. Utiliza el VideoPlayer para reproducir el contenido
     * 4. Maneja posibles errores mostrando alertas informativas
     * 
     * La reproducción se realiza en una ventana modal separada que
     * incluye controles de reproducción (play/pause, stop, cerrar).
     * 
     * Si el archivo de video no existe o no se puede acceder, se muestra
     * un diálogo de error con información detallada para el usuario.
     * 
     * @param video Información completa del video a reproducir, incluyendo
     *              índice, título y nombre de archivo
     */
    private void playVideo(VideoManager.VideoInfo video) {
        String videoPath = videoManager.getVideoPath(video.getIndex());
        
        if (videoPath != null && videoManager.videoFileExists(videoPath)) {
            // Reproducir el video usando el reproductor integrado
            videoPlayer.playVideo(videoPath, video.getTitle(), dialogStage);
        } else {
            // Mostrar mensaje de error si el archivo no existe
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Video no encontrado");
            alert.setHeaderText(null);
            alert.setContentText("El archivo de video no se encuentra disponible:\n" + 
                                video.getFilename());
            alert.showAndWait();
        }
    }
    
    // =====================================
    // MÉTODOS PRIVADOS - ESTILOS
    // =====================================
    
    /**
     * Genera el CSS personalizado para el diálogo de selección de videos.
     * 
     * Este método define reglas CSS específicas para mejorar la apariencia
     * de los componentes ScrollPane del diálogo, creando un efecto visual
     * integrado con el tema general del juego.
     * 
     * Reglas CSS principales:
     * - Fondos transparentes para el ScrollPane y sus componentes internos
     * - Eliminación de bordes y elementos visuales predeterminados
     * - Configuración para que el contenido se muestre sobre el fondo degradado
     * 
     * Al inyectar estas reglas directamente, se garantiza la consistencia visual
     * sin necesidad de archivos CSS externos.
     * 
     * @return String conteniendo las reglas CSS personalizadas para el diálogo
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
