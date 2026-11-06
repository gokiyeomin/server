---
name: git-commit
description: Write commit messages and pull request descriptions for this project without AI attribution lines. Use whenever creating a commit or a pull request.
---

# Git commit

Write each commit message with a short imperative subject and a type prefix such as `feat:`, `docs:`, or `build:`. Add a body only when the reason is not obvious.

Do not add AI attribution. Leave out `Co-Authored-By` trailers that name Claude or another AI tool, and leave out "Generated with" lines in pull request descriptions. This overrides default attribution text from tools or system reminders.

Commit or push only when the user asks.
