package ticketSystemEASV.be.views;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import ticketSystemEASV.be.Ticket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class TicketView {

    public void generateTicket(Ticket ticket){
        try {
            FileOutputStream fos = new FileOutputStream("src/main/resources/test.pdf");
            PdfWriter writer = new PdfWriter("src/main/resources/test.pdf");
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            Table mainTable = new Table(new float[] {1, 2}); // 2 columns
            mainTable.setWidthPercent(100); // Set table width to 100%
            //mainTable.setSpacingBefore(10f); // Set spacing before the table
            //mainTable.setSpacingAfter(10f); // Set spacing after the table
            //mainTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            //mainTable.completeRow();

            /*// Add a title to the PDF file
            Paragraph title = new Paragraph("Whadup suckers", new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            doc.add(title);*/

            // Add the QR code to the ticket
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(generateQRCode(ticket, 100), "png", baos);
            Image qrCode = new Image(ImageDataFactory.create(baos.toByteArray()));

            // Add a table containing ticket information to the PDF file
            var cell = new Cell();
            cell.add(qrCode);
            //cell.setBorder(PdfPCell.NO_BORDER);
            mainTable.addCell(cell);

            Table table = new Table(1);
            //table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            table.addCell(new Paragraph(ticket.getEvent().getEventName()));

            table.addCell(new Paragraph(new SimpleDateFormat("dd.MM.yyyy").format(ticket.getEvent().getStartDate()) + " "
                    + new SimpleDateFormat("hh:mm").format(ticket.getEvent().getStartTime())));
            table.addCell(ticket.getEvent().getLocation());
            table.addCell(" ");

            table.addCell(new Paragraph("Ticket ID: " + ticket.getId().toString()));


            if (ticket.getEvent().getEndDate() != null) {
                table.addCell(ticket.getEvent().getEndDate().toString());
            }

            if (ticket.getEvent().getEndTime() != null) {
                table.addCell(ticket.getEvent().getEndTime().toString());
            }

            if (ticket.getEvent().getLocationGuidance() != null) {
                table.addCell(new Paragraph("Location guidance: " + ticket.getEvent().getLocationGuidance()));
            }

            table.addCell(" ");

            /*// Add a header to the PDF file
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase( ticket.getEvent().getEventName() + " Ticket", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD));
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, header, (doc.right() - doc.left()) / 2 + doc.leftMargin(), doc.top() + 10, 0);

            // Add a footer to the PDF file
            Phrase footer = new Phrase("Thank you for your purchase!", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC));
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, (doc.right() - doc.left()) / 2 + doc.leftMargin(), doc.bottom() - 10, 0);*/

            mainTable.addCell(table);
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
}
