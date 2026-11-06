---
name: wiki-sync
description: Update the project's Notion wiki page whenever a wiki-covered source changes, currently ClientError. Run it in the same task as the change.
---

# Wiki sync

Keep the project wiki in Notion in step with the code it describes. Run this skill in the same task that changes a wiki-covered source, before reporting the task as done.

## Covered sources

| Source | Notion page | Content |
| --- | --- | --- |
| `src/main/java/kr/co/ratiko/weiter/common/exception/ClientError.java` | [ClientError](https://app.notion.com/p/4102776ec1898005bc5ff59c95568665) | Error code table, code ranges, and how to add a code |

Add a row here when a new source gets a Notion page.

## Steps

1. Read the changed source and rebuild the affected page content from it. Do not edit from memory.
2. Fetch the Notion page and compare it with the rebuilt content.
3. Draft the changed text, then run `techwriting-reviewer` on the draft. Save only after every applicable check passes.
4. Update the page in place with the smallest edit, using the Notion update tool. Keep the page URL unchanged.
5. Report the page name and what changed.

## When the page is unavailable

If the Notion page cannot be fetched or edited, keep the drafted text, tell the user, and do not create a replacement page without their approval.
