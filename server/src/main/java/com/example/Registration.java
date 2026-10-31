package com.example;

import java.time.Instant;

/** A confirmed registration. The password is deliberately never stored. */
public record Registration(String name, String mobile, String email, Instant registeredAt) {

  /** e.g. "s*****@example.com" */
  public String maskedEmail() {
    int at = email.indexOf('@');
    return email.charAt(0) + "*".repeat(Math.max(at - 1, 1)) + email.substring(at);
  }

  /** e.g. "******3210" */
  public String maskedMobile() {
    String digits = mobile.replaceAll("\\D", "");
    int keep = Math.min(4, digits.length());
    return "*".repeat(Math.max(digits.length() - keep, 0)) + digits.substring(digits.length() - keep);
  }
}
