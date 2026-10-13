package com.assignment.support;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/** Lightweight test-only host for the supplied form and its REST endpoint. */
public final class TestApplicationServer implements AutoCloseable {
  private final HttpServer server;
  private volatile String storedResponse = "{}";
  private volatile String getResponse;
  private volatile int postResponseStatus = 200;

  public TestApplicationServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/autocomplete-form", this::serveForm);
    server.createContext("/api/responses", this::responses);
    server.setExecutor(Executors.newCachedThreadPool());
    server.start();
  }

  public String baseUrl() {
    return "http://127.0.0.1:" + server.getAddress().getPort();
  }

  public String storedResponse() {
    return storedResponse;
  }

  public void setGetResponse(String response) {
    getResponse = response;
  }

  /** Allows UI tests to verify that the form handles a persistence failure. */
  public void setPostResponseStatus(int status) {
    postResponseStatus = status;
  }

  private void serveForm(HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
      send(exchange, 405, "");
      return;
    }

    try (InputStream stream = getClass().getResourceAsStream("/web/form.html")) {
      String html = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
      send(exchange, 200, html, "text/html; charset=utf-8");
    }
  }

  private void responses(HttpExchange exchange) throws IOException {
    if ("POST".equals(exchange.getRequestMethod())) {
      storedResponse = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
      boolean accepted = postResponseStatus == 200;
      send(exchange, postResponseStatus, "{\"accepted\":" + accepted + "}", "application/json");
    } else if ("GET".equals(exchange.getRequestMethod())) {
      send(exchange, 200, getResponse == null ? storedResponse : getResponse, "application/json");
    } else {
      send(exchange, 405, "");
    }
  }

  private void send(HttpExchange exchange, int status, String body) throws IOException {
    send(exchange, status, body, "text/plain; charset=utf-8");
  }

  private void send(HttpExchange exchange, int status, String body, String contentType) throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().set("Content-Type", contentType);
    exchange.sendResponseHeaders(status, bytes.length);
    exchange.getResponseBody().write(bytes);
    exchange.close();
  }

  @Override
  public void close() {
    server.stop(0);
  }
}
