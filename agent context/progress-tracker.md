# Progress Tracker

Status legend: `Not Started` | `In Progress` | `Blocked` | `Done`

Update this file whenever a feature's status changes. Keep entries short — this is a status board, not a design doc.

## Phase 0 — Setup
| Item | Status | Notes |
|---|---|---|
| Project scaffolding (Kotlin + Compose + MVVM structure) | Done | |
| Package structure per Architecture-context.md | Done | |
| Base theme / design tokens from UI-context.md | Done | |
| AdMob SDK integration (test ad unit IDs) | Done | |
| Consent/UMP flow for ads | Done | |

## Phase 1 — Core features (MVP)

| Item | Status | Notes |
|---|---|---|
| Image picker (single + multi-select) | Done | |
| Compress to target size (auto quality) | Done | |
| Batch compression | Done | |
| Resize (dimensions / %) | Done | |
| Crop (fixed ratios + free-form) | Done | |
| JPG ↔ PNG conversion | Done | |
| JPG/PNG → PDF (single image) | Done | |
| Before/after preview comparison | Done | |
| Custom output folder selection | Done | Uses ScopedStorage / MediaStore |
| Share-sheet integration (open app from Share menu) | In Progress | Manifest updated, MainActivity intent handling needed |

## Phase 2 — Polish & reliability
| Item | Status | Notes |
|---|---|---|
| Low-RAM device testing (2–3GB devices) | Not Started | |
| Large batch stress test (50+ images) | Done | Automated BatchStressTest created |
| OOM / corrupt file handling per-image | Done | Catching OutOfMemoryError in all use-cases |
| Crash-free rate baseline established | Not Started | |
| Unit tests for compression/resize/convert use-cases | Done | Tests for Resize, Crop, Compress |
| UI tests for core flows | In Progress | CompressScreenTest added |

## Phase 3 — v2 candidates (not started until MVP ships)
| Item | Status | Notes |
|---|---|---|
| WebP / AVIF support | Not Started | |
| HEIC → JPG/PNG | Not Started | |
| Multi-image → single PDF (reorder, page size) | Not Started | |
| Compress existing PDF | Not Started | |
| EXIF/metadata strip toggle | Not Started | |
| Auto-compress watched folder | Not Started | |
| One-time "remove ads" IAP | Not Started | |

## Release checklist (fill in closer to launch)
- [ ] Privacy policy published (states no data collection, AdMob disclosure)
- [ ] Play Store data safety form filled accurately
- [ ] Release build strips debug logging
- [ ] Signed AAB tested on a physical low-end device
- [ ] Ads tested with production ad unit IDs
