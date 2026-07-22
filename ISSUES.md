# Project Issues & Discrepancies

This document outlines architectural contradictions, design system discrepancies, and technical UI issues found in the current state of the repository.

## 1. Architectural Contradictions (Offline vs. Online)

There is a significant conflict between the core repository documentation and the recently proposed suggestions:

* **The original constraint:** `agent context/project-overview.md` and `agent context/Architecture-context.md` explicitly mandate a strict "Offline only", "No backend", "No sign-in", and "Zero data collection" architecture.
* **The pivot:** `SUGGESTIONS.md` (specifically Section 5) proposes cloud backup, server-side AI processing, and cloud storage integrations to boost ad revenue.
* **Action Required:** The project stakeholders must decide whether to adhere to the strict privacy/offline model (and reject Section 5 of SUGGESTIONS.md) or update the core `agent context/` documents to reflect the new online/cloud-enabled architecture. Leaving both in the repository will confuse AI agents and developers.

## 2. Design System Discrepancies

The repository contains multiple, conflicting design system definitions:

* **`lumina_utility_1/DESIGN.md`:** Defines a "Corporate Modern with Glassmorphic accents" style. Its primary gradient is Purple (#7C3AED) to Pink (#EC4899), and its accent colors are cool-toned.
* **`lumina_utility_2/DESIGN.md`:** Defines a high-energy "Gold-to-Crimson" spectrum. Its primary gradient is Gold (#FFD700) to Crimson (#FF0000).
* **`design.md`:** The original prompt file specifies the Purple-to-Pink gradient.
* **Action Required:** The team needs to consolidate these design files. If `lumina_utility_2` (the Gold theme) is the chosen direction (as evidenced by the `lumina_home_gold` mockup), then `lumina_utility_1` and the original `design.md` should be archived or deleted to prevent confusion.

## 3. Technical Issues in HTML Mockup (`lumina_home_gold`)

A review of `stitch_markdown_design_system/stitch_markdown_design_system/lumina_home_gold/code.html` revealed the following technical and UI issues:

* **Tailwind Configuration Errors:**
  * The inline Tailwind config defines `accent-red` as `#FF0000`, but this color doesn't align with the variables defined in `lumina_utility_2/DESIGN.md` (which uses `#ffb4ab` for error or `#ff5540` for secondary-container). The hardcoded red is jarring.
  * The inline config defines `primary` as `#ffd700` and `surface` as `#131317`.
* **Accessibility (a11y) Issues:**
  * The main action buttons (Compress, Resize, etc.) are implemented as `<button>` elements but lack proper `aria-label`s.
  * The text contrast on the "Ad" banner (`text-on-surface/60 italic` on a dark gradient background) may not pass WCAG AA standards.
* **Responsive / Layout Issues:**
  * The BottomNavBar uses `px-4 py-1` for the active "Compress" state, making it significantly larger than the other nav items. This causes horizontal crowding on narrow mobile screens (e.g., iPhone SE).
  * The `pb-safe` utility is used on the BottomNavBar, but it is not defined in the inline Tailwind configuration, meaning the padding won't be applied on iOS devices with a home indicator.
* **UX Inconsistencies:**
  * The "Recent Activity" list items have chevron icons suggesting they are clickable, but they are not wrapped in `<a>` or `<button>` tags, and have no hover/active states.
