package com.assignment.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.assignment.support.TestApplicationServer;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class AutocompleteResponseApiTest {

    static TestApplicationServer server;
    static final List<String> SUGGESTIONS = List.of("agile methodology", "agile methodology process", "agile methodology process testing");

    @BeforeClass
    static void start() throws Exception {
        server = new TestApplicationServer();
    }

    @AfterClass
    static void stop() {
        server.close();
    }

    private String get() throws Exception {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(server.baseUrl() + "/api/responses")).GET().build(), HttpResponse.BodyHandlers.ofString()).body();
    }

    @Test
    public void responseMatchesFr05SchemaTypesDatesLocaleAndMatchingSuggestions() throws Exception {
        server.setGetResponse("""
      {"account_id":"98765","account_email":"test123@gmail.com","start_date":"2024-03-15T10:30:00+05:30","end_date":"2024-03-15T10:32:00+05:30","locale":"en-IN","text":"testing","suggestion_list":"agile methodology process testing","completed":true}
      """);
        ResponseContractValidator.validate(get(), SUGGESTIONS);
    }

    @Test
    public void rejectsMissingRequiredField() {
        server.setGetResponse("{" + "\"account_id\":\"98765\",\"account_email\":\"test123@gmail.com\",\"start_date\":\"2024-03-15T10:30:00+05:30\",\"end_date\":\"2024-03-15T10:32:00+05:30\",\"locale\":\"en-IN\",\"text\":\"testing\",\"completed\":true}");
        AssertionError error = validationFailure();
        assertTrue(error.getMessage().contains("suggestion_list"));
    }

    @Test
    public void rejectsStringBoolean() {
        server.setGetResponse("{" + "\"account_id\":\"98765\",\"account_email\":\"test123@gmail.com\",\"start_date\":\"2024-03-15T10:30:00+05:30\",\"end_date\":\"2024-03-15T10:32:00+05:30\",\"locale\":\"en-IN\",\"text\":\"testing\",\"suggestion_list\":\"agile methodology process testing\",\"completed\":\"true\"}");
        AssertionError error = validationFailure();
        assertTrue(error.getMessage().contains("completed must be boolean"));
    }

    @Test
    public void rejectsInvalidLocale() {
        server.setGetResponse("{" + "\"account_id\":\"98765\",\"account_email\":\"test123@gmail.com\",\"start_date\":\"2024-03-15T10:30:00+05:30\",\"end_date\":\"2024-03-15T10:32:00+05:30\",\"locale\":\"english\",\"text\":\"testing\",\"suggestion_list\":\"agile methodology process testing\",\"completed\":true}");
        AssertionError error = validationFailure();
        assertTrue(error.getMessage().contains("locale"));
    }

    @Test
    public void rejectsUtcTimestamp() {
        server.setGetResponse("{" + "\"account_id\":\"98765\",\"account_email\":\"test123@gmail.com\",\"start_date\":\"2024-03-15T10:30:00Z\",\"end_date\":\"2024-03-15T10:32:00+05:30\",\"locale\":\"en-IN\",\"text\":\"testing\",\"suggestion_list\":\"agile methodology process testing\",\"completed\":true}");
        AssertionError error = validationFailure();
        assertTrue(error.getMessage().contains("+05:30"));
    }

    @Test
    public void rejectsUnmatchedSuggestion() {
        server.setGetResponse("{" + "\"account_id\":\"98765\",\"account_email\":\"test123@gmail.com\",\"start_date\":\"2024-03-15T10:30:00+05:30\",\"end_date\":\"2024-03-15T10:32:00+05:30\",\"locale\":\"en-IN\",\"text\":\"testing\",\"suggestion_list\":\"agile methodology\",\"completed\":true}");
        AssertionError error = validationFailure();
        assertTrue(error.getMessage().contains("non-matching"));
    }

    private AssertionError validationFailure() {
        try {
            ResponseContractValidator.validate(get(), SUGGESTIONS);
            fail("Expected the response contract validation to fail");
            return null;
        } catch (AssertionError error) {
            return error;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
