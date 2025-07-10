package Controlador;

import Controlador.navigation.NavigationManager;
import Controlador.utils.SessionManager;
import Modelo.dao.QuizDAO;
import Modelo.dto.QuizQuestion;
import Modelo.dto.QuizResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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
 * Controlador para la funcionalidad del Quiz.
 * 
 * Esta clase gestiona toda la lógica del sistema de quiz, incluyendo:
 * - Carga y presentación de preguntas de física
 * - Manejo de respuestas del usuario y validación
 * - Sistema de puntuación y progreso
 * - Timer de tiempo límite por pregunta
 * - Navegación entre preguntas y resultados
 * - Interfaz gráfica dinámica con efectos visuales
 * 
 * El quiz presenta preguntas de opción múltiple sobre conceptos de física
 * relacionados con las fórmulas desbloqueadas en el juego principal.
 */
public class QuizController {
    
    // Elementos FXML
    @FXML private AnchorPane quizBackground;
    @FXML private Label lblTitulo;
    @FXML private Label lblProgreso;
    @FXML private ProgressBar progressBar;
    @FXML private Label lblTiempo;
    @FXML private Label lblPuntuacion;
    @FXML private Label lblIncorrectas;
    @FXML private Label lblPregunta;
    @FXML private RadioButton rbOpcionA;
    @FXML private RadioButton rbOpcionB;
    @FXML private RadioButton rbOpcionC;
    @FXML private RadioButton rbOpcionD;
    @FXML private ToggleGroup toggleGroup;
    @FXML private VBox vboxPista;
    @FXML private Label lblPista;
    @FXML private VBox vboxExplicacion;
    @FXML private Label lblExplicacion;
    @FXML private Button btnPista;
    @FXML private Button btnResponder;
    @FXML private Button btnVolver;
    @FXML private javafx.scene.control.ScrollPane quizScrollPane;
    
    // Variables del quiz
    private List<QuizQuestion> preguntas;
    private int preguntaActual = 0;
    private int respuestasCorrectas = 0;
    private int respuestasIncorrectas = 0;
    private long tiempoInicio;
    private Timer timer;
    private boolean quizCompletado = false;
    private boolean pistaUtilizada = false;
    private boolean preguntaRespondida = false;
    
