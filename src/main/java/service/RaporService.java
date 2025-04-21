package service;

import dao.MyConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import model.SatisRaporu;
import javax.swing.table.DefaultTableModel;

public class RaporService {
    
    public SatisRaporu gunlukRaporOlustur(LocalDateTime tarih) {
        LocalDateTime baslangic = tarih.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime bitis = baslangic.plusDays(1);
        return raporOlustur(baslangic, bitis);
    }
    
    public SatisRaporu aylikRaporOlustur(int yil, int ay) {
        LocalDateTime baslangic = LocalDateTime.of(yil, ay, 1, 0, 0);
        LocalDateTime bitis = baslangic.plusMonths(1);
        return raporOlustur(baslangic, bitis);
    }
    
    public SatisRaporu yillikRaporOlustur(int yil) {
        LocalDateTime baslangic = LocalDateTime.of(yil, 1, 1, 0, 0);
        LocalDateTime bitis = baslangic.plusYears(1);
        return raporOlustur(baslangic, bitis);
    }
    
    private SatisRaporu raporOlustur(LocalDateTime baslangic, LocalDateTime bitis) {
        SatisRaporu rapor = new SatisRaporu();
        rapor.setBaslangicTarihi(baslangic);
        rapor.setBitisTarihi(bitis);
        
        try (Connection conn = MyConnection.connection()) {
            // Toplam satış ve kar bilgisi
            String sql = "SELECT COUNT(*) as satis_adedi, " +
                        "SUM(total_price) as toplam_satis, " +
                        "SUM(gain) as toplam_kar " +
                        "FROM Sales " +
                        "WHERE sale_date BETWEEN ? AND ?";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, baslangic);
            ps.setObject(2, bitis);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                rapor.setSatisAdedi(rs.getInt("satis_adedi"));
                rapor.setToplamSatis(rs.getDouble("toplam_satis"));
                rapor.setToplamKar(rs.getDouble("toplam_kar"));
            }
            
            // En çok satan ürün
            sql = "SELECT p.name, SUM(s.quantity) as toplam_satis " +
                  "FROM Sales s " +
                  "JOIN Products p ON s.product_id = p.product_id " +
                  "WHERE s.sale_date BETWEEN ? AND ? " +
                  "GROUP BY p.product_id, p.name " +
                  "ORDER BY toplam_satis DESC " +
                  "LIMIT 1";
            
            ps = conn.prepareStatement(sql);
            ps.setObject(1, baslangic);
            ps.setObject(2, bitis);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                rapor.setEnCokSatanUrun(rs.getString("name"));
            }
            
            // En çok satan kategori
            sql = "SELECT c.category_name, SUM(s.quantity) as toplam_satis " +
                  "FROM Sales s " +
                  "JOIN Products p ON s.product_id = p.product_id " +
                  "JOIN Categories c ON p.category_id = c.category_id " +
                  "WHERE s.sale_date BETWEEN ? AND ? " +
                  "GROUP BY c.category_id, c.category_name " +
                  "ORDER BY toplam_satis DESC " +
                  "LIMIT 1";
            
            ps = conn.prepareStatement(sql);
            ps.setObject(1, baslangic);
            ps.setObject(2, bitis);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                rapor.setEnCokSatanKategori(rs.getString("category_name"));
            }
            
        } catch (Exception e) {
            System.out.println("Rapor oluşturulurken hata: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rapor;
    }
    
    public DefaultTableModel getRaporTableModel(LocalDateTime baslangic, LocalDateTime bitis) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabloyu düzenlenemez yap
            }
        };
        
        model.addColumn("Tarih");
        model.addColumn("Ürün Adı");
        model.addColumn("Kategori");
        model.addColumn("Satış Adedi");
        model.addColumn("Birim Fiyat (TL)");
        model.addColumn("Toplam Satış (TL)");
        model.addColumn("Kar (TL)");
        
        try (Connection conn = MyConnection.connection()) {
            String sql = "SELECT DATE(s.sale_date) as tarih, " +
                        "p.name as urun_adi, " +
                        "c.category_name as kategori, " +
                        "s.quantity as satis_adedi, " +
                        "s.total_price/s.quantity as birim_fiyat, " +
                        "s.total_price as toplam_satis, " +
                        "s.gain as kar " +
                        "FROM Sales s " +
                        "JOIN Products p ON s.product_id = p.product_id " +
                        "JOIN Categories c ON p.category_id = c.category_id " +
                        "WHERE s.sale_date BETWEEN ? AND ? " +
                        "ORDER BY s.sale_date DESC";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, baslangic);
            ps.setObject(2, bitis);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getDate("tarih"),
                    rs.getString("urun_adi"),
                    rs.getString("kategori"),
                    rs.getInt("satis_adedi"),
                    String.format("%.2f", rs.getDouble("birim_fiyat")),
                    String.format("%.2f", rs.getDouble("toplam_satis")),
                    String.format("%.2f", rs.getDouble("kar"))
                });
            }
            
        } catch (Exception e) {
            System.out.println("Rapor tablosu oluşturulurken hata: " + e.getMessage());
            e.printStackTrace();
        }
        
        return model;
    }
}
