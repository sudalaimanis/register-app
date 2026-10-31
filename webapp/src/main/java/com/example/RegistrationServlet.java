package com.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/** JSON API: POST /api/register and GET /api/registrations. */
@WebServlet(urlPatterns = {"/api/register", "/api/registrations"})
public class RegistrationServlet extends HttpServlet {
  private final RegistrationService service = new RegistrationService();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    req.setCharacterEncoding("UTF-8");
    try {
      Registration r = service.register(req.getParameter("name"), req.getParameter("mobile"),
          req.getParameter("email"), req.getParameter("password"), req.getParameter("passwordRepeat"));
      json(resp, 201, "{\"ok\":true,\"name\":" + str(r.name()) + ",\"email\":" + str(r.email()) + "}");
    } catch (ValidationException e) {
      StringBuilder sb = new StringBuilder("{\"ok\":false,\"errors\":{");
      boolean first = true;
      for (Map.Entry<String, String> en : e.getErrors().entrySet()) {
        if (!first) sb.append(',');
        first = false;
        sb.append(str(en.getKey())).append(':').append(str(en.getValue()));
      }
      json(resp, 400, sb.append("}}").toString());
    } catch (DuplicateRegistrationException e) {
      json(resp, 409, "{\"ok\":false,\"errors\":{\"email\":\"This email is already registered.\"}}");
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    List<Registration> all = service.list();
    StringBuilder sb = new StringBuilder("{\"count\":").append(all.size()).append(",\"registrations\":[");
    for (int i = 0; i < all.size(); i++) {
      Registration r = all.get(i);
      if (i > 0) sb.append(',');
      sb.append("{\"name\":").append(str(r.name()))
          .append(",\"email\":").append(str(r.maskedEmail()))
          .append(",\"mobile\":").append(str(r.maskedMobile()))
          .append(",\"registeredAt\":").append(str(r.registeredAt().toString())).append('}');
    }
    json(resp, 200, sb.append("]}").toString());
  }

  private static void json(HttpServletResponse resp, int status, String body) throws IOException {
    resp.setStatus(status);
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.setHeader("Cache-Control", "no-store");
    resp.getWriter().write(body);
  }

  private static String str(String s) {
    StringBuilder sb = new StringBuilder("\"");
    for (char c : s.toCharArray()) {
      switch (c) {
        case '"': sb.append("\\\""); break;
        case '\\': sb.append("\\\\"); break;
        case '\n': sb.append("\\n"); break;
        case '\r': sb.append("\\r"); break;
        case '\t': sb.append("\\t"); break;
        default:
          if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c);
      }
    }
    return sb.append('"').toString();
  }
}
