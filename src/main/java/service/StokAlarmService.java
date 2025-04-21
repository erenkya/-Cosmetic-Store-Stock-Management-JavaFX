package service;

import dao.MyConnection;
import dao.UrunDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Urun;

public class StokAlarmService {
    private static final int KONTROL_SURESI = 30 * 60 * 1000; // 30 dakika
    private static boolean alarmAktif = true;
    private static Thread alarmThread;
    
    public static void alarmBaslat() {
        if (alarmThread != null && alarmThread.isAlive()) {
            return;
        }
        
        alarmThread = new Thread(() -> {
            while (alarmAktif) {
                List<Urun> azalanUrunler = azalanUrunleriGetir();
                if (!azalanUrunler.isEmpty()) {
                    alarmGoster(azalanUrunler);
                }
                
                try {
                    Thread.sleep(KONTROL_SURESI);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        
        alarmThread.setDaemon(true);
        alarmThread.start();
    }
    
    public static void alarmDurdur() {
        alarmAktif = false;
        if (alarmThread != null) {
            alarmThread.interrupt();
        }
    }
    
    private static List<Urun> azalanUrunleriGetir() {
        List<Urun> azalanUrunler = new ArrayList<>();
        
        try (Connection conn = MyConnection.connection()) {
            String sql = "SELECT p.*, c.category_name, sc.sub_category_name, " +
                        "b.brand_name, col.color_name, s.size_name " +
                        "FROM Products p " +
                        "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                        "LEFT JOIN SubCategories sc ON p.sub_category_id = sc.sub_category_id " +
                        "LEFT JOIN Brands b ON p.brand_id = b.brand_id " +
                        "LEFT JOIN Colors col ON p.color_id = col.color_id " +
                        "LEFT JOIN Sizes s ON p.size_id = s.size_id " +
                        "WHERE p.stock_quantity <= p.reorder_level";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Urun urun = new Urun();
                urun.setId(rs.getInt("product_id"));
                urun.setBarkod(rs.getString("barcode"));
                urun.setAd(rs.getString("name"));
                urun.setKategori(rs.getString("category_name"));
                urun.setAltKategori(rs.getString("sub_category_name"));
                urun.setMarka(rs.getString("brand_name"));
                urun.setRenk(rs.getString("color_name"));
                urun.setBeden(rs.getString("size_name"));
                urun.setSatisFiyati(rs.getDouble("price"));
                urun.setMaliyet(rs.getDouble("cost"));
                urun.setStokMiktari(rs.getInt("stock_quantity"));
                urun.setReorderLevel(rs.getInt("reorder_level"));
                
                azalanUrunler.add(urun);
            }
            
        } catch (Exception e) {
            System.out.println("Azalan ürünler kontrol edilirken hata: " + e.getMessage());
            e.printStackTrace();
        }
        
        return azalanUrunler;
    }
    
    private static void alarmGoster(List<Urun> azalanUrunler) {
        StringBuilder mesaj = new StringBuilder("Aşağıdaki ürünlerin stok seviyesi kritik:\n\n");
        
        for (Urun urun : azalanUrunler) {
            mesaj.append(String.format("%s (Barkod: %s)\n", urun.getAd(), urun.getBarkod()));
            mesaj.append(String.format("Mevcut Stok: %d, Minimum Stok: %d\n\n", 
                urun.getStokMiktari(), urun.getReorderLevel()));
        }
        
        // Swing thread-safe çağrı
        javax.swing.SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, mesaj.toString(), 
                "Stok Alarm", JOptionPane.WARNING_MESSAGE);
        });
    }
    
    // Manuel kontrol için
    public static void hemenKontrolEt() {
        List<Urun> azalanUrunler = azalanUrunleriGetir();
        if (!azalanUrunler.isEmpty()) {
            alarmGoster(azalanUrunler);
        } else {
            JOptionPane.showMessageDialog(null, 
                "Tüm ürünlerin stok seviyeleri yeterli.", 
                "Stok Durumu", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
