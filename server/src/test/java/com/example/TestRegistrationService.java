package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestRegistrationService {

  @Test
  public void registersAndListsValidUser() throws Exception {
    RegistrationService s = new RegistrationService();
    Registration r = s.register("Asha Rao", "+91 98765 43210", "Asha@Example.com", "password1", "password1");
    assertEquals("asha@example.com", r.email());
    assertEquals(1, s.list().size());
    assertEquals("a***@example.com", r.maskedEmail());
    assertEquals("********3210", r.maskedMobile());
  }

  @Test
  public void rejectsInvalidInput() throws Exception {
    try {
      new RegistrationService().register("", "abc", "nope", "short", "different");
      fail("expected ValidationException");
    } catch (ValidationException e) {
      assertEquals(5, e.getErrors().size());
    }
  }

  @Test
  public void rejectsDuplicateEmail() throws Exception {
    RegistrationService s = new RegistrationService();
    s.register("A B", "9876543210", "a@b.com", "password1", "password1");
    try {
      s.register("C D", "9876543211", "A@B.com", "password1", "password1");
      fail("expected DuplicateRegistrationException");
    } catch (DuplicateRegistrationException e) {
      assertTrue(s.list().size() == 1);
    }
  }
}
