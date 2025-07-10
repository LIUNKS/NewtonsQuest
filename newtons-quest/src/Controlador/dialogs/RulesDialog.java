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
 * Diálogo de reglas del juego.
 * 
 * Esta clase proporciona una interfaz visual completa para mostrar las reglas
 * del juego de manera clara y organizada. Incluye información sobre objetivos,
 * controles, sistema de puntuación, elementos del juego y progresión.
 * 
 * Características:
 * - Interfaz visual consistente con el tema del juego
 * - Contenido organizado en secciones con scroll
 * - Botón de cierre con estilo del juego
 * - Ventana modal centrada automáticamente
 * - Aplicación de estilos CSS del juego
 */
public class RulesDialog {
    
    /** Escenario del diálogo de reglas */
    private Stage rulesStage;
    
    /** Escenario padre para centrar el diálogo */
    private Stage parentStage;
    
    /**
     * Constructor del diálogo de reglas.
     * 
     * @param parentStage Escenario padre que servirá como referencia para centrar el diálogo
     */
    public RulesDialog(Stage parentStage) {
        this.parentStage = parentStage;
        initializeDialog();
    }
    
    /**
     * Inicializa y configura el diálogo de reglas completo.
     */
    private void initializeDialog() {
        // === Configuración del escenario ===
        rulesStage = new Stage();
        rulesStage.initModality(Modality.APPLICATION_MODAL);
        rulesStage.initStyle(StageStyle.DECORATED);
        rulesStage.setTitle(GameConstants.RULES_TITLE);
        rulesStage.setResizable(false);
        
        // === Contenedor principal con fondo del juego ===
        StackPane root = new StackPane();
        StyleUtils.applyGameBackground(root);
        
        // === Construcción del contenido ===
        VBox mainContainer = createMainContainer();
        Text mainTitle = createMainTitle();
        Text description = createDescription();
        VBox contentContainer = createContentContainer();
        ScrollPane scrollPane = createScrollPane(contentContainer);
        Button closeButton = createCloseButton();
        
        // === Ensamblaje del contenido ===
        mainContainer.getChildren().addAll(mainTitle, description, scrollPane, closeButton);
        root.getChildren().add(mainContainer);
        
        // === Configuración de la escena ===
        Scene rulesScene = new Scene(root, GameConstants.RULES_DIALOG_WIDTH, GameConstants.RULES_DIALOG_HEIGHT);
        applyCSSStyles(rulesScene);
        
        rulesStage.setScene(rulesScene);
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
        VBox specialMechanicsSection = RulesSectionFactory.createSpecialMechanicsSection();
        VBox tipsSection = RulesSectionFactory.createTipsSection();
        
        // Añadir todas las secciones al contenedor
        contentContainer.getChildren().addAll(
            objectiveSection, controlsSection, pointsSection,
            objectsSection, progressSection, specialMechanicsSection, tipsSection
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
    
    /**
     * Aplica los estilos CSS del juego a la escena del diálogo.
     * 
     * @param rulesScene Escena donde aplicar los estilos
     */
    private void applyCSSStyles(Scene rulesScene) {
        // === Aplicación de estilos CSS desde directorio de desarrollo ===
        File mainCssFile = new File("src/Vista/resources/main.css");
        if (mainCssFile.exists()) {
            rulesScene.getStylesheets().add(mainCssFile.toURI().toString());
        }
    }
    
    /**
     * Centra la ventana del diálogo respecto a la ventana padre.
     */
    private void centerWindow() {
        if (parentStage != null) {
            rulesStage.setX(parentStage.getX() + (parentStage.getWidth() - GameConstants.RULES_DIALOG_WIDTH) / 2);
            rulesStage.setY(parentStage.getY() + (parentStage.getHeight() - GameConstants.RULES_DIALOG_HEIGHT) / 2);
        }
    }
    
    /**
     * Muestra el diálogo de reglas de forma modal.
     */
    public void showAndWait() {
        rulesStage.showAndWait();
    }
}
