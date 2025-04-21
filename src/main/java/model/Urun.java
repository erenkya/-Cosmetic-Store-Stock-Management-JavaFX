package model;

import service.ExchangeRateService;

public class Urun {
    private int id;
    private String barkod;
    private String ad;
    private String kategori;
    private String altKategori;
    private String marka;
    private String renk;
    private String beden;

    public double getSatisFiyati() {
        return satisFiyati;
    }

    public void setSatisFiyati(double satisFiyati) {
        this.satisFiyati = satisFiyati;
    }

    public double getMaliyet() {
        return maliyet;
    }

    public void setMaliyet(double maliyet) {
        this.maliyet = maliyet;
    }
    private double satisFiyati;
    private double maliyet;
    private int stokMiktari;
    private int reorderLevel;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarkod() {
        return barkod;
    }

    public void setBarkod(String barkod) {
        this.barkod = barkod;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getAltKategori() {
        return altKategori;
    }

    public void setAltKategori(String altKategori) {
        this.altKategori = altKategori;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public String getRenk() {
        return renk;
    }

    public void setRenk(String renk) {
        this.renk = renk;
    }   

    public String getBeden() {
        return beden;
    }

    public void setBeden(String beden) {
        this.beden = beden;
    }

   

    public int getStokMiktari() {
        return stokMiktari;
    }

    public void setStokMiktari(int stokMiktari) {
        this.stokMiktari = stokMiktari;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
 
}