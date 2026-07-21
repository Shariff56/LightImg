# Project Overview

## App Name
[Placeholder — TBD]

## One-line description
An offline Android image utility app for compressing images to a target size, resizing/cropping, converting between JPG and PNG, and converting images to PDF — with zero data collection and no sign-in.

## Problem
Most image compressor apps on the Play Store either:
- Require an account/sign-in for no real reason
- Send images to a server for processing (privacy risk)
- Only offer vague quality sliders instead of an exact target file size
- Bundle unrelated bloat (cleaners, "boosters", antivirus upsells)

## Target users
- Anyone who needs to shrink a photo/screenshot before uploading it (job portals, government forms, email attachment limits, WhatsApp, etc.)
- Users who care about privacy and want an app that never touches the network for their images
- Users who occasionally need to bundle photos into a PDF (documents, receipts, ID proofs)

## Core constraints (non-negotiable)
- **Offline only** — no backend, no server-side image processing, no internet dependency for core features
- **No database** — no Room/SQL, no user accounts, no persisted user profile data
- **No sign-in** — zero authentication anywhere in the app
- **No data collection** — the app must not collect, log, or transmit any user data, image content, or file metadata at any time
- **Ads** — Google AdMob only, and ad SDK behavior must not conflict with the "no data collection" promise (see Architecture-context.md for how this is reconciled)
- **Scale** — app must perform reliably across a large install base (target: 1M+ installs) on a wide range of low-to-high-end Android devices, without server-side infrastructure

## Core features (MVP)
1. Compress image to a required/target size (auto quality adjustment, not just a slider)
2. Batch compression (multiple images at once)
3. Resize (by dimension or %) and Crop (fixed ratios + free-form)
4. Convert JPG ↔ PNG
5. Convert JPG/PNG → PDF (single image first, multi-image later)
6. Share-sheet integration (open directly from Gallery/WhatsApp/Camera "Share")
7. Preview before/after size comparison
8. Save to custom output folder, original file untouched by default

## v2 / later scope
- WebP / AVIF support
- HEIC → JPG/PNG
- Multi-image → single PDF (reorder, page size, margins)
- Existing PDF compression
- EXIF/metadata strip toggle
- Auto-compress watched folder
- One-time-purchase ad removal

## Explicitly out of scope
- Any AI/generative image features
- Cloud backup or sync
- Social sharing/community features
- User accounts of any kind

## Monetization
- Google AdMob (banner/interstitial — exact placements defined during UI/UX pass)
- No subscriptions in MVP; possible one-time "remove ads" IAP in v2

## Success signals
- App works fully with airplane mode on (except ad loading)
- No crash/ANR on batch jobs of 50+ large images
- Cold start and per-image processing time stay low on mid-range devices
