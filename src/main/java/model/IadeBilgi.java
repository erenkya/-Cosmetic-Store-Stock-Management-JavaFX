package model;

import java.time.LocalDateTime;

public class IadeBilgi {
    private int refundId;
    private int saleId;
    private LocalDateTime refundDate;
    private String reason;
    private String notes;
    
    public IadeBilgi() {
        this.refundDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getRefundId() {
        return refundId;
    }
    
    public void setRefundId(int refundId) {
        this.refundId = refundId;
    }
    
    public int getSaleId() {
        return saleId;
    }
    
    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }
    
    public LocalDateTime getRefundDate() {
        return refundDate;
    }
    
    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
