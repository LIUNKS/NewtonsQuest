package Controlador.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Diálogo de configuración del juego (placeholder para futuras funcionalidades)
 */
public class SettingsDialog {
    
    private Stage settingsStage;
    private Stage parentStage;
    
    public SettingsDialog(Stage parentStage) {
        this.parentStage = parentStage;
        initializeDialog();
    }
    
    private void initializeDialog() {
        settingsStage = new Stage();
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.initStyle(StageStyle.DECORATED);
        settingsStage.setTitle("Newton's Apple Quest - Configuración");
        settingsStage.setResizable(false);
        
        // Contenedor principal
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0f0c29, #1a1a2e, #24243e);");
        
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.05);" +
            "-fx-background-radius: 20px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0, 0, 5);"
        );
        
        // Título
        Label title = new Label("⚙️ CONFIGURACIÓN");
        title.setFont(Font.font("System Bold", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #ff6b6b;");
        
        // Mensaje temporal
        Label message = new Label("Próximamente: Configuración de audio, controles y más...");
        message.setFont(Font.font("System", 16));
        message.setStyle("-fx-text-fill: white;");
        message.setWrapText(true);
        
        // Botón cerrar
        Button closeButton = new Button("Cerrar");
        closeButton.setFont(Font.font("System Bold", FontWeight.BOLD, 14));
        closeButton.setStyle(
            "-fx-background-color: #e94560;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 20px;"
        );
        closeButton.setOnAction(e -> settingsStage.close());
        
        mainContainer.getChildren().addAll(title, message, closeButton);
        root.getChildren().add(mainContainer);
        
        Scene scene = new Scene(root, 400, 250);
        settingsStage.setScene(scene);
        
        // Centrar ventana
        if (parentStage != null) {
            settingsStage.setX(parentStage.getX() + (parentStage.getWidth() - 400) / 2);
            settingsStage.setY(parentStage.getY() + (parentStage.getHeight() - 250) / 2);
        }
    }
    
    public void showAndWait() {
        settingsStage.showAndWait();
    }
}
