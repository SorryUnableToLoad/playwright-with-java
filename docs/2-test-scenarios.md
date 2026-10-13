# Top 10 test scenarios (risk ranked)

| Rank | Scenario | Risk | Rationale |
|---|---|---|---|
| 1 | Valid submission persists all FR-05 fields and returns 200 | Critical | Lost or corrupted user responses are the core business failure. |
| 2 | Invalid typed value is rejected with an error and not marked complete | Critical | Prevents invalid data from being persisted. |
| 3 | API contract has all fields with correct types, timestamps and locale | High | Downstream systems depend on this data being machine-readable. |
| 4 | `suggestion_list` includes only suggestions matching the response | High | Incorrect matching silently corrupts stored data. |
| 5 | Default prefix filtering hides non-prefix results | High | This is the primary form-discovery behavior. |
| 6 | Configured match-anywhere filtering retains substring results | High | Configuration can alter behavior for all users. |
| 7 | Clicking a suggestion populates the input exactly | Medium | Selection is a common route to a valid response. |
| 8 | Keyboard Tab, Enter and Escape work correctly | Medium | Keyboard accessibility and completion depend on them. |
| 9 | Success message appears only after a 200 response | Medium | Avoids misleading confirmation after persistence failure. |
| 10 | India locale/timezone are correctly recorded | Medium | The supplied environment makes localization a specified behavior. |
