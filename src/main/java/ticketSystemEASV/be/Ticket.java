package ticketSystemEASV.be;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.scene.image.Image;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Customer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Ticket {
    private Event event;
    private Customer customer;
    private UUID id;
    private String ticketType, ticketQR;

    public Ticket(Event event, Customer customer, String ticketType, String ticketQR) {
        this.event = event;
        this.customer = customer;
        this.ticketType = ticketType;
        this.ticketQR = ticketQR;

        try {
            generateQRCode();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Ticket(UUID id, Event event, Customer customer, String ticketType, String ticketQR) {
        this(event, customer, ticketType, ticketQR);
        this.id = id;
    }

    public Ticket(Event event, Customer customer, String ticketType) {
        this.event = event;
        this.customer = customer;
        this.ticketType = ticketType;
        try {
            generateQRCode();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketQR() {
        return ticketQR;
    }

    public void setTicketQR(String ticketQR) {
        this.ticketQR = ticketQR;
    }

    public UUID getId() {
        return id;
    }

    private void generateQRCode() throws WriterException, IOException {
        //the BitMatrix class represents the 2D matrix of bits
        //MultiFormatWriter is a factory class that finds the appropriate Writer subclass for the BarcodeFormat requested and encodes the barcode with the supplied contents.
        File file = new File("src/main/resources/ticket.png");
        Image i = new Image("file:C:\\test.png");
        String path = "src/main/resources/ticket.png";
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String((event.getEventName() + " " + customer.getName()).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), BarcodeFormat.QR_CODE, 300, 300);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }
}
