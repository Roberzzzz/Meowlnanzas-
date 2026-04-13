package javapynanzas;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document; 
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;     
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;
import javax.swing.JOptionPane;
import com.itextpdf.text.pdf.PdfPTable;
import java.util.ArrayList;



public class GenPdf {
    
    public static void generarReporteCurso(String nombreCurso, String filtro, java.util.List<Object[]> listaEstudiantes, double totalPendienteGlobal) {
        String ruta = "Reportes/Cursos/";
        File dir = new File(ruta);
        if (!dir.exists()) dir.mkdirs();

        String nombreArchivo = ruta + "Reporte_" + nombreCurso.replace(" ", "_") + ".pdf";
        Document documento = new Document(PageSize.LETTER);

        try {
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();

            Font fTitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font fSubtitulo = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
            Font fNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
            Font fTablaCabecera = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);

            documento.add(new Paragraph("Meowlnanzas - Reporte de pagos por curso", fNormal));
            documento.add(new Paragraph("Generado el: " + new java.util.Date().toString(), fNormal));
            documento.add(new Paragraph("Filtro: " + filtro, fNormal));
            documento.add(new Paragraph(" "));

            Paragraph tituloCentrado = new Paragraph("REPORTE DE PAGOS POR CURSO", fTitulo);
            tituloCentrado.setAlignment(Element.ALIGN_CENTER);
            documento.add(tituloCentrado);
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph("Curso: " + nombreCurso, fSubtitulo));
            documento.add(new Paragraph("Total pendiente global: Bs. " + String.format("%.2f", totalPendienteGlobal), fNormal));
            documento.add(new Paragraph("Personas en este reporte: " + listaEstudiantes.size(), fNormal));
            documento.add(new Paragraph(" "));
            documento.add(new Chunk(new LineSeparator(1f, 100, BaseColor.BLACK, Element.ALIGN_CENTER, 0)));
            documento.add(new Paragraph(" "));

            for (Object[] est : listaEstudiantes) {
                documento.add(new Paragraph((String) est[0], fSubtitulo));
                documento.add(new Paragraph("Saldo pendiente individual: " + est[1], fNormal));
                documento.add(new Paragraph("Información de pagos realizados:", fSubtitulo));
                documento.add(new Paragraph(" "));

                // Tabla de pagos 
                PdfPTable tabla = new PdfPTable(5);
                tabla.setWidthPercentage(100);
                String[] headers = {"Fecha", "Monto", "Cuota", "Método", "Banco"};

                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, fTablaCabecera));
                    cell.setBackgroundColor(new BaseColor(59, 130, 246)); 
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(cell);
                }

                java.util.List<Object[]> pagos = (java.util.List<Object[]>) est[2];
                for (Object[] p : pagos) {
                    tabla.addCell(new Phrase(p[0].toString(), fNormal)); // Fecha
                    tabla.addCell(new Phrase(p[1].toString(), fNormal)); // Monto
                    tabla.addCell(new Phrase(p[2].toString(), fNormal)); // Cuota
                    tabla.addCell(new Phrase(p[3].toString(), fNormal)); // Método
                    tabla.addCell(new Phrase(p[4].toString(), fNormal)); // Banco
                }

                documento.add(tabla);
                documento.add(new Paragraph(" "));
                documento.add(new Chunk(new LineSeparator(0.5f, 100, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, 0)));
                documento.add(new Paragraph(" "));
            }

            documento.close();
            Desktop.getDesktop().open(new File(nombreArchivo));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte: " + e.getMessage());
        }
    }
    
    public static void generarReciboPago(int idInscripcion, String nroCuota, String nombre, 
                                        String cedula, String curso, String modalidad, 
                                        double monto, String metodo, String banco, 
                                        String fecha, String obs, String ref) {
        
        String rutaCarpeta = "Recibos/" + cedula + "/" + curso.replaceAll("[^a-zA-Z0-9]", "_");
        File directorio = new File(rutaCarpeta);
        if (!directorio.exists()) directorio.mkdirs();

        String nombreArchivo = rutaCarpeta + "/recibo_cuota_" + nroCuota + ".pdf";
        Document documento = new Document(PageSize.LETTER);

        try {
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();

            Font fTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font fNormal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font fPie = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
            Paragraph titulo = new Paragraph("RECIBO DE PAGO", fTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10);
            documento.add(titulo);

            documento.add(new Chunk(new LineSeparator(1f, 100, BaseColor.BLACK, Element.ALIGN_CENTER, 0)));
            documento.add(new Paragraph(" ")); 

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100); 
            tabla.setWidths(new float[]{30, 70}); 

            agregarFila(tabla, "Alumno:", nombre, fNormal);
            agregarFila(tabla, "Cédula:", cedula, fNormal);
            agregarFila(tabla, "Curso:", curso, fNormal);
            agregarFila(tabla, "Modalidad de pago:", modalidad.substring(0, 1).toUpperCase() + modalidad.substring(1).toLowerCase(), fNormal);
            agregarFila(tabla, "Número de cuota:", nroCuota, fNormal);
            agregarFila(tabla, "Monto pagado:", "Bs. " + String.format("%.2f", monto), fNormal);
            agregarFila(tabla, "Método de pago:", metodo.substring(0, 1).toUpperCase() + metodo.substring(1).toLowerCase(), fNormal);
            
            if (metodo.equalsIgnoreCase("transferencia")) {
                agregarFila(tabla, "Banco:", banco, fNormal);
                agregarFila(tabla, "Nro. de referencia:", ref, fNormal);
            } else {
                agregarFila(tabla, "Banco:", "No aplica", fNormal);
            }
            
            agregarFila(tabla, "Fecha de pago:", fecha, fNormal);

            documento.add(tabla);

            if (obs != null && !obs.trim().isEmpty()) {
                Paragraph pObs = new Paragraph("\nObservaciones: " + obs, fNormal);
                pObs.setIndentationLeft(10);
                documento.add(pObs);
            }

            documento.add(new Paragraph(" "));
            documento.add(new Chunk(new LineSeparator(1f, 100, BaseColor.BLACK, Element.ALIGN_CENTER, 0)));
          
            Paragraph pie = new Paragraph("\nID Inscripción: " + idInscripcion + " | Recibo generado automáticamente", fPie);
            pie.setAlignment(Element.ALIGN_CENTER);
            documento.add(pie);

            documento.close();

            Desktop.getDesktop().open(new File(nombreArchivo));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al crear PDF: " + e.getMessage());
        }
    }

    private static void agregarFila(PdfPTable tabla, String etiqueta, String valor, Font fuente) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, fuente));
        celdaEtiqueta.setBorder(Rectangle.NO_BORDER);
        celdaEtiqueta.setPaddingBottom(8); 
        tabla.addCell(celdaEtiqueta);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, fuente));
        celdaValor.setBorder(Rectangle.NO_BORDER);
        celdaValor.setPaddingBottom(8);
        tabla.addCell(celdaValor);
    }
}