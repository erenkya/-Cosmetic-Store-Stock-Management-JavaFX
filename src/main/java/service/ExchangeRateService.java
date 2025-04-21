package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.time.LocalDateTime;

public class ExchangeRateService {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD";
    private static double lastKnownRate = 35.0; // Varsayılan değer
    private static LocalDateTime lastUpdateTime = null;
    private static final int UPDATE_INTERVAL_MINUTES = 30; // 30 dakikada bir güncelle
    
    public static double getDolarKuru() {
        // Eğer son güncelleme zamanı null ise veya 30 dakika geçtiyse güncelle
        if (lastUpdateTime == null || 
            LocalDateTime.now().minusMinutes(UPDATE_INTERVAL_MINUTES).isAfter(lastUpdateTime)) {
            updateExchangeRate();
        }
        return lastKnownRate;
    }
    
    private static synchronized void updateExchangeRate() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5 saniye bağlantı zaman aşımı
            conn.setReadTimeout(5000); // 5 saniye okuma zaman aşımı
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject rates = jsonResponse.getJSONObject("rates");
            lastKnownRate = rates.getDouble("TRY");
            lastUpdateTime = LocalDateTime.now();
            
        } catch (Exception e) {
            System.out.println("Döviz kuru alınırken hata: " + e.getMessage());
            // Hata durumunda mevcut kur değerini koruyoruz
        }
    }
    
    // Verilen dolar miktarını TL'ye çevirir
    public static double dolarToTL(double dolarMiktari) {
        return dolarMiktari * getDolarKuru();
    }
    
    // Mevcut kur bilgisini formatlanmış string olarak döndürür
    public static String getFormattedKur() {
        return String.format("Güncel Dolar Kuru: %.2f ₺", getDolarKuru());
    }
}
