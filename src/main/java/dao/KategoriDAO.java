package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {
    public List<String> tumKategorileriGetir() {
        List<String> kategoriler = new ArrayList<>();
        
        try {
            String sql = "SELECT category_name FROM Categories ORDER BY category_id";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                kategoriler.add(rs.getString("category_name"));
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("Kategoriler getirilirken hata: " + e.getMessage());
        }
        
        return kategoriler;
    }

    public List<String> kiyafetKategorileriGetir() {
        List<String> kategoriler = new ArrayList<>();
        
        try {
            String sql = "SELECT category_name FROM Categories WHERE category_type = 'KIYAFET' ORDER BY category_id";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                kategoriler.add(rs.getString("category_name"));
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("Kıyafet kategorileri getirilirken hata: " + e.getMessage());
        }
        
        return kategoriler;
    }

    public int getKategoriId(String kategoriAdi) {
        int kategoriId = -1;
        
        try {
            String sql = "SELECT category_id FROM Categories WHERE category_name = ?";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kategoriAdi);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()) {
                kategoriId = rs.getInt("category_id");
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("Kategori ID getirilirken hata: " + e.getMessage());
        }
        
        return kategoriId;
    }
} 