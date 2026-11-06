---
name: weiter-test-strategy
description: Select meaningful tests for this Java 25 service by each class's responsibility and Bibbidi layer contract. Prefer handwritten Fakes; never test Spring Framework or Spring Boot behavior itself.
---

# Weiter test strategy

Read `../../../docs/engineering-principles.md` and `../java-junit/SKILL.md`. Apply Elegant Objects and Clean Code to test design. Select tests from behavior, not a coverage number.

## Select behavior

For every changed class, state its responsibility and observable contract. Test the business decisions, invariants, state changes, boundary cases, and failures that belong to it. Also verify the contract of its Bibbidi layer. Avoid repeating the same rule at every layer; each test should catch a distinct defect. Skip trivial accessors and framework-generated behavior unless they carry a meaningful contract. JaCoCo's 80% line threshold and PIT are verification gates, not test selection criteria.

## Choose execution boundary

| Responsibility being checked | Test boundary |
| --- | --- |
| Domain rule, value object, conversion, or local state change | Construct the class directly. Use no Spring context. |
| Service use case, decisions, collaboration, and visible side effects | Construct the service directly with handwritten Fakes for its repository or outbound ports. Use no Spring context. |
| Repository mapping and JPA entity mapping, query, constraint, fetch behavior | Use a JPA test slice with a real database engine suitable for the contract. Check the persisted result or query behavior. A Fake cannot prove JPA behavior. H2 results do not establish vendor-specific SQL compatibility. |
| Controller or exception-handler logic this project wrote | Call the method directly and assert the returned DTO or thrown error. Supply a handwritten Fake for the service. Do not test Spring routing, binding, serialization, or validation. |

The rule is: do not test Spring Framework or Spring Boot behavior, such as routing, data binding, validation, auto-configuration, bean wiring, security filters, or transaction proxies. Assume the framework works and test the code this project wrote. If the expected result still makes sense when the class is created with `new`, test it that way.

## Fake contract

Use a small, explicit in-memory implementation of the same interface as the real collaborator. Give it realistic success and failure behavior, deterministic state, and inspection methods only when the test needs to observe a result. Keep it local to tests unless several test classes genuinely share the contract. Prefer checking the resulting state or output over counting calls. Do not add an interface solely to satisfy a test framework; introduce a port only when it is a real architectural boundary. Do not use Mockito or other interaction mocks as the default.

Start with class tests. Do not add Spring-context tests for user flows, security, or transactions; the framework already tests those mechanisms.

When a contract requires a particular production database, use that database for the relevant integration test instead of claiming H2 proves parity. Name each test for the observable behavior and assert its result or state. Keep tests deterministic.
