package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import dao.MyConnection;

public class SubKategoriDAO {
    public List<String> subKategorileriGetirByKategori(int kategoriId) {
        List<String> subKategoriler = new ArrayList<>();
        
        try {
            String sql = "SELECT sub_category_name FROM SubCategories WHERE category_id = ? ORDER BY sub_category_name";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, kategoriId);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                subKategoriler.add(rs.getString("sub_category_name"));
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("Alt kategoriler getirilirken hata: " + e.getMessage());
            e.printStackTrace();
        }
        
        return subKategoriler;
    }

    public int getSubKategoriId(String subKategoriAdi) {
        int subKategoriId = -1;
        
        try {
            String sql = "SELECT sub_category_id FROM SubCategories WHERE sub_category_name = ?";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, subKategoriAdi);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()) {
                subKategoriId = rs.getInt("sub_category_id");
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("Alt kategori ID getirilirken hata: " + e.getMessage());
            e.printStackTrace();
        }
        
        return subKategoriId;
    }
} 