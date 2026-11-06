# Project guidance

Read `docs/engineering-principles.md` for Java work. Elegant Objects and Clean Code are the highest project design requirements; use Bibbidi's feature-local layer structure. The task-specific skills are in `.agents/skills/`. For tests, follow `.agents/skills/weiter-test-strategy/SKILL.md`: verify each layer's responsibility and each changed class's responsibility, prefer handwritten Fakes, and never test Spring Framework or Spring Boot behavior itself.

When a source listed in `.agents/skills/wiki-sync/SKILL.md` changes, run `wiki-sync` in the same task and report the wiki result.

When creating a commit or pull request, follow `.agents/skills/git-commit/SKILL.md`; it forbids AI attribution lines.

For all prose destined for a project file, draft the text first, run `techwriting-reviewer` against the draft and its file context, and write only after every applicable check passes. If the reviewer finds an issue, revise and review again before saving. This includes documentation, templates, comments, prompts, and user-facing copy. Use `humanize-korean` only for a separate Korean prose revision request.
