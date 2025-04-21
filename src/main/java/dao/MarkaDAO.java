package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MarkaDAO {

    public List<String> markalariGetirByKategori(int kategoriId) {
        List<String> markalar = new ArrayList<>();
        
        try {
            String sql = "SELECT b.brand_name FROM Brands b " +
                        "INNER JOIN Brand_Categories bc ON b.brand_id = bc.brand_id " +
                        "WHERE bc.category_id = ? " +
                        "ORDER BY b.brand_name";
                        
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, kategoriId);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                markalar.add(rs.getString("brand_name"));
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("Markalar getirilirken hata: " + e.getMessage());
        }
        
        return markalar;
    }
    
    public List<String> tumMarkalariGetir() {
        List<String> markalar = new ArrayList<>();
        
        try {
            String sql = "SELECT brand_name FROM Brands ORDER BY brand_name";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                markalar.add(rs.getString("brand_name"));
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("Markalar getirilirken hata: " + e.getMessage());
        }
        
        return markalar;
    }
    
    public boolean markaEkle(String markaAdi) {
        // Önce markanın var olup olmadığını kontrol et
        String kontrolSql = "SELECT COUNT(*) as count FROM Brands WHERE brand_name = ?";
        String ekleSql = "INSERT INTO Brands (brand_name) VALUES (?)";
        
        try (Connection conn = MyConnection.connection()) {
            // Marka kontrolü
            try (PreparedStatement pstmt = conn.prepareStatement(kontrolSql)) {
                pstmt.setString(1, markaAdi);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt("count") > 0) {
                    throw new SQLException("DUPLICATE");
                }
            }
            
            // Marka ekleme
            try (PreparedStatement pstmt = conn.prepareStatement(ekleSql)) {
                pstmt.setString(1, markaAdi);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            if ("DUPLICATE".equals(e.getMessage())) {
                return false;
            }
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean markaSil(String markaAdi) {
        String markaIdSql = "SELECT brand_id FROM Brands WHERE brand_name = ?";
        String urunKontrolSql = "SELECT COUNT(*) as count FROM Products WHERE brand_id = ?";
        String iliskiSilSql = "DELETE FROM Brand_Categories WHERE brand_id = ?";
        String markaSilSql = "DELETE FROM Brands WHERE brand_id = ?";
        
        try (Connection conn = MyConnection.connection()) {
            // Marka ID'sini al
            int markaId;
            try (PreparedStatement pstmt = conn.prepareStatement(markaIdSql)) {
                pstmt.setString(1, markaAdi);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) return false;
                markaId = rs.getInt("brand_id");
            }
            
            // Önce markaya ait ürün var mı kontrol et
            try (PreparedStatement pstmt = conn.prepareStatement(urunKontrolSql)) {
                pstmt.setInt(1, markaId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt("count") > 0) {
                    throw new SQLException("HAS_PRODUCTS");
                }
            }
            
            // Önce brand_categories tablosundan ilişkileri sil
            try (PreparedStatement pstmt = conn.prepareStatement(iliskiSilSql)) {
                pstmt.setInt(1, markaId);
                pstmt.executeUpdate();
            }
            
            // Sonra markayı sil
            try (PreparedStatement pstmt = conn.prepareStatement(markaSilSql)) {
                pstmt.setInt(1, markaId);
                return pstmt.executeUpdate() > 0;
            }
            
        } catch (SQLException e) {
            if ("HAS_PRODUCTS".equals(e.getMessage())) {
                return false;
            }
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean markaGuncelle(String eskiMarkaAdi, String yeniMarkaAdi) {
        try {
            // Önce yeni marka adının başka bir markada kullanılıp kullanılmadığını kontrol et
            String checkSql = "SELECT COUNT(*) FROM Brands WHERE brand_name = ? AND brand_name != ?";
            Connection conn = MyConnection.connection();
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, yeniMarkaAdi);
            checkPs.setString(2, eskiMarkaAdi);
            ResultSet rs = checkPs.executeQuery();
            rs.next();
            
            if (rs.getInt(1) > 0) {
                System.out.println("Bu marka adı zaten kullanımda!");
                return false;
            }
            
            // Marka adını güncelle
            String updateSql = "UPDATE Brands SET brand_name = ? WHERE brand_name = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setString(1, yeniMarkaAdi);
            updatePs.setString(2, eskiMarkaAdi);
            int affectedRows = updatePs.executeUpdate();
            
            rs.close();
            checkPs.close();
            updatePs.close();
            conn.close();
            
            return affectedRows > 0;
            
        } catch (Exception e) {
            System.out.println("Marka güncellenirken hata: " + e.getMessage());
            return false;
        }
    }
    
    public List<String> tumKategorileriGetir() {
        List<String> kategoriler = new ArrayList<>();
        String sql = "SELECT category_name FROM Categories ORDER BY category_name";
        
        try (Connection conn = MyConnection.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                kategoriler.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kategoriler;
    }
    
    public boolean markaKategoriIliskisiKur(String markaAdi, List<String> kategoriAdlari) {
        String markaIdSql = "SELECT brand_id FROM Brands WHERE brand_name = ?";
        String kategoriIdSql = "SELECT category_id FROM Categories WHERE category_name = ?";
        String iliskiSql = "INSERT INTO Brand_Categories (brand_id, category_id) VALUES (?, ?)";
        
        try (Connection conn = MyConnection.connection()) {
            // Marka ID'sini al
            int markaId;
            try (PreparedStatement pstmt = conn.prepareStatement(markaIdSql)) {
                pstmt.setString(1, markaAdi);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) return false;
                markaId = rs.getInt("brand_id");
            }
            
            // Her kategori için ilişki kur
            for (String kategoriAdi : kategoriAdlari) {
                // Kategori ID'sini al
                int kategoriId;
                try (PreparedStatement pstmt = conn.prepareStatement(kategoriIdSql)) {
                    pstmt.setString(1, kategoriAdi);
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.next()) continue;
                    kategoriId = rs.getInt("category_id");
                }
                
                // İlişkiyi kur
                try (PreparedStatement pstmt = conn.prepareStatement(iliskiSql)) {
                    pstmt.setInt(1, markaId);
                    pstmt.setInt(2, kategoriId);
                    pstmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<String> markaKategorileriniGetir(String markaAdi) {
        List<String> kategoriler = new ArrayList<>();
        String sql = "SELECT c.category_name FROM Categories c " +
                    "INNER JOIN Brand_Categories bc ON c.category_id = bc.category_id " +
                    "INNER JOIN Brands b ON bc.brand_id = b.brand_id " +
                    "WHERE b.brand_name = ?";
                    
        try (Connection conn = MyConnection.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, markaAdi);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                kategoriler.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kategoriler;
    }
    
    public boolean markaKategorileriniGuncelle(String markaAdi, List<String> yeniKategoriler) {
        String markaIdSql = "SELECT brand_id FROM Brands WHERE brand_name = ?";
        String silmeSql = "DELETE FROM Brand_Categories WHERE brand_id = ?";
        
        try (Connection conn = MyConnection.connection()) {
            // Marka ID'sini al
            int markaId;
            try (PreparedStatement pstmt = conn.prepareStatement(markaIdSql)) {
                pstmt.setString(1, markaAdi);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) return false;
                markaId = rs.getInt("brand_id");
            }
            
            // Mevcut ilişkileri sil
            try (PreparedStatement pstmt = conn.prepareStatement(silmeSql)) {
                pstmt.setInt(1, markaId);
                pstmt.executeUpdate();
            }
            
            // Yeni ilişkileri kur
            return markaKategoriIliskisiKur(markaAdi, yeniKategoriler);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
