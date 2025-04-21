/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author susa
 */
public class UrunGuncelleDAO {
    
    public String getProductIdByBarcode(String barcode) throws Exception {
        try (Connection conn = MyConnection.connection()) {
            String sql = "SELECT product_id FROM Products WHERE barcode = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, barcode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("product_id");
                    } else {
                        throw new Exception("Ürün bulunamadı: " + barcode);
                    }
                }
            }
        }
    }
    
    public void kategoriDegistiginde(String secilenKategori, 
            JComboBox<String> altKategoriBox, 
            JComboBox<String> markaBox, 
            JComboBox<String> bedenBox, 
            JComboBox<String> renkBox) {
        try {
            Connection con = MyConnection.connection();
            
            // Alt kategorileri doldur
            altKategoriDoldur(con, secilenKategori, altKategoriBox);
            
            // Seçilen kategoriye göre markaları doldur
            markaDoldur(con, secilenKategori, null, markaBox);
            
            // Beden ve renk için sabit "Standart" değerini ayarla
            bedenBox.removeAllItems();
            bedenBox.addItem("Standart");
            
            renkBox.removeAllItems();
            renkBox.addItem("Standart");
            
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Veriler yüklenirken hata oluştu: " + e.getMessage());
        }
    }
    
    public void altKategoriDegistiginde(String secilenKategori, String secilenAltKategori,
            JComboBox<String> markaBox, 
            JComboBox<String> bedenBox) {
        try {
            Connection con = MyConnection.connection();
            
            // Seçilen alt kategoriye göre markaları doldur
            markaDoldur(con, secilenKategori, secilenAltKategori, markaBox);
            
            // Beden için sabit "Standart" değerini ayarla
            bedenBox.removeAllItems();
            bedenBox.addItem("Standart");
            
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Veriler yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    private void altKategoriDoldur(Connection con, String kategoriAdi, JComboBox<String> box) throws Exception {
        String sql = "SELECT DISTINCT sc.sub_category_name " +
                    "FROM SubCategories sc " +
                    "INNER JOIN Categories c ON sc.category_id = c.category_id " +
                    "WHERE c.category_name = ? " +
                    "ORDER BY sc.sub_category_name";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kategoriAdi);
            ResultSet rs = ps.executeQuery();
            
            box.removeAllItems();
            while (rs.next()) {
                box.addItem(rs.getString("sub_category_name"));
            }
            rs.close();
        }
    }
    
    private void markaDoldur(Connection con, String kategoriAdi, String altKategoriAdi, JComboBox<String> box) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT b.brand_name FROM Brands b ")
           .append("INNER JOIN Brand_Categories bc ON b.brand_id = bc.brand_id ")
           .append("INNER JOIN Categories c ON bc.category_id = c.category_id ")
           .append("WHERE c.category_name = ? ")
           .append("ORDER BY b.brand_name");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setString(1, kategoriAdi);
            
            //System.out.println("Executing SQL: " + sql.toString()); // Debug için
            ResultSet rs = ps.executeQuery();
            box.removeAllItems();
            while (rs.next()) {
                box.addItem(rs.getString("brand_name"));
            }
            rs.close();
        }
    }
    
    private void bedenDoldur(Connection con, String kategoriAdi, String altKategoriAdi, JComboBox<String> box) throws Exception {
        box.removeAllItems();
        box.addItem("Standart");
    }
    
    public void kategoriComboBoxDoldur(JComboBox<String> box) {
        try {
            Connection con = MyConnection.connection();
            String sql = "SELECT category_name FROM Categories ORDER BY category_name";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            box.removeAllItems();
            while (rs.next()) {
                box.addItem(rs.getString("category_name"));
            }
            
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void renkComboBoxDoldur(JComboBox<String> box) {
        box.removeAllItems();
        box.addItem("Standart");
    }
    
    public void tabloyuGuncelle(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            Connection con = MyConnection.connection();
            String sql = "SELECT p.product_id, p.barcode, p.name AS product_name, "
               + "c.category_name, sc.sub_category_name, b.brand_name, "
               + "co.color_name, s.size_name, p.cost, p.price, "
               + "CAST(p.stock_quantity AS SIGNED) as stock_quantity, "
               + "CAST(p.reorder_level AS SIGNED) as reorder_level, "
               + "p.status "
               + "FROM Products p "
               + "INNER JOIN Categories c ON p.category_id = c.category_id "
               + "LEFT JOIN SubCategories sc ON p.sub_category_id = sc.sub_category_id "
               + "INNER JOIN Brands b ON p.brand_id = b.brand_id "
               + "LEFT JOIN Colors co ON p.color_id = co.color_id "
               + "LEFT JOIN Sizes s ON p.size_id = s.size_id";
           
            PreparedStatement ps = con.prepareCall(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("barcode"),
                    rs.getString("product_name"),
                    rs.getString("category_name"),
                    rs.getString("sub_category_name"),
                    rs.getString("color_name"),
                    rs.getString("brand_name"),
                    rs.getString("size_name"),
                    rs.getBigDecimal("cost"),
                    rs.getBigDecimal("price"),
                    rs.getInt("stock_quantity"),  
                    rs.getInt("reorder_level"),   
                    rs.getString("status")
                });
            }
            
            con.close();
            ps.close();
            rs.close();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void tablodanSecimYapildiginda(JComboBox<String> bedenBox, JComboBox<String> renkBox) {
        bedenBox.removeAllItems();
        renkBox.removeAllItems();
        
        // Sadece "Standart" seçeneğini ekle
        bedenBox.addItem("Standart");
        renkBox.addItem("Standart");
    }
    
    public boolean urunGuncelle(String urunId, String urunAdi, String kategori, String altKategori, 
            String renk, String beden, String marka, double satisFiyatiDolar, 
            double maliyetDolar, int eklenecekStok, int reorderLevel, String durum, String yeniBarkod) {
        try {
            Connection conn = MyConnection.connection();
            
            // Önce mevcut stok miktarını al
            String stokSql = "SELECT stock_quantity FROM Products WHERE product_id = ?";
            PreparedStatement psStok = conn.prepareStatement(stokSql);
            psStok.setString(1, urunId);
            ResultSet rsStok = psStok.executeQuery();
            
            int mevcutStok = 0;
            if (rsStok.next()) {
                mevcutStok = rsStok.getInt("stock_quantity");
            }
            
            // Toplam stok = mevcut stok + eklenecek stok
            int yeniToplamStok = mevcutStok + eklenecekStok;
            
            // Barkodun benzersiz olup olmadığını kontrol et
            String barkodKontrolSql = "SELECT COUNT(*) as count FROM Products WHERE barcode = ? AND product_id != ?";
            PreparedStatement psBarkodKontrol = conn.prepareStatement(barkodKontrolSql);
            psBarkodKontrol.setString(1, yeniBarkod);
            psBarkodKontrol.setString(2, urunId);
            ResultSet rsBarkodKontrol = psBarkodKontrol.executeQuery();
            
            if (rsBarkodKontrol.next() && rsBarkodKontrol.getInt("count") > 0) {
                throw new Exception("Bu barkod başka bir ürün tarafından kullanılıyor: " + yeniBarkod);
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

            // Ürünü güncelle
            String sql = "UPDATE Products SET " +
                    "name = ?, " +
                    "category_id = ?, " +
                    "sub_category_id = ?, " +
                    "color_id = ?, " +
                    "size_id = ?, " +
                    "brand_id = ?, " +
                    "product_type = ?, " +
                    "price = ?, " +
                    "cost = ?, " +
                    "stock_quantity = ?, " +
                    "reorder_level = ?, " +
                    "barcode = ?, " +
                    "status = ? " +
                    "WHERE product_id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            int paramIndex = 1;
            
            ps.setString(paramIndex++, urunAdi);
            ps.setInt(paramIndex++, kategoriId);
            ps.setInt(paramIndex++, altKategoriId);
            ps.setInt(paramIndex++, renkId);
            ps.setInt(paramIndex++, bedenId);
            ps.setInt(paramIndex++, markaId);
            ps.setString(paramIndex++, durum);
            ps.setDouble(paramIndex++, satisFiyatiDolar);
            ps.setDouble(paramIndex++, maliyetDolar);
            ps.setInt(paramIndex++, yeniToplamStok); // Toplam stok miktarını kullan
            ps.setInt(paramIndex++, reorderLevel);
            ps.setString(paramIndex++, yeniBarkod);
            ps.setString(paramIndex++, durum); // Durum değerini ekle
            ps.setString(paramIndex++, urunId);
            
            int sonuc = ps.executeUpdate();
            ps.close();
            conn.close();
            
            return sonuc > 0;
            
        } catch (Exception e) {
            System.out.println("Ürün güncellenirken detaylı hata:");
            System.out.println("Hata Mesajı: " + e.getMessage());
            System.out.println("Hata Sınıfı: " + e.getClass().getName());
            System.out.println("Stack Trace:");
            e.printStackTrace();
            return false;
        }
    }
    
    public void kategoriVerileriniYukle(JComboBox comboYeniKategori, JComboBox ComboYeniAltKategori, 
            JComboBox comboYeniRenk, JComboBox comboYeniBeden, JComboBox comboYeniMarka, JComboBox comboYeniDurum) {
        try {
            Connection conn = MyConnection.connection();
            
            // Kategorileri yükle
            String kategoriSql = "SELECT category_name FROM Categories ORDER BY category_name";
            PreparedStatement psKategori = conn.prepareStatement(kategoriSql);
            ResultSet rsKategori = psKategori.executeQuery();
            while (rsKategori.next()) {
                comboYeniKategori.addItem(rsKategori.getString("category_name"));
            }
            
            // Alt kategorileri yükle
            String altKategoriSql = "SELECT DISTINCT sub_category_name FROM SubCategories ORDER BY sub_category_name";
            PreparedStatement psAltKategori = conn.prepareStatement(altKategoriSql);
            ResultSet rsAltKategori = psAltKategori.executeQuery();
            while (rsAltKategori.next()) {
                ComboYeniAltKategori.addItem(rsAltKategori.getString("sub_category_name"));
            }
            
            // Renkleri yükle
            String renkSql = "SELECT color_name FROM Colors WHERE color_name = 'Standart' ORDER BY color_name";
            PreparedStatement psRenk = conn.prepareStatement(renkSql);
            ResultSet rsRenk = psRenk.executeQuery();
            while (rsRenk.next()) {
                comboYeniRenk.addItem(rsRenk.getString("color_name"));
            }
            
            // Bedenleri yükle
            
            String bedenSql = "SELECT size_name FROM sizes WHERE size_name = 'Standart' ORDER BY size_id";
            PreparedStatement psBeden = conn.prepareStatement(bedenSql);
            ResultSet rsBeden = psBeden.executeQuery();
            while (rsBeden.next()) {
                comboYeniBeden.addItem(rsBeden.getString("size_name"));
            }
            
            // Markaları yükle
            String markaSql = "SELECT brand_name FROM Brands ORDER BY brand_name";
            PreparedStatement psMarka = conn.prepareStatement(markaSql);
            ResultSet rsMarka = psMarka.executeQuery();
            while (rsMarka.next()) {
                comboYeniMarka.addItem(rsMarka.getString("brand_name"));
            }
            
            // Durumları yükle - sadece active ve inactive
            comboYeniDurum.addItem("active");
            comboYeniDurum.addItem("inactive");
            
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void durumComboBoxDoldur(JComboBox<String> box) {
        box.removeAllItems();
        box.addItem("active");
        box.addItem("inactive");
    }
    
}
