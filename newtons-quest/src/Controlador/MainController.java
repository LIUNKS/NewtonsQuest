package Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainController {
    
    @FXML private Button startButton;
    @FXML private Button rulesButton;
    @FXML private Button settingsButton;
    
    public void initialize() {
        // Por ahora, solo imprimimos mensajes en la consola para verificar que funcionan
        startButton.setOnAction(event -> System.out.println("Iniciar juego"));
        rulesButton.setOnAction(event -> System.out.println("Mostrar reglas"));
        settingsButton.setOnAction(event -> System.out.println("Abrir configuración"));
    }
}
