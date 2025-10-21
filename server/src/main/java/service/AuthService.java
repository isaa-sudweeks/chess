package service;

import java.util.UUID;

public class AuthService {

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
