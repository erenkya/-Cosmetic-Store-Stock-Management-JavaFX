package util;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordHashTest {
    public static void main(String[] args) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String password = "123";
        String hash = argon2.hash(10, 65536, 1, password.toCharArray());
        System.out.println("Orijinal şifre: " + password);
        System.out.println("Hash edilmiş şifre: " + hash);
        
        // Hash doğrulama testi
        boolean verified = argon2.verify(hash, password.toCharArray());
        System.out.println("Hash doğrulama: " + verified);
    }
}
