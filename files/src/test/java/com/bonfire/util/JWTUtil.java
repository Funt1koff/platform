package com.bonfire.util;

import io.smallrye.jwt.build.Jwt;

import java.util.UUID;

public class JWTUtil {

    public static String generateJwtToken(String userId) {
        return Jwt.issuer("https://example.com/issuer")
                .subject(UUID.randomUUID().toString())
                .claim("user_id", userId)
                .claim("sid", UUID.randomUUID().toString())
                .sign();
    }
}
