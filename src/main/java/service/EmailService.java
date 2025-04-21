package service;

import dao.MyConnection;
import javax.mail.*;
import javax.mail.internet.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class EmailService {
    private static final String EMAIL_ADDRESS = "maviyasin68@gmail.com";
    private static final String EMAIL_PASSWORD = "cheh qydj npqc nhbg";

    public static boolean sendDailyReport(String toEmail) {
    try {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_ADDRESS, EMAIL_PASSWORD);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(EMAIL_ADDRESS));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));  // Burada 'toEmail' parametresi kullanılmalı
        msg.setSubject("Günlük Satış Raporu");

        String htmlContent = getGunSonuRaporu();
        msg.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(msg);
        System.out.println("Mail başarıyla gönderildi.");
        return true;  // Başarı durumunda true döndür

    } catch (Exception e) {
        e.printStackTrace();
        return false;  // Hata durumunda false döndür
    }
}

    
    private static String getGunSonuRaporu() {
        StringBuilder html = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String bugun = sdf.format(new Date());

        html.append("<html><body>");
        html.append("<h2>Günlük Satış Raporu - ").append(bugun).append("</h2>");
        html.append("<h3>Satış Detayları:</h3>");
        
        try {
            Connection conn = MyConnection.connection();
            // Satış detayları için SQL sorgusu - sadece var olan sütunları kullan
            
            String sql = "SELECT p.name as urun_adi, " +
            "s.quantity as satis_adedi, " +
            "s.total_price, " +  // direkt total_price
            "s.gain, " +         // direkt gain
            "s.type_of_sale as odeme_tipi " +
            "FROM Sales s " +
            "JOIN Products p ON s.product_id = p.product_id " +
            "WHERE DATE(s.sale_date) = CURDATE()";    

            // Değişkenleri tanımla
            double toplamTutar = 0;
            double toplamGain = 0;

            // Tablo başlıkları
            html.append("<table border='1' style='border-collapse: collapse;'>");
            html.append("<tr bgcolor='#CCCCCC'>");
            html.append("<td><b>Ürün Adı</b></td>");
            html.append("<td><b>Satış Adedi</b></td>");
            html.append("<td><b>Toplam Tutar</b></td>");
            html.append("<td><b>Toplam Kazanç</b></td>");
            html.append("<td><b>Ödeme Tipi</b></td>");
            html.append("</tr>");

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();


            // Satırları doldur
            while (rs.next()) {
                double tutar = rs.getDouble("total_price");
                double gain = rs.getDouble("gain");

                html.append("<tr>");
                html.append("<td>").append(rs.getString("urun_adi")).append("</td>");
                html.append("<td>").append(rs.getInt("satis_adedi")).append("</td>");
                html.append("<td>").append(String.format("%.2f TL", tutar)).append("</td>");
                html.append("<td>").append(String.format("%.2f TL", gain)).append("</td>");
                html.append("<td>").append(rs.getString("odeme_tipi")).append("</td>");
                html.append("</tr>");

                toplamTutar += tutar;
                toplamGain += gain;
            }
            
            


            html.append("<tr bgcolor='#CCCCCC'>");
            html.append("<td colspan='2'><b>Günlük Toplamlar:</b></td>");
            html.append("<td><b>Toplam Tutar: ").append(String.format("%.2f TL", toplamTutar)).append("</b></td>");
            html.append("<td><b>Toplam Kazanç: ").append(String.format("%.2f TL", toplamGain)).append("</b></td>");
            html.append("<td></td>");
            html.append("</tr>");


            // Stok uyarısı tablosu kısmını güncelleyelim
            
            
            
            
            
            html.append("<table border='1' style='border-collapse: collapse;'>");
            html.append("<tr bgcolor='#CCCCCC'>");
            html.append("<td><b>Ürün Adı</b></td>");
            html.append("<td><b>Kategori</b></td>");
            html.append("<td><b>Marka</b></td>");
            html.append("<td><b>Mevcut Stok</b></td>");
            html.append("<td><b>Minimum Stok</b></td>");
            html.append("</tr>");

            String stokSql = "SELECT p.name, " +
                            "c.category_name as category_name, " +
                            "b.brand_name as brand_name, " +
                            "p.stock_quantity, p.reorder_level " +
                            "FROM Products p " +
                            "JOIN Categories c ON p.category_id = c.category_id " +
                            "JOIN Brands b ON p.brand_id = b.brand_id " +
                            "WHERE p.stock_quantity <= p.reorder_level";

            PreparedStatement stokPs = conn.prepareStatement(stokSql);
            ResultSet stokRs = stokPs.executeQuery();
            html.append("<h3>Stok Uyarısı Olan Ürünler:</h3>");
            while (stokRs.next()) {
                int mevcutStok = stokRs.getInt("stock_quantity");
                int minimumStok = stokRs.getInt("reorder_level");

                // Stok minimum seviyenin altındaysa kırmızı arka plan
                String backgroundColor = (mevcutStok <= minimumStok) ? "#FFCCCC" : ""; // Açık kırmızı renk

                html.append("<tr style='background-color: ").append(backgroundColor).append(";'>");
                html.append("<td>").append(stokRs.getString("name")).append("</td>");
                html.append("<td>").append(stokRs.getString("category_name")).append("</td>");
                html.append("<td>").append(stokRs.getString("brand_name")).append("</td>");
                html.append("<td>").append(mevcutStok).append("</td>");
                html.append("<td>").append(minimumStok).append("</td>");
                html.append("</tr>");
            }

            html.append("</table>");
            html.append("</body></html>");

            rs.close();
            ps.close();
            stokRs.close();
            stokPs.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            html.append("Rapor oluşturulurken hata oluştu: ").append(e.getMessage());
        }

        return html.toString();
    }
}