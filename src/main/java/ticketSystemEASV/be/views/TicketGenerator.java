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
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.gui.model.EventModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TicketGenerator {
    private static PdfFont FONT;
    private static final int FONT_SIZE = 14;

    public void generateTicket(Ticket ticket){
        try {
            int rows = 0;
            FONT = PdfFontFactory.createFont(FontConstants.HELVETICA);
            //TODO choose a font

            // Open a new PDF document
            PdfWriter writer = new PdfWriter("src/main/resources/" + ticket.getId() + ".pdf");
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            Table mainTable = new Table(UnitValue.createPercentArray(new float[] {30, 70})); // 2 columns
            mainTable.setWidthPercent(100); // Set table width to 100%

            // Add the event information
            Table table = new Table(1);
            table.setMarginTop(10);
            table.setMarginLeft(10);

            Cell eventName = new Cell(1, 1);
            eventName.add(new Paragraph(new Text(ticket.getEvent().getEventName()).setBold())).setFontSize(FONT_SIZE+4);
            rows++;

            Cell eventDateAndTime = new Cell(1, 1);
            eventDateAndTime.add(new Paragraph(
                    new Text(new SimpleDateFormat("dd.MM.yyyy").format(ticket.getEvent().getStartDate()) + " "
                            + new SimpleDateFormat("hh:mm").format(ticket.getEvent().getStartTime())).setBold()))
                    .setFontSize(FONT_SIZE+2); // Event date and time
            rows++;

            Cell eventLocation = new Cell(1, 1);
            eventLocation.add(new Paragraph(ticket.getEvent().getLocation())).setFontSize(FONT_SIZE+2); // Event location
            rows++;

            Cell locationGuidance = null;
            if (!ticket.getEvent().getLocationGuidance().isEmpty()) {
                locationGuidance= new Cell(1, 1);
                locationGuidance.add(new Paragraph("Location guidance: " + ticket.getEvent().getLocationGuidance())).setFontSize(FONT_SIZE); // Event location guidance
                rows++;
            }

            // If an event has an end date and/or time, add it to the ticket
            Cell endDateAndTime = null;
            Paragraph endDateTime = new Paragraph();
            if (ticket.getEvent().getEndDate() != null)
                endDateTime.add(new SimpleDateFormat("dd.MM.yyyy").format(ticket.getEvent().getEndDate())); // Event end date
            if (ticket.getEvent().getEndTime() != null)
                endDateTime.add(" " + new SimpleDateFormat("hh:mm").format(ticket.getEvent().getEndTime())); // Event end time
            if (!endDateTime.isEmpty()){
                endDateAndTime = new Cell(1, 1);
                endDateAndTime.add(new Paragraph("End time: ").add(endDateTime)).setFontSize(FONT_SIZE);
                rows++;
            }

            // Add the QR code to the ticket
            Image qrCode = new Image(ImageDataFactory.create(ticket.getTicketQR()));
            qrCode.setHorizontalAlignment(HorizontalAlignment.CENTER);
            qrCode.setAutoScale(true);

            Cell qrCell = new Cell(rows, 1);
            qrCell.add(qrCode);
            qrCell.setHorizontalAlignment(HorizontalAlignment.CENTER);
            qrCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

            mainTable.addCell(qrCell);
            mainTable.addCell(eventName);
            mainTable.addCell(eventDateAndTime);
            mainTable.addCell(eventLocation);
            if (locationGuidance != null)
                mainTable.addCell(locationGuidance);
            if (endDateAndTime != null)
                mainTable.addCell(endDateAndTime);

            Cell cell = new Cell(1, 2); // Ticket ID
            cell.add(new Paragraph("Ticket ID: " + ticket.getId())).setFontSize(FONT_SIZE);
            mainTable.addCell(cell);

            doc.add(mainTable);
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] generateQRCode(Ticket ticket, int size) throws WriterException {
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.MARGIN, 1);

        //The BitMatrix class represents the 2D matrix of bits
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String((ticket.getEvent().getEventName()
                        + " " + ticket.getCustomer().getName()
                        + " " + ticket.getId()).getBytes(StandardCharsets.UTF_8)),
                BarcodeFormat.QR_CODE, 1, 1, hintMap);
        //MultiFormatWriter is a factory class that finds the appropriate Writer subclass for the BarcodeFormat requested and encodes the barcode with the supplied contents.

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void removeBorder(Table table) {
        for (IElement iElement : table.getChildren()) {
            ((Cell)iElement).setBorder(Border.NO_BORDER);
        }
    }
}
