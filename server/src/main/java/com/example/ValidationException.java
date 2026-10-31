package com.example;

import java.util.Map;

/** Thrown when submitted registration details are invalid. */
public class ValidationException extends Exception {
  private final Map<String, String> errors;

  public ValidationException(Map<String, String> errors) {
    super("Invalid registration details: " + errors);
    this.errors = errors;
  }

  /** Field name to message. */
  public Map<String, String> getErrors() {
    return errors;
  }
}
