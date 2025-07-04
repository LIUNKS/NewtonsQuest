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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Diálogo de configuración del juego con controles de volumen y brillo
 */
public class SettingsDialog {
    
    private Stage settingsStage;
    private Stage parentStage;
    private GameSettings gameSettings;    // Controles de configuración
    private Slider musicVolumeSlider;
    private Slider brightnessSlider;
    
    // Variables para guardar valores originales
    private double originalMusicVolume;
    private double originalBrightness;
    
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
    }    private void initializeDialog() {
        // Guardar valores originales para poder cancelar
        originalMusicVolume = gameSettings.getMusicVolume();
        originalBrightness = gameSettings.getBrightness();
        
        settingsStage = new Stage();
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.initStyle(StageStyle.DECORATED);        settingsStage.setTitle(GameConstants.SETTINGS_TITLE);        settingsStage.setResizable(true);
        settingsStage.setMinWidth(650);
        settingsStage.setMinHeight(480);
        
        // Configurar el comportamiento al cerrar
        settingsStage.setOnCloseRequest(e -> {
            e.consume(); // Prevenir el cierre automático
            cancelAndClose(); // Usar el método de cancelar
        });
        
        // Contenedor principal
        StackPane root = new StackPane();
        StyleUtils.applyGameBackground(root);
        
        // Contenedor principal
        VBox mainContainer = createMainContainer();
        
        // Título principal
        Text title = createMainTitle();
          // Secciones de configuración
        VBox audioSection = createAudioSection();
        VBox visualSection = createVisualSection();
        
        // Botones de acción
        HBox buttonContainer = createButtonContainer();
          // Añadir todo al contenedor principal
        mainContainer.getChildren().addAll(
            title,
            audioSection,
            visualSection,
            buttonContainer
        );
        
        root.getChildren().add(mainContainer);        // Configurar la escena
        Scene scene = new Scene(root, 650, 480);
        applyCSSStyles(scene);
        settingsStage.setScene(scene);
        
        // Centrar ventana
        centerWindow();
    }
      public void showAndWait() {
        settingsStage.showAndWait();
    }    private VBox createMainContainer() {
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(30, 45, 25, 45));
        StyleUtils.applyTransparentContainer(mainContainer);
        return mainContainer;
    }    private Text createMainTitle() {
        Text title = new Text("CONFIGURACIÓN");
        StyleUtils.applyMainTitleStyle(title);
        return title;
    }private VBox createAudioSection() {
        VBox audioSection = new VBox(18);
        audioSection.setAlignment(Pos.CENTER_LEFT);
        audioSection.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 20;"
        );
        
        // Título de la sección
        Text audioTitle = new Text("🔊 AUDIO");
        StyleUtils.applySecondaryTitleStyle(audioTitle);        // Control de volumen de música
        VBox musicVolumeBox = createSliderControl(
            "Volumen de Música",
            0.0, 100.0, gameSettings.getMusicVolume() * 100,
            (observable, oldValue, newValue) -> {
                gameSettings.setMusicVolume(newValue.doubleValue() / 100.0);
                if (onVolumeChanged != null) {
                    onVolumeChanged.run();
                }
            }
        );
        musicVolumeSlider = (Slider) ((HBox) musicVolumeBox.getChildren().get(1)).getChildren().get(0);
        
        audioSection.getChildren().addAll(audioTitle, musicVolumeBox);
        return audioSection;
    }    private VBox createVisualSection() {
        VBox visualSection = new VBox(18);
        visualSection.setAlignment(Pos.CENTER_LEFT);
        visualSection.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 20;"
        );
        
        // Título de la sección
        Text visualTitle = new Text("🎨 VISUALES");
        StyleUtils.applySecondaryTitleStyle(visualTitle);
          // Control de brillo
        VBox brightnessBox = createSliderControl(
            "Brillo de Pantalla",
            10.0, 200.0, gameSettings.getBrightness() * 100,
            (observable, oldValue, newValue) -> {
                gameSettings.setBrightness(newValue.doubleValue() / 100.0);
                if (onBrightnessChanged != null) {
                    onBrightnessChanged.run();
                }
            }
        );
        brightnessSlider = (Slider) ((HBox) brightnessBox.getChildren().get(1)).getChildren().get(0);
        
        visualSection.getChildren().addAll(visualTitle, brightnessBox);
        return visualSection;
    }    private VBox createSliderControl(String labelText, double min, double max, double currentValue,
                                   javafx.beans.value.ChangeListener<Number> changeListener) {
        VBox container = new VBox(12);
        container.setAlignment(Pos.CENTER_LEFT);
        
        // Etiqueta
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Container para slider y valor
        HBox sliderContainer = new HBox(20);
        sliderContainer.setAlignment(Pos.CENTER_LEFT);        // Slider
        Slider slider = new Slider(min, max, currentValue);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);
        slider.setPrefWidth(450);
        slider.setPrefHeight(25);
        slider.setStyle(
            "-fx-control-inner-background: rgba(255, 255, 255, 0.2);" +
            "-fx-accent: " + GameConstants.PRIMARY_COLOR + ";" +
            "-fx-background-radius: 12;"
        );
        
        // Etiqueta del valor actual
        Label valueLabel = new Label();
        valueLabel.setStyle(
            "-fx-text-fill: " + GameConstants.SECONDARY_COLOR + "; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 60px; " +
            "-fx-alignment: center; " +
            "-fx-background-color: rgba(255,255,255,0.1); " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 5 10 5 10;"
        );
        
        // Función para actualizar la etiqueta del valor
        Runnable updateValueLabel = () -> {
            valueLabel.setText(String.format("%.0f%%", slider.getValue()));
        };
        
        // Establecer valor inicial
        updateValueLabel.run();
        
        // Actualizar etiqueta cuando cambie el slider
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateValueLabel.run();
            changeListener.changed(observable, oldValue, newValue);
        });
        
        sliderContainer.getChildren().addAll(slider, valueLabel);
        container.getChildren().addAll(label, sliderContainer);
        
        return container;
    }    private HBox createButtonContainer() {
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(20, 0, 0, 0));        // Botón para restaurar valores por defecto
        Button resetButton = new Button("🔄 Restaurar");
        resetButton.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-border-color: #c0392b;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 20px;"
        );
        resetButton.setPrefWidth(150);
        resetButton.setOnAction(e -> resetToDefaults());        // Botón guardar
        Button saveButton = new Button("💾 Guardar");
        saveButton.setStyle(
            "-fx-background-color: #27ae60;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-border-color: #229954;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 20px;"
        );
        saveButton.setPrefWidth(160);
        saveButton.setOnAction(e -> saveAndClose());
          // Botón cancelar
        Button cancelButton = new Button("❌ Cancelar");
        cancelButton.setStyle(
            "-fx-background-color: #6c757d;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-border-color: #5a6268;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 20px;"
        );
        cancelButton.setPrefWidth(150);
        cancelButton.setOnAction(e -> cancelAndClose());
        
        buttonContainer.getChildren().addAll(resetButton, cancelButton, saveButton);
        return buttonContainer;
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
    }    private void centerWindow() {
        if (parentStage != null) {
            settingsStage.setX(parentStage.getX() + (parentStage.getWidth() - 650) / 2);
            settingsStage.setY(parentStage.getY() + (parentStage.getHeight() - 480) / 2);
        }
    }private void resetToDefaults() {
        // Restaurar sliders a valores por defecto (escala 0-100)
        musicVolumeSlider.setValue(50); // 50% = 0.5 en escala original
        brightnessSlider.setValue(100); // 100% = 1.0 en escala original
        
        // Los listeners de los sliders ya aplicarán los cambios a gameSettings
        // y ejecutarán los callbacks automáticamente
        
        // Configuraciones restauradas a valores por defecto
    }
      private void saveAndClose() {
        gameSettings.saveSettings();
        settingsStage.close();
        // Configuraciones guardadas correctamente
    }
    
    private void cancelAndClose() {
        // Restaurar valores originales
        gameSettings.setMusicVolume(originalMusicVolume);
        gameSettings.setBrightness(originalBrightness);
        
        // Aplicar los valores originales
        if (onVolumeChanged != null) {
            onVolumeChanged.run();
        }
        if (onBrightnessChanged != null) {
            onBrightnessChanged.run();
        }
        
        settingsStage.close();
        // Configuraciones canceladas - valores restaurados
    }
}
