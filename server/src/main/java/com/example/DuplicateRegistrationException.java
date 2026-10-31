package com.example;

/** Thrown when the email address is already registered. */
public class DuplicateRegistrationException extends Exception {
  public DuplicateRegistrationException(String email) {
    super("Already registered: " + email);
  }
}