    /**
     * Para el entorno de runtime, este método es llamado después de que todos los elementos @FXML
     * han sido inyectados, pero antes de que la vista sea mostrada.
     * Este enfoque es utilizado por JavaFX para la inicialización controlada.
     */
    public void initialize() {
        try {
            // Inicializando QuizController
            System.out.println("Inicializando QuizController...");
            
            // Verificar que los botones estén correctamente inicializados
            if (btnPista == null || btnResponder == null || btnVolver == null) {
                System.err.println("Error: Componentes FXML no inicializados correctamente");
                return;
            }
            
            // Asegurar que el botón de volver siempre esté por encima del ScrollPane
            asegurarVisibilidadBotonVolver();
            
            // Cargar fondo
            loadBackgroundImage();
            
            // Obtener las preguntas
            preguntas = QuizDAO.obtenerTodasLasPreguntas();
            
            // Inicializar el timer
            iniciarTimer();
            
            // Mostrar la primera pregunta
            mostrarPregunta();
            
            // Configurar eventos
            configurarEventos();
            
            // Configuración post-inicialización
            Platform.runLater(() -> {
                // Esto se ejecutará después de que la escena sea completamente creada
                // Asegurar que el botón está por encima y es visible
                if (btnVolver != null) {
                    btnVolver.toFront();
                    System.out.println("Botón volver llevado al frente en post-inicialización");
                    
                    // Añadir un efecto de hover para mejor retroalimentación visual
                    btnVolver.setOnMouseEntered(e -> {
                        btnVolver.setStyle(btnVolver.getStyle().replace("-fx-background-color: #e74c3c", 
                                                                       "-fx-background-color: #c0392b"));
                    });
                    
                    btnVolver.setOnMouseExited(e -> {
                        btnVolver.setStyle(btnVolver.getStyle().replace("-fx-background-color: #c0392b", 
                                                                       "-fx-background-color: #e74c3c"));
                    });
                }
                
                // Asegurar que el ScrollPane no tape el botón
                if (quizScrollPane != null) {
                    quizScrollPane.toBack();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error durante inicialización: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Asegura que el botón de volver siempre esté visible y por encima del ScrollPane
     */
    private void asegurarVisibilidadBotonVolver() {
        if (btnVolver != null && quizScrollPane != null) {
            // Asegurar que el botón esté en el frente (mayor z-index)
            btnVolver.toFront();
            
            // Hacer el ScrollPane transparente para que se vea el fondo detrás
            quizScrollPane.setStyle("-fx-background-color: transparent;");
            
            // Mejorar la apariencia del botón y añadir cursor de mano
            String estiloActual = btnVolver.getStyle();
            if (!estiloActual.contains("-fx-cursor: hand")) {
                btnVolver.setStyle(estiloActual + "; -fx-cursor: hand;");
            }
            
            // Configurar múltiples eventos para mejorar la respuesta
            
            // Evento principal de acción
            btnVolver.setOnAction(event -> {
                System.out.println("Botón Volver presionado - Regresando al mapa...");
                onVolverButtonClick(event);
            });
            
            // Evento adicional de clic para mayor respuesta
            btnVolver.setOnMouseClicked(event -> {
                System.out.println("Clic detectado en botón volver");
                onVolverButtonClick(new ActionEvent(btnVolver, null));
                event.consume();
            });
        }
    }
    
    /**
     * Configura el fondo degradado del quiz
     */
    private void loadBackgroundImage() {
        try {
            System.out.println("Configurando fondo degradado para el quiz...");
            
            // Aplicar gradiente de color según la paleta oficial del proyecto
            // De azul oscuro a azul medio a morado, siguiendo los colores del CSS
            quizBackground.setStyle("-fx-background-color: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");
            
            System.out.println("Fondo degradado aplicado correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al configurar el fondo degradado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inicializa el timer para contar el tiempo
     */
    private void iniciarTimer() {
        tiempoInicio = System.currentTimeMillis();
        timer = new Timer();
        
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (!quizCompletado) {
                        long tiempoTranscurrido = (System.currentTimeMillis() - tiempoInicio) / 1000;
                        actualizarTiempo(tiempoTranscurrido);
                    }
                });
            }
        }, 0, 1000); // Actualizar cada segundo
    }
    
    /**
     * Actualiza la etiqueta de tiempo
     */
    private void actualizarTiempo(long segundos) {
        long minutos = segundos / 60;
        long segs = segundos % 60;
        lblTiempo.setText(String.format("Tiempo: %02d:%02d", minutos, segs));
    }
    
    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        // Configurar el toggle group para detectar cambios
        toggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null && !preguntaRespondida) {
                // Habilitar el botón de responder cuando se seleccione una opción
                btnResponder.setDisable(false);
            }
        });
        
