/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.SatisDAO;
import dao.UrunDAO;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import model.Urun;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.DefaultListModel;
import javax.swing.JFormattedTextField;
import service.ExchangeRateService;

/**
 *
 * @author susa
 */
public class TopluUrunSatis extends javax.swing.JFrame {

    private Urun secilenUrun;
    private double indirimOrani = 0; // Varsayılan olarak indirim yok
    private double toplamIndirimOrani = 0; // Toplam indirim oranını tutacak değişken
    
    private 
    
    
    /**
     * Creates new form UrunSatis
     */
    
    ExchangeRateService exchangeRateService = new ExchangeRateService();
  
    
    
    
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private ArrayList<Object[]> topluUrunListesi = new ArrayList<>();


    
    public TopluUrunSatis() {
        initComponents();
        setInitialState();
        
        jList1.setModel(listModel);

        
        
      
        
        
        
        // Barkod alanı için KeyListener ekle
        tfBarkod.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String barkod = tfBarkod.getText().trim();
                    if (!barkod.isEmpty()) {
                        urunGetir(barkod);
                        txtBarkodKeyReleased(e);
                    }
                }
            }
        });
        
        // Pazarlıklı satış fiyatı için input kontrolü
        tfPazarlikSatisFiyati.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Sadece rakam, nokta ve backspace'e izin ver
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // Karakteri engelle
                }
                // İkinci bir nokta girişini engelle
                if (c == '.' && tfPazarlikSatisFiyati.getText().contains(".")) {
                    e.consume();
                }
            }
        });
    }
    
    
    
    private void setInitialState() {
        
        // Başlangıçta sadece barkod alanı aktif olsun 
        tfPazarlikSatisFiyati.setEnabled(false);
        jSpinner.setEnabled(false);
        btnSatis.setEnabled(false);
        jComboBox1.setEnabled(false);
    
        
        // Label'ları temizle
        lblAd.setText("");
        lblKategori.setText("");
        lblMarka.setText("");
        lbBeden.setText("");
        lbSatisFiyat.setText("");
        lbStok.setText("");
        lbMaliyetFiyati.setText("");
        tfBarkod.setText("");
        // Barkod alanına odaklan
        tfBarkod.requestFocus();
        
    }
    
    
    
    private void txtBarkodKeyReleased(KeyEvent evt) {
        
        
        
        String barkod = tfBarkod.getText().trim();
        
        // Barkod boş değilse spinner'ı kontrol et
        if (!barkod.isEmpty()) {
            try {
                int stokMiktari = Integer.parseInt(lbStok.getText());
                if (stokMiktari > 0) {
                    // Stok varsa spinner'ı aktif et
                    jSpinner.setEnabled(true);
                    SpinnerNumberModel model = new SpinnerNumberModel(1, 1, stokMiktari, 1);
                    jSpinner.setModel(model);
                } else {
                    // Stok yoksa spinner'ı devre dışı bırak
                    jSpinner.setEnabled(false);
                }
            } catch (NumberFormatException e) {
                jSpinner.setEnabled(false);
            }
        } else {
            jSpinner.setEnabled(false);
        }
        
        // Satış butonunu kontrol et
        checkSatisButton();
    
    }
    
    
    
    private void checkSatisButton() {
        try {
            // Eğer barkod girilmiş ve stok varsa satış butonunu ve ödeme tipini aktif et

            boolean satisAktif = !tfBarkod.getText().trim().isEmpty() &&
                    !lbStok.getText().isEmpty() &&
                    Integer.parseInt(lbStok.getText()) > 0;
            btnSatis.setEnabled(satisAktif);
            tfPazarlikSatisFiyati.setEnabled(satisAktif);
            jComboBox1.setEnabled(satisAktif);
        } catch (NumberFormatException e) {
            btnSatis.setEnabled(false);
            tfPazarlikSatisFiyati.setEnabled(false);
            jComboBox1.setEnabled(false);
        }
    }
    
    private void urunGetir(String barkod) {
        UrunDAO urunDAO = new UrunDAO();
        secilenUrun = urunDAO.urunGetirByBarkod(barkod);
        
        ExchangeRateService exchangeRateService = new ExchangeRateService();
        
        
       
        
        
        if (secilenUrun != null) {
            // Ürün bilgilerini göster
            lblAd.setText(secilenUrun.getAd());
            lblKategori.setText(secilenUrun.getKategori() + " / " + secilenUrun.getAltKategori());
            lblMarka.setText(secilenUrun.getMarka());
            lbBeden.setText(secilenUrun.getBeden());
            
            DecimalFormat df = new DecimalFormat("#.0");
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            
            
            String maliyetKurDegeri = df.format(secilenUrun.getMaliyet() );
            lbSatisFiyat.setText(df.format(secilenUrun.getSatisFiyati() ));
            lbStok.setText(String.valueOf(secilenUrun.getStokMiktari()));

            
            
            // Stok kontrolü yap
            if (secilenUrun.getStokMiktari() > 0) {
                // Spinner'ı ayarla
                SpinnerNumberModel model = new SpinnerNumberModel(1, 1, secilenUrun.getStokMiktari(), 1);
                jSpinner.setModel(model);
                jSpinner.setEnabled(true);

                // Fiyat alanını ve satış butonunu aktif et
                tfPazarlikSatisFiyati.setEnabled(true);
                tfPazarlikSatisFiyati.setText(df.format(secilenUrun.getSatisFiyati()));
                lbMaliyetFiyati.setText(df.format(secilenUrun.getMaliyet())  );
                
                btnSatis.setEnabled(true);
                jComboBox1.setEnabled(true);
               
            } else {
                JOptionPane.showMessageDialog(this, "Bu ürünün stoğu tükenmiştir!", "Stok Yok", JOptionPane.WARNING_MESSAGE);
                setInitialState();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Ürün bulunamadı veya satışa kapalı!", "Hata", JOptionPane.ERROR_MESSAGE);
            setInitialState();
        }
    }
    
    private void btnSatisActionPerformed(java.awt.event.ActionEvent evt) {
        if (secilenUrun != null) {
            try {
               
                ExchangeRateService exchangeRateService = new ExchangeRateService();
                
        
                // Satış miktarını al
                int miktar = (int) jSpinner.getValue();
                
                // Pazarlıklı fiyatı al (bu fiyat zaten indirimli)
                double satisFiyati = Double.parseDouble(tfPazarlikSatisFiyati.getText().trim());
                
                // Maliyet kontrolü
                if (satisFiyati  < secilenUrun.getMaliyet() ) {
                    JOptionPane.showMessageDialog(this,
                            String.format(Locale.US, "Satış fiyatı (%.2f TL) maliyet fiyatının (%.2f TL) altında olamaz!",
                                    satisFiyati , secilenUrun.getMaliyet() ),
                            "Uyarı",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Toplam fiyatı hesapla
                double toplamFiyat = satisFiyati * miktar;
                
                // Kazancı hesapla (satış fiyatı - maliyet) * miktar
                double kazanc = (satisFiyati  - secilenUrun.getMaliyet() ) * miktar;
                
                // Ödeme tipini al
                String odemeTipi = jComboBox1.getSelectedItem().toString();
                
                // Satış işlemini gerçekleştir
                SatisDAO satisDAO = new SatisDAO();
                boolean satisBasarili = satisDAO.satisYap(secilenUrun.getId(), miktar, toplamFiyat, kazanc, odemeTipi);
                
                if (satisBasarili) {
                    // Stok güncelleme
                    UrunDAO urunDAO = new UrunDAO();
                    boolean stokGuncellendi = urunDAO.stokGuncelle(secilenUrun.getId(), miktar);
                    
                    if (stokGuncellendi) {
                        JOptionPane.showMessageDialog(this,
                                String.format(Locale.US, "Satış başarılı!\nToplam Tutar: %.2f TL\nKazanç: %.2f TL\nÖdeme Tipi: %s",
                                        toplamFiyat, kazanc, odemeTipi),
                                "Başarılı",
                                JOptionPane.INFORMATION_MESSAGE);
                        
                        // Formu temizle
                        alanlariTemizle();
                        tfBarkod.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Stok güncellenirken hata oluştu!", 
                            "Hata", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Satış kaydedilirken hata oluştu!", 
                        "Hata", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Geçersiz fiyat formatı!", 
                    "Hata", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void alanlariTemizle() {
        lblAd.setText("");
        lblKategori.setText("");
        lblMarka.setText("");
        lbBeden.setText("");
        lbSatisFiyat.setText("");
        lbStok.setText("");
        tfPazarlikSatisFiyati.setText("");
        tfBarkod.setText("");
        tfPazarlikSatisFiyati.setEnabled(false);
        btnSatis.setEnabled(false);
        jComboBox1.setEnabled(false);
        jComboBox1.setSelectedIndex(0);
        secilenUrun = null;
        jSpinner.setValue(1);
        jSpinner.setEnabled(false);
        
        lbMaliyetFiyati.setText("");
        indirimOrani = 0;
        toplamIndirimOrani = 0;
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
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tfBarkod = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblAd = new javax.swing.JLabel();
        lblKategori = new javax.swing.JLabel();
        lblMarka = new javax.swing.JLabel();
        lbBeden = new javax.swing.JLabel();
        lbSatisFiyat = new javax.swing.JLabel();
        lbStok = new javax.swing.JLabel();
        btnSatis = new javax.swing.JButton();
        jSpinner = new javax.swing.JSpinner();
        tfPazarlikSatisFiyati = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        lbMaliyetFiyati = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        indirimLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new java.awt.Dimension(527, 577));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Toplu Ürün Satış");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Barkod");

        jLabel3.setText("Ad");

        jLabel4.setText("Kategori");

        jLabel5.setText("Marka");

        jLabel6.setText("Beden");

        jLabel7.setText("Satış Fiyatı");

        jLabel8.setText("Stok");

        btnSatis.setText("Satış");
        btnSatis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSatisActionPerformed(evt);
            }
        });

        tfPazarlikSatisFiyati.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPazarlikSatisFiyatiActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Kredi Kartı", "Nakit", "Havale>A", "Havale>E" }));

        jLabel9.setText("TL");

        jLabel10.setText("Alt Satış Sınırı");

        jButton2.setText("Ekle");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)
                        .addComponent(tfBarkod, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)
                        .addComponent(lblAd, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)
                        .addComponent(lblKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)
                        .addComponent(lblMarka, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)
                        .addComponent(lbSatisFiyat, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)
                        .addComponent(lbStok, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(234, 234, 234)
                        .addComponent(jSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(167, 167, 167)
                        .addComponent(tfPazarlikSatisFiyati, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(234, 234, 234)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(211, 211, 211)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSatis, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(111, 111, 111)
                                .addComponent(lbMaliyetFiyati, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(111, 111, 111)
                                .addComponent(lbBeden, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(36, 36, 36))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfBarkod, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAd, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMarka, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbBeden, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbMaliyetFiyati, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbSatisFiyat, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbStok, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(tfPazarlikSatisFiyati, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSatis)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        jButton8.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        jButton8.setText("<");
        jButton8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jList1);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setText("Toplu Satış");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("Reset");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("←");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        indirimLbl.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        indirimLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        indirimLbl.setText("!");
        indirimLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                indirimLblMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(indirimLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(152, 152, 152))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(197, 197, 197))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(21, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton3)
                        .addComponent(jButton4))
                    .addComponent(indirimLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        this.setVisible(false);
        Anasayfa anasayfa = new Anasayfa();
        anasayfa.setVisible(true);

    }//GEN-LAST:event_jButton8ActionPerformed

    private void tfPazarlikSatisFiyatiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPazarlikSatisFiyatiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPazarlikSatisFiyatiActionPerformed

    
    ///11
    
    private double totalAmount = 0;
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
      try {
        String productName = lblAd.getText();
        String productPrice = lbSatisFiyat.getText();
        int quantity = (int) jSpinner.getValue();
        String fiyatText = tfPazarlikSatisFiyati.getText().trim();
        
        double sellingPrice = Double.parseDouble(tfPazarlikSatisFiyati.getText().trim());
        if (sellingPrice < secilenUrun.getMaliyet() ) {
                    JOptionPane.showMessageDialog(this,
                            String.format(Locale.US, "Satış fiyatı (%.2f TL) maliyet fiyatının (%.2f TL) altında olamaz!",
                                    sellingPrice , secilenUrun.getMaliyet() ),
                            "Uyarı",
                            JOptionPane.WARNING_MESSAGE);
                    return;
        }
        
        if (fiyatText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fiyat alanı boş bırakılamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        double pricePerUnit = Double.parseDouble(fiyatText);
        double totalPrice = quantity * pricePerUnit;
        double revenue = (pricePerUnit - secilenUrun.getMaliyet())* quantity;
        
        String paymentType = jComboBox1.getSelectedItem().toString();
//-----------------------------------------------------------------------------------------------------------------------------------------
        DecimalFormat df = new DecimalFormat("#.00");
        
        
        //Uida toplu satışları göster
        String listItem = productName + " | " + productPrice + "₺ | " + quantity + " Adet | " + df.format(totalPrice) + "₺";
        listModel.addElement(listItem); // artık doğrudan kendi modelimizi kullanıyoruz

        
        //Total fiyat göster
        totalAmount += totalPrice;
        jLabel11.setText(totalAmount + " ₺");
//------------------------------------------------------------------------------------------------------------------------------------------
        
        //Ürün bilgilerini listeye yükle
        topluUrunListesi.add(new Object[]{secilenUrun.getId() , quantity  , totalPrice, revenue, paymentType} );
        


        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Geçersiz fiyat formatı!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        topluUrunListesi.clear();
        listModel.clear();
        
        //System.out.print(remove);
        
        
        totalAmount = 0;
        jLabel11.setText(totalAmount + " ₺");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        Object[] removeTopluUrunListesi = topluUrunListesi.get(topluUrunListesi.size()-1);
        topluUrunListesi.remove(topluUrunListesi.size() - 1);
 
        String removeListModel = listModel.remove(listModel.getSize()-1);

        totalAmount -= (double)removeTopluUrunListesi[2];
        jLabel11.setText(totalAmount + " ₺");
        
        
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        SatisDAO satisDAO = new SatisDAO();
        UrunDAO urunDAO = new UrunDAO();

        for (int i = topluUrunListesi.size() - 1; i >= 0; i--) {
            Object[] product = topluUrunListesi.get(i);
            int id = (int) product[0];
            int miktar = (int) product[1];
            double toplamFiyat = (double) product[2];
            double kazanc = (double) product[3];
            String odemeTipi = (String) product[4];

            boolean satisBasarili = satisDAO.satisYap(id, miktar, toplamFiyat, kazanc, odemeTipi);
            boolean stokGuncellendi = urunDAO.stokGuncelle(id, miktar);

            if (satisBasarili && stokGuncellendi) {
                topluUrunListesi.remove(i);
                listModel.remove(i);
                totalAmount -= toplamFiyat;
            }
        }
        totalAmount = 0;
        alanlariTemizle();
        tfBarkod.requestFocus();
        jLabel11.setText(totalAmount+"₺");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void indirimLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_indirimLblMouseClicked

        if (secilenUrun != null) {
            IndirimDialog indirimDialog = new IndirimDialog(this, true);
            indirimDialog.setLocationRelativeTo(this);
            indirimDialog.setVisible(true);

            if (indirimDialog.isOnaylandi()) {
                try {
                    // Yeni indirim oranını al

                    double yeniIndirimOrani = indirimDialog.getIndirimOrani();

                    // Mevcut pazarlık fiyatını al
                    double mevcutFiyat = Double.parseDouble(tfPazarlikSatisFiyati.getText().trim());

                    // Yeni indirimli fiyatı hesapla
                    double indirimliFiyat = mevcutFiyat * (1 - yeniIndirimOrani/100);

                    // Toplam indirim oranını güncelle
                    toplamIndirimOrani = 100 - ((100 - toplamIndirimOrani) * (100 - yeniIndirimOrani) / 100);
                    indirimOrani = toplamIndirimOrani;

                    // Fiyatları güncelle
                    DecimalFormat df = new DecimalFormat("#.0");
                    df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

                    // Pazarlık fiyatına yeni indirimli değeri yaz
                    tfPazarlikSatisFiyati.setText(df.format(indirimliFiyat));

                    // TL Label'a toplam indirimli orijinal fiyatı yaz
                    double kurDegeri = ExchangeRateService.getDolarKuru();
                    double orijinalFiyat = secilenUrun.getSatisFiyati() * kurDegeri;
                    double tlIndirimliFiyat = orijinalFiyat * (1 - toplamIndirimOrani/100);

                    // Kullanıcıya bilgi ver
                    JOptionPane.showMessageDialog(this,
                        String.format(Locale.US,
                            "Yeni indirim (%%%.1f) uygulandı!\nToplam indirim: %%%.1f\n" +
                            "İndirimli TL fiyatı: %.2f TL\n" +
                            "İndirimli pazarlık fiyatı: %.2f TL",
                            yeniIndirimOrani, toplamIndirimOrani,
                            tlIndirimliFiyat, indirimliFiyat),
                        "İndirim Uygulandı",
                        JOptionPane.INFORMATION_MESSAGE);

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "Fiyat hesaplanırken hata oluştu!",
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_indirimLblMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TopluUrunSatis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TopluUrunSatis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TopluUrunSatis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TopluUrunSatis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TopluUrunSatis().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSatis;
    private javax.swing.JLabel indirimLbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner;
    private javax.swing.JLabel lbBeden;
    private javax.swing.JLabel lbMaliyetFiyati;
    private javax.swing.JLabel lbSatisFiyat;
    private javax.swing.JLabel lbStok;
    private javax.swing.JLabel lblAd;
    private javax.swing.JLabel lblKategori;
    private javax.swing.JLabel lblMarka;
    private javax.swing.JTextField tfBarkod;
    private javax.swing.JTextField tfPazarlikSatisFiyati;
    // End of variables declaration//GEN-END:variables
}
