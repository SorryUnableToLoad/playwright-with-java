package com.assignment.ui.tests;

import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.assignment.support.TestApplicationServer;
import com.assignment.ui.pages.AutocompleteFormPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AutocompleteFormUiTest {
  private static TestApplicationServer server;
  private static Playwright playwright;
  private static Browser browser;

  private BrowserContext context;
  private Page page;
  private AutocompleteFormPage form;

  @BeforeClass
  static void startApplication() throws Exception {
    server = new TestApplicationServer();
    playwright = Playwright.create();
    browser = playwright.chromium()
    .launch(new BrowserType.LaunchOptions().setHeadless(true));
  }

  @AfterClass
  static void stopApplication() {
    browser.close();
    playwright.close();
    server.close();
  }

  @BeforeMethod
  void openForm() {
    server.setPostResponseStatus(200);
    context = browser.newContext(new Browser.NewContextOptions()
        .setLocale("en-IN")
        .setTimezoneId("Asia/Kolkata"));
    page = context.newPage();
    form = new AutocompleteFormPage(page);
    form.open(server.baseUrl() + "/autocomplete-form");
  }

  @AfterMethod
  void closeContext() {
    context.close();
  }

  @Test
  public void filtersSuggestionsByPrefixAndByConfiguredSubstring() {
    form.type("agile methodology process");
    assertEquals(List.of("agile methodology process", "agile methodology process testing"), form.visibleSuggestions());

    form.type("process");
    assertTrue(form.visibleSuggestions().isEmpty());

    form.open(server.baseUrl() + "/autocomplete-form?match=anywhere");
    form.type("process");
    assertEquals(List.of("agile methodology process", "agile methodology process testing"), form.visibleSuggestions());
  }

  @Test
  public void clickSelectionPopulatesInputAndValidSubmissionShowsSuccess() {
    form.selectSuggestion("agile methodology");
    assertThat(form.input()).hasValue("agile methodology");

    form.submit();

    assertThat(form.success()).isVisible();
    assertTrue(server.storedResponse().contains("\"completed\":true"));
    assertTrue(server.storedResponse().contains("\"locale\":\"en-IN\""));
    assertTrue(server.storedResponse().contains("+05:30"));
    assertTrue(server.storedResponse().contains("\"suggestion_list\":\"agile methodology\""));
  }
  
  @Test
  public void invalidInputShowsErrorAndDoesNotShowSuccess() {
    form.type("invalid value");
    form.submit();

    assertThat(form.error()).isVisible();
    assertThat(form.success()).isHidden();
  }

  @Test
  public void persistenceFailureDoesNotShowSuccess() {
    server.setPostResponseStatus(500);
    form.selectSuggestion("agile methodology");
    form.submit();

    assertThat(form.error()).isVisible();
    assertThat(form.success()).isHidden();
  }

  @Test
  public void tabEnterAndEscapeSupportKeyboardOnlyUse() {
    form.input().focus();
    page.keyboard().press("Tab");
    assertEquals("next-button", page.evaluate("document.activeElement.id"));

    page.keyboard().press("Shift+Tab");
    form.type("agile methodology");
    page.keyboard().press("Enter");
    assertThat(form.success()).isVisible();

    form.input().focus();
    page.keyboard().press("Escape");
    assertThat(form.input()).hasValue("");
    assertTrue(page.locator(".suggestions").isHidden());
  }
}
