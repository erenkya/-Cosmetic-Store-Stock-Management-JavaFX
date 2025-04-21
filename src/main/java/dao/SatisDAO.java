/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import degisken.Degiskenler;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Satis;

/**
 *
 * @author susa
 */
public class SatisDAO {
    
    public boolean satisYap(int productId, int quantity, double totalPrice, double gain, String odemeTipi) {
        try {
            String sql = "INSERT INTO Sales (product_id, quantity, total_price, gain, type_of_sale, user_id) VALUES (?, ?, ?, ?, ?, ?)";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setInt(1, productId);
            ps.setInt(2, quantity);
            ps.setDouble(3, totalPrice);
            ps.setDouble(4, gain);
            ps.setString(5, odemeTipi);
            ps.setInt(6, Degiskenler.loginKul.getId());
            
            int sonuc = ps.executeUpdate();
            return sonuc > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean stokGuncelle(int productId, int quantity) {
        try {
            String sql = "UPDATE Products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            
            int sonuc = ps.executeUpdate();
            return sonuc > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Map<String, Object>> gunlukSatisRaporu() {
        List<Map<String, Object>> satislar = new ArrayList<>();
        try {
            String sql = "SELECT p.name as urunAdi, COUNT(*) as satisAdedi, " +
                        "SUM(s.total_price) as toplamTutar, " +
                        "SUM(s.gain) as toplamKazanc, " +
                        "s.type_of_sale as odemeTipi " +
                        "FROM Sales s " +
                        "INNER JOIN Products p ON s.product_id = p.product_id " +
                        "WHERE DATE(s.sale_date) = CURRENT_DATE " +
                        "GROUP BY p.product_id, p.name, s.type_of_sale";
            
            Connection con = MyConnection.connection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> satis = new HashMap<>();
                satis.put("urunAdi", rs.getString("urunAdi"));
                satis.put("satisAdedi", rs.getInt("satisAdedi"));
                satis.put("toplamTutar", rs.getDouble("toplamTutar"));
                satis.put("toplamKazanc", rs.getDouble("toplamKazanc"));
                satis.put("odemeTipi", rs.getString("odemeTipi"));
                satislar.add(satis);
            }
            
            rs.close();
            ps.close();
            con.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return satislar;
    }
    
    public List<Map<String, Object>> aylikSatisRaporu() {
        List<Map<String, Object>> satislar = new ArrayList<>();
        
        try {
            String sql = "SELECT p.name, COUNT(s.sale_id) as satis_adedi, " +
                        "SUM(s.quantity) as toplam_adet, " +
                        "SUM(s.total_price) as toplam_tutar, " +
                        "SUM(s.gain) as toplam_kazanc " +
                        "FROM Sales s " +
                        "JOIN Products p ON s.product_id = p.product_id " +
                        "WHERE MONTH(s.sale_date) = MONTH(CURRENT_DATE) " +
                        "AND YEAR(s.sale_date) = YEAR(CURRENT_DATE) " +
                        "GROUP BY p.product_id, p.name " +
                        "ORDER BY toplam_tutar DESC";
            
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> satis = new HashMap<>();
                satis.put("urunAdi", rs.getString("name"));
                satis.put("satisAdedi", rs.getInt("satis_adedi"));
                satis.put("toplamTutar", rs.getDouble("toplam_tutar"));
                satis.put("toplamKazanc", rs.getDouble("toplam_kazanc"));
                satislar.add(satis);
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return satislar;
    }
    
    public List<Map<String, Object>> urunAdinaGoreAylikSatisRaporu(String urunAdi) {
        List<Map<String, Object>> satislar = new ArrayList<>();

        try {
            String sql = "SELECT p.name, COUNT(s.sale_id) as satis_adedi, " +
                        "SUM(s.quantity) as toplam_adet, " +
                        "SUM(s.total_price) as toplam_tutar, " +
                        "SUM(s.gain) as toplam_kazanc " +
                        "FROM Sales s " +
                        "JOIN Products p ON s.product_id = p.product_id " +
                        "WHERE MONTH(s.sale_date) = MONTH(CURRENT_DATE) " +
                        "AND YEAR(s.sale_date) = YEAR(CURRENT_DATE) " +
                        "AND p.name LIKE ? " +
                        "GROUP BY p.product_id, p.name " +
                        "HAVING p.name LIKE ? " +  // Baştan başlayarak filtreleme için
                        "ORDER BY CASE " +
                        "   WHEN p.name LIKE ? THEN 0 " +  // Tam baştan başlayanlar
                        "   WHEN p.name LIKE ? THEN 1 " +  // Kelime ortasında olanlar
                        "   ELSE 2 " +
                        "END, " +
                        "p.name ASC";  // Alfabetik sıralama

            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);

            // İlk LIKE için - genel arama
            ps.setString(1, "%" + urunAdi + "%");
            // İkinci LIKE için - baştan başlayan arama
            ps.setString(2, urunAdi + "%");
            // Üçüncü LIKE için - tam baştan başlayanlar
            ps.setString(3, urunAdi + "%");
            // Dördüncü LIKE için - kelime ortasında olanlar
            ps.setString(4, "%" + urunAdi + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> satis = new HashMap<>();
                satis.put("urunAdi", rs.getString("name"));
                satis.put("satisAdedi", rs.getInt("satis_adedi"));
                satis.put("toplamTutar", rs.getDouble("toplam_tutar"));
                satis.put("toplamKazanc", rs.getDouble("toplam_kazanc"));
                satislar.add(satis);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return satislar;
    }
}
