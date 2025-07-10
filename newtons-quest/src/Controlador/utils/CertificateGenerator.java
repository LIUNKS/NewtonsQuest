package Controlador.utils;

import Modelo.dto.Player;
import Modelo.dto.QuizResult;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Generador de certificados PDF para Newton's Apple Quest.
 * 
 * Esta clase se encarga de crear certificados de aprovechamiento en formato PDF
 * para usuarios que completan exitosamente los quizes del juego.
 * 
 * Funcionalidades principales:
 * - Verificación de elegibilidad para certificado (puntuación ≥ 85%)
 * - Generación de certificados personalizados en formato PDF
 * - Inclusión de información detallada del quiz y jugador
 * - Diseño profesional generado completamente por código
 * - Guardado automático en directorio seleccionado por el usuario
 * 
 * Características del certificado:
 * - Tamaño carta (Letter) con orientación horizontal
 * - Información personalizada del usuario (nombre, fecha, puntuación)
 * - Estadísticas detalladas del quiz realizado
 * - Diseño atractivo con elementos gráficos generados por código
 * - Marca de agua y elementos de seguridad básicos
 * 
 * Utiliza la librería iText para la generación de documentos PDF.
 * Los certificados se guardan con nombres únicos basados en timestamp.
 */
public class CertificateGenerator {
    
    private static final float CERTIFICATE_SCORE_THRESHOLD = 85.0f;
    
    /**
     * Verifica si un usuario cumple con los requisitos para obtener un certificado.
     * 
     * @param quizResult El resultado del quiz del usuario
     * @return true si el usuario cumple con los requisitos, false en caso contrario
     */
    public static boolean isEligibleForCertificate(QuizResult quizResult) {
        if (quizResult == null) {
            return false;
        }
        
        return quizResult.getPercentage() >= CERTIFICATE_SCORE_THRESHOLD;
    }
    
