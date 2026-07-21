# Google Jules Repository Guidelines — LightImg (Android Image Utility)

This file (`JULES.md`) provides autonomous agent context and instructions for **Google Jules** working on this repository.

---

## 1. Project Overview & Mission
- **App Type:** Offline Android Image Utility App.
- **Key Features:** Image compression to exact target size (auto-quality adjustment), batch processing, resize & crop, JPG ↔ PNG conversion, JPG/PNG → PDF conversion, before/after preview, SAF save-to-folder.
- **Tech Stack:** Native Android (Kotlin), Jetpack Compose (UI), MVVM + Unidirectional Data Flow, Coroutines + Flow, WorkManager (batch processing), Scoped Storage / Storage Access Framework (SAF).

---

## 2. Hard Architectural Constraints (NON-NEGOTIABLE)
1. **Fully On-Device & Offline First:** No backend, no REST/GraphQL endpoints, no cloud APIs. Core image processing features MUST function fully without internet or airplane mode enabled.
2. **Zero Database & Zero Sign-In:** No Room/SQLite, no account management, no user profiles, no database sync.
3. **Zero Data Collection:** The app must NEVER collect, log, or transmit user images, file paths, metadata, or telemetry. The ONLY network traffic allowed comes from the Google Mobile Ads SDK (AdMob).
4. **Preserve Original Files:** Default behavior is always **save as new file**. Never overwrite original user media unless explicitly requested.
5. **No Unvetted SDKs:** Do NOT introduce any third-party SDK without verifying it strictly respects offline and zero-data-collection policies.

---

## 3. Directory & Layer Architecture
Maintain a clean 3-tier architecture with strict boundaries:
```
app/
├── presentation/        # Compose UI screens, ViewModels, UiState data classes
│   ├── home/
│   ├── compress/
│   ├── resize_crop/
│   ├── convert/
│   ├── pdf/
│   └── settings/
├── domain/               # Pure Kotlin business rules & UseCases (no Android UI dependencies)
│   ├── model/
│   └── usecase/
├── data/                 # Data implementations (Bitmap encoders, SAF wrappers, PdfDocument)
│   ├── imageprocessing/
│   ├── pdf/
│   └── files/
├── ads/                  # Isolated AdMob wrappers
└── core/                 # Shared utils, DI, Kotlin extensions
```

---

## 4. Context & Specification Index
Before executing non-trivial tasks, review the detailed specs under `agent context/`:
- [`agent context/project-overview.md`](file:///agent%20context/project-overview.md) — Product requirements, MVP scope, success metrics.
- [`agent context/Architecture-context.md`](file:///agent%20context/Architecture-context.md) — Technical stack, scaling rules, memory limits.
- [`agent context/code-standards.md`](file:///agent%20context/code-standards.md) — Kotlin style, Compose state hoisting, error handling rules.
- [`agent context/AI-workflow-rules.md`](file:///agent%20context/AI-workflow-rules.md) — Core execution guidelines and safety checks.
- [`agent context/progress-tracker.md`](file:///agent%20context/progress-tracker.md) — Task status board. Update whenever work status changes.

---

## 5. Coding & Performance Standards
- **Memory Safety:** Stream and downsample high-resolution Bitmaps before decoding to avoid `OutOfMemoryError` (OOM) on high-megapixel sensors (e.g. 108MP).
- **Background Execution:** Offload all heavy image operations to `Dispatchers.Default` or `Dispatchers.IO`. Use `WorkManager` for background batch tasks.
- **Graceful Error Recovery:** Wrap file/image operations in sealed `Result` classes. If a file fails during batch compression, record the error and continue processing the remaining images.
- **State Hoisting:** Composables should be stateless; ViewModels own single `UiState` objects.
- **Testing Requirements:** Every new UseCase in `domain/usecase/` must include corresponding unit tests.

---

## 6. Build & Test Commands
Google Jules should use standard Gradle commands when building and validating code:
- **Build App:** `./gradlew assembleDebug`
- **Run Unit Tests:** `./gradlew test`
- **Run Lint Checks:** `./gradlew lint`

---

## 7. Task & PR Expectations for Jules
When Jules receives a GitHub Issue or Task:
1. Review relevant specifications in `agent context/`.
2. Keep changes focused, modular, and single-responsibility.
3. Write/update unit tests in `app/src/test/` for domain logic changes.
4. Update `agent context/progress-tracker.md` to reflect task progress.
5. Create a descriptive PR with summary, test status, and verification steps.
