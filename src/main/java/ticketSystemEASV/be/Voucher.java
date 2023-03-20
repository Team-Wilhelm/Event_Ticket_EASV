package ticketSystemEASV.be;

public class Voucher {
    private int id;
    private String restrictions, voucherType, voucherQR;
    private boolean redeemed;

    public Voucher(int id, String restrictions, String voucherType, String voucherQR, boolean redeemed) {
        this.id = id;
        this.restrictions = restrictions;
        this.voucherType = voucherType;
        this.voucherQR = voucherQR;
        this.redeemed = redeemed;
    }

    public int getId() {
        return id;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
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
}
