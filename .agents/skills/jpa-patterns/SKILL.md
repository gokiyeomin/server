---
name: jpa-patterns
description: Design or review JPA entities, mappings, queries, fetch plans, and transactions in this Spring Boot service. Use when persistence behavior or query performance is in scope.
---

# JPA in Weiter

Read `../../../docs/engineering-principles.md`. Elegant Objects and Clean Code govern the domain model; Bibbidi's layers govern where persistence code lives.

- Keep business behavior in `domain`. Put JPA entities and Spring Data interfaces in `<feature>/persistence`; expose domain-oriented operations from `<feature>/repository`; convert between the two with a mapper there. Do not return a JPA entity from a controller or make a domain object depend on JPA annotations.
- Decide aggregate boundaries and invariants before cascades. Use lazy relationships by default and choose a fetch plan for each real read path. Check N+1 with observed SQL or statistics, then choose an entity graph, fetch join, projection, or separate query.
- Do not combine collection fetch joins with database pagination. Use a page of IDs followed by a bounded fetch, or a projection, when both are needed.
- Put transaction boundaries on service use cases. Use `readOnly = true` for pure reads; keep network calls outside database transactions. Enforce uniqueness with a database constraint.
- Choose indexes from actual filters, joins, ordering, and query plans. Do not copy sample pool sizes, cache settings, or migration tools without a concrete need.
- Follow `../weiter-test-strategy/SKILL.md` for persistence tests. Use a real JPA context to verify mappings, queries, constraints, and fetch behavior; a Fake cannot prove those contracts. Run existing build checks after persistence changes.

Adapted from [affaan-m/ECC](https://github.com/affaan-m/ECC/tree/b38be6a7f6350964d59328b52f0624e1d1d552d1/skills/jpa-patterns). License: `../../../third_party/licenses/affaan-m-ecc.txt`.
