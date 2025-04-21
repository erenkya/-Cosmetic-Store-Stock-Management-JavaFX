package model;

public enum SatisStatus {
    COMPLETED("Tamamlandı"),
    CANCELLED("İptal Edildi"),
    REFUNDED("İade Edildi");
    
    private final String label;
    
    SatisStatus(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    @Override
    public String toString() {
        return label;
    }
}
