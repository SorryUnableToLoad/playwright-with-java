# AI usage reflection

## Tools used
Codex (GPT-5) was used as the AI coding assistant for this assignment.

## Usage areas
It helped translate the brief into a Java/Playwright Maven structure, draft Page Object and API-contract test code, create Markdown documentation, and suggest risk-ranked coverage.

## Modifications made
1. Generated test ideas initially treated the supplied UTC timestamps as valid ISO timestamps. I corrected the defect analysis to distinguish **valid ISO syntax** from the requirement that the timestamps be in the user's local IST time.
2. The generic contract approach accepted any string for `completed`; I changed the executable validator to require `JsonNode.isBoolean()`, so the supplied `"true"` fails as required.
3. The assignment's wording about `suggestion_list` is ambiguous for a selected exact string. I documented the assumption and added a matching-only validation rule instead of asserting that the full configured list is always invalid.

## AI limitations
AI cannot resolve missing product decisions from the brief: it cannot know whether “local time” must be an offset timestamp or a local date-time plus timezone, nor the exact intended matching semantics for a selected suggestion. These require product-owner clarification.
