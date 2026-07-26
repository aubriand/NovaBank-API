package com.aubrian.bank_api.security.config;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTests {
  
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Test
  void shouldEncodeAndMatchPassword() {
    String rawPassword = "StrongPassword123!";
    String encodedPassword = passwordEncoder.encode(rawPassword);

    assertNotEquals(rawPassword, encodedPassword);
    assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
  }
}
