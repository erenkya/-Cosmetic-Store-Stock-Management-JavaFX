/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.Urun;

/**
 *
 * @author susa
 */
public class UrunTabloDAO {
    
    
    /*
    public void tableDoldur(JTable table){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            Connection con = MyConnection.connection();
            String sql = "SELECT p.product_id, p.barcode, p.name AS product_name, "
               + "c.category_name, sc.sub_category_name, b.brand_name, "
               + "co.color_name, s.size_name, p.price, p.stock_quantity, p.reorder_level "
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
                    rs.getInt("product_id"),
                    rs.getString("barcode"),
                    rs.getString("product_name"),
                    rs.getString("category_name"),
                    rs.getString("sub_category_name"),
                    rs.getString("brand_name"),
                    rs.getString("color_name"),
                    rs.getString("size_name"),
                    rs.getBigDecimal("price"),
                    rs.getInt("stock_quantity"),
                    rs.getInt("reorder_level")
                });
            }
            
            con.close();
            ps.close();
            rs.close();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
    
    public void tableDoldur(JTable table){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            Connection con = MyConnection.connection();
            String sql = "SELECT p.product_id, p.barcode, p.name AS product_name, "
               + "c.category_name, sc.sub_category_name, b.brand_name, "
               + "co.color_name, s.size_name, p.price, p.stock_quantity, p.reorder_level "
               + "FROM Products p "
               + "INNER JOIN Categories c ON p.category_id = c.category_id "
               + "LEFT JOIN SubCategories sc ON p.sub_category_id = sc.sub_category_id "
               + "INNER JOIN Brands b ON p.brand_id = b.brand_id "
               + "LEFT JOIN Colors co ON p.color_id = co.color_id "
               + "LEFT JOIN Sizes s ON p.size_id = s.size_id "
               + "ORDER BY p.product_id DESC";
           
            PreparedStatement ps = con.prepareCall(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getInt("product_id"),
                    rs.getString("barcode"),
                    rs.getString("product_name"),
                    rs.getString("category_name"),
                    rs.getString("sub_category_name"),
                    rs.getString("brand_name"),
                    rs.getString("color_name"),
                    rs.getString("size_name"),
                    rs.getBigDecimal("price"),
                    rs.getInt("stock_quantity"),
                    rs.getInt("reorder_level")
                });
            }
            
            rs.close();
            ps.close();
            con.close();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void guncelleDoldur(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            Connection con = MyConnection.connection();
            String sql = "SELECT p.product_id, p.barcode, p.name AS product_name, "
               + "c.category_name, sc.sub_category_name, b.brand_name, "
               + "co.color_name, s.size_name, p.price, p.stock_quantity, p.reorder_level "
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
                    rs.getString("product_name"),
                    rs.getString("barcode"),
                    rs.getBigDecimal("price"),
                    rs.getInt("stock_quantity")
                });
            }

            con.close();
            ps.close();
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    public void tabloGuncelleGetir(JTable table){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            Connection con = MyConnection.connection();
            String sql = "SELECT p.product_id, p.barcode, p.name AS product_name, "
               + "c.category_name, sc.sub_category_name, b.brand_name, "
               + "co.color_name, s.size_name, p.cost, p.price, p.stock_quantity, p.reorder_level , p.status, "
               + "p.reorder_level "
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
                
                //System.out.println(rs.getString("status"));
                //System.out.println("1");
                
                
            }
            
            
            con.close();
            ps.close();
            rs.close();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    public void urunDetayGetir(int productId, JTextField... textFields) {
        try {
            Connection con = MyConnection.connection();
            String sql = "SELECT p.product_id, p.barcode, p.name AS product_name, "
                    + "p.description, p.purchase_price, p.supplier_code, "  // Örnek ek bilgiler
                    + "s.supplier_name, w.warehouse_name "  // İlişkili tablolardan bilgiler
                    + "FROM Products p "
                    + "LEFT JOIN Suppliers s ON p.supplier_id = s.supplier_id "
                    + "LEFT JOIN Warehouses w ON p.warehouse_id = w.warehouse_id "
                    + "WHERE p.product_id = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Her bir TextField'a ilgili veriyi set et
                textFields[0].setText(rs.getString("description"));
                textFields[1].setText(rs.getString("purchase_price"));
                textFields[2].setText(rs.getString("supplier_name"));
                textFields[3].setText(rs.getString("warehouse_name"));
                // ... diğer alanlar
            }

            con.close();
            ps.close();
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void urunAramaYap(JTable table, String aramaMetni) {
        try {
            Connection con = MyConnection.connection();
            String sql = "SELECT p.product_id, p.barcode, p.name AS product_name, "
                + "c.category_name, sc.sub_category_name, b.brand_name, "
                + "co.color_name, s.size_name, p.cost, p.price, p.stock_quantity, p.reorder_level , p.status "
                + "FROM Products p "
                + "INNER JOIN Categories c ON p.category_id = c.category_id "
                + "LEFT JOIN SubCategories sc ON p.sub_category_id = sc.sub_category_id "
                + "INNER JOIN Brands b ON p.brand_id = b.brand_id "
                + "LEFT JOIN Colors co ON p.color_id = co.color_id "
                + "LEFT JOIN Sizes s ON p.size_id = s.size_id "
                + "WHERE p.name LIKE ? OR p.barcode LIKE ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + aramaMetni + "%");
            ps.setString(2, "%" + aramaMetni + "%");

            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // Tabloyu temizle

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

            rs.close();
            ps.close();
            con.close();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void comboBoxDoldur(JComboBox<String> comboBox, String selectedValue) {
        try {
            Connection con = MyConnection.connection();
            String sql = "SELECT DISTINCT status FROM Products ORDER BY status";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            comboBox.removeAllItems();
            comboBox.addItem("Seçiniz");
            
            while (rs.next()) {
                String status = rs.getString("status");
                comboBox.addItem(status);
            }
            
            if (selectedValue != null && !selectedValue.isEmpty()) {
                comboBox.setSelectedItem(selectedValue);
            }
            
            con.close();
            ps.close();
            rs.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
