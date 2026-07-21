# Architecture Context

## Platform & stack (assumed — confirm if different)
- **Platform:** Native Android
- **Language:** Kotlin
- **UI toolkit:** Jetpack Compose
- **Architecture pattern:** MVVM (Model-View-ViewModel), unidirectional data flow
- **Min SDK / Target SDK:** Min SDK 24+ (Android 7.0) recommended for broad reach; Target SDK = latest stable at build time
- **Async:** Kotlin Coroutines + Flow
- **Background/batch jobs:** WorkManager (for batch compression jobs that may outlive a screen)
- **Image loading/preview:** Coil
- **Image encode/decode:** Android `BitmapFactory` / `ImageDecoder` + platform JPEG/PNG encoders (evaluate MozJPEG/Jpegli via native bindings only if quality gains justify added complexity — NOT required for MVP)
- **PDF generation:** Android's built-in `PdfDocument` API (no third-party PDF library needed for MVP)
- **Ads:** Google Mobile Ads SDK (AdMob)

## No-backend, no-database architecture
This is a **fully on-device** app. There is no server component and this document should never grow one. Consequences:
- No REST/GraphQL client, no networking layer for app logic
- No Room, no SQLite, no server-synced state
- "Local storage" = Android Storage Access Framework / Scoped Storage only, used purely to read the user's chosen image(s) and write output files. No app-managed database of file records.
- App settings (e.g. last-used quality preset, default output folder) may use `DataStore` (key-value) — this is local device preference storage, not user data collection, and never leaves the device.
- The **only** network calls in the entire app come from the AdMob SDK itself (ad requests/impressions). This must remain the sole exception. Any future feature proposal that requires a network call for core functionality should be flagged, not silently added.

## Layered structure
```
app/
├── presentation/        # Compose screens, ViewModels, UI state
│   ├── home/
│   ├── compress/
│   ├── resize_crop/
│   ├── convert/
│   ├── pdf/
│   └── settings/
├── domain/               # Use-cases, business rules (pure Kotlin, no Android deps where possible)
│   ├── model/
│   └── usecase/
├── data/                 # File I/O, image processing implementations
│   ├── imageprocessing/
│   ├── pdf/
│   └── files/            # SAF wrappers, output-folder resolution
├── ads/                  # AdMob integration, isolated behind an interface
└── core/                 # Shared utils, extensions, DI setup
```

## Reconciling "no data collection" with Google Ads
Google Mobile Ads SDK does its own ad-serving data handling (industry standard, governed by Google's policies, not this app's). To keep the "we collect nothing" claim accurate and defensible:
- Do not enable AdMob personalized ads by default without user-visible consent flow if operating in regions requiring it (GDPR/UMP consent form via Google's User Messaging Platform)
- The app itself must not add any custom analytics, crash reporting with PII, or third-party SDKs beyond AdMob (+ optionally Play Integrity/Play Core for app updates — no user data involved)
- Privacy policy must clearly state: "This app does not collect or transmit any user files or personal data. Ad serving is handled by Google AdMob per Google's own privacy policy."
- No Firebase Analytics, no Crashlytics-with-PII, no third-party trackers, unless explicitly re-scoped later — flag before adding any new SDK

## Scaling to 1M+ users (device-side, not server-side)
Since there's no backend, "scale" here means **robustness across a huge, heterogeneous device fleet**, not server capacity:
- Process images off the main thread (Coroutines/Dispatchers.Default or IO) — never block UI
- Stream/downsample large bitmaps (avoid `OutOfMemoryError` on high-res camera images, e.g. 108MP sensors)
- Use `WorkManager` for batch jobs so they survive process death / backgrounding
- Test on low-RAM devices (2–3GB) as a hard requirement, not an edge case
- Keep APK/AAB size lean (Play Feature Delivery / App Bundle) since 1M installs across varied networks/regions means download size matters
- Avoid memory leaks in Compose (proper `remember`/lifecycle scoping) since long batch sessions are a core use case
- Crash-free rate is the key scale metric to track post-launch, not requests/sec

## Permissions
- Read/write media via Storage Access Framework — avoid broad `READ_EXTERNAL_STORAGE`/`WRITE_EXTERNAL_STORAGE` where scoped storage + SAF suffices
- No camera, contacts, location, or any permission unrelated to picking/saving image files
