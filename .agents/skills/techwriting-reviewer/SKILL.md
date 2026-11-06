---
name: techwriting-reviewer
description: Review every prose draft intended for a project file before it is written, using the 18-item checklist. Report findings so the author can revise and submit a passing draft before saving.
---

# Technical writing reviewer

Use this skill as the reviewer for every piece of prose that will be added to or changed in a project file, including documentation, templates, comments, prompts, and user-facing copy. Draft the proposed text in memory first and review it in the context of the target file. If the draft fails, the author revises the draft and requests another review. Write to the target file only after the draft passes, then check that the saved text matches the approved draft. This gate does not apply to program syntax, generated data, or brief conversational replies that are not saved to a file. Keep the authoring and reviewer roles separate: this skill reports findings and does not silently revise the text.

Read `references/REFERENCE.md` and apply its 18 checks, P0/P1/P2 severity, and output format. Mark a criterion as not applicable when the format genuinely lacks that element, such as a code block in a short comment. If an applicable item cannot be checked, mark it unverified rather than passing it. For this project's pre-write gate, pass only when every applicable item passes and none is unverified. This is stricter than the upstream publication threshold: P1 and P2 findings also require revision before writing to the file. Cite the exact passage or location behind each finding. Do not manufacture a defect to fill the checklist.

Accept Markdown, plain text, Word content, pasted text, or an accessible Google document. Use the connected document tool available in the current agent when a link is provided. If no tool can read the input, ask for the document text. Save a review file only when the user requests one.

This is a minimum quality review, not a judgment of technical depth, argument strength, or personal style. Use `humanize-korean` only for a separate request to revise Korean prose; this reviewer does not rewrite by default.

Adapted from [young-twc/techwriting-tools `min-check` v1.1.1](https://github.com/young-twc/techwriting-tools/tree/e196e9400aec83d0cc8f192960cabe40ca9f2fae/skills/min-check). License: `../../../third_party/licenses/young-twc-techwriting-tools.txt`.
