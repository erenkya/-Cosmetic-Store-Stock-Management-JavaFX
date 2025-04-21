/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.MarkaDAO;
import model.Kullanici;
import javax.swing.JOptionPane;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

/**
 *
 * @author susa
 */
public class MarkaEkle extends javax.swing.JFrame {

    private MarkaDAO markaDAO;
    private DefaultListModel<String> kategoriListModel;
    private JList<String> kategoriList;
    private JScrollPane scrollPane;
    
    /**
     * Creates new form Gelir
     */
    public MarkaEkle() {
        initComponents();
        markaDAO = new MarkaDAO();
        
        // Kategori listesi için model oluştur
        kategoriListModel = new DefaultListModel<>();
        kategoriList = new JList<>(kategoriListModel);
        kategoriList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        kategoriList.setFont(new java.awt.Font("Helvetica Neue", 0, 12));
        
        // Scroll pane oluştur ve listeyi içine ekle
        scrollPane = new JScrollPane(kategoriList);
        scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createEtchedBorder(), 
            "Kategoriler"
        ));
        
        // ScrollPane'i form'a ekle
        getContentPane().add(scrollPane);
        scrollPane.setBounds(20, 200, 400, 200);
        
        // Form bileşenlerini yükle
        markalariYukle();
        kategorileriYukle();
        
        // Butonlara action listener ekle
        btnYeniMarkaKaydet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnYeniMarkaKaydetActionPerformed(evt);
            }
        });
        
        btnMarkaGuncelle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarkaGuncelleActionPerformed(evt);
            }
        });
        
        comboMarkalar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMarkalarActionPerformed(evt);
            }
        });
        
        // Form ayarları
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Marka Yönetimi");
        setResizable(false);
        getRootPane().setDefaultButton(btnYeniMarkaKaydet);
    }
    
    private void kategorileriYukle() {
        kategoriListModel.clear();
        List<String> kategoriler = markaDAO.tumKategorileriGetir();
        for (String kategori : kategoriler) {
            kategoriListModel.addElement(kategori);
        }
    }
    
    private void markalariYukle() {
        // ComboBox'ı temizle
        comboMarkalar.removeAllItems();
        
        // Tüm markaları getir ve ComboBox'a ekle
        List<String> markalar = markaDAO.tumMarkalariGetir();
        for (String marka : markalar) {
            comboMarkalar.addItem(marka);
        }
    }
    
    private void btnYeniMarkaKaydetActionPerformed(java.awt.event.ActionEvent evt) {
        String yeniMarka = tfYeniMarka.getText().trim();
        List<String> secilenKategoriler = kategoriList.getSelectedValuesList();
        
        if (yeniMarka.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Marka adı boş olamaz!");
            return;
        }
        
        if (secilenKategoriler.isEmpty()) {
            JOptionPane.showMessageDialog(this, "En az bir kategori seçmelisiniz!");
            return;
        }
        
        // Önce markayı ekle
        if (markaDAO.markaEkle(yeniMarka)) {
            // Sonra kategori ilişkilerini kur
            if (markaDAO.markaKategoriIliskisiKur(yeniMarka, secilenKategoriler)) {
                JOptionPane.showMessageDialog(this, "Marka ve kategorileri başarıyla eklendi.");
                tfYeniMarka.setText(""); // Text field'ı temizle
                kategoriList.clearSelection(); // Kategori seçimlerini temizle
                markalariYukle(); // ComboBox'ı güncelle
            } else {
                JOptionPane.showMessageDialog(this, "Marka eklendi fakat kategoriler eklenirken hata oluştu!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Bu marka zaten mevcut!", "Hata", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void btnMarkaGuncelleActionPerformed(java.awt.event.ActionEvent evt) {
        String secilenMarka = (String) comboMarkalar.getSelectedItem();
        if (secilenMarka == null || secilenMarka.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellenecek markayı seçin!");
            return;
        }
        
        String yeniMarkaAdi = tfYeniMarka.getText().trim();
        List<String> secilenKategoriler = kategoriList.getSelectedValuesList();
        
        if (yeniMarkaAdi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Yeni marka adı boş olamaz!");
            return;
        }
        
        if (secilenKategoriler.isEmpty()) {
            JOptionPane.showMessageDialog(this, "En az bir kategori seçmelisiniz!");
            return;
        }
        
        // Önce marka adını güncelle
        if (markaDAO.markaGuncelle(secilenMarka, yeniMarkaAdi)) {
            // Sonra kategorileri güncelle
            if (markaDAO.markaKategorileriniGuncelle(yeniMarkaAdi, secilenKategoriler)) {
                JOptionPane.showMessageDialog(this, "Marka ve kategorileri başarıyla güncellendi.");
                tfYeniMarka.setText(""); // Text field'ı temizle
                kategoriList.clearSelection(); // Kategori seçimlerini temizle
                markalariYukle(); // ComboBox'ı güncelle
            } else {
                JOptionPane.showMessageDialog(this, "Marka güncellendi fakat kategoriler güncellenirken hata oluştu!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Marka güncellenirken bir hata oluştu!");
        }
    }
    
    private void comboMarkalarActionPerformed(java.awt.event.ActionEvent evt) {
        String secilenMarka = (String) comboMarkalar.getSelectedItem();
        if (secilenMarka != null && !secilenMarka.isEmpty()) {
            // Markanın mevcut kategorilerini getir ve listede işaretle
            List<String> markaKategorileri = markaDAO.markaKategorileriniGetir(secilenMarka);
            kategoriList.clearSelection();
            
            for (String kategori : markaKategorileri) {
                int index = kategoriListModel.indexOf(kategori);
                if (index != -1) {
                    kategoriList.addSelectionInterval(index, index);
                }
            }
            
            tfYeniMarka.setText(secilenMarka);
        }
    }

    private void initComponents() {
        jButton1 = new javax.swing.JButton();
        btnYeniMarkaKaydet = new javax.swing.JButton();
        comboMarkalar = new javax.swing.JComboBox<>();
        tfYeniMarka = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnMarkaGuncelle = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        // Form ayarları
        setLayout(null);
        getContentPane().setBackground(new java.awt.Color(240, 240, 240));
        
        // Geri butonu
        jButton1.setFont(new java.awt.Font("Helvetica Neue", 1, 12));
        jButton1.setText("<");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.setFocusPainted(false);
        jButton1.setBounds(10, 10, 30, 30);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1);

        // Başlık
        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 14));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Marka Yönetimi");
        jLabel2.setBounds(50, 10, 370, 30);
        add(jLabel2);

        // Marka combo box
        comboMarkalar.setBounds(20, 50, 400, 30);
        comboMarkalar.setFont(new java.awt.Font("Helvetica Neue", 0, 12));
        add(comboMarkalar);

        // Marka adı text field
        tfYeniMarka.setBounds(20, 90, 400, 30);
        tfYeniMarka.setFont(new java.awt.Font("Helvetica Neue", 0, 12));
        add(tfYeniMarka);

        // Butonlar
        btnMarkaGuncelle.setText("Güncelle");
        btnMarkaGuncelle.setFont(new java.awt.Font("Helvetica Neue", 0, 12));
        btnMarkaGuncelle.setFocusPainted(false);
        btnMarkaGuncelle.setBounds(160, 130, 120, 30);
        add(btnMarkaGuncelle);

        btnYeniMarkaKaydet.setText("Kaydet");
        btnYeniMarkaKaydet.setFont(new java.awt.Font("Helvetica Neue", 0, 12));
        btnYeniMarkaKaydet.setFocusPainted(false);
        btnYeniMarkaKaydet.setBounds(300, 130, 120, 30);
        add(btnYeniMarkaKaydet);

        // Kategori başlığı
        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 12));
        jLabel3.setText("Kategoriler (Ctrl ile çoklu seçim yapabilirsiniz)");
        jLabel3.setBounds(20, 170, 400, 20);
        add(jLabel3);

        // Form boyutu
        setSize(450, 450);
        setLocationRelativeTo(null);
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
        Anasayfa anasayfa = new Anasayfa();
        anasayfa.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMarkaGuncelle;
    private javax.swing.JButton btnYeniMarkaKaydet;
    private javax.swing.JComboBox<String> comboMarkalar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField tfYeniMarka;
    // End of variables declaration//GEN-END:variables
}