        // Asegurar que el botón de volver tenga su evento configurado correctamente
        if (btnVolver != null) {
            // Aplicar un evento independiente que no se vea afectado por el scroll
            btnVolver.setOnMouseClicked(event -> {
                System.out.println("Evento de mouse click en botón volver detectado");
                onVolverButtonClick(new ActionEvent(btnVolver, null));
                event.consume(); // Importante para evitar propagación
            });
            
            // Agregar también el evento estándar como respaldo
            btnVolver.setOnAction(event -> {
                System.out.println("Evento de botón volver detectado");
                onVolverButtonClick(event);
            });
            
            // Mejorar el estilo visual para indicar interactividad
            btnVolver.setStyle(btnVolver.getStyle() + "; -fx-cursor: hand;");
            
            // Asegurar que esté siempre visible y en primer plano
            Platform.runLater(() -> {
                btnVolver.toFront();
                if (quizScrollPane != null) {
                    quizScrollPane.toBack();
                }
            });
        } else {
            System.err.println("Error: btnVolver no inicializado correctamente");
        }
    }
    
    /**
     * Muestra la pregunta actual
     */
    private void mostrarPregunta() {
        if (preguntaActual < preguntas.size()) {
            QuizQuestion pregunta = preguntas.get(preguntaActual);
            
            // Auto-scroll hacia arriba para nueva pregunta
            autoScrollUp();
            
            // Actualizar información de progreso
            lblProgreso.setText(String.format("Pregunta %d de %d", preguntaActual + 1, preguntas.size()));
            double progreso = (double) preguntaActual / preguntas.size();
            progressBar.setProgress(progreso);
            
            // Actualizar puntuación
            lblPuntuacion.setText("Correctas: " + respuestasCorrectas);
            lblIncorrectas.setText("Incorrectas: " + respuestasIncorrectas);
            
            // Mostrar pregunta y opciones
            lblPregunta.setText(pregunta.getQuestion());
            rbOpcionA.setText("A) " + pregunta.getOption(0));
            rbOpcionB.setText("B) " + pregunta.getOption(1));
            rbOpcionC.setText("C) " + pregunta.getOption(2));
            rbOpcionD.setText("D) " + pregunta.getOption(3));
            
            // Limpiar selección anterior
            toggleGroup.selectToggle(null);
            
            // Resetear estado de la pregunta
            preguntaRespondida = false;
            pistaUtilizada = false;
            
            // Ocultar pista y explicación
            vboxPista.setVisible(false);
            vboxExplicacion.setVisible(false);
            
            // Habilitar botón de pista y deshabilitar responder
            btnPista.setDisable(false);
            btnResponder.setDisable(true);
            btnResponder.setText("Responder");
            
        } else {
            // Quiz completado
            completarQuiz();
        }
    }
    
    /**
     * Maneja el click en el botón de pista
     */
    @FXML
    public void onPistaButtonClick(ActionEvent event) {
        if (!pistaUtilizada && preguntaActual < preguntas.size()) {
            QuizQuestion pregunta = preguntas.get(preguntaActual);
            
            // Mostrar la pista
            lblPista.setText(pregunta.getHint());
            vboxPista.setVisible(true);
            
            // Auto-scroll para mostrar la pista
            autoScrollDown();
            
            // Deshabilitar el botón de pista
            btnPista.setDisable(true);
            pistaUtilizada = true;
        }
    }
    
    /**
     * Maneja el click en el botón de responder
     */
    @FXML
    public void onResponderButtonClick(ActionEvent event) {
        if (!preguntaRespondida && preguntaActual < preguntas.size()) {
            // Obtener la opción seleccionada
            RadioButton seleccionada = (RadioButton) toggleGroup.getSelectedToggle();
            
            if (seleccionada != null) {
                // Determinar el índice de la respuesta seleccionada
                int respuestaSeleccionada = -1;
                if (seleccionada == rbOpcionA) respuestaSeleccionada = 0;
                else if (seleccionada == rbOpcionB) respuestaSeleccionada = 1;
                else if (seleccionada == rbOpcionC) respuestaSeleccionada = 2;
                else if (seleccionada == rbOpcionD) respuestaSeleccionada = 3;
                
                // Verificar si la respuesta es correcta
                QuizQuestion pregunta = preguntas.get(preguntaActual);
                boolean esCorrecta = pregunta.isCorrect(respuestaSeleccionada);
                
                if (esCorrecta) {
                    respuestasCorrectas++;
                } else {
                    respuestasIncorrectas++;
                }
                
                // Mostrar explicación
                lblExplicacion.setText(pregunta.getExplanation());
                vboxExplicacion.setVisible(true);
                
                // Auto-scroll para mostrar la explicación
                autoScrollDown();
                
                // Marcar pregunta como respondida
                preguntaRespondida = true;
                
                // Cambiar botón a "Siguiente"
                btnResponder.setText("Siguiente");
                btnResponder.setDisable(false);
                
                // Deshabilitar opciones
                rbOpcionA.setDisable(true);
                rbOpcionB.setDisable(true);
                rbOpcionC.setDisable(true);
                rbOpcionD.setDisable(true);
                
                // Resaltar respuesta correcta
                resaltarRespuestaCorrecta(pregunta.getCorrectAnswerIndex());
                
                // Realizar auto-scroll hacia abajo
                autoScrollDown();
            }
        } else if (preguntaRespondida) {
            // Pasar a la siguiente pregunta
            siguientePregunta();
        }
    }
    
    /**
     * Resalta la respuesta correcta
     */
    private void resaltarRespuestaCorrecta(int indiceCorrecta) {
        // Resetear estilos
        rbOpcionA.getStyleClass().remove("quiz-option-correct");
        rbOpcionB.getStyleClass().remove("quiz-option-correct");
        rbOpcionC.getStyleClass().remove("quiz-option-correct");
        rbOpcionD.getStyleClass().remove("quiz-option-correct");
        
        // Aplicar estilo a la respuesta correcta
        RadioButton correcta = null;
        switch (indiceCorrecta) {
            case 0: correcta = rbOpcionA; break;
            case 1: correcta = rbOpcionB; break;
            case 2: correcta = rbOpcionC; break;
            case 3: correcta = rbOpcionD; break;
        }
        
        if (correcta != null) {
            correcta.getStyleClass().add("quiz-option-correct");
        }
    }
    
    /**
     * Avanza a la siguiente pregunta
     */
    private void siguientePregunta() {
        // Habilitar opciones para la siguiente pregunta
        rbOpcionA.setDisable(false);
        rbOpcionB.setDisable(false);
        rbOpcionC.setDisable(false);
        rbOpcionD.setDisable(false);
        
        // Limpiar estilos
        rbOpcionA.getStyleClass().remove("quiz-option-correct");
        rbOpcionB.getStyleClass().remove("quiz-option-correct");
        rbOpcionC.getStyleClass().remove("quiz-option-correct");
        rbOpcionD.getStyleClass().remove("quiz-option-correct");
        
        preguntaActual++;
        mostrarPregunta();
    }
    
    /**
     * Completa el quiz y muestra los resultados
     */
    private void completarQuiz() {
        quizCompletado = true;
        
        // Detener el timer
        if (timer != null) {
            timer.cancel();
        }
        
        // Calcular tiempo total
        long tiempoTotal = (System.currentTimeMillis() - tiempoInicio) / 1000;
        
        // Crear resultado
        SessionManager sessionManager = SessionManager.getInstance();
        int userId = sessionManager.isLoggedIn() ? sessionManager.getCurrentUserId() : -1;
        
        QuizResult resultado = new QuizResult(userId, preguntas.size(), respuestasCorrectas, tiempoTotal);
        
        // Guardar resultado si hay usuario logueado
        if (userId != -1) {
            boolean guardado = QuizDAO.guardarResultadoQuiz(resultado);
            if (!guardado) {
                // Error silencioso al guardar resultado del quiz
            }
        }
        
        // Mostrar pantalla de resultados
        mostrarResultados(resultado);
    }
    
    /**
     * Muestra la pantalla de resultados
     */
    private void mostrarResultados(QuizResult resultado) {
        try {
            String resultFxmlPath = "src/Vista/QuizResult.fxml";
            File resultFxmlFile = new File(resultFxmlPath);
            
            FXMLLoader loader;
            if (resultFxmlFile.exists()) {
                loader = new FXMLLoader(resultFxmlFile.toURI().toURL());
            } else {
                loader = new FXMLLoader(getClass().getResource("/Vista/QuizResult.fxml"));
            }
            
            Parent root = loader.load();
            
            // Obtener el controlador de resultados y pasarle los datos
            QuizResultController resultController = loader.getController();
            resultController.setResultado(resultado);
            
            // Cambiar la escena
            Scene scene = new Scene(root, 900, 700);
            
            // Añadir CSS si existe
            try {
                String cssPath = "src/Vista/resources/quiz.css";
                File cssFile = new File(cssPath);
                if (cssFile.exists()) {
                    scene.getStylesheets().add(cssFile.toURI().toURL().toExternalForm());
                }
            } catch (Exception e) {
                // Error silencioso al cargar CSS
            }
            
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Newton's Apple Quest - Resultados del Quiz");
            
        } catch (Exception e) {
            // Error silencioso al mostrar resultados
            
            // Fallback: volver al mapa
            onVolverButtonClick(null);
        }
    }
    
    /**
     * Maneja el click en el botón de volver
     */
    @FXML
    public void onVolverButtonClick(ActionEvent event) {
        try {
            System.out.println("Botón Volver presionado - Regresando al mapa...");
            
            // Detener el timer si está corriendo
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            
            // Limpiar recursos antes de navegar
            cleanup();
            
            // Intentar navegar utilizando diferentes estrategias
            Stage stage = null;
            
            // Estrategia 1: Utilizar el botón de volver
            if (btnVolver != null && btnVolver.getScene() != null && btnVolver.getScene().getWindow() instanceof Stage) {
                stage = (Stage) btnVolver.getScene().getWindow();
            } 
            // Estrategia 2: Utilizar el título
            else if (lblTitulo != null && lblTitulo.getScene() != null && lblTitulo.getScene().getWindow() instanceof Stage) {
                stage = (Stage) lblTitulo.getScene().getWindow();
            }
            // Estrategia 3: Utilizar el evento si está disponible
            else if (event != null && event.getSource() instanceof javafx.scene.Node) {
                javafx.scene.Node source = (javafx.scene.Node) event.getSource();
                stage = (Stage) source.getScene().getWindow();
            }
            
            // Si se encontró un stage, intentar navegar
            if (stage != null) {
                NavigationManager.navigateToMap(stage);
                return; // Salir después de navegar exitosamente
            }
            
            // Si todas las estrategias fallan, intentar un método alternativo
            System.err.println("No se pudo determinar el Stage para la navegación. Intentando método alternativo.");
            NavigationManager.navigateToMapAlternative();
            
        } catch (Exception e) {
            System.err.println("Error al volver al mapa: " + e.getMessage());
            e.printStackTrace();
            
            // Último intento de navegación de emergencia
            try {
                NavigationManager.navigateToMapEmergency();
            } catch (Exception ex) {
                System.err.println("Error crítico al intentar volver al mapa: " + ex.getMessage());
                System.err.println("La aplicación puede estar en un estado inconsistente.");
            }
        }
    }
    
    /**
     * Limpia recursos al destruir el controlador
     */
    public void cleanup() {
        if (timer != null) {
            timer.cancel();
        }
    }
    
    /**
     * Realiza auto-scroll hacia abajo para mostrar contenido dinámico
     */
    private void autoScrollDown() {
        if (quizScrollPane != null) {
            Platform.runLater(() -> {
                // Pequeño delay para que el contenido se renderice primero
                Timer scrollTimer = new Timer();
                scrollTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            // Scroll hacia abajo gradualmente
                            double currentVvalue = quizScrollPane.getVvalue();
                            double targetVvalue = Math.min(1.0, currentVvalue + 0.3);
                            quizScrollPane.setVvalue(targetVvalue);
                        });
                    }
                }, 100); // 100ms delay
            });
        }
    }
    
    /**
     * Realiza auto-scroll hacia arriba para mostrar el inicio de una nueva pregunta
     */
    private void autoScrollUp() {
        if (quizScrollPane != null) {
            Platform.runLater(() -> {
                quizScrollPane.setVvalue(0.0); // Scroll al inicio
            });
        }
    }
}
