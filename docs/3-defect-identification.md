# FR-05 discrepancies in the supplied GET response

1. `start_date` and `end_date` end in `Z`, therefore represent UTC, not the user's local IST time required by FR-05. The expected local representation would carry `+05:30` (or otherwise unambiguously preserve local time).
2. `locale` is `en`, but the required user locale is BCP 47 such as `en-IN`; `en` is syntactically a language tag but omits the required India regional context from the test environment.
3. `completed` is the string `"true"`, rather than the boolean `true`.
4. `suggestion_list` contains all configured suggestions, rather than only suggestions matching the value entered/selected. For the selected value `agile methodology`, the expected exact-selection interpretation is only `agile methodology`.

All other shown fields have the expected names and plausible values. Timestamp local-vs-UTC treatment and selected-value matching should be clarified with the product owner because the prose leaves implementation detail open.
