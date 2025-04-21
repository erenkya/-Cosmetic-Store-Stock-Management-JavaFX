package ui;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.*;
import model.SatisRaporu;
import service.RaporService;

public class Raporlar extends JFrame {
    private final RaporService raporService;
    private final JTabbedPane tabbedPane;
    private final JPanel gunlukPanel;
    private final JPanel aylikPanel;
    private final JPanel yillikPanel;
    private final JTable gunlukTable;
    private final JTable aylikTable;
    private final JTable yillikTable;
    private final JTextArea gunlukOzet;
    private final JTextArea aylikOzet;
    private final JTextArea yillikOzet;
    
    public Raporlar() {
        raporService = new RaporService();
        
        setTitle("Satış Raporları");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Ana panel oluştur
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Üst panel için geri dönüş butonu
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton geriButton = new JButton("Ana Sayfaya Dön");
        geriButton.addActionListener(e -> {
            dispose(); // Bu pencereyi kapat
            Anasayfa anasayfa = new Anasayfa();
            anasayfa.setVisible(true);
        });
        topPanel.add(geriButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Rapor panelleri
        tabbedPane = new JTabbedPane();
        gunlukPanel = new JPanel(new BorderLayout(10, 10));
        aylikPanel = new JPanel(new BorderLayout(10, 10));
        yillikPanel = new JPanel(new BorderLayout(10, 10));
        
        // Tablolar
        gunlukTable = new JTable();
        aylikTable = new JTable();
        yillikTable = new JTable();
        
        // Özet alanları
        gunlukOzet = new JTextArea(5, 40);
        gunlukOzet.setEditable(false);
        gunlukOzet.setBackground(this.getBackground());
        gunlukOzet.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        aylikOzet = new JTextArea(5, 40);
        aylikOzet.setEditable(false);
        aylikOzet.setBackground(this.getBackground());
        aylikOzet.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        yillikOzet = new JTextArea(5, 40);
        yillikOzet.setEditable(false);
        yillikOzet.setBackground(this.getBackground());
        yillikOzet.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        setupGunlukPanel();
        setupAylikPanel();
        setupYillikPanel();
        
        tabbedPane.addTab("Günlük Rapor", gunlukPanel);
        tabbedPane.addTab("Aylık Rapor", aylikPanel);
        tabbedPane.addTab("Yıllık Rapor", yillikPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void setupGunlukPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(200, 30));
        dateChooser.setDate(new Date()); // Bugünün tarihini seç
        JButton raporButton = new JButton("Rapor Oluştur");
        
        controlPanel.add(new JLabel("Tarih: "));
        controlPanel.add(dateChooser);
        controlPanel.add(raporButton);
        
        // Tablo paneli
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(gunlukTable), BorderLayout.CENTER);
        
        // Özet paneli
        JPanel ozetPanel = new JPanel(new BorderLayout());
        ozetPanel.setBorder(BorderFactory.createTitledBorder("Özet Bilgiler"));
        ozetPanel.add(new JScrollPane(gunlukOzet), BorderLayout.CENTER);
        
        // Ana panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(ozetPanel, BorderLayout.SOUTH);
        
        gunlukPanel.add(controlPanel, BorderLayout.NORTH);
        gunlukPanel.add(contentPanel, BorderLayout.CENTER);
        
        raporButton.addActionListener(e -> {
            if (dateChooser.getDate() != null) {
                LocalDateTime tarih = dateChooser.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
                SatisRaporu rapor = raporService.gunlukRaporOlustur(tarih);
                gunlukTable.setModel(raporService.getRaporTableModel(
                    tarih, tarih.plusDays(1)));
                gunlukOzet.setText(getOzetText(rapor));
                gunlukTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lütfen bir tarih seçin", 
                    "Uyarı", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
    }
    
    private void setupAylikPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Integer> yilCombo = new JComboBox<>();
        JComboBox<String> ayCombo = new JComboBox<>();
        JButton raporButton = new JButton("Rapor Oluştur");
        
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 5; year <= currentYear; year++) {
            yilCombo.addItem(year);
        }
        yilCombo.setSelectedItem(currentYear);
        
        String[] aylar = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
                         "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
        for (String ay : aylar) {
            ayCombo.addItem(ay);
        }
        ayCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        
        controlPanel.add(new JLabel("Yıl: "));
        controlPanel.add(yilCombo);
        controlPanel.add(new JLabel("Ay: "));
        controlPanel.add(ayCombo);
        controlPanel.add(raporButton);
        
        // Tablo paneli
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(aylikTable), BorderLayout.CENTER);
        
        // Özet paneli
        JPanel ozetPanel = new JPanel(new BorderLayout());
        ozetPanel.setBorder(BorderFactory.createTitledBorder("Özet Bilgiler"));
        ozetPanel.add(new JScrollPane(aylikOzet), BorderLayout.CENTER);
        
        // Ana panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(ozetPanel, BorderLayout.SOUTH);
        
        aylikPanel.add(controlPanel, BorderLayout.NORTH);
        aylikPanel.add(contentPanel, BorderLayout.CENTER);
        
        raporButton.addActionListener(e -> {
            int yil = (Integer) yilCombo.getSelectedItem();
            int ay = ayCombo.getSelectedIndex() + 1;
            SatisRaporu rapor = raporService.aylikRaporOlustur(yil, ay);
            aylikTable.setModel(raporService.getRaporTableModel(
                LocalDateTime.of(yil, ay, 1, 0, 0),
                LocalDateTime.of(yil, ay, 1, 0, 0).plusMonths(1)));
            aylikOzet.setText(getOzetText(rapor));
            aylikTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        });
    }
    
