package model;

import java.time.LocalDateTime;

public class SatisRaporu {
    private LocalDateTime baslangicTarihi;
    private LocalDateTime bitisTarihi;
    private double toplamSatis;
    private double toplamKar;
    private int satisAdedi;
    private String enCokSatanUrun;
    private String enCokSatanKategori;
    
    public SatisRaporu() {
    }
    
    // Getters and Setters
    public LocalDateTime getBaslangicTarihi() {
        return baslangicTarihi;
    }
    
    public void setBaslangicTarihi(LocalDateTime baslangicTarihi) {
        this.baslangicTarihi = baslangicTarihi;
    }
    
    public LocalDateTime getBitisTarihi() {
        return bitisTarihi;
    }
    
    public void setBitisTarihi(LocalDateTime bitisTarihi) {
        this.bitisTarihi = bitisTarihi;
    }
    
    public double getToplamSatis() {
        return toplamSatis;
    }
    
    public void setToplamSatis(double toplamSatis) {
        this.toplamSatis = toplamSatis;
    }
    
    public double getToplamKar() {
        return toplamKar;
    }
    
    public void setToplamKar(double toplamKar) {
        this.toplamKar = toplamKar;
    }
    
    public int getSatisAdedi() {
        return satisAdedi;
    }
    
    public void setSatisAdedi(int satisAdedi) {
        this.satisAdedi = satisAdedi;
    }
    
    public String getEnCokSatanUrun() {
        return enCokSatanUrun;
    }
    
    public void setEnCokSatanUrun(String enCokSatanUrun) {
        this.enCokSatanUrun = enCokSatanUrun;
    }
    
    public String getEnCokSatanKategori() {
        return enCokSatanKategori;
    }
    
    public void setEnCokSatanKategori(String enCokSatanKategori) {
        this.enCokSatanKategori = enCokSatanKategori;
    }
}
