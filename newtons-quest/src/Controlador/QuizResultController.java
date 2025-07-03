package Controlador;

import Controlador.navigation.NavigationManager;
import Modelo.QuizResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controlador para mostrar los resultados del quiz.
 * Presenta los resultados obtenidos por el usuario al completar el quiz.
 */
public class QuizResultController {
    
    // Elementos FXML
    @FXML private AnchorPane resultBackground;
    @FXML private Label lblTitulo;
    @FXML private Label lblEstado;
    @FXML private Label lblPorcentaje;
    @FXML private Label lblCalificacion;
    @FXML private Label lblRespuestas;
    @FXML private Label lblTiempo;
    @FXML private VBox vboxCertificado;
    @FXML private VBox vboxMotivacion;
    @FXML private Button btnRepetir;
    @FXML private Button btnVolver;
    
    // Datos del resultado
    private QuizResult resultado;
    
    /**
     * Inicializa el controlador
     */
    public void initialize() {
        try {
            System.out.println("Inicializando QuizResultController");
            
            // Cargar fondo
            loadBackgroundImage();
            
            // Configurar efectos de hover para los botones
            configurarEfectosBotones();
            
        } catch (Exception e) {
            System.err.println("Error al inicializar QuizResultController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga la imagen de fondo
     */
    private void loadBackgroundImage() {
        try {
            String imagePath = "src/recursos/imagenes/quiz_result_background.jpg";
            File imageFile = new File(imagePath);
            
            Image backgroundImage;
            
            if (imageFile.exists()) {
                backgroundImage = new Image(new FileInputStream(imageFile));
            } else {
                // Usar imagen por defecto del mapa
                backgroundImage = new Image(getClass().getResourceAsStream("/recursos/imagenes/map_background.jpg"));
            }
            
            BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
            );
            
            resultBackground.setBackground(new Background(bgImage));
            
        } catch (Exception e) {
            System.err.println("Error al cargar imagen de fondo de resultados: " + e.getMessage());
        }
    }
    
    /**
     * Configura los efectos de hover para los botones
     */
    private void configurarEfectosBotones() {
        // Efecto hover para el botón repetir
        if (btnRepetir != null) {
            btnRepetir.setOnMouseEntered(e -> {
                btnRepetir.setStyle(btnRepetir.getStyle() + "; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
            });
            btnRepetir.setOnMouseExited(e -> {
                btnRepetir.setStyle(btnRepetir.getStyle().replace("; -fx-scale-x: 1.05; -fx-scale-y: 1.05;", ""));
            });
        }
        
        // Efecto hover para el botón volver
        if (btnVolver != null) {
            btnVolver.setOnMouseEntered(e -> {
                btnVolver.setStyle(btnVolver.getStyle() + "; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
            });
            btnVolver.setOnMouseExited(e -> {
                btnVolver.setStyle(btnVolver.getStyle().replace("; -fx-scale-x: 1.05; -fx-scale-y: 1.05;", ""));
            });
        }
    }
    
    /**
     * Establece el resultado del quiz y actualiza la interfaz
     * @param resultado Resultado del quiz a mostrar
     */
    public void setResultado(QuizResult resultado) {
        this.resultado = resultado;
        actualizarInterfaz();
    }
    
    /**
     * Actualiza la interfaz con los datos del resultado
     */
    private void actualizarInterfaz() {
        if (resultado == null) {
            System.err.println("No hay resultado para mostrar");
            return;
        }
        
        // Actualizar título basado en el resultado
        if (resultado.isPassed()) {
            lblTitulo.setText("¡Quiz Completado!");
            lblEstado.setText("¡Aprobado!");
            lblEstado.setStyle("-fx-text-fill: #4caf50; -fx-effect: dropshadow(gaussian, rgba(76, 175, 80, 0.5), 5, 0, 0, 2);");
        } else {
            lblTitulo.setText("Quiz Completado");
            lblEstado.setText("No Aprobado");
            lblEstado.setStyle("-fx-text-fill: #ff6b6b; -fx-effect: dropshadow(gaussian, rgba(255, 107, 107, 0.5), 5, 0, 0, 2);");
        }
        
        // Actualizar información detallada
        lblPorcentaje.setText("Puntuación: " + resultado.getFormattedPercentage());
        lblCalificacion.setText("Calificación: " + resultado.getLetterGrade());
        lblRespuestas.setText(String.format("Respuestas correctas: %d de %d", 
                                          resultado.getCorrectAnswers(), 
                                          resultado.getTotalQuestions()));
        lblTiempo.setText("Tiempo empleado: " + resultado.getFormattedTime());
        
        // Mostrar mensaje apropiado
        if (resultado.isPassed()) {
            // Mostrar mensaje de certificado
            vboxCertificado.setVisible(true);
            vboxMotivacion.setVisible(false);
            
            // Cambiar el color del título para aprobados
            lblTitulo.setStyle("-fx-text-fill: #ffffff; -fx-effect: dropshadow(gaussian, rgba(76, 175, 80, 0.3), 10, 0, 0, 2);");
            
            System.out.println("Usuario aprobó el quiz con " + resultado.getFormattedPercentage());
        } else {
            // Mostrar mensaje de motivación
            vboxCertificado.setVisible(false);
            vboxMotivacion.setVisible(true);
            
            // Cambiar el color del título para no aprobados
            lblTitulo.setStyle("-fx-text-fill: #ffffff; -fx-effect: dropshadow(gaussian, rgba(255, 107, 107, 0.3), 10, 0, 0, 2);");
            
            System.out.println("Usuario no aprobó el quiz con " + resultado.getFormattedPercentage());
        }
        
        // Personalizar mensajes según el porcentaje
        personalizarMensajes();
    }
    
    /**
     * Personaliza los mensajes según el desempeño
     */
    private void personalizarMensajes() {
        if (resultado == null) return;
        
        double porcentaje = resultado.getPercentage();
        
        try {
            if (porcentaje >= 95) {
                lblTitulo.setText("¡Excelente Desempeño!");
                if (vboxCertificado.isVisible() && vboxCertificado.getChildren().size() >= 3) {
                    // Cambiar el mensaje para resultados excelentes
                    ((Label) vboxCertificado.getChildren().get(0)).setText("🌟 ¡Extraordinario!");
                    ((Label) vboxCertificado.getChildren().get(1)).setText("Has demostrado un dominio excepcional del tema.");
                    ((Label) vboxCertificado.getChildren().get(2)).setText("¡Eres un verdadero experto en física!");
                }
            } else if (porcentaje >= 90) {
                lblTitulo.setText("¡Muy Buen Trabajo!");
            } else if (porcentaje >= 85) {
                lblTitulo.setText("¡Buen Trabajo!");
            } else if (porcentaje >= 70) {
                if (vboxMotivacion.isVisible() && vboxMotivacion.getChildren().size() >= 3) {
                    ((Label) vboxMotivacion.getChildren().get(0)).setText("💪 ¡Casi lo logras!");
                    ((Label) vboxMotivacion.getChildren().get(1)).setText("Estás muy cerca del objetivo del 85%.");
                    ((Label) vboxMotivacion.getChildren().get(2)).setText("¡Un poco más de estudio y lo conseguirás!");
                }
            } else if (porcentaje >= 50) {
                if (vboxMotivacion.isVisible() && vboxMotivacion.getChildren().size() >= 3) {
                    ((Label) vboxMotivacion.getChildren().get(0)).setText("💪 ¡Buen intento!");
                    ((Label) vboxMotivacion.getChildren().get(1)).setText("Ya tienes algunas bases sólidas.");
                    ((Label) vboxMotivacion.getChildren().get(2)).setText("¡Sigue practicando para mejorar!");
                }
            } else {
                if (vboxMotivacion.isVisible() && vboxMotivacion.getChildren().size() >= 3) {
                    ((Label) vboxMotivacion.getChildren().get(0)).setText("💪 ¡No te rindas!");
                    ((Label) vboxMotivacion.getChildren().get(1)).setText("Cada intento es una oportunidad de aprender.");
                    ((Label) vboxMotivacion.getChildren().get(2)).setText("¡Revisa los videos y vuelve a intentarlo!");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Error al personalizar mensajes: " + e.getMessage());
            // Usar mensajes por defecto si hay problemas
        } catch (ClassCastException e) {
            System.err.println("Error de tipo al personalizar mensajes: " + e.getMessage());
        }
    }
    
    /**
     * Maneja el click en el botón de repetir quiz
     */
    @FXML
    public void onRepetirButtonClick(ActionEvent event) {
        try {
            // Cargar nuevamente la pantalla del quiz
            String quizFxmlPath = "src/Vista/Quiz.fxml";
            File quizFxmlFile = new File(quizFxmlPath);
            
            FXMLLoader loader;
            if (quizFxmlFile.exists()) {
                loader = new FXMLLoader(quizFxmlFile.toURI().toURL());
            } else {
                loader = new FXMLLoader(getClass().getResource("/Vista/Quiz.fxml"));
            }
            
            Parent root = loader.load();
            
            // Crear nueva escena
            Scene scene = new Scene(root, 900, 700);
            
            // Añadir CSS si existe
            try {
                String cssPath = "src/Vista/resources/quiz.css";
                File cssFile = new File(cssPath);
                if (cssFile.exists()) {
                    scene.getStylesheets().add(cssFile.toURI().toURL().toExternalForm());
                }
            } catch (Exception e) {
                System.err.println("Error al cargar CSS: " + e.getMessage());
            }
            
            Stage stage = (Stage) btnRepetir.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Newton's Apple Quest - Quiz");
            
            System.out.println("Reiniciando quiz...");
            
        } catch (Exception e) {
            System.err.println("Error al repetir quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Maneja el click en el botón de volver al mapa
     */
    @FXML
    public void onVolverButtonClick(ActionEvent event) {
        try {
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            NavigationManager.navigateToMap(stage);
            
            System.out.println("Volviendo al mapa desde resultados del quiz...");
            
        } catch (IOException e) {
            System.err.println("Error al volver al mapa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
