import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IadeDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/stock_management";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public void iadeKaydet(String urunKodu, String iadeNedeni) throws SQLException {
        String sql = "INSERT INTO iade (urun_kodu, iade_nedeni) VALUES (?, ?);";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, urunKodu);
            statement.setString(2, iadeNedeni);
            statement.executeUpdate();
        }
    }
}
