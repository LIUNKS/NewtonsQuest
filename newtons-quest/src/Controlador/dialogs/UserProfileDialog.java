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

import java.sql.SQLException;
import java.util.Optional;

/**
 * Diálogo para mostrar y editar el perfil del usuario
 */
public class UserProfileDialog {
    
    private Stage dialogStage;
    private String currentUsername;
    private int currentUserId;
    
    // Campos editables
    private TextField nombreCompletoField;
    private TextField correoField;
    
    public UserProfileDialog(Stage parentStage, String username, int userId) {
        this.currentUsername = username;
        this.currentUserId = userId;
        createDialog(parentStage);
    }
    
    private void createDialog(Stage parentStage) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.setTitle("Perfil de Usuario - " + currentUsername);
        dialogStage.setResizable(false);
        
        // Crear el contenido del diálogo
        VBox mainContent = createMainContent();
        
        // Crear la escena
        Scene scene = new Scene(mainContent, 500, 600);
        scene.getStylesheets().add(getClass().getResource("/Vista/main.css").toExternalForm());
        
        dialogStage.setScene(scene);
        
        // Centrar el diálogo en la ventana padre
        dialogStage.setOnShown(e -> {
            dialogStage.setX(parentStage.getX() + (parentStage.getWidth() - dialogStage.getWidth()) / 2);
            dialogStage.setY(parentStage.getY() + (parentStage.getHeight() - dialogStage.getHeight()) / 2);
        });
    }
    
    private VBox createMainContent() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(25));
        mainContent.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");
        
        // Título
        Label titleLabel = new Label("👤 PERFIL DE USUARIO");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.GOLD);
        titleLabel.setAlignment(Pos.CENTER);
        
        // Contenedor para la información del usuario
        VBox userInfoContainer = createUserInfoSection();
        
        // Contenedor para estadísticas del juego
        VBox statsContainer = createStatsSection();
        
        // Botones de acción
        HBox buttonsContainer = createButtonsSection();
        
        mainContent.getChildren().addAll(titleLabel, userInfoContainer, statsContainer, buttonsContainer);
        
        return mainContent;
    }
    
    private VBox createUserInfoSection() {
        VBox container = new VBox(15);
        container.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1); " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #3498db; " +
            "-fx-border-radius: 10; " +
            "-fx-border-width: 2;"
        );
        container.setPadding(new Insets(20));
        
        // Título de la sección
        Label sectionTitle = new Label("📋 Información Personal");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.LIGHTBLUE);
        
        // Obtener datos del usuario desde la base de datos
        String[] userData = getUserData();
        
        // Campo de nombre de usuario (no editable)
        VBox usernameBox = createReadOnlyField("Nombre de Usuario:", currentUsername, "👤");
        
        // Campo de nombre completo (editable)
        VBox nombreBox = createEditableField("Nombre Completo:", userData[0], "📝");
        nombreCompletoField = (TextField) ((HBox) nombreBox.getChildren().get(1)).getChildren().get(1);
        
        // Campo de correo (editable)
        VBox correoBox = createEditableField("Correo Electrónico:", userData[1], "📧");
        correoField = (TextField) ((HBox) correoBox.getChildren().get(1)).getChildren().get(1);
        
        // Campo de fecha de registro (no editable)
        VBox fechaBox = createReadOnlyField("Fecha de Registro:", userData[2], "📅");
        
        container.getChildren().addAll(sectionTitle, usernameBox, nombreBox, correoBox, fechaBox);
        
        return container;
    }
    
    private VBox createStatsSection() {
        VBox container = new VBox(15);
        container.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1); " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #e74c3c; " +
            "-fx-border-radius: 10; " +
            "-fx-border-width: 2;"
        );
        container.setPadding(new Insets(20));
        
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
        VBox fieldBox = new VBox(5);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.WHITE);
        
        HBox valueBox = new HBox(10);
        valueBox.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 16));
        
        Label valueLabel = new Label(value.isEmpty() ? "No disponible" : value);
        valueLabel.setFont(Font.font("Arial", 14));
        valueLabel.setTextFill(Color.LIGHTGRAY);
        valueLabel.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-padding: 8; -fx-background-radius: 5;");
        valueLabel.setPrefWidth(300);
        
        valueBox.getChildren().addAll(iconLabel, valueLabel);
        fieldBox.getChildren().addAll(label, valueBox);
        
        return fieldBox;
    }
    
    private VBox createEditableField(String labelText, String value, String icon) {
        VBox fieldBox = new VBox(5);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.WHITE);
        
        HBox valueBox = new HBox(10);
        valueBox.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 16));
        
        TextField textField = new TextField(value);
        textField.setFont(Font.font("Arial", 14));
        textField.setPrefWidth(300);
        textField.setStyle(
            "-fx-background-color: rgba(255,255,255,0.9); " +
            "-fx-text-fill: black; " +
            "-fx-background-radius: 5; " +
            "-fx-border-color: #3498db; " +
            "-fx-border-radius: 5;"
        );
        
        valueBox.getChildren().addAll(iconLabel, textField);
        fieldBox.getChildren().addAll(label, valueBox);
        
        return fieldBox;
    }
    
    private HBox createButtonsSection() {
        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        
        // Botón Guardar
        Button saveButton = new Button("💾 Guardar Cambios");
        saveButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        saveButton.setPrefWidth(150);
        saveButton.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
        );
        saveButton.setOnAction(e -> saveChanges());
        
        // Botón Cerrar
        Button closeButton = new Button("❌ Cerrar");
        closeButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        closeButton.setPrefWidth(120);
        closeButton.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
        );
        closeButton.setOnAction(e -> dialogStage.close());
        
        buttonsBox.getChildren().addAll(saveButton, closeButton);
        
        return buttonsBox;
    }
    
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
            System.err.println("Error al obtener datos del usuario: " + e.getMessage());
            return new String[]{"", "", "No disponible"};
        }
    }
    
    private String[] getUserStats() {
        try {
            // Obtener estadísticas del usuario
            Controlador.componentes.RankingManager rankingManager = Controlador.componentes.RankingManager.getInstance();
            
            String mejorPuntaje = "0";
            String posicionRanking = "No clasificado";
            String formulasCompletadas = "0/5";
            String ultimaPartida = "Nunca";
            
            // Verificar si el usuario está en el ranking
            int posicion = rankingManager.getCurrentUserPosition();
            if (posicion > 0) {
                posicionRanking = "#" + posicion;
            }
            
            // Obtener mejor puntaje desde RankingDAO si existe
            try {
                int puntaje = Modelo.RankingDAO.obtenerMejorPuntaje(currentUserId);
                if (puntaje > 0) {
                    mejorPuntaje = String.valueOf(puntaje);
                    formulasCompletadas = "5/5 ✅";
                    
                    // Obtener fecha de última completación
                    java.sql.Timestamp fechaCompletacion = Modelo.RankingDAO.obtenerFechaCompletacion(currentUserId);
                    if (fechaCompletacion != null) {
                        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        ultimaPartida = formatter.format(fechaCompletacion);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al obtener estadísticas del ranking: " + e.getMessage());
            }
            
            return new String[]{mejorPuntaje, posicionRanking, formulasCompletadas, ultimaPartida};
        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas del usuario: " + e.getMessage());
            return new String[]{"0", "No clasificado", "0/5", "Nunca"};
        }
    }
    
    private void saveChanges() {
        try {
            String nuevoNombreCompleto = nombreCompletoField.getText().trim();
            String nuevoCorreo = correoField.getText().trim();
            
            // Validaciones básicas
            if (nuevoNombreCompleto.isEmpty()) {
                showAlert("Error", "El nombre completo no puede estar vacío.");
                return;
            }
            
            if (!nuevoCorreo.isEmpty() && !isValidEmail(nuevoCorreo)) {
                showAlert("Error", "Por favor, ingrese un correo electrónico válido.");
                return;
            }
            
            // Guardar cambios en la base de datos
            boolean success = UsuarioDAO.actualizarPerfilUsuario(currentUserId, nuevoNombreCompleto, nuevoCorreo);
            
            if (success) {
                showAlert("Éxito", "Los cambios han sido guardados exitosamente.");
            } else {
                showAlert("Error", "No se pudieron guardar los cambios. Inténtelo de nuevo.");
            }
            
        } catch (Exception e) {
            System.err.println("Error al guardar cambios: " + e.getMessage());
            showAlert("Error", "Ocurrió un error al guardar los cambios: " + e.getMessage());
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void showAndWait() {
        dialogStage.showAndWait();
    }
    
    public void show() {
        dialogStage.show();
    }
}
