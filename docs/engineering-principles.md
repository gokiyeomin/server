# Backend engineering principles

These rules guide this server's Java work. When guidance conflicts, follow the user's current request first, then Elegant Objects and Clean Code, then Bibbidi's layer structure, then examples in imported skills. Do not turn a sample implementation into a project requirement.

## Object design

- Put a business decision beside the state it governs. Prefer objects that answer or perform a meaningful operation to services that pull out fields and decide everything themselves.
- Make required state and collaborators explicit in constructors. Prefer immutable domain state and methods that preserve invariants. Do not create setters, generic managers, or abstractions without a concrete reason.
- Use records for transparent immutable data at boundaries. A domain value with rules may need an encapsulated class even if a record would be shorter. JPA entities may need framework-specific construction; keep that compromise inside `persistence`.
- Name concepts by domain meaning. Keep functions cohesive and at a consistent level of abstraction. Remove duplication when it represents the same rule; avoid extracting trivial wrappers just to shorten a method.
- Make errors and nullability explicit at their boundary. Do not return a plausible default to hide a failure. Add comments for non-obvious reasons and contracts, not to narrate code.

## Lombok

Use only `@Slf4j` and `@RequiredArgsConstructor`. Annotations that generate accessors, mutators, builders, or all-argument constructors, such as `@Data`, `@Getter`, `@Setter`, `@Builder`, and `@AllArgsConstructor`, hide object behavior from the class that owns it. `lombok.config` turns their use into a compile error.

## Bibbidi layer structure

The reference is `C:\woowahan-techcourse\3.LEVEL_3\bibbdi_source_code\BE` and its `docs/convention/code-convention.md`. Adapt its package shape to this project without copying its product behavior or dependencies:

```text
kr.co.ratiko.weiter.<feature>/
  controller/        HTTP and request/response DTOs
  service/           use cases, transaction boundaries, results
  repository/        domain-facing persistence operations and mapping
  persistence/       JPA entities, Spring Data repositories, SQL adapters
  domain/            business behavior and value objects
kr.co.ratiko.weiter.common/  genuinely shared code only
```

The usual call path is Controller → Service → Repository → Persistence. The domain model stays independent of the HTTP and persistence layers. A controller does not call a JPA repository directly. The repository converts persistence entities to domain objects. Introduce only the folders a feature actually needs.

## Verification boundary

Keep the existing Java 25, Spring Boot 4, JaCoCo 80% line gate, and PIT settings unless a task changes them. Test each changed class's observable responsibility and its layer's contract. Use direct construction and handwritten Fakes for ordinary object behavior. Do not test Spring Framework or Spring Boot behavior itself; load a Spring context only when a test needs one to run a persistence mapping or query that this project wrote. Follow `.agents/skills/weiter-test-strategy/SKILL.md` for the decision rules.
