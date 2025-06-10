package Controlador.dialogs;

import Controlador.constants.GameConstants;
import Controlador.utils.StyleUtils;
import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Diálogo personalizado para mostrar las reglas del juego.
 * Mantiene el estilo visual consistente con el tema del juego.
 */
public class RulesDialog {
    
    private Stage rulesStage;
    private Stage parentStage;
    
    public RulesDialog(Stage parentStage) {
        this.parentStage = parentStage;
        initializeDialog();
    }
    
    private void initializeDialog() {
        // Crear una nueva ventana (Stage) personalizada
        rulesStage = new Stage();
        rulesStage.initModality(Modality.APPLICATION_MODAL);
        rulesStage.initStyle(StageStyle.DECORATED);        rulesStage.setTitle(GameConstants.RULES_TITLE);
        rulesStage.setResizable(false);
          // Contenedor principal con el fondo del juego
        StackPane root = new StackPane();
        StyleUtils.applyGameBackground(root);
        
        // Contenedor del contenido con scroll
        VBox mainContainer = createMainContainer();
        
        // Título principal con estilo del juego
        Text mainTitle = createMainTitle();
        
        // Descripción principal
        Text description = createDescription();
        
        // Contenedor para las secciones con scroll
        VBox contentContainer = createContentContainer();
        
        // ScrollPane con estilo personalizado
        ScrollPane scrollPane = createScrollPane(contentContainer);
        
        // Botón de cierre con estilo del juego
        Button closeButton = createCloseButton();
        
        // Añadir todo al contenedor principal
        mainContainer.getChildren().addAll(mainTitle, description, scrollPane, closeButton);
        
        // Añadir al root
        root.getChildren().add(mainContainer);
          // Configurar la escena
        Scene rulesScene = new Scene(root, GameConstants.RULES_DIALOG_WIDTH, GameConstants.RULES_DIALOG_HEIGHT);
        
        // Intentar aplicar los estilos CSS del juego
        applyCSSStyles(rulesScene);
        
        rulesStage.setScene(rulesScene);
        
        // Centrar la ventana en relación a la ventana principal
        centerWindow();
    }
      private VBox createMainContainer() {
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(30, 40, 30, 40));
        StyleUtils.applyTransparentContainer(mainContainer);
        return mainContainer;
    }
      private Text createMainTitle() {
        Text mainTitle = new Text("🍎 REGLAS Y GUÍA DE JUEGO 🍎");
        StyleUtils.applyMainTitleStyle(mainTitle);
        return mainTitle;
    }
    
    private Text createDescription() {
        Text description = new Text("Una aventura educativa inspirada en Isaac Newton");
        StyleUtils.applySecondaryTitleStyle(description);
        description.setFont(Font.font("System", 16)); // Override font size
        return description;
    }
    
    private VBox createContentContainer() {
        VBox contentContainer = new VBox(25);
        contentContainer.setPadding(new Insets(20, 0, 20, 0));
        
        // Crear todas las secciones
        VBox objectiveSection = RulesSectionFactory.createObjectiveSection();
        VBox controlsSection = RulesSectionFactory.createControlsSection();
        VBox pointsSection = RulesSectionFactory.createPointsSection();
        VBox objectsSection = RulesSectionFactory.createObjectsSection();
        VBox progressSection = RulesSectionFactory.createProgressSection();
        VBox tipsSection = RulesSectionFactory.createTipsSection();
        
        // Añadir todas las secciones al contenedor
        contentContainer.getChildren().addAll(
            objectiveSection, controlsSection, pointsSection,
            objectsSection, progressSection, tipsSection
        );
        
        return contentContainer;
    }
      private ScrollPane createScrollPane(VBox contentContainer) {
        ScrollPane scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(600, 400);
        scrollPane.setStyle(StyleUtils.getTransparentScrollPaneStyle());
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }
      private Button createCloseButton() {
        Button closeButton = new Button("¡Entendido, a jugar! 🚀");
        StyleUtils.applyPrimaryButtonStyle(closeButton);
        closeButton.setOnAction(e -> rulesStage.close());
        return closeButton;
    }
    
    private void applyCSSStyles(Scene rulesScene) {
        try {
            File mainCssFile = new File("src/Vista/main.css");
            if (mainCssFile.exists()) {
                rulesScene.getStylesheets().add(mainCssFile.toURI().toURL().toExternalForm());
            } else {
                // Fallback para producción
                String cssPath = getClass().getResource("/Vista/main.css").toExternalForm();
                rulesScene.getStylesheets().add(cssPath);
            }
        } catch (Exception cssEx) {
            System.err.println("No se pudo cargar CSS, usando estilos inline: " + cssEx.getMessage());
        }
    }
      private void centerWindow() {
        if (parentStage != null) {
            rulesStage.setX(parentStage.getX() + (parentStage.getWidth() - GameConstants.RULES_DIALOG_WIDTH) / 2);
            rulesStage.setY(parentStage.getY() + (parentStage.getHeight() - GameConstants.RULES_DIALOG_HEIGHT) / 2);
        }
    }
    
    /**
     * Muestra el diálogo de reglas de forma modal
     */
    public void showAndWait() {
        try {
            rulesStage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error al mostrar las reglas: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: mostrar reglas básicas en consola
            showFallbackRules();
        }
    }
    
    private void showFallbackRules() {
        System.out.println("=== REGLAS DE NEWTON'S APPLE QUEST ===");
        System.out.println("Objetivo: Atrapa manzanas rojas (+10 pts) y evita las verdes (-5 pts)");
        System.out.println("Controles: Flechas para mover, ESC para pausar");
        System.out.println("Vidas: Pierdes vida con manzanas verdes o rojas perdidas");
        System.out.println("Fórmulas: Desbloquea fórmulas de Newton con puntos");
    }
}
