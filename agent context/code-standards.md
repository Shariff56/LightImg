# Code Standards

## Language & style
- Kotlin, following the official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use `ktlint` or Android Studio's default Kotlin formatter — no custom formatting debates, just run the formatter
- Prefer `val` over `var` everywhere possible
- No `!!` (non-null assertion) except in genuinely unreachable-null cases with a comment explaining why
- Avoid nullable types leaking into the UI layer — resolve at the domain/data boundary

## Naming
- Classes/Objects: `PascalCase` (e.g. `ImageCompressor`, `CompressUseCase`)
- Functions/variables: `camelCase`
- Constants: `UPPER_SNAKE_CASE` in a `companion object` or top-level `const val`
- Compose functions: `PascalCase`, noun-based (e.g. `CompressScreen`, `ImagePreviewCard`)
- Files: match the primary class/composable name

## Project structure rules
- Follow the package layout defined in `Architecture-context.md` — don't introduce new top-level packages without updating that file
- One feature = one package under `presentation/` with its own `ViewModel`, `UiState`, and `Screen` composable
- Business logic lives in `domain/usecase/`, never inline inside a ViewModel or Composable
- File I/O and image processing implementation details live in `data/`, exposed via interfaces defined in `domain/`

## Compose conventions
- State hoisting: Composables should be stateless where possible; state lives in the ViewModel
- Use `UiState` data classes per screen (e.g. `CompressUiState(isLoading, previewImage, targetSizeKb, ...)`)
- No business logic inside Composables — only layout, formatting, and UI event dispatch
- Reusable UI pieces go in a shared `presentation/components/` package once used in 2+ screens

## Error handling
- Use sealed classes / `Result`-style wrappers for use-case outcomes (success, failure with reason) — no silent try/catch swallowing
- Every file operation (read/write/compress/convert) must handle: file not found, insufficient storage, corrupt/unsupported image, OOM on large images — with a user-facing message, not a crash
- Never crash the app on a bad input image — fail gracefully per file, especially in batch mode (one bad file shouldn't abort the whole batch)

## Testing
- Unit tests for all `domain/usecase/` logic (pure Kotlin, no Android framework dependency where avoidable)
- Unit tests for compression/resize/convert core logic with fixture images (varied sizes, formats, corrupt files)
- UI tests (Compose testing APIs) for critical flows: pick image → compress → save, batch flow, PDF conversion flow
- No PR/feature is "done" without a passing test for its core logic (see progress-tracker.md)

## Privacy & data rules (hard constraints, see project-overview.md)
- No new third-party SDK may be added without explicitly checking it against the "no data collection" and "offline-first" constraints
- No logging of file names, file contents, image bytes, or file paths to any remote service, ever
- Local debug logs (Logcat) must be stripped/disabled in release builds

## Git & commits
- Conventional commit style: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`
- One logical change per commit
- No committed secrets, keystores, or AdMob IDs in plaintext in version control — use local.properties / secrets management

## Documentation
- Every `usecase/` and public `data/` interface gets a short KDoc explaining intent (not restating the function signature)
- Update `progress-tracker.md` when a feature moves state (Not Started → In Progress → Done)
