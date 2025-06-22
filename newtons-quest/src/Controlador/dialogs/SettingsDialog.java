package Controlador.dialogs;

import Controlador.utils.GameSettings;
import Controlador.utils.StyleUtils;
import Controlador.constants.GameConstants;
import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Diálogo de configuración del juego con controles de volumen, brillo y otras opciones
 */
public class SettingsDialog {
    
    private Stage settingsStage;
    private Stage parentStage;
    private GameSettings gameSettings;
    
    // Controles de configuración
    private Slider musicVolumeSlider;
    private Slider effectVolumeSlider;
    private Slider brightnessSlider;
    private CheckBox fullscreenCheckbox;
    
    // Callback para aplicar cambios de brillo
    private Runnable onBrightnessChanged;
    private Runnable onVolumeChanged;
      public SettingsDialog(Stage parentStage) {
        this.parentStage = parentStage;
        this.gameSettings = GameSettings.getInstance();
        initializeDialog();
    }
    
    /**
     * Constructor con callbacks para aplicar cambios
     */
    public SettingsDialog(Stage parentStage, Runnable onBrightnessChanged, Runnable onVolumeChanged) {
        this.parentStage = parentStage;
        this.gameSettings = GameSettings.getInstance();
        this.onBrightnessChanged = onBrightnessChanged;
        this.onVolumeChanged = onVolumeChanged;
        initializeDialog();
    }
      private void initializeDialog() {
        settingsStage = new Stage();
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.initStyle(StageStyle.DECORATED);
        settingsStage.setTitle(GameConstants.SETTINGS_TITLE);
        settingsStage.setResizable(false);
        
        // Contenedor principal
        StackPane root = new StackPane();
        StyleUtils.applyGameBackground(root);
        
        // Contenedor principal con scroll
        VBox mainContainer = createMainContainer();
        
        // Título principal
        Text title = createMainTitle();
        
        // Secciones de configuración
        VBox audioSection = createAudioSection();
        VBox visualSection = createVisualSection();
        VBox generalSection = createGeneralSection();
        
        // Botones de acción
        HBox buttonContainer = createButtonContainer();
        
        // Añadir todo al contenedor principal
        mainContainer.getChildren().addAll(
            title,
            audioSection,
            new Separator(),
            visualSection,
            new Separator(),
            generalSection,
            buttonContainer
        );
        
        // ScrollPane para el contenido
        ScrollPane scrollPane = createScrollPane(mainContainer);
        root.getChildren().add(scrollPane);
        
        // Configurar la escena
        Scene scene = new Scene(root, 500, 600);
        applyCSSStyles(scene);
        settingsStage.setScene(scene);
        
        // Centrar ventana
        centerWindow();
    }
      public void showAndWait() {
        settingsStage.showAndWait();
    }
    
