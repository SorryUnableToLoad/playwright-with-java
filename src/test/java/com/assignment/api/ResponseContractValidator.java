package com.assignment.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.regex.Pattern;

/** Executable representation of FR-05's response schema and business rules. */
public final class ResponseContractValidator {

  private static final ObjectMapper JSON = new ObjectMapper();

  private static final Pattern BCP_47 = Pattern.compile("^[A-Za-z]{2,3}(-[A-Za-z0-9]{2,8})*$");
  
  private static final List<String> REQUIRED_FIELDS = List.of(
      "account_id", "account_email", "start_date", "end_date",
      "locale", "text", "suggestion_list", "completed");

  public static void validate(String body, List<String> knownSuggestions) throws Exception {
    JsonNode node = JSON.readTree(body);
    validateRequiredFields(node);
    validateTypes(node);
    validateTimestamps(node);
    validateMatchingSuggestions(node, knownSuggestions);
  }

  private static void validateRequiredFields(JsonNode node) {
    for (String field : REQUIRED_FIELDS) {
      if (!node.hasNonNull(field)) {
        throw new AssertionError("Missing required field: " + field);
      }
    }
  }

  private static void validateTypes(JsonNode node) {
    if (!node.path("account_id").isTextual() || node.path("account_id").asText().isBlank()) {
      throw new AssertionError("account_id must be a non-empty string");
    }
    if (!node.path("account_email").isTextual() || !node.path("account_email").asText().contains("@")) {
      throw new AssertionError("account_email must be an email string");
    }
    if (!node.path("completed").isBoolean()) {
      throw new AssertionError("completed must be boolean");
    }
    if (!BCP_47.matcher(node.path("locale").asText()).matches()) {
      throw new AssertionError("locale must be IETF BCP 47");
    }
  }

  private static void validateTimestamps(JsonNode node) {
    OffsetDateTime start = parseIndiaLocalTimestamp(node.path("start_date").asText(), "start_date");
    OffsetDateTime end = parseIndiaLocalTimestamp(node.path("end_date").asText(), "end_date");
    if (end.isBefore(start)) {
      throw new AssertionError("end_date must not precede start_date");
    }
  }

  private static OffsetDateTime parseIndiaLocalTimestamp(String value, String field) {
    try {
      OffsetDateTime timestamp = OffsetDateTime.parse(value);
      if (timestamp.getOffset().getTotalSeconds() != 19_800) {
        throw new AssertionError(field + " must use the India local offset +05:30");
      }
      return timestamp;
    } catch (java.time.format.DateTimeParseException exception) {
      throw new AssertionError(field + " must be an ISO-8601 timestamp with an offset", exception);
    }
  }

  private static void validateMatchingSuggestions(JsonNode node, List<String> knownSuggestions) {
    String text = node.path("text").asText().toLowerCase();
    for (String suggestion : node.path("suggestion_list").asText().split(",\\s*")) {
      boolean isKnownSuggestion = knownSuggestions.contains(suggestion);
      boolean matchesText = suggestion.toLowerCase().contains(text);
      if (!isKnownSuggestion || !matchesText) {
        throw new AssertionError("suggestion_list includes a non-matching suggestion: " + suggestion);
      }
    }
  }

  private ResponseContractValidator() {
  }
}