    /**
     * Genera un certificado en PDF para el usuario con un diseño visual mejorado
     * creado completamente por código sin dependencias de imágenes externas.
     * 
     * @param player El jugador para el que se generará el certificado
     * @param quizResult El resultado del quiz del jugador
     * @param parentStage La ventana principal desde donde se llamó esta función
     * @return La ruta del archivo PDF generado, o null si ocurre un error
     */
    public static String generateCertificate(Player player, QuizResult quizResult, Stage parentStage) {
        if (player == null || quizResult == null || !isEligibleForCertificate(quizResult)) {
            return null;
        }
        
        // Pedir al usuario que elija dónde guardar el certificado
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Guardar certificado");
        File selectedDirectory = directoryChooser.showDialog(parentStage);
        
        if (selectedDirectory == null) {
            return null; // El usuario canceló la operación
        }
        
        // Crear nombre de archivo para el certificado
        String fileName = "Certificado_Newton_" + player.getUsername() + "_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
        
        String filePath = selectedDirectory.getAbsolutePath() + File.separator + fileName;
        
        try {
            // Crear el documento PDF con tamaño A4 horizontal (más adecuado para certificados)
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            Rectangle pageSize = new Rectangle(842, 595); // A4 horizontal
            document.setPageSize(pageSize);
            document.setMargins(30, 30, 30, 30); // Márgenes más pequeños para aprovechar el espacio
            document.open();
            
            // Crear diseño visual generado por código
            com.itextpdf.text.pdf.PdfContentByte canvas = writer.getDirectContent();
            
            // Dibujar borde decorativo
            canvas.saveState();
            canvas.setColorStroke(new com.itextpdf.text.BaseColor(233, 69, 96)); // Color del juego
            canvas.setLineWidth(3f);
            canvas.rectangle(20, 20, pageSize.getWidth() - 40, pageSize.getHeight() - 40);
            canvas.stroke();
            
            // Dibujar borde interior más fino
            canvas.setLineWidth(1f);
            canvas.setColorStroke(new com.itextpdf.text.BaseColor(70, 130, 180));
            canvas.rectangle(30, 30, pageSize.getWidth() - 60, pageSize.getHeight() - 60);
            canvas.stroke();
            
            // Añadir decoración de esquinas
            float cornerSize = 40;
            canvas.setColorFill(new com.itextpdf.text.BaseColor(233, 69, 96, 60));
            
            // Esquina superior izquierda
            canvas.circle(30, pageSize.getHeight() - 30, cornerSize);
            canvas.fill();
            
            // Esquina superior derecha
            canvas.circle(pageSize.getWidth() - 30, pageSize.getHeight() - 30, cornerSize);
            canvas.fill();
            
            // Esquina inferior izquierda
            canvas.circle(30, 30, cornerSize);
            canvas.fill();
            
            // Esquina inferior derecha
            canvas.circle(pageSize.getWidth() - 30, 30, cornerSize);
            canvas.fill();
            
            // Añadir fórmulas físicas como marcas de agua
            canvas.setFontAndSize(com.itextpdf.text.pdf.BaseFont.createFont(), 12);
            canvas.setColorFill(new com.itextpdf.text.BaseColor(100, 100, 100, 30));
            canvas.showTextAligned(Element.ALIGN_CENTER, "F = ma", pageSize.getWidth()/4, pageSize.getHeight()/2, 45);
            canvas.showTextAligned(Element.ALIGN_CENTER, "E = mc²", pageSize.getWidth()/2, pageSize.getHeight()/4, 30);
            canvas.showTextAligned(Element.ALIGN_CENTER, "F = G(m₁m₂)/r²", 3*pageSize.getWidth()/4, pageSize.getHeight()/2, 45);
            
            canvas.restoreState();
            
            // Añadir espacio en la parte superior
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            
            // Título del certificado con tipografía mejorada
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, Font.BOLD, new com.itextpdf.text.BaseColor(20, 20, 90));
            Paragraph title = new Paragraph("CERTIFICADO DE EXCELENCIA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);
            
            // Subtítulo con estilo mejorado
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 18, Font.ITALIC, new com.itextpdf.text.BaseColor(233, 69, 96));
            Paragraph subtitle = new Paragraph("Newton's Apple Quest", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(30);
            document.add(subtitle);
            
            // Contenido del certificado
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 14);
            Paragraph content = new Paragraph("Este certificado acredita que:", contentFont);
            content.setAlignment(Element.ALIGN_CENTER);
            content.setSpacingAfter(15);
            document.add(content);
            
            // Nombre del usuario con estilo mejorado
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Font.BOLD, new com.itextpdf.text.BaseColor(20, 20, 90));
            Paragraph name = new Paragraph(player.getFullName(), nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            name.setSpacingAfter(15);
            document.add(name);
            
            // Descripción del logro con formato mejorado y espaciado optimizado
            Font achievementFont = FontFactory.getFont(FontFactory.HELVETICA, 14);
            Paragraph achievement = new Paragraph(
                "Ha completado con éxito el desafío de física de Newton's Apple Quest, " +
                "demostrando un excepcional entendimiento de los principios físicos con una " +
                "puntuación de " + String.format("%.1f", quizResult.getPercentage()) + "%.", achievementFont);
            achievement.setAlignment(Element.ALIGN_CENTER);
            achievement.setSpacingAfter(25);
            document.add(achievement);
            
            // Fecha con formato mejorado
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 14);
            Paragraph date = new Paragraph(
                "Fecha: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            date.setSpacingAfter(45);
            document.add(date);
            
            // Dibujar línea para firma
            canvas.saveState();
            canvas.setColorStroke(new com.itextpdf.text.BaseColor(20, 20, 90));
            canvas.setLineWidth(1f);
            float signatureLineY = 120;
            canvas.moveTo(pageSize.getWidth()/2 - 100, signatureLineY);
            canvas.lineTo(pageSize.getWidth()/2 + 100, signatureLineY);
            canvas.stroke();
            canvas.restoreState();
            
            // Agregar firma como texto estilizado
            Font signatureNameFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.ITALIC);
            Paragraph signatureName = new Paragraph("Sir Isaac Newton", signatureNameFont);
            signatureName.setAlignment(Element.ALIGN_CENTER);
            signatureName.setSpacingBefore(5);
            document.add(signatureName);
            
            // Pie de página con estilo sutil
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, new com.itextpdf.text.BaseColor(100, 100, 100));
            Paragraph footer = new Paragraph(
                "Este certificado reconoce el esfuerzo y dedicación en el estudio de la física " +
                "a través del juego educativo Newton's Apple Quest.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(25);
            document.add(footer);
            
            document.close();
            writer.close();
            
            return filePath;
            
        } catch (DocumentException | IOException e) {
            System.err.println("Error al generar el certificado: " + e.getMessage());
            return null;
        }
    }
}