    private VBox createMainContainer() {
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(30, 40, 30, 40));
        StyleUtils.applyTransparentContainer(mainContainer);
        return mainContainer;
    }
    
    private Text createMainTitle() {
        Text title = new Text("⚙️ CONFIGURACIÓN DEL JUEGO");
        StyleUtils.applyMainTitleStyle(title);
        return title;
    }
    
    private VBox createAudioSection() {
        VBox audioSection = new VBox(15);
        audioSection.setAlignment(Pos.CENTER_LEFT);
        
        // Título de la sección
        Text audioTitle = new Text("🔊 AUDIO");
        StyleUtils.applySecondaryTitleStyle(audioTitle);
        
        // Control de volumen de música
        VBox musicVolumeBox = createSliderControl(
            "Volumen de Música",
            0.0, 1.0, gameSettings.getMusicVolume(),
            (observable, oldValue, newValue) -> {
                gameSettings.setMusicVolume(newValue.doubleValue());
                if (onVolumeChanged != null) {
                    onVolumeChanged.run();
                }
            }
        );
        musicVolumeSlider = (Slider) ((VBox) musicVolumeBox.getChildren().get(1)).getChildren().get(1);
        
        // Control de volumen de efectos
        VBox effectVolumeBox = createSliderControl(
            "Volumen de Efectos",
            0.0, 1.0, gameSettings.getEffectVolume(),
            (observable, oldValue, newValue) -> {
                gameSettings.setEffectVolume(newValue.doubleValue());
                if (onVolumeChanged != null) {
                    onVolumeChanged.run();
                }
            }
        );
        effectVolumeSlider = (Slider) ((VBox) effectVolumeBox.getChildren().get(1)).getChildren().get(1);
        
        audioSection.getChildren().addAll(audioTitle, musicVolumeBox, effectVolumeBox);
        return audioSection;
    }
    
    private VBox createVisualSection() {
        VBox visualSection = new VBox(15);
        visualSection.setAlignment(Pos.CENTER_LEFT);
        
        // Título de la sección
        Text visualTitle = new Text("🎨 VISUALES");
        StyleUtils.applySecondaryTitleStyle(visualTitle);
        
        // Control de brillo
        VBox brightnessBox = createSliderControl(
            "Brillo de Pantalla",
            0.1, 2.0, gameSettings.getBrightness(),
            (observable, oldValue, newValue) -> {
                gameSettings.setBrightness(newValue.doubleValue());
                if (onBrightnessChanged != null) {
                    onBrightnessChanged.run();
                }
            }
        );
        brightnessSlider = (Slider) ((VBox) brightnessBox.getChildren().get(1)).getChildren().get(1);
        
        visualSection.getChildren().addAll(visualTitle, brightnessBox);
        return visualSection;
    }
    
    private VBox createGeneralSection() {
        VBox generalSection = new VBox(15);
        generalSection.setAlignment(Pos.CENTER_LEFT);
        
        // Título de la sección
        Text generalTitle = new Text("⚙️ GENERAL");
        StyleUtils.applySecondaryTitleStyle(generalTitle);
        
        // Checkbox para pantalla completa
        fullscreenCheckbox = new CheckBox("Pantalla Completa");
        fullscreenCheckbox.setSelected(gameSettings.isFullscreen());
        fullscreenCheckbox.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        fullscreenCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            gameSettings.setFullscreen(newValue);
        });
        
        generalSection.getChildren().addAll(generalTitle, fullscreenCheckbox);
        return generalSection;
    }
    
    private VBox createSliderControl(String labelText, double min, double max, double currentValue,
                                   javafx.beans.value.ChangeListener<Number> changeListener) {
        VBox container = new VBox(8);
        container.setAlignment(Pos.CENTER_LEFT);
        
        // Etiqueta
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        // Container para slider y valor
        VBox sliderContainer = new VBox(5);
        sliderContainer.setAlignment(Pos.CENTER_LEFT);
        
        // Slider
        Slider slider = new Slider(min, max, currentValue);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4);
        slider.setPrefWidth(300);
        slider.setStyle(
            "-fx-control-inner-background: rgba(255, 255, 255, 0.1);" +
            "-fx-accent: " + GameConstants.PRIMARY_COLOR + ";"
        );
        
        // Etiqueta del valor actual
        Label valueLabel = new Label(String.format("%.0f%%", currentValue * 100));
        valueLabel.setStyle("-fx-text-fill: " + GameConstants.SECONDARY_COLOR + "; -fx-font-size: 12px;");
        
        // Actualizar etiqueta cuando cambie el slider
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (max <= 2.0) { // Para brillo, mostrar como multiplicador
                valueLabel.setText(String.format("%.1fx", newValue.doubleValue()));
            } else {
                valueLabel.setText(String.format("%.0f%%", newValue.doubleValue() * 100));
            }
            changeListener.changed(observable, oldValue, newValue);
        });
        
        sliderContainer.getChildren().addAll(valueLabel, slider);
        container.getChildren().addAll(label, sliderContainer);
        
        return container;
    }
    
    private HBox createButtonContainer() {
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(20, 0, 0, 0));
        
        // Botón para restaurar valores por defecto
        Button resetButton = new Button("Restaurar Predeterminados");
        StyleUtils.applySecondaryButtonStyle(resetButton);
        resetButton.setOnAction(e -> resetToDefaults());
        
        // Botón guardar y cerrar
        Button saveButton = new Button("Guardar y Cerrar");
        StyleUtils.applyPrimaryButtonStyle(saveButton);
        saveButton.setOnAction(e -> saveAndClose());
        
        // Botón cancelar
        Button cancelButton = new Button("Cancelar");
        cancelButton.setStyle(
            "-fx-background-color: #6c757d;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-font-weight: bold;"
        );
        cancelButton.setOnAction(e -> settingsStage.close());
        
        buttonContainer.getChildren().addAll(resetButton, cancelButton, saveButton);
        return buttonContainer;
    }
    
    private ScrollPane createScrollPane(VBox content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(500, 600);
        scrollPane.setStyle(StyleUtils.getTransparentScrollPaneStyle());
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }
    
    private void applyCSSStyles(Scene scene) {
        try {
            File mainCssFile = new File("src/Vista/resources/main.css");
            if (mainCssFile.exists()) {
                scene.getStylesheets().add(mainCssFile.toURI().toURL().toExternalForm());            } else {
                // Fallback para producción
                String cssPath = getClass().getResource("/Vista/resources/main.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
            }
        } catch (Exception cssEx) {
            System.err.println("No se pudo cargar CSS para el diálogo de configuración: " + cssEx.getMessage());
        }
    }
    
    private void centerWindow() {
        if (parentStage != null) {
            settingsStage.setX(parentStage.getX() + (parentStage.getWidth() - 500) / 2);
            settingsStage.setY(parentStage.getY() + (parentStage.getHeight() - 600) / 2);
        }
    }
    
    private void resetToDefaults() {
        // Restaurar sliders a valores por defecto
        musicVolumeSlider.setValue(0.5);
        effectVolumeSlider.setValue(0.7);
        brightnessSlider.setValue(1.0);
        fullscreenCheckbox.setSelected(false);
        
        // Aplicar cambios inmediatamente
        gameSettings.resetToDefaults();
        
        // Ejecutar callbacks si están disponibles
        if (onVolumeChanged != null) {
            onVolumeChanged.run();
        }
        if (onBrightnessChanged != null) {
            onBrightnessChanged.run();
        }
        
        System.out.println("Configuraciones restauradas a valores por defecto");
    }
    
    private void saveAndClose() {
        gameSettings.saveSettings();
        settingsStage.close();
        System.out.println("Configuraciones guardadas correctamente");
    }
}
