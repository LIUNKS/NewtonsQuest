package Controlador.dialogs;

import Controlador.componentes.RankingManager;
import Modelo.RankingEntry;
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

import java.util.List;

/**
 * Diálogo para mostrar el ranking de jugadores que han completado las 5 fórmulas
 */
public class RankingDialog {
    
    private Stage dialogStage;
    private RankingManager rankingManager;
    
    public RankingDialog(Stage parentStage) {
        this.rankingManager = RankingManager.getInstance();
        createDialog(parentStage);
    }
    
    private void createDialog(Stage parentStage) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.setTitle("Ranking de Maestros de la Física");
        dialogStage.setResizable(false);
        
        // Crear el contenido del diálogo
        VBox mainContent = createMainContent();
        
        // Crear la escena
        Scene scene = new Scene(mainContent, 650, 500);
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
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");
        
        // Título
        Label titleLabel = new Label("🏆 RANKING DE MAESTROS DE LA FÍSICA 🏆");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.GOLD);
        titleLabel.setAlignment(Pos.CENTER);
        
        // Subtítulo
        Label subtitleLabel = new Label("Jugadores que han desbloqueado las 5 fórmulas de Newton");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);
        subtitleLabel.setAlignment(Pos.CENTER);
        
        // Crear la tabla de ranking
        ScrollPane rankingScrollPane = createRankingTable();
        
        // Botón de cerrar
        Button closeButton = new Button("Cerrar");
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
        
        // Layout del botón
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(closeButton);
        
        mainContent.getChildren().addAll(titleLabel, subtitleLabel, rankingScrollPane, buttonBox);
        
        return mainContent;
    }
    
    private ScrollPane createRankingTable() {
        VBox rankingContent = new VBox(5);
        rankingContent.setPadding(new Insets(10));
        
        // Obtener los datos del ranking
        List<RankingEntry> rankingEntries = rankingManager.getTopPlayers(20); // Top 20
        
        if (rankingEntries.isEmpty()) {
            Label noDataLabel = new Label("😔 Aún no hay maestros en el ranking");
            noDataLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            noDataLabel.setTextFill(Color.LIGHTGRAY);
            noDataLabel.setAlignment(Pos.CENTER);
            rankingContent.getChildren().add(noDataLabel);
        } else {
            // Crear encabezado
            HBox header = createRankingHeader();
            rankingContent.getChildren().add(header);
            
            // Agregar separador
            Separator separator = new Separator();
            separator.setStyle("-fx-background-color: #34495e;");
            rankingContent.getChildren().add(separator);
            
            // Agregar cada entrada del ranking
            for (int i = 0; i < rankingEntries.size(); i++) {
                RankingEntry entry = rankingEntries.get(i);
                HBox rankingRow = createRankingRow(i + 1, entry);
                rankingContent.getChildren().add(rankingRow);
            }
        }
        
        // Crear el ScrollPane
        ScrollPane scrollPane = new ScrollPane(rankingContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle(
            "-fx-background: transparent; " +
            "-fx-background-color: rgba(255,255,255,0.1); " +
            "-fx-border-color: #34495e; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );
        
        return scrollPane;
    }
    
    private HBox createRankingHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(5, 10, 5, 10));
        header.setStyle("-fx-background-color: rgba(52, 73, 94, 0.8); -fx-background-radius: 5;");
        
        Label positionLabel = new Label("Pos");
        positionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        positionLabel.setTextFill(Color.WHITE);
        positionLabel.setPrefWidth(50);
        
        Label usernameLabel = new Label("Usuario");
        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        usernameLabel.setTextFill(Color.WHITE);
        usernameLabel.setPrefWidth(200);
        
        Label scoreLabel = new Label("Puntaje");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setPrefWidth(100);
        
        Label dateLabel = new Label("Fecha de Logro");
        dateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setPrefWidth(150);
        
        header.getChildren().addAll(positionLabel, usernameLabel, scoreLabel, dateLabel);
        
        return header;
    }
    
    private HBox createRankingRow(int position, RankingEntry entry) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 10, 8, 10));
        
        // Estilo especial para los primeros 3 puestos
        String backgroundColor;
        String textColor;
        String medal = "";
        
        if (position == 1) {
            backgroundColor = "rgba(255, 215, 0, 0.3)"; // Dorado
            textColor = "#FFD700";
            medal = "🥇 ";
        } else if (position == 2) {
            backgroundColor = "rgba(192, 192, 192, 0.3)"; // Plateado
            textColor = "#C0C0C0";
            medal = "🥈 ";
        } else if (position == 3) {
            backgroundColor = "rgba(205, 127, 50, 0.3)"; // Bronce
            textColor = "#CD7F32";
            medal = "🥉 ";
        } else {
            backgroundColor = "rgba(255, 255, 255, 0.1)";
            textColor = "#FFFFFF";
        }
        
        row.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background-radius: 3;");
        
        // Verificar si es el usuario actual
        boolean isCurrentUser = rankingManager.getCurrentUserId() == entry.getUserId();
        if (isCurrentUser) {
            row.setStyle(row.getStyle() + " -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 3;");
        }
        
        Label positionLabel = new Label(medal + position);
        positionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        positionLabel.setTextFill(Color.web(textColor));
        positionLabel.setPrefWidth(50);
        
        Label usernameLabel = new Label(entry.getUsername() + (isCurrentUser ? " (Tú)" : ""));
        usernameLabel.setFont(Font.font("Arial", isCurrentUser ? FontWeight.BOLD : FontWeight.NORMAL, 14));
        usernameLabel.setTextFill(Color.web(isCurrentUser ? "#3498db" : textColor));
        usernameLabel.setPrefWidth(200);
        
        Label scoreLabel = new Label(String.valueOf(entry.getScore()));
        scoreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        scoreLabel.setTextFill(Color.web(textColor));
        scoreLabel.setPrefWidth(100);
        
        Label dateLabel = new Label(entry.getFormattedCompletionDate());
        dateLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        dateLabel.setTextFill(Color.web(textColor));
        dateLabel.setPrefWidth(150);
        
        row.getChildren().addAll(positionLabel, usernameLabel, scoreLabel, dateLabel);
        
        return row;
    }
    
    public void showAndWait() {
        dialogStage.showAndWait();
    }
    
    public void show() {
        dialogStage.show();
    }
}
