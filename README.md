# Autocomplete Form Practical Assignment

Java 17, Playwright Java, and TestNG solution for the supplied Autocomplete Form brief.

## Contents

- `docs/`: requirement analysis, scenarios, supplied-response defect analysis, detailed test cases, AI reflection, and architecture notes.
- `src/test/java/com/assignment/ui/pages/`: Page Object classes.
- `src/test/java/com/assignment/ui/tests/`: executable Playwright UI tests.
- `src/test/java/com/assignment/api/`: FR-05 executable contract validator and API tests (including negative cases).
- `src/test/java/com/assignment/support/` and `src/test/resources/web/`: the local HTTP fixture used by the test suite.
- `src/test/resources/config/browser.properties`: browser/environment configuration.
- `ai-artifacts/`: assignment prompt and relevant conversation transcript artifact.

## Prerequisites

- JDK 17+
- Maven 3.9+
- Internet access for the first Maven dependency download and Playwright Chromium installation

## Run

From the repository root, run the assignment suite:

```bash
mvn test -Dsuite=assignment-testng.xml
```

On a new machine, install Chromium once before running UI tests:

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

The dedicated TestNG suite contains both UI and API coverage. It does not run the existing Docker/Selenium suite.

The suite runs headlessly against an in-process fixture at `http://127.0.0.1:<random-port>/autocomplete-form`; this replaces the unavailable assumed URL `https://test.com/autocomplete-form`. See `docs/8-architecture-discussion.md` for the production-environment approach.
# playwright-with-java
