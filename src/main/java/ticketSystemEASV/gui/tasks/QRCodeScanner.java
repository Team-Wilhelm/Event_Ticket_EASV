package ticketSystemEASV.gui.tasks;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javafx.scene.control.Label;
import ticketSystemEASV.gui.model.TicketModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRCodeScanner implements Runnable {
    private Webcam webcam;
    private TicketModel ticketModel;
    public Label ticketType, ticketValue, ticketEvent, ticketDate, ticketTime, ticketId, ticketLocation;

    public QRCodeScanner(Webcam webcam, TicketModel ticketModel, Label ticketType, Label ticketValue, Label ticketEvent, Label ticketDate, Label ticketTime, Label ticketId, Label ticketLocation) {
        this.webcam = webcam;
        this.ticketModel = ticketModel;
        this.ticketType = ticketType;
        this.ticketValue = ticketValue;
        this.ticketEvent = ticketEvent;
        this.ticketDate = ticketDate;
        this.ticketTime = ticketTime;
        this.ticketId = ticketId;
        this.ticketLocation = ticketLocation;
    }

    @Override
    public void run() {
        while (true) {
            try {
                BufferedImage image = webcam.getImage();
                Result result = readQRCode(image);
                if (result != null) {
                    System.out.println("QR Code Text: " + result.getText());
                    var ticket = ticketModel.getAllTickets().stream().filter(t -> t.getId().toString().toLowerCase().equals(result.getText().toLowerCase())).findFirst().get();
                    if (ticket != null) {
                        ticketId.setText(ticket.getId().toString());
                        ticketType.setText(ticket.getTicketType().toString());
                        if (ticket.getEvent() != null) {
                            ticketEvent.setText(ticket.getEvent().getEventName());
                            ticketDate.setText(ticket.getEvent().getStartDate().toString());
                            ticketTime.setText(ticket.getEvent().getStartTime().toString());
                            ticketLocation.setText(ticket.getEvent().getLocation());
                        }
                    }
                }
            } catch (IOException e) {
                // handle exception
            } catch (IllegalArgumentException e) {
                // handle exception
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Result readQRCode(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
        BufferedImage bufferedImage = ImageIO.read(bis);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
        Result result = null;
        try {
            result = new MultiFormatReader().decode(binaryBitmap);
        } catch (Exception e) {
            // No QR code found in the image
        }
        return result;
    }
}
