package service;

import dao.MyConnection;
import model.IadeBilgi;
import model.SatisStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SatisIptalService {
    
    /**
     * Satışı iptal eder
     * @param saleId Satış ID
     * @return İşlem başarılı ise true
     */
    public boolean satisIptalEt(int saleId) {
        try (Connection conn = MyConnection.connection()) {
            // Önce stok miktarını geri al
            if (!stokMiktariniGeriAl(conn, saleId)) {
                return false;
            }
            
            // Satış durumunu güncelle
            String sql = "UPDATE Sales SET status = ? WHERE sale_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, SatisStatus.CANCELLED.name());
            ps.setInt(2, saleId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Satış iptal edilirken hata: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Satışı iade alır ve iade bilgilerini kaydeder
     * @param saleId Satış ID
     * @param iadeBilgi İade bilgileri
     * @return İşlem başarılı ise true
     */
    public boolean satisIadeAl(int saleId, IadeBilgi iadeBilgi) {
        Connection conn = null;
        try {
            conn = MyConnection.connection();
            conn.setAutoCommit(false);
            
            // Önce stok miktarını geri al
            if (!stokMiktariniGeriAl(conn, saleId)) {
                conn.rollback();
                return false;
            }
            
            // Satış durumunu güncelle
            String sql = "UPDATE Sales SET status = ? WHERE sale_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, SatisStatus.REFUNDED.name());
            ps.setInt(2, saleId);
            
            if (ps.executeUpdate() == 0) {
                conn.rollback();
                return false;
            }
            
            // İade bilgilerini kaydet
            sql = "INSERT INTO RefundedSales (sale_id, reason, notes) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, saleId);
            ps.setString(2, iadeBilgi.getReason());
            ps.setString(3, iadeBilgi.getNotes());
            
            if (ps.executeUpdate() > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback hatası: " + ex.getMessage());
            }
            System.err.println("Satış iade alınırken hata: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Bağlantı kapatılırken hata: " + e.getMessage());
            }
        }
    }
    
    /**
     * Satılan ürünün stok miktarını geri alır
     */
    private boolean stokMiktariniGeriAl(Connection conn, int saleId) throws SQLException {
        // Önce satış bilgilerini al
        String sql = "SELECT product_id, quantity FROM Sales WHERE sale_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, saleId);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            int productId = rs.getInt("product_id");
            int quantity = rs.getInt("quantity");
            
            // Stok miktarını güncelle
            sql = "UPDATE Products SET stock = stock + ? WHERE product_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            
            return ps.executeUpdate() > 0;
        }
        
        return false;
    }
}
