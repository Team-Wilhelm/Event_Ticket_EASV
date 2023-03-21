package ticketSystemEASV.be.views;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import ticketSystemEASV.be.Ticket;

import javax.imageio.ImageIO;
import javax.swing.text.StyleConstants;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class TicketView {

    public void generateTicket(Ticket ticket){
        try {
            int rows = 0;

            // Open a new PDF document
            PdfWriter writer = new PdfWriter("src/main/resources/test.pdf");
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            Table mainTable = new Table(new float[] {1, 2}); // 2 columns
            mainTable.setWidthPercent(100); // Set table width to 100%

            // Add the event information
            Table table = new Table(1);
            table.addCell(new Paragraph(ticket.getEvent().getEventName())); // Event name
            rows++;

            table.addCell(new Paragraph(new SimpleDateFormat("dd.MM.yyyy").format(ticket.getEvent().getStartDate()) + " "
                    + new SimpleDateFormat("hh:mm").format(ticket.getEvent().getStartTime()))); // Event date and time
            rows++;

            table.addCell(new Paragraph(ticket.getEvent().getLocation())); // Event location
            rows++;

            if (ticket.getEvent().getLocationGuidance() != null) {
                table.addCell(new Paragraph("Location guidance: " + ticket.getEvent().getLocationGuidance())); // Event location guidance
                rows++;
            }

            // If an event has an end date and/or time, add it to the ticket
            Paragraph endDateTime = new Paragraph();
            if (ticket.getEvent().getEndDate() != null)
                endDateTime.add(new SimpleDateFormat("dd.MM.yyyy").format(ticket.getEvent().getEndDate())); // Event end date
            if (ticket.getEvent().getEndTime() != null)
                endDateTime.add(" " + new SimpleDateFormat("hh:mm").format(ticket.getEvent().getEndTime())); // Event end time
            if (!endDateTime.isEmpty()){
                table.addCell(endDateTime);
                rows++;
            }
            removeBorder(table);

            // Add the QR code to the ticket
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(generateQRCode(ticket, 100), "png", baos);
            Image qrCode = new Image(ImageDataFactory.create(baos.toByteArray()));
            qrCode.setHorizontalAlignment(HorizontalAlignment.CENTER);
            qrCode.setAutoScale(true);

            mainTable.addCell(qrCode);
            mainTable.addCell(table);
            Cell cell = new Cell(1, 2); // Ticket ID
            cell.add(new Paragraph("Ticket ID: " + ticket.getId()));
            mainTable.addCell(cell);

            doc.add(mainTable);
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage generateQRCode(Ticket ticket, int size) throws WriterException {
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.MARGIN, 1);

        //The BitMatrix class represents the 2D matrix of bits
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String((ticket.getEvent().getEventName()
                        + " " + ticket.getCustomer().getName()
                        + " " + ticket.getId()).getBytes(StandardCharsets.UTF_8)),
                BarcodeFormat.QR_CODE, size, size, hintMap);
        return MatrixToImageWriter.toBufferedImage(matrix);
        //MultiFormatWriter is a factory class that finds the appropriate Writer subclass for the BarcodeFormat requested and encodes the barcode with the supplied contents.
    }

    private static void removeBorder(Table table) {
        for (IElement iElement : table.getChildren()) {
            ((Cell)iElement).setBorder(Border.NO_BORDER);
        }
    }
}
