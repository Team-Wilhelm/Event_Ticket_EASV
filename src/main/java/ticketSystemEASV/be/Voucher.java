package ticketSystemEASV.be;

import java.util.Objects;
import java.util.UUID;

public class Voucher implements ITicket {
    private UUID id;
    private String voucherType, voucherQR;
    private TicketType ticketType;
    private Event event;
    private boolean redeemed;

    public Voucher(String voucherType) {
        this.voucherType = voucherType;
        this.redeemed = false;
        this.ticketType = TicketType.VOUCHER;
    }

    public Voucher(Event event, String voucherType) {
        this(voucherType);
        this.event = event;
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

    public String getVoucherQR() {
        return voucherQR;
    }

    public void setVoucherQR(String voucherQR) {
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
}
