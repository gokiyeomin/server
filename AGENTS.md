# Project guidance

Read `docs/engineering-principles.md` for Java work. Elegant Objects and Clean Code are the highest project design requirements; use Bibbidi's feature-local layer structure. The task-specific skills are in `.agents/skills/`. For tests, follow `.agents/skills/weiter-test-strategy/SKILL.md`: verify each layer's responsibility and each changed class's responsibility, prefer handwritten Fakes, and load Spring only for Spring behavior.

For technical prose drafted or edited in this project, use `techwriting-reviewer` as the final reviewer. It reports issues; the author decides on revisions. Use `humanize-korean` only for a separate Korean prose revision request.
