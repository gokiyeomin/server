---
name: java-coding-standards
description: Apply this project's Elegant Objects and Clean Code design rules when writing or reviewing Java. Use for object boundaries, naming, cohesion, and package placement; it does not define test scope.
---

# Java design standards

Read `../../../docs/engineering-principles.md` first. Elegant Objects and Clean Code shape design; Bibbidi determines feature-local layers; this skill resolves Java implementation choices within those constraints.

- Give each object a clear responsibility and expose behavior that protects invariants. Avoid moving decisions into services by repeatedly reading a domain object's fields through getters.
- Prefer immutable state and constructor-supplied required collaborators. Use records for transparent immutable boundary data; use classes when a domain value has behavior or an invariant. Allow framework-specific JPA construction in persistence entities without weakening domain design.
- Name classes, methods, and variables for their role. Keep methods cohesive and at one level of abstraction. Extract a helper when it clarifies intent; avoid one-line wrappers and speculative interfaces.
- Treat null, exceptions, and validation at the proper boundary. Do not scatter defensive checks or broad catches across every layer; do not conceal failures with a default value.
- Follow `kr.co.ratiko.weiter.<feature>/{controller,service,repository,persistence,domain}` rather than a single application-wide controller/service/repository tree.
- Preserve local formatting and dependency choices. Do not add libraries or framework patterns merely to satisfy an example.

## Object calisthenics for domain objects

Apply these nine rules when writing or reviewing domain code:

1. Use only one level of indentation in each method.
2. Do not use `else`.
3. Wrap primitive values and strings in domain value objects.
4. Use at most one method-call dot per line.
5. Use complete names instead of abbreviations.
6. Keep entities small and focused on one responsibility.
7. Keep fewer than three instance fields in each class.
8. Give collections their own class and put collection behavior there.
9. Do not expose getters, setters, or passive property holders. Ask objects to perform domain operations instead of extracting their state.

Keep domain classes as separate top-level types rather than nested classes. When a task only defines state, use constructor-supplied immutable fields and wait for a concrete business operation before adding behavior. Do not add accessors or invented behavior to make a state-only draft appear complete.

Adapted from [affaan-m/ECC](https://github.com/affaan-m/ECC/tree/b38be6a7f6350964d59328b52f0624e1d1d552d1/skills/java-coding-standards). License: `../../../third_party/licenses/affaan-m-ecc.txt`.
