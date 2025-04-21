package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class BedenDAO {
    
    public List<String> tumBedenleriGetir() {
        List<String> bedenler = new ArrayList<>();
        
        try {
            String sql = "SELECT size_name FROM sizes ORDER BY size_id";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                bedenler.add(rs.getString("size_name"));
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Bedenler getirililrken hata :" + e.getMessage());
            
        }
        
        return bedenler;
    }
    
    
    
    public List<String> bedenleriGetirByKategori(int kategoriId) {
        List<String> bedenler = new ArrayList<>();
        
        try {
            String sql = "SELECT size_name FROM Sizes WHERE category_id = ? ORDER BY size_id";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, kategoriId);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                bedenler.add(rs.getString("size_name"));
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Bedenler getirililrken hata :" + e.getMessage());
            
        }
        
        return bedenler;
    }
    
    
    
    public List<String> bedenGetir(int kategoriId) {
        List<String> bedenler = new ArrayList<>();
        
        try {
            String sql = "SELECT size_name FROM Sizes WHERE category_id = ? ORDER BY size_id";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, kategoriId);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                bedenler.add(rs.getString("size_name"));
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Bedenler getirililrken hata :" + e.getMessage());
        }
        
        return bedenler;
    }
} 