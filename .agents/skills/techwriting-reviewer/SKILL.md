---
name: techwriting-reviewer
description: Review Korean technical writing and documentation for minimum publication quality using an 18-item checklist. Use after drafting or editing a technical document, and when the user asks for a writing review. Report findings; do not rewrite without a request.
---

# Technical writing reviewer

Use this skill as the reviewer for technical prose in this project. Review a draft after it is written or edited, even when the user did not separately ask for a review. For routine code changes or brief conversational answers, it is not a required step. Keep the authoring and reviewer roles separate: this skill reports findings and does not silently revise the text.

Read `references/REFERENCE.md` and apply its 18 checks, P0/P1/P2 severity, and output format. If an item cannot be checked from the supplied format, mark it unverified rather than passing it. A P0 blocks publication readiness; P1 and P2 are advice for the author. Cite the exact passage or location behind each finding. Do not manufacture a defect to fill the checklist.

Accept Markdown, plain text, Word content, pasted text, or an accessible Google document. Use the connected document tool available in the current agent when a link is provided. If no tool can read the input, ask for the document text. Save a review file only when the user requests one.

This is a minimum quality review, not a judgment of technical depth, argument strength, or personal style. Use `humanize-korean` only for a separate request to revise Korean prose; this reviewer does not rewrite by default.

Adapted from [young-twc/techwriting-tools `min-check` v1.1.1](https://github.com/young-twc/techwriting-tools/tree/e196e9400aec83d0cc8f192960cabe40ca9f2fae/skills/min-check). License: `../../../third_party/licenses/young-twc-techwriting-tools.txt`.
