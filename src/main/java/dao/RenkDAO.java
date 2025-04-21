package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class RenkDAO {
    public List<String> tumRenkleriGetir() {
        List<String> renkler = new ArrayList<>();
        
        try {
            String sql = "SELECT color_name FROM colors ORDER BY color_id";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                renkler.add(rs.getString("color_name"));
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            
            JOptionPane.showMessageDialog(null, "Renkler getirililrken hata :" + e.getMessage());
        }
        
        return renkler;
    }
    
    
    
    
    public List<String> renkGetir(){
        List<String> renkler = new ArrayList<>();
        
        try {
            String sql = "SELECT color_name FROM colors WHERE color_name = 'Standart' ORDER BY color_id";
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                renkler.add(rs.getString("color_name"));
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Renkler getirililrken hata :" + e.getMessage());
        }
        
        return renkler;
        
    }
} 