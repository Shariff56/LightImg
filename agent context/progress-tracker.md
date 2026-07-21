# Progress Tracker

Status legend: `Not Started` | `In Progress` | `Blocked` | `Done`

Update this file whenever a feature's status changes. Keep entries short — this is a status board, not a design doc.

## Phase 0 — Setup
| Item | Status | Notes |
|---|---|---|
| Project scaffolding (Kotlin + Compose + MVVM structure) | Not Started | |
| Package structure per Architecture-context.md | Not Started | |
| Base theme / design tokens from UI-context.md | Not Started | |
| AdMob SDK integration (test ad unit IDs) | Not Started | |
| Consent/UMP flow for ads | Not Started | |

## Phase 1 — Core features (MVP)
| Item | Status | Notes |
|---|---|---|
| Image picker (single + multi-select) | Not Started | |
| Compress to target size (auto quality) | Not Started | |
| Batch compression | Not Started | |
| Resize (dimensions / %) | Not Started | |
| Crop (fixed ratios + free-form) | Not Started | |
| JPG ↔ PNG conversion | Not Started | |
| JPG/PNG → PDF (single image) | Not Started | |
| Before/after preview comparison | Not Started | |
| Custom output folder selection | Not Started | |
| Share-sheet integration (open app from Share menu) | Not Started | |

## Phase 2 — Polish & reliability
| Item | Status | Notes |
|---|---|---|
| Low-RAM device testing (2–3GB devices) | Not Started | |
| Large batch stress test (50+ images) | Not Started | |
| OOM / corrupt file handling per-image | Not Started | |
| Crash-free rate baseline established | Not Started | |
| Unit tests for compression/resize/convert use-cases | Not Started | |
| UI tests for core flows | Not Started | |

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
