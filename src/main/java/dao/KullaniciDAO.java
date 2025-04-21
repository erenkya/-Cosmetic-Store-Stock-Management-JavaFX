/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Kullanici;

/**
 *
 * @author susa
 */
public class KullaniciDAO {
    private final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    
    public String hashPassword(String password) {
        return argon2.hash(10, 65536, 1, password.toCharArray());
    }
    
    public boolean verifyPassword(String hash, String password) {
        return argon2.verify(hash, password.toCharArray());
    }
    
    public Kullanici LoginControl(String userName, String password) {
        Kullanici kul = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM Users WHERE LOWER(username) = LOWER(?)";
            conn = MyConnection.connection();
            
            if (conn == null) {
                System.out.println("Veritabanı bağlantısı başarısız!");
                return null;
            }
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            rs = ps.executeQuery();
            
            if(rs.next()) {
                String hashedPassword = rs.getString("password");
                
                if (verifyPassword(hashedPassword, password)) {
                    kul = new Kullanici();
                    kul.setId(rs.getInt("user_id"));
                    kul.setUserName(rs.getString("username"));
                    kul.setFullName(rs.getString("full_name"));
                    kul.setEmail(rs.getString("email"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQL Hatası: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("Bağlantı kapatma hatası: " + ex.getMessage());
            }
        }
        
        return kul;
    }
    
    public boolean kullaniciEkle(Kullanici kullanici, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            String hashedPassword = hashPassword(password);
            String sql = "INSERT INTO Users (username, password, full_name, email) VALUES (?, ?, ?, ?)";
            
            conn = MyConnection.connection();
            if (conn == null) {
                return false;
            }
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, kullanici.getUserName());
            ps.setString(2, hashedPassword);
            ps.setString(3, kullanici.getFullName());
            ps.setString(4, kullanici.getEmail());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("SQL Hatası: " + ex.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("Bağlantı kapatma hatası: " + ex.getMessage());
            }
        }
    }
    
    public boolean sifreGuncelle(int userId, String yeniSifre) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            String hashedPassword = hashPassword(yeniSifre);
            String sql = "UPDATE Users SET password = ? WHERE user_id = ?";
            
            conn = MyConnection.connection();
            if (conn == null) {
                return false;
            }
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("SQL Hatası: " + ex.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("Bağlantı kapatma hatası: " + ex.getMessage());
            }
        }
    }
}
