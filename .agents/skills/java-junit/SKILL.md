---
name: java-junit
description: Write or maintain JUnit Jupiter tests in this Java 25 Spring Boot project, using deterministic assertions and handwritten Fakes. Use weiter-test-strategy to select test boundaries.
---

# JUnit mechanics

Read `../../../docs/engineering-principles.md` and `../weiter-test-strategy/SKILL.md`. This skill explains test mechanics; the project test strategy selects behaviors and execution boundaries.

- Use the JUnit Jupiter and Spring Boot managed versions in `build.gradle`. Keep Gradle's `useJUnitPlatform()` and `junit-platform-launcher` setup.
- Name tests for observable behavior. Assert outcomes and state; avoid assertion-free tests added solely to meet the JaCoCo threshold.
- Use parameterized tests when the same behavior has several meaningful inputs. Keep time, randomness, locale, and external I/O deterministic.
- Use small, handwritten Fakes for collaborators at real boundaries. Model their contract with in-memory state and observable results. Do not use Mockito or verify call counts as a substitute for checking behavior.
- Do not disable or weaken a failing test to make `check` green. Keep the existing JaCoCo 80% line gate and PIT setup intact unless the user changes them.
- Run the relevant test and then the existing `check` task.

Adapted from [github/awesome-copilot](https://github.com/github/awesome-copilot/tree/60dcb901abbd87609d93d6281f1916c22b8bbd8c/skills/java-junit). License: `../../../third_party/licenses/github-awesome-copilot.txt`.
