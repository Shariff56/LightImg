# AI Workflow Rules

These rules apply to any AI coding agent (Claude Code, Cursor, Copilot, etc.) working in this repository. Read this file first, before making any changes.

## Read order for context
Before starting any non-trivial task, read (in this order):
1. `project-overview.md` — what this app is and its hard constraints
2. `Architecture-context.md` — stack, structure, and the no-backend/no-DB rules
3. `code-standards.md` — how code should be written here
4. `progress-tracker.md` — what's already done, what's in progress
5. `UI-context.md` — visual/design direction, when touching UI

## Hard rules — never violate these
1. **No backend, no database, no sign-in, no data collection.** Never add networking code for app logic, never add Room/SQLite, never add authentication, never add analytics/crash SDKs that transmit user data. The **only** permitted network calls are from the Google Mobile Ads SDK.
2. **No new third-party dependency** without first checking it against rule 1 and flagging it explicitly in the response — don't silently add a library.
3. **Never process user images off-device.** All compression/resize/crop/convert/PDF logic must run locally on the device.
4. **Don't touch the original file** unless the user explicitly chose "replace/overwrite" — default behavior is always save-as-new-file.
5. **Don't break offline capability.** Any feature (besides ads) must work fully with no network connection.

## How to approach a task
- If a request is ambiguous, make the most reasonable assumption consistent with `project-overview.md` and state the assumption rather than blocking on a question.
- If a request conflicts with a hard rule above (e.g. "add a login screen", "sync images to cloud"), stop and flag the conflict instead of implementing it — don't silently comply, don't silently refuse without saying why.
- Prefer small, testable, single-responsibility changes over large sweeping ones.
- When adding a feature, follow the layered structure in `Architecture-context.md` — don't put business logic in a Composable or a networking call in a ViewModel.
- Write or update tests alongside any change to `domain/usecase/` logic (see `code-standards.md`).

## Before finishing a task
- Update `progress-tracker.md` with the new status of whatever was worked on.
- Confirm the change compiles and, where applicable, passes existing tests.
- Note any new dependency, permission, or deviation from the architecture doc explicitly in the summary of work — don't bury it.

## Performance/scale awareness
This app targets a large, low-to-high-end device fleet (see "Scaling to 1M+ users" in `Architecture-context.md`). When writing image-processing code:
- Never load a full-resolution bitmap into memory without considering downsampling first
- Run heavy work off the main thread
- Handle OOM and corrupt-file cases per-image in batch operations, without crashing the whole batch

## Communication style for the agent
- State assumptions made, especially around ambiguous UI/UX or feature scope
- Flag anything that looks like scope creep beyond `project-overview.md`'s MVP list
- Keep answers focused on the task; don't re-explain the whole architecture on every response — only reference relevant sections
