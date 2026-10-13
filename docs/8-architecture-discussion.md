# Architecture discussion

The assignment is an isolated Maven module. UI tests use TestNG and Playwright Java; `AutocompleteFormPage` contains selectors and user actions so test classes express behavior rather than page mechanics. Browser contexts specify `en-IN` and `Asia/Kolkata` to represent the supplied environment.

`TestApplicationServer` is an in-process HTTP host. It serves a minimal, executable form fixture (including the required title) and captures POST payloads, keeping the repository runnable without the unavailable production domain. The fixture sends local offset timestamps and the browser context is set to `en-IN` / `Asia/Kolkata`. Production execution would replace this fixture URL with environment configuration and use a real API test account.

API tests make real HTTP GET calls to the fixture endpoint, then use `ResponseContractValidator` as an executable FR-05 schema/business-rule contract. Separate unit-level negative responses protect missing fields and invalid types. In CI, run the Maven command headlessly and publish Surefire XML plus Playwright traces/screenshots on failures.
