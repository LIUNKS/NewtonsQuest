package Controlador.dialogs;

import Controlador.componentes.RankingManager;
import Modelo.dao.UsuarioDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Diálogo de perfil de usuario en Newton's Quest.
 * 
 * Esta clase proporciona una interfaz completa para que los usuarios
 * visualicen su información personal y estadísticas de juego.
 * 
 * Características:
 * - Visualización de información personal del usuario
 * - Estadísticas de juego (puntaje, ranking, fórmulas completadas)
 * - Fecha de última partida y fecha de registro
 * - Interfaz visual atractiva con gradientes y efectos
 * - Sincronización automática de datos de ranking
 */
public class UserProfileDialog {
    
    // ===================================
    // ATRIBUTOS PRIVADOS
    // ===================================
    
    /** Escenario del diálogo de perfil */
    private Stage dialogStage;
    
    /** Nombre de usuario actual */
    private String currentUsername;
    
    /** ID del usuario actual */
    private int currentUserId;
    
    
    // ===================================
    // CONSTRUCTOR
    // ===================================
    
    /**
     * Constructor del diálogo de perfil de usuario.
     * 
     * @param parentStage Ventana padre del diálogo
     * @param username Nombre de usuario actual
     * @param userId ID del usuario en la base de datos
     */
    public UserProfileDialog(Stage parentStage, String username, int userId) {
        this.currentUsername = username;
        this.currentUserId = userId;
        createDialog(parentStage);
    }
    
    // ===================================
    // MÉTODOS DE CONFIGURACIÓN
    // ===================================
    
    /**
     * Crea y configura el diálogo de perfil de usuario.
     * 
     * @param parentStage Ventana padre para centrar el diálogo
     */
    private void createDialog(Stage parentStage) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.setTitle("Perfil de Usuario - " + currentUsername);
        dialogStage.setResizable(true);
        dialogStage.setMinWidth(520);
        dialogStage.setMinHeight(650);
        
        // Crear el contenido del diálogo
        VBox mainContent = createMainContent();
        
        // Crear la escena con dimensiones más grandes
        Scene scene = new Scene(mainContent, 520, 700);
        scene.getStylesheets().add(getClass().getResource("/Vista/resources/main.css").toExternalForm());
        
        dialogStage.setScene(scene);
        
