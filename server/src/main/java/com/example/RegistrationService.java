package com.example;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/** Validates and stores event registrations (in memory; lost on restart). */
public class RegistrationService {
  private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$");
  private static final Pattern MOBILE = Pattern.compile("^\\+?[0-9 ()-]{7,20}$");

  private final Map<String, Registration> byEmail = new ConcurrentHashMap<>();

  public Registration register(String name, String mobile, String email, String password,
      String passwordRepeat) throws ValidationException, DuplicateRegistrationException {
    name = trim(name);
    mobile = trim(mobile);
    email = trim(email).toLowerCase(Locale.ROOT);
    password = password == null ? "" : password;

    Map<String, String> errors = new LinkedHashMap<>();
    if (name.length() < 2 || name.length() > 100) {
      errors.put("name", "Please enter your full name.");
    }
    if (!MOBILE.matcher(mobile).matches() || mobile.replaceAll("\\D", "").length() < 7) {
      errors.put("mobile", "Please enter a valid mobile number.");
    }
    if (email.length() > 150 || !EMAIL.matcher(email).matches()) {
      errors.put("email", "Please enter a valid email address.");
    }
    if (password.length() < 8) {
      errors.put("password", "Password must be at least 8 characters.");
    }
    if (!password.equals(passwordRepeat)) {
      errors.put("passwordRepeat", "Passwords do not match.");
    }
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }

    Registration registration = new Registration(name, mobile, email, Instant.now());
    if (byEmail.putIfAbsent(email, registration) != null) {
      throw new DuplicateRegistrationException(email);
    }
    return registration;
  }

  /** Newest first. */
  public List<Registration> list() {
    List<Registration> all = new ArrayList<>(byEmail.values());
    all.sort((a, b) -> b.registeredAt().compareTo(a.registeredAt()));
    return all;
  }

  private static String trim(String s) {
    return s == null ? "" : s.trim();
  }
}
