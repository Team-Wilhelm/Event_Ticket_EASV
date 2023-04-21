package ticketSystemEASV.be;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Voucher extends Ticket {
    private UUID id;
    private String voucherType;
    private byte[] voucherQR;
    private TicketType ticketType;
    private Event event;
    private boolean redeemed;

    public Voucher(String voucherType, byte[] QRcode) {
        super(QRcode);
        this.voucherType = voucherType;
        this.redeemed = false;
        this.ticketType = TicketType.VOUCHER;
    }

    public Voucher(Event event, String voucherType) {
        super();
        this.voucherType = voucherType;
        this.event = event;
        this.ticketType = TicketType.VOUCHER;

        try {
            generateQRCode(150);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Voucher(Event event, String voucherType, byte[] QRcode) {
        super(event, null, QRcode);
        this.voucherType = voucherType;
        this.ticketType = TicketType.VOUCHER;
    }

    public Voucher(Event event, String voucherType, UUID id, byte[] QRcode, boolean redeemed) {
        super(event, null);
        this.id = id;
        this.voucherType = voucherType;
        this.voucherQR = QRcode;
        this.redeemed = redeemed;
        this.ticketType = TicketType.VOUCHER;
    }

    public UUID getId() {
        return id;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public byte[] getVoucherQR() {
        return voucherQR;
    }

    public void setVoucherQR(byte[] voucherQR) {
        this.voucherQR = voucherQR;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public boolean setRedeemed(boolean redeemed) {
        if(redeemed) {
            this.redeemed = true;
        }
        return false;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voucher voucher = (Voucher) o;
        return Objects.equals(id, voucher.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void generateQRCode(int size) throws WriterException {
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.MARGIN, 1);

        //The BitMatrix class represents the 2D matrix of bits
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String((voucherType
                        + " " + redeemed
                        + " " + id).getBytes(StandardCharsets.UTF_8)),
                BarcodeFormat.QR_CODE, 1, 1, hintMap);
        //MultiFormatWriter is a factory class that finds the appropriate Writer subclass for the BarcodeFormat requested and encodes the barcode with the supplied contents.

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "png", baos);
            this.voucherQR = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