        // Centrar el diálogo en la ventana padre
        dialogStage.setOnShown(e -> {
            dialogStage.setX(parentStage.getX() + (parentStage.getWidth() - dialogStage.getWidth()) / 2);
            dialogStage.setY(parentStage.getY() + (parentStage.getHeight() - dialogStage.getHeight()) / 2);
        });
    }
    
    /**
     * Crea el contenido principal del diálogo.
     * 
     * @return VBox con el contenido completo del diálogo
     */
    private VBox createMainContent() {
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");
        
        // Título
        Label titleLabel = new Label("👤 PERFIL DE USUARIO");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.GOLD);
        titleLabel.setAlignment(Pos.CENTER);
        
        // Contenedor para la información del usuario
        VBox userInfoContainer = createUserInfoSection();
        
        // Contenedor para estadísticas del juego
        VBox statsContainer = createStatsSection();
        
        // Botón de cerrar
        HBox buttonsContainer = createCloseButtonSection();
        
        mainContent.getChildren().addAll(titleLabel, userInfoContainer, statsContainer, buttonsContainer);
        
        return mainContent;
    }
    
    // ===================================
    // MÉTODOS DE CREACIÓN DE SECCIONES
    // ===================================
    
    /**
     * Crea la sección de información personal del usuario.
     * 
     * @return VBox con la información personal del usuario
     */
    private VBox createUserInfoSection() {
        VBox container = new VBox(12);
        container.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1); " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #3498db; " +
            "-fx-border-radius: 10; " +
            "-fx-border-width: 2;"
        );
        container.setPadding(new Insets(15));
        
        // Título de la sección
        Label sectionTitle = new Label("📋 Información Personal");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.LIGHTBLUE);
        
        // Obtener datos del usuario desde la base de datos
        String[] userData = getUserData();
        
        // Campo de nombre de usuario (no editable)
        VBox usernameBox = createReadOnlyField("Nombre de Usuario:", currentUsername, "👤");
        
        // Campo de nombre completo (no editable)
        VBox nombreBox = createReadOnlyField("Nombre Completo:", userData[0], "📝");
        
        // Campo de correo (no editable)
        VBox correoBox = createReadOnlyField("Correo Electrónico:", userData[1], "📧");
        
        // Campo de fecha de registro (no editable)
        VBox fechaBox = createReadOnlyField("Fecha de Registro:", userData[2], "📅");
        
        container.getChildren().addAll(sectionTitle, usernameBox, nombreBox, correoBox, fechaBox);
        
        return container;
    }
    
    /**
     * Crea la sección de estadísticas del juego.
     * 
     * @return VBox con las estadísticas del usuario
     */
    private VBox createStatsSection() {
        VBox container = new VBox(12);
        container.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1); " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #e74c3c; " +
            "-fx-border-radius: 10; " +
            "-fx-border-width: 2;"
        );
        container.setPadding(new Insets(15));
        
        // Título de la sección
        Label sectionTitle = new Label("📊 Estadísticas del Juego");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.LIGHTCORAL);
        
        // Obtener estadísticas del usuario
        String[] stats = getUserStats();
        
        // Mejor puntaje
        VBox puntajeBox = createReadOnlyField("Mejor Puntaje:", stats[0], "🏆");
        
        // Posición en ranking
        VBox posicionBox = createReadOnlyField("Posición en Ranking:", stats[1], "📈");
        
        // Fórmulas completadas
        VBox formulasBox = createReadOnlyField("Fórmulas Completadas:", stats[2], "🧮");
        
        // Última partida
        VBox ultimaPartidaBox = createReadOnlyField("Última Partida:", stats[3], "🎮");
        
        container.getChildren().addAll(sectionTitle, puntajeBox, posicionBox, formulasBox, ultimaPartidaBox);
        
        return container;
    }
    
    /**
     * Crea un campo de solo lectura con icono.
     * 
     * @param labelText Texto de la etiqueta
     * @param value Valor a mostrar
     * @param icon Icono para el campo
     * @return VBox con el campo formateado
     */
    private VBox createReadOnlyField(String labelText, String value, String icon) {
        VBox fieldBox = new VBox(3);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        label.setTextFill(Color.WHITE);
        
        HBox valueBox = new HBox(10);
        valueBox.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 16));
        
        Label valueLabel = new Label((value == null || value.isEmpty()) ? "No disponible" : value);
        valueLabel.setFont(Font.font("Arial", 14));
        valueLabel.setTextFill(Color.LIGHTGRAY);
        valueLabel.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-padding: 8; -fx-background-radius: 5;");
        valueLabel.setPrefWidth(300);
        
        valueBox.getChildren().addAll(iconLabel, valueLabel);
        fieldBox.getChildren().addAll(label, valueBox);
        
        return fieldBox;
    }
    
    /**
     * Crea la sección de botones del diálogo.
     * 
     * @return HBox con los botones del diálogo
     */
    private HBox createCloseButtonSection() {
        HBox buttonsBox = new HBox();
        buttonsBox.setAlignment(Pos.CENTER);
        
        // Solo botón Cerrar
        Button closeButton = new Button("❌ Cerrar");
        closeButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        closeButton.setPrefWidth(120);
        closeButton.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
        );
        closeButton.setOnAction(e -> dialogStage.close());
        
        buttonsBox.getChildren().add(closeButton);
        
        return buttonsBox;
    }
    
    // ===================================
    // MÉTODOS DE DATOS
    // ===================================
    
    /**
     * Obtiene los datos personales del usuario desde la base de datos.
     * 
     * @return Array con los datos del usuario [nombre, correo, fecha]
     */
    private String[] getUserData() {
        try {
            // Obtener datos del usuario desde la base de datos
            String nombreCompleto = UsuarioDAO.obtenerNombreCompleto(currentUserId);
            String correo = UsuarioDAO.obtenerCorreo(currentUserId);
            String fechaRegistro = UsuarioDAO.obtenerFechaRegistro(currentUserId);
            
            return new String[]{
                nombreCompleto != null ? nombreCompleto : "",
                correo != null ? correo : "",
                fechaRegistro != null ? fechaRegistro : "No disponible"
            };
        } catch (Exception e) {
            // Retornar valores por defecto en caso de error
            return new String[]{"", "", "No disponible"};
        }
    }
    
    /**
     * Obtiene las estadísticas del usuario desde la base de datos.
     * 
     * @return Array con las estadísticas [puntaje, posición, fórmulas, última partida]
     */
    private String[] getUserStats() {
        try {
            // Obtener estadísticas del usuario desde la tabla usuarios
            RankingManager rankingManager = RankingManager.getInstance();
            
            String mejorPuntaje = "0";
            String posicionRanking = "No clasificado";
            String formulasCompletadas = "0/5";
            String ultimaPartida = "Nunca";
            
            // Obtener estadísticas básicas desde la tabla usuarios
            try {
                // Primero intentar sincronizar datos del ranking si existen
                UsuarioDAO.sincronizarDatosRankingAUsuarios(currentUserId);
                
                int puntaje = UsuarioDAO.obtenerMejorPuntajeUsuario(currentUserId);
                mejorPuntaje = String.valueOf(puntaje);
                
                int formulas = UsuarioDAO.obtenerFormulasCompletadasUsuario(currentUserId);
                formulasCompletadas = formulas + "/5";
                if (formulas >= 5) {
                    formulasCompletadas += " ✅";
                }
                
                ultimaPartida = UsuarioDAO.obtenerUltimaPartidaUsuario(currentUserId);
                
            } catch (Exception e) {
                // Continuar con valores por defecto en caso de error
            }
            
            // Verificar posición en el ranking global (solo si completó las 5 fórmulas)
            int posicion = rankingManager.getCurrentUserPosition();
            if (posicion > 0) {
                posicionRanking = "#" + posicion;
            } else {
                posicionRanking = "Sin clasificar";
            }
            
            return new String[]{mejorPuntaje, posicionRanking, formulasCompletadas, ultimaPartida};
        } catch (Exception e) {
            // Retornar valores por defecto en caso de error
            return new String[]{"0", "No clasificado", "0/5", "Nunca"};
        }
    }
    
    // ===================================
    // MÉTODOS PÚBLICOS
    // ===================================
    
    /**
     * Muestra el diálogo de forma modal y espera a que se cierre.
     */
    public void showAndWait() {
        dialogStage.showAndWait();
    }
    
    /**
     * Muestra el diálogo de forma no modal.
     */
    public void show() {
        dialogStage.show();
    }
}
