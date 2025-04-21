/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author susa
 */
public class MyConnection {
    private static final String HOST = "****";
    private static final String USER_NAME = "*****";
    private static final String USER_PASSWORD = "*****";
   

    private static final String DB_NAME = "StockManagement";
    
    public static Connection connection(){
        Connection con = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://"+HOST+"/"+DB_NAME+"?"
                    + "user="+USER_NAME+"&password="+USER_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        return con;
        
    }
    
}
