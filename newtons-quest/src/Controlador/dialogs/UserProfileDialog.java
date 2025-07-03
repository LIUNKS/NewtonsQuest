package Controlador.dialogs;

import Modelo.UsuarioDAO;
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
 * Diálogo para mostrar el perfil del usuario
 */
public class UserProfileDialog {
    
    private Stage dialogStage;
    private String currentUsername;
    private int currentUserId;
    
    public UserProfileDialog(Stage parentStage, String username, int userId) {
        this.currentUsername = username;
        this.currentUserId = userId;
        createDialog(parentStage);
    }
    
    private void createDialog(Stage parentStage) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.initStyle(StageStyle.DECORATED);        dialogStage.setTitle("Perfil de Usuario - " + currentUsername);
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
    }private String[] getUserData() {
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
            System.err.println("Error al obtener datos del usuario: " + e.getMessage());
            e.printStackTrace();
            return new String[]{"", "", "No disponible"};
        }
    }
    
    private String[] getUserStats() {
        try {
            // Obtener estadísticas del usuario desde la tabla usuarios
            Controlador.componentes.RankingManager rankingManager = Controlador.componentes.RankingManager.getInstance();
            
            String mejorPuntaje = "0";
            String posicionRanking = "No clasificado";
            String formulasCompletadas = "0/5";
            String ultimaPartida = "Nunca";
            
            // Obtener estadísticas básicas desde la tabla usuarios
            try {
                // Primero intentar sincronizar datos del ranking si existen
                Modelo.UsuarioDAO.sincronizarDatosRankingAUsuarios(currentUserId);
                
                int puntaje = Modelo.UsuarioDAO.obtenerMejorPuntajeUsuario(currentUserId);
                mejorPuntaje = String.valueOf(puntaje);
                
                int formulas = Modelo.UsuarioDAO.obtenerFormulasCompletadasUsuario(currentUserId);
                formulasCompletadas = formulas + "/5";
                if (formulas >= 5) {
                    formulasCompletadas += " ✅";
                }
                
                ultimaPartida = Modelo.UsuarioDAO.obtenerUltimaPartidaUsuario(currentUserId);
                
            } catch (Exception e) {
                System.err.println("Error al obtener estadísticas básicas del usuario: " + e.getMessage());
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
            System.err.println("Error al obtener estadísticas del usuario: " + e.getMessage());
            return new String[]{"0", "No clasificado", "0/5", "Nunca"};
        }
    }
    
    public void showAndWait() {
        dialogStage.showAndWait();
    }
    
    public void show() {
        dialogStage.show();
    }
}
