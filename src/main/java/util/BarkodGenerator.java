package util;

import dao.UrunDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import dao.MyConnection;

public class BarkodGenerator {
    
    public static String generateBarkod() {
        // Tarih ve saat bilgisini al
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String timeStamp = now.format(formatter);
        
        // 4 haneli random sayı üret
        Random random = new Random();
        String randomNum = String.format("%04d", random.nextInt(10000));
        
        // Barkodu oluştur: YYMMDDHHMMSSRRRR (YY:Yıl, MM:Ay, DD:Gün, HH:Saat, MM:Dakika, SS:Saniye, RRRR:Random)
        String barkod = timeStamp + randomNum;
        
        // Barkodun benzersiz olduğunu kontrol et
        while(barkodVarMi(barkod)) {
            randomNum = String.format("%04d", random.nextInt(10000));
            barkod = timeStamp + randomNum;
        }
        
        return barkod;
    }
    
    private static boolean barkodVarMi(String barkod) {
        try {
            Connection conn = MyConnection.connection();
            String sql = "SELECT COUNT(*) FROM Products WHERE barcode = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, barkod);
            ResultSet rs = ps.executeQuery();
            
            boolean varMi = false;
            if(rs.next()) {
                varMi = rs.getInt(1) > 0;
            }
            
            rs.close();
            ps.close();
            conn.close();
            
            return varMi;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}