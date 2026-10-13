package com.assignment.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.List;

public final class AutocompleteFormPage {
  private final Page page;

  public AutocompleteFormPage(Page page) {
    this.page = page;
  }

  public void open(String url) {
    page.navigate(url);
  }

  public Locator input() {
    return page.locator("#input-field");
  }

  public Locator nextButton() {
    return page.locator("#next-button");
  }

  public Locator error() {
    return page.locator(".error-message");
  }

  public Locator success() {
    return page.locator(".success-container");
  }

  public void type(String value) {
    input().fill(value);
  }

  public List<String> visibleSuggestions() {
    return page.locator(".suggestions li").allTextContents();
  }

  public void selectSuggestion(String value) {
    page.locator(".suggestions li:text-is('" + value + "')").click();
  }

  public void submit() {
    nextButton().click();
  }
}
