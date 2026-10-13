# Detailed test cases

## TC-UI-01 — Default prefix filtering
**Preconditions:** Form is open with default configuration. **Test data:** `agile methodology process`, then `process`.

1. Type `agile methodology process`.
2. Observe the list.
3. Replace the text with `process`.

**Expected:** Step 2 shows only `agile methodology process` and `agile methodology process testing`; step 3 shows no suggestions.

## TC-UI-02 — Configured match-anywhere filtering
**Preconditions:** Form opens with match-anywhere configuration enabled. **Test data:** `process`.

1. Type `process`.
2. Inspect the suggestions.

**Expected:** `agile methodology process` and `agile methodology process testing` remain visible.

## TC-UI-03 — Select a suggestion
**Preconditions:** Default form is open. **Test data:** `agile methodology`.

1. Click `agile methodology`.
2. Read the input value.

**Expected:** Input contains exactly the selected suggestion.

## TC-UI-04 — Valid click submission
**Preconditions:** Default form is open. **Test data:** `agile methodology`.

1. Select `agile methodology`.
2. Click Next.

**Expected:** One POST is sent; its payload includes `en-IN`, local IST (`+05:30`) start/end timestamps, and boolean `completed: true`. A 200 response displays the success message and hides the error.

## TC-UI-05 — Invalid free text
**Preconditions:** Default form is open. **Test data:** `invalid value`.

1. Type the value.
2. Click Next.

**Expected:** Error is visible; success is not visible; no completed response is recorded.

## TC-UI-05a — Persistence failure
**Preconditions:** Default form is open and the response API is configured to return HTTP 500. **Test data:** `agile methodology`.

1. Select the valid suggestion.
2. Click Next.

**Expected:** An error is visible and the success message remains hidden. Success is shown only after HTTP 200.

## TC-UI-06 — Keyboard navigation and submission
**Preconditions:** Form is open and input has focus. **Test data:** `agile methodology`.

1. Press Tab.
2. Verify Next receives focus.
3. Shift+Tab, enter the test data, and press Enter.

**Expected:** Focus order is input → Next; Enter submits and displays success.

## TC-UI-07 — Escape clears/closes
**Preconditions:** Form is open with input focused. **Test data:** `agile`.

1. Type the value.
2. Press Escape.

**Expected:** Input is cleared and suggestion list is closed.

## TC-API-08 — FR-05 valid contract
**Preconditions:** GET response is available. **Test data:** valid ID/email, ISO-8601 timestamps, `en-IN`, `testing`, one matching suggestion, boolean `true`.

1. GET the persisted response.
2. Validate required fields and data types.
3. Validate timestamp parsing, BCP 47 locale, chronological order, and matching suggestions.

**Expected:** Status 200 and all validation assertions pass.

## TC-API-09 — Missing required field
**Preconditions:** Test API can return controlled response. **Test data:** response without `suggestion_list`.

1. GET the response.
2. Run schema validation.

**Expected:** Validation fails and identifies `suggestion_list` as missing.

## TC-API-10 — Invalid completed type
**Preconditions:** Test API can return controlled response. **Test data:** `completed: "true"`.

1. GET the response.
2. Run validation.

**Expected:** Validation fails and reports that `completed` must be a boolean.

## TC-API-11 — Invalid locale, timestamp, and suggestion variants
**Preconditions:** Test API can return controlled response. **Test data:** Three variants: `locale: "english"`; a UTC (`Z`) `start_date`; and text `testing` with `agile methodology` in `suggestion_list`.

1. GET each controlled response.
2. Run the contract validation for each response.

**Expected:** Each variant fails independently: invalid locale, non-IST local timestamp, and non-matching suggestion respectively.
