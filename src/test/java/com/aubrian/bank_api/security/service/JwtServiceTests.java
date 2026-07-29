package com.aubrian.bank_api.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import com.aubrian.bank_api.security.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

class JwtServiceTests {

  @Test
  void generateToken_shouldCreateTokenWithExpectedSubjectIssuerAndExpiration() {
    SecretKey key = Jwts.SIG.HS256.key().build();
    String encodedSecret = Encoders.BASE64.encode(key.getEncoded());

    JwtProperties jwtProperties = new JwtProperties(encodedSecret, Duration.ofDays(1));
    JwtService jwtService = new JwtService(jwtProperties);

    String username = "novabank-test";

    byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
    SecretKey decodedKey = Keys.hmacShaKeyFor(keyBytes);

    String token = jwtService.generateToken(username);
    JwtParser parser = Jwts.parser().verifyWith(decodedKey).build();

    Claims claims = parser.parseSignedClaims(token).getPayload();

    assertEquals(username, claims.getSubject());
    assertEquals("novabank-api", claims.getIssuer());
    assertNotNull(claims.getIssuedAt());
    assertNotNull(claims.getExpiration());
    assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
  }
}
