package com.kumar.crudapi.security;

//import io.jsonwebtoken.*;

public class JwtUtil {

    private static final String SECRET = "mysecretkey";

    public static String extractUsername(String token) {
//        return Jwts.parser()
//                .setSigningKey(SECRET)
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
        return "ss";
    }

    public static boolean validateToken(String token) {
        try {
//            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}