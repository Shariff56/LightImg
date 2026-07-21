UI/UX Design Prompt — Image Compressor & Converter (Android)
Use this as a single prompt for an AI design tool (v0, Lovable, Figma AI, Galileo, etc.) or hand it directly to a designer. It's self-contained — no other file needed.
App summary
Design a native Android app (dark-themed, offline, no sign-in) for four core jobs: compress images to a target size, resize/crop, convert JPG↔PNG, and turn images into a PDF. The app never uploads user files anywhere — this "your files never leave your phone" feeling should come through in the design, not just be a privacy-policy line. The app is ad-supported (Google AdMob), so ad placements need to be designed in from the start without feeling intrusive.
Visual style


Mood: modern, confident, calm — not playful/cartoonish, not enterprise/cold. Think "premium utility app," closer to a well-designed camera or file tool than a toy.

Theme: dark-first (near-black background), single theme for MVP — no light mode required

Signature element: a vibrant purple→pink (or blue→purple) gradient used sparingly as the primary accent — on the main CTA buttons, active states, and progress indicators — never as a full-screen background wash

Cards: rounded corners (16–24dp), soft glow/elevation instead of harsh drop shadows, occasional glassmorphism (blurred translucent panel) for sheets/overlays

Typography: bold, large headlines; muted light-gray (not pure white) for body text to reduce eye strain in a dark UI

Imagery: image thumbnails/previews always shown in rounded cards; before/after comparisons should feel tactile — side-by-side or slider-based, not just two numbers
Suggested tokens
Background:        #0E0E12
Surface/Card:       #1A1A22
Primary gradient:   #7C3AED → #EC4899
Secondary gradient: #3B82F6 → #7C3AED
Text primary:       #F5F5F7
Text secondary:     #A0A0AC
Success:            #34D399
Error:              #F87171
Radius:             16dp cards / 24dp sheets / 100dp pills
Spacing:            4 / 8 / 12 / 16 / 24 / 32
Type scale:         Display 28sp bold, Title 20sp semibold, Body 16sp regular, Caption 13sp

Navigation model
Bottom-anchored or home-grid entry (design both, pick the stronger one): four primary actions always reachable within one tap from Home. No deep hierarchy — every core task should be picker → adjust → save in 3 screens or fewer.
Screens to design
1. Home


App name/logo, minimal

Four large action cards: Compress · Resize/Crop · Convert · Image → PDF — each with a distinct icon, short one-line description

Small "recent activity" row (session-only, not persisted data — e.g. last file processed this session) is optional

Banner ad slot at the very bottom, visually separated from the action cards
2. Image Picker (shared component, not a standalone screen)


Grid of device images, multi-select support (checkmarks on selected tiles)

Clear selected-count indicator and a prominent "Next" CTA

Empty state if no images found
3. Compress


Selected image(s) shown as thumbnails

Target size control: a slider and an exact numeric field (KB/MB toggle) — both should stay in sync

Live before/after: original size crossed out or grayed, new estimated size shown prominently as it updates

Batch mode: each thumbnail gets its own mini progress ring during processing

Primary CTA: "Compress & Save" (gradient button)

Result state: success toast/sheet with "Save," "Share," and "Compress another" options
4. Resize / Crop


Full-bleed image preview with crop overlay (draggable handles + aspect-ratio chip row: Free, 1:1, 4:3, 16:9)

Toggle between Resize mode (width/height inputs, lock-aspect-ratio switch) and Crop mode

Real-time dimension readout as the user drags

Primary CTA: "Apply & Save"
5. Convert


Simple, minimal screen: thumbnail + format toggle (JPG ⇄ PNG) as a segmented control

Optional quality note ("PNG is lossless, larger file size") shown as inline helper text

Primary CTA: "Convert & Save"
6. Image → PDF


Multi-select thumbnails, drag-to-reorder

Page size chips (A4, Letter, Fit to image)

Live page-count indicator

Primary CTA: "Create PDF"

Result state: PDF thumbnail/preview + Save/Share
7. Share-sheet entry flow


When the app is opened via Android's Share intent from Gallery/WhatsApp/Camera, skip Home entirely

Show a lightweight bottom sheet directly over the shared image: "What do you want to do with this?" with the four actions as compact buttons

This should feel fast — one extra tap max before the user is in the actual tool
8. Settings


Default output folder picker

Default compression quality preset

Ads/consent management entry (opens Google's consent form)

"About" + link to privacy policy (should visibly state: no data collected, files never leave device)

No account section — deliberately absent, don't leave a placeholder for it
Interaction & motion notes


Processing states should feel alive but not slow things down: subtle progress rings, not full-screen blocking spinners, wherever batch items are involved

Success states get a small, satisfying confirmation (checkmark animation, brief haptic if feasible) — reinforces trust since there's no account/history to fall back on

Errors (corrupt file, storage full, etc.) shown per-item inline in batch views, never as an app-wide crash-like dead end
Ad placement rules (design constraint)


Banner: bottom of Home screen only, never on an active working screen (Compress/Resize/Crop/Convert/PDF canvas)

Interstitial (if any): only after a completed save/export action, never mid-task, never between picker and editor

Ads must be visually distinguishable from app content (label + subtle divider) — no disguising ad units as app UI
Accessibility


Text contrast must meet WCAG AA against the dark background (verify muted grays against #0E0E12)

Touch targets minimum 48x48dp, especially on crop-handle and batch-thumbnail interactions

All icon-only buttons need content descriptions for TalkBack
Deliverable ask
Produce: Home, Image Picker, Compress, Resize/Crop, Convert, Image→PDF, Share-sheet bottom sheet, and Settings — as a cohesive set using the tokens above, plus a small component library (primary/secondary buttons, thumbnail card, progress ring, aspect-ratio chip, bottom sheet).