package ticketSystemEASV.be;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class TicketView {
    private Ticket ticket;

    /*public void constructTicketView(Ticket ticket) {
        File file = new File("src/main/resources/images/test.png");
        Image i = new Image("file:C:\\ticket.png");
        BufferedImage bufferedImage = new BufferedImage(250, 100, BufferedImage.TYPE_INT_ARGB);

        this.ticket = ticket;
        VBox vbox = new VBox(10,
                new Label("Ticket for " + ticket.getEvent().getEventName()),
                new Label("Customer: " + ticket.getCustomer().getName()));
        vbox.setPrefWidth(250);
        vbox.setPrefHeight(100);

        Stage stage = new Stage();
        stage.setScene(new Scene(vbox));
        stage.show();

        WritableImage snapshot = vbox.snapshot(new SnapshotParameters(), null);
        vbox.getChildren().add(new ImageView(snapshot));
        BufferedImage image;
        image = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, bufferedImage);
        try {
            Graphics2D gd = (Graphics2D) image.getGraphics();
            gd.translate(vbox.getWidth(), vbox.getHeight());
            ImageIO.write(image, "png", file);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }*/

    public void generateTicket(Ticket ticket){
        try {
            FileOutputStream fos = new FileOutputStream("src/main/resources/test.pdf");
            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, fos);
            doc.open();

            PdfPTable table = new PdfPTable(2); // 2 columns
            table.setWidthPercentage(100); // Set table width to 100%
            table.setSpacingBefore(10f); // Set spacing before the table
            table.setSpacingAfter(10f); // Set spacing after the table

            // Add a title to the PDF file
            Paragraph title = new Paragraph("Whadup suckers", new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            doc.add(title);

            // Add the QR code to the ticket
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(generateQRCode(ticket, 150), "png", baos);
            com.itextpdf.text.Image iTextImage = com.itextpdf.text.Image.getInstance(baos.toByteArray());
            doc.add(iTextImage);

            // Add a table containing ticket information to the PDF file
            table.addCell("Ticket ID");
            table.addCell(ticket.getId().toString());

            table.addCell("Event");
            table.addCell(ticket.getEvent().getEventName());

            table.addCell("Email");
            table.addCell(ticket.getCustomer().getEmail());

            table.addCell("Start date");
            table.addCell(ticket.getEvent().getStartDate().toString());

            table.addCell("Start time");
            table.addCell(ticket.getEvent().getStartTime().toString());

            table.addCell("Location");
            table.addCell(ticket.getEvent().getLocation());

            if (ticket.getEvent().getEndDate() != null) {
                table.addCell("End date");
                table.addCell(ticket.getEvent().getEndDate().toString());
            }

            if (ticket.getEvent().getEndTime() != null) {
                table.addCell("End time");
                table.addCell(ticket.getEvent().getEndTime().toString());
            }

            if (ticket.getEvent().getLocationGuidance() != null) {
                table.addCell("Location guidance");
                table.addCell(ticket.getEvent().getLocationGuidance());
            }

            // Add a header to the PDF file
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase( ticket.getEvent().getEventName() + " Ticket", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD));
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, header, (doc.right() - doc.left()) / 2 + doc.leftMargin(), doc.top() + 10, 0);

            // Add a footer to the PDF file
            Phrase footer = new Phrase("Thank you for your purchase!", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC));
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, (doc.right() - doc.left()) / 2 + doc.leftMargin(), doc.bottom() - 10, 0);

            doc.add(table);
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage generateQRCode(Ticket ticket, int size) throws WriterException {
        //The BitMatrix class represents the 2D matrix of bits
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String((ticket.getEvent().getEventName() + " " + ticket.getCustomer().getName()).getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8), BarcodeFormat.QR_CODE, size, size);
        return MatrixToImageWriter.toBufferedImage(matrix);
        //MultiFormatWriter is a factory class that finds the appropriate Writer subclass for the BarcodeFormat requested and encodes the barcode with the supplied contents.
    }
}
