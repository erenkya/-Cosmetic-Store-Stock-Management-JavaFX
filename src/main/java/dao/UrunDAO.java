package dao;

import degisken.Degiskenler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Urun;
import service.ExchangeRateService;

public class UrunDAO {
    public boolean urunEkle(String urunAdi, String barkod, String kategori, String altKategori, String renk, 
                           String beden, String marka, double satisFiyatiDolar, double maliyetDolar, int stok, int reorderLevel, String urunTipi) {
        int kulId = Degiskenler.loginKul.getId();
        
        try {
            Connection conn = MyConnection.connection();
            
            // Barkod kontrolü
            String sql = "SELECT COUNT(*) FROM Products WHERE barcode = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, barkod);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Bu barkod zaten kullanımda! Lütfen başka bir barkod giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Kategori ID'sini al
            String kategoriIdSql = "SELECT category_id FROM Categories WHERE category_name = ?";
            PreparedStatement psKategori = conn.prepareStatement(kategoriIdSql);
            psKategori.setString(1, kategori);
            ResultSet rsKategori = psKategori.executeQuery();
            
            if (!rsKategori.next()) {
                throw new Exception("Kategori bulunamadı: " + kategori);
            }
            int kategoriId = rsKategori.getInt("category_id");
            rsKategori.close();
            psKategori.close();
            
            // Alt Kategori ID'sini al
            String altKategoriIdSql = "SELECT sub_category_id FROM SubCategories WHERE sub_category_name = ? AND category_id = ?";
            PreparedStatement psAltKategori = conn.prepareStatement(altKategoriIdSql);
            psAltKategori.setString(1, altKategori);
            psAltKategori.setInt(2, kategoriId);
            ResultSet rsAltKategori = psAltKategori.executeQuery();
            
            if (!rsAltKategori.next()) {
                throw new Exception("Alt kategori bulunamadı: " + altKategori);
            }
            int altKategoriId = rsAltKategori.getInt("sub_category_id");
            rsAltKategori.close();
            psAltKategori.close();
            
            // Renk ID'sini al
            String renkIdSql = "SELECT color_id FROM Colors WHERE color_name = ?";
            PreparedStatement psRenk = conn.prepareStatement(renkIdSql);
            psRenk.setString(1, renk);
            ResultSet rsRenk = psRenk.executeQuery();
            
            if (!rsRenk.next()) {
                throw new Exception("Renk bulunamadı: " + renk);
            }
            int renkId = rsRenk.getInt("color_id");
            rsRenk.close();
            psRenk.close();
            
            // Beden ID'sini al
            String bedenIdSql = "SELECT size_id FROM Sizes WHERE size_name = ? AND category_id = ?";
            PreparedStatement psBeden = conn.prepareStatement(bedenIdSql);
            psBeden.setString(1, beden);
            psBeden.setInt(2, kategoriId);
            ResultSet rsBeden = psBeden.executeQuery();
            
            if (!rsBeden.next()) {
                throw new Exception("Beden bulunamadı: " + beden);
            }
            int bedenId = rsBeden.getInt("size_id");
            rsBeden.close();
            psBeden.close();
            
            // Marka ID'sini al
            String markaIdSql = "SELECT brand_id FROM Brands WHERE brand_name = ?";
            PreparedStatement psMarka = conn.prepareStatement(markaIdSql);
            psMarka.setString(1, marka);
            ResultSet rsMarka = psMarka.executeQuery();
            
            if (!rsMarka.next()) {
                throw new Exception("Marka bulunamadı: " + marka);
            }
            int markaId = rsMarka.getInt("brand_id");
            rsMarka.close();
            psMarka.close();
            
            // Ürünü ekle
            String sqlEkle = "INSERT INTO Products (name, barcode, category_id, sub_category_id, color_id, " +
                         "size_id, brand_id, product_type, price, cost, stock_quantity, reorder_level, created_by) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement psEkle = conn.prepareStatement(sqlEkle);
            int paramIndex = 1;
            
            psEkle.setString(paramIndex++, urunAdi);
            psEkle.setString(paramIndex++, barkod);
            psEkle.setInt(paramIndex++, kategoriId);
            psEkle.setInt(paramIndex++, altKategoriId);
            psEkle.setInt(paramIndex++, renkId);
            psEkle.setInt(paramIndex++, bedenId);
            psEkle.setInt(paramIndex++, markaId);
            psEkle.setString(paramIndex++, urunTipi);
            psEkle.setDouble(paramIndex++, satisFiyatiDolar);
            psEkle.setDouble(paramIndex++, maliyetDolar);
            psEkle.setInt(paramIndex++, stok);
            psEkle.setInt(paramIndex++, reorderLevel);
            psEkle.setInt(paramIndex++, kulId);
            
            int sonuc = psEkle.executeUpdate();
            
            psEkle.close();
            conn.close();
            
            return sonuc > 0;
            
        } catch (Exception e) {
            System.out.println("Ürün eklenirken hata: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Ürün eklenirken hata: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Urun urunGetirByBarkod(String barkod) {
        try {
            String sql = "SELECT p.*, c.category_name, sc.sub_category_name, cl.color_name, " +
                        "s.size_name, b.brand_name " +
                        "FROM Products p " +
                        "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                        "LEFT JOIN SubCategories sc ON p.sub_category_id = sc.sub_category_id " +
                        "LEFT JOIN Colors cl ON p.color_id = cl.color_id " +
                        "LEFT JOIN Sizes s ON p.size_id = s.size_id " +
                        "LEFT JOIN Brands b ON p.brand_id = b.brand_id " +
                        "WHERE p.barcode = ? AND p.status = 'Active'";

            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, barkod);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Urun urun = new Urun();
                urun.setId(rs.getInt("product_id"));
                urun.setAd(rs.getString("name"));
                urun.setBarkod(rs.getString("barcode"));
                urun.setKategori(rs.getString("category_name"));
                urun.setAltKategori(rs.getString("sub_category_name"));
                urun.setRenk(rs.getString("color_name"));
                urun.setBeden(rs.getString("size_name"));
                urun.setMarka(rs.getString("brand_name"));
                urun.setSatisFiyati(rs.getDouble("price"));
                urun.setMaliyet(rs.getDouble("cost"));
                urun.setStokMiktari(rs.getInt("stock_quantity"));
                urun.setReorderLevel(rs.getInt("reorder_level"));
                System.out.println(rs.getString("status"));
                return urun;
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ürün Getirme Hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean stokGuncelle(int productId, int quantity) {
        String sql = "UPDATE Products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
        try (Connection conn = MyConnection.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, productId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean urunEkleVeyaGuncelle(String urunAdi, String barkod, String kategori, String altKategori,
            String renk, String beden, String marka, double satisFiyatiDolar,
            int stok, int reorderLevel, String urunTipi) {
        
        try {
            Connection conn = MyConnection.connection();
            String sql = "SELECT product_id, stock_quantity FROM Products WHERE barcode = ?";
            PreparedStatement kontrolStmt = conn.prepareStatement(sql);
            kontrolStmt.setString(1, barkod);
            ResultSet rs = kontrolStmt.executeQuery();
            
            if (rs.next()) {
                
                int mevcutStok = rs.getInt("stock_quantity");
                int yeniStok = mevcutStok + stok;

                String updateSql = "UPDATE Products SET " +
                    "name = ?, " +
                    "category_id = (SELECT category_id FROM Categories WHERE category_name = ?), " +
                    "sub_category_id = (SELECT sub_category_id FROM SubCategories WHERE sub_category_name = ?), " +
                    "brand_id = (SELECT brand_id FROM Brands WHERE brand_name = ?), " +
                    "color_id = (SELECT color_id FROM Colors WHERE color_name = ?), " +
                    "size_id = (SELECT size_id FROM Sizes WHERE size_name = ?), " +
                    "price = ?, " +
                    "stock_quantity = ?, " +
                    "reorder_level = ?, " +
                    "product_type = ? " +
                    "WHERE barcode = ?";

                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, urunAdi);
                updateStmt.setString(2, kategori);
                updateStmt.setString(3, altKategori);
                updateStmt.setString(4, marka);
                updateStmt.setString(5, renk);
                updateStmt.setString(6, beden);
                updateStmt.setDouble(7, satisFiyatiDolar);
                updateStmt.setInt(8, yeniStok);
                updateStmt.setInt(9, reorderLevel);
                updateStmt.setString(10, urunTipi);
                updateStmt.setString(11, barkod);

                return updateStmt.executeUpdate() > 0;
            
            } else {
                String insertSql = "INSERT INTO Products (barcode, name, category_id, sub_category_id, " +
                        "brand_id, color_id, size_id, price, stock_quantity, reorder_level, product_type) " +
                        "VALUES (?, ?, " +
                        "(SELECT category_id FROM Categories WHERE category_name = ?), " +
                        "(SELECT sub_category_id FROM SubCategories WHERE sub_category_name = ?), " +
                        "(SELECT brand_id FROM Brands WHERE brand_name = ?), " +
                        "(SELECT color_id FROM Colors WHERE color_name = ?), " +
                        "(SELECT size_id FROM Sizes WHERE size_name = ?), " +
                        "?, ?, ?, ?, ?, ?)";
                
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, barkod);
                insertStmt.setString(2, urunAdi);
                insertStmt.setString(3, kategori);
                insertStmt.setString(4, altKategori);
                insertStmt.setString(5, marka);
                insertStmt.setString(6, renk);
                insertStmt.setString(7, beden);
                insertStmt.setDouble(8, satisFiyatiDolar);
                insertStmt.setInt(9, stok);
                insertStmt.setInt(10, reorderLevel);
                insertStmt.setString(11, urunTipi);

                return insertStmt.executeUpdate() > 0;
            
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Urun> getReorderLevelUrunler() {
        List<Urun> urunler = new ArrayList<>();
        try {
            String sql = "SELECT p.*, c.category_name, b.brand_name "
                    + "FROM Products p "
                    + "INNER JOIN Categories c ON p.category_id = c.category_id "
                    + "INNER JOIN Brands b ON p.brand_id = b.brand_id "
                    + "WHERE p.stock_quantity <= p.reorder_level";
            
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Urun urun = new Urun();
                urun.setId(rs.getInt("product_id"));
                urun.setAd(rs.getString("name"));
                urun.setStokMiktari(rs.getInt("stock_quantity"));
                urun.setReorderLevel(rs.getInt("reorder_level"));
                urun.setKategori(rs.getString("category_name"));
                urun.setMarka(rs.getString("brand_name"));
                urunler.add(urun);
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urunler;
    }
    
    public DefaultTableModel getUrunlerTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        
        model.addColumn("ID");
        model.addColumn("Barkod");
        model.addColumn("Ürün Adı");
        model.addColumn("Kategori");
        model.addColumn("Alt Kategori");
        model.addColumn("Marka");
        model.addColumn("Renk");
        model.addColumn("Beden");
        model.addColumn("Satış Fiyatı ($)");
        model.addColumn("Satış Fiyatı (₺)");
        model.addColumn("Stok");
        model.addColumn("Reorder Level");
        
        try {
            String sql = "SELECT p.*, c.category_name, sc.sub_category_name, " +
                        "b.brand_name, col.color_name, s.size_name " +
                        "FROM Products p " +
                        "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                        "LEFT JOIN SubCategories sc ON p.sub_category_id = sc.sub_category_id " +
                        "LEFT JOIN Brands b ON p.brand_id = b.brand_id " +
                        "LEFT JOIN Colors col ON p.color_id = col.color_id " +
                        "LEFT JOIN Sizes s ON p.size_id = s.size_id";
            
            Connection conn = MyConnection.connection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                double fiyatDolar = rs.getDouble("price");
                double fiyatTL = ExchangeRateService.dolarToTL(fiyatDolar);
                
                model.addRow(new Object[]{
                    rs.getInt("product_id"),
                    rs.getString("barcode"),
                    rs.getString("name"),
                    rs.getString("category_name"),
                    rs.getString("sub_category_name"),
                    rs.getString("brand_name"),
                    rs.getString("color_name"),
                    rs.getString("size_name"),
                    String.format("%.2f", fiyatDolar),
                    String.format("%.2f", fiyatTL),
                    rs.getInt("stock_quantity"),
                    rs.getInt("reorder_level")
                });
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            
            JOptionPane.showMessageDialog(null, "Ürün Listelenirken hata oluştu: " + e.getMessage());
            
            //System.out.println("Ürünler listelenirken hata: " + e.getMessage());
            e.printStackTrace();
        }
        
        return model;
    }
}