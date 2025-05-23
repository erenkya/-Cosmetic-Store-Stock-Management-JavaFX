/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.BedenDAO;
import dao.KategoriDAO;
import dao.RenkDAO;
import dao.MarkaDAO;
import dao.UrunDAO;
import dao.UrunTabloDAO;
import degisken.Degiskenler;
import java.util.List;
import javax.swing.JOptionPane;
import model.Kullanici;
import ui.islemler.UrunGiris;

/**
 *
 * @author susa
 */
public class UrunEkleme extends javax.swing.JFrame {
    /**
     * Creates new form UrunEkleme
     */
    public UrunEkleme() {
        initComponents();
       
        UrunTabloDAO urunTabloDAO = new UrunTabloDAO();
        urunTabloDAO.tableDoldur(jTableUrun);
        
        
        

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableUrun = new javax.swing.JTable();
        pBody = new javax.swing.JPanel();
        btnKiyafetGiris1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel1.setText("ÜRÜN EKLEME ");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(110, 10, 100, 18);

        jButton1.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        jButton1.setText("<");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(0, 0, 60, 40);

        jTableUrun.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Barcod", "Ürün Adı", "Kategori", "Alt Kategori", "Marka", "Renk", "Beden", "Ücret", "Stok Miktarı", "Yeniden Sipariş"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableUrun);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(350, 0, 950, 660);

        pBody.setLayout(null);
        getContentPane().add(pBody);
        pBody.setBounds(0, 100, 350, 540);

        btnKiyafetGiris1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        btnKiyafetGiris1.setText("Ürün");
        btnKiyafetGiris1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKiyafetGiris1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnKiyafetGiris1);
        btnKiyafetGiris1.setBounds(20, 60, 310, 30);

        setSize(new java.awt.Dimension(1309, 664));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
        Anasayfa anasayfa = new Anasayfa();
        anasayfa.setVisible(true);
       
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnKiyafetGiris1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKiyafetGiris1ActionPerformed
        pBody.removeAll();
        UrunGiris urunGiris = new UrunGiris(jTableUrun);
        urunGiris.setBounds(0, 0, pBody.getWidth(), pBody.getHeight());
        pBody.add(urunGiris);
        pBody.revalidate();
        pBody.repaint();
        
    }//GEN-LAST:event_btnKiyafetGiris1ActionPerformed

   
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnKiyafetGiris1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableUrun;
    private javax.swing.JPanel pBody;
    // End of variables declaration//GEN-END:variables
}