    private void setupYillikPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Integer> yilCombo = new JComboBox<>();
        JButton raporButton = new JButton("Rapor Oluştur");
        
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 5; year <= currentYear; year++) {
            yilCombo.addItem(year);
        }
        yilCombo.setSelectedItem(currentYear);
        
        controlPanel.add(new JLabel("Yıl: "));
        controlPanel.add(yilCombo);
        controlPanel.add(raporButton);
        
        // Tablo paneli
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(yillikTable), BorderLayout.CENTER);
        
        // Özet paneli
        JPanel ozetPanel = new JPanel(new BorderLayout());
        ozetPanel.setBorder(BorderFactory.createTitledBorder("Özet Bilgiler"));
        ozetPanel.add(new JScrollPane(yillikOzet), BorderLayout.CENTER);
        
        // Ana panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(ozetPanel, BorderLayout.SOUTH);
        
        yillikPanel.add(controlPanel, BorderLayout.NORTH);
        yillikPanel.add(contentPanel, BorderLayout.CENTER);
        
        raporButton.addActionListener(e -> {
            int yil = (Integer) yilCombo.getSelectedItem();
            SatisRaporu rapor = raporService.yillikRaporOlustur(yil);
            yillikTable.setModel(raporService.getRaporTableModel(
                LocalDateTime.of(yil, 1, 1, 0, 0),
                LocalDateTime.of(yil, 1, 1, 0, 0).plusYears(1)));
            yillikOzet.setText(getOzetText(rapor));
            yillikTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        });
    }
    
    private String getOzetText(SatisRaporu rapor) {
        if (rapor == null) return "Rapor oluşturulamadı.";
        
        return String.format("""
            Rapor Özeti:
            Satış Adedi: %d
            Toplam Satış: %.2f TL
            Toplam Kar: %.2f TL
            En Çok Satan Ürün: %s
            En Çok Satan Kategori: %s""",
            rapor.getSatisAdedi(),
            rapor.getToplamSatis(),
            rapor.getToplamKar(),
            rapor.getEnCokSatanUrun() != null ? rapor.getEnCokSatanUrun() : "Veri yok",
            rapor.getEnCokSatanKategori() != null ? rapor.getEnCokSatanKategori() : "Veri yok"
        );
    }
}
