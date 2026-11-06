---
name: java-springboot
description: Implement or review Spring Boot features in this Java 25 service, including configuration, dependency injection, and REST boundaries. Use for Spring Boot code changes; use jpa-patterns for persistence details.
---

# Spring Boot in Weiter

Read `../../../docs/engineering-principles.md` before changing code. Elegant Objects and Clean Code guide object design; Bibbidi's feature-local layers determine placement. Follow the user's request and the existing API contract before examples from upstream.

1. Inspect the build, nearby feature code, and endpoint contract. Preserve Java 25 and Spring Boot 4 unless asked to change them.
2. Place a feature under `kr.co.ratiko.weiter.<feature>`. HTTP translation and request/response DTOs belong in `controller`, use-case coordination and transactions in `service`, domain-facing storage in `repository`, JPA details in `persistence`, and behavior-rich objects in `domain`.
3. Inject collaborators through constructors. A controller must not reach JPA directly; a domain object must not depend on Spring or a JPA entity.
4. Validate request DTOs at the HTTP boundary. Preserve the chosen success and error response format; do not impose `ProblemDetail`, `ResponseEntity`, or a new envelope solely because an example uses one.
5. Use Spring Boot 4 packages and starters matching the build. Follow `../weiter-test-strategy/SKILL.md` to choose direct construction, a Spring test slice, or a full context; replace external collaborators with handwritten Fakes.
6. Run relevant existing Gradle checks.

Adapted from [github/awesome-copilot](https://github.com/github/awesome-copilot/tree/60dcb901abbd87609d93d6281f1916c22b8bbd8c/skills/java-springboot). License: `../../../third_party/licenses/github-awesome-copilot.txt`.
