---
name: Lumina Utility
colors:
  surface: '#1A1A22'
  surface-dim: '#131317'
  surface-bright: '#39393d'
  surface-container-lowest: '#0e0e12'
  surface-container-low: '#1b1b1f'
  surface-container: '#1f1f23'
  surface-container-high: '#2a292e'
  surface-container-highest: '#353439'
  on-surface: '#e4e1e7'
  on-surface-variant: '#ccc3d8'
  inverse-surface: '#e4e1e7'
  inverse-on-surface: '#303034'
  outline: '#958da1'
  outline-variant: '#4a4455'
  surface-tint: '#d2bbff'
  primary: '#d2bbff'
  on-primary: '#3f008e'
  primary-container: '#7c3aed'
  on-primary-container: '#ede0ff'
  inverse-primary: '#732ee4'
  secondary: '#adc6ff'
  on-secondary: '#002e6a'
  secondary-container: '#0566d9'
  on-secondary-container: '#e6ecff'
  tertiary: '#ffb784'
  on-tertiary: '#4f2500'
  tertiary-container: '#a15100'
  on-tertiary-container: '#ffe0cd'
  error: '#F87171'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#eaddff'
  primary-fixed-dim: '#d2bbff'
  on-primary-fixed: '#25005a'
  on-primary-fixed-variant: '#5a00c6'
  secondary-fixed: '#d8e2ff'
  secondary-fixed-dim: '#adc6ff'
  on-secondary-fixed: '#001a42'
  on-secondary-fixed-variant: '#004395'
  tertiary-fixed: '#ffdcc6'
  tertiary-fixed-dim: '#ffb784'
  on-tertiary-fixed: '#301400'
  on-tertiary-fixed-variant: '#713700'
  background: '#131317'
  on-background: '#e4e1e7'
  surface-variant: '#353439'
  accent-pink: '#EC4899'
  text-primary: '#F5F5F7'
  text-secondary: '#A0A0AC'
  success: '#34D399'
typography:
  display-lg:
    fontFamily: Sora
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.02em
  headline-sm:
    fontFamily: Sora
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-caps:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
  caption:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  xs: 4px
  sm: 8px
  md: 12px
  lg: 16px
  xl: 24px
  xxl: 32px
  gutter: 16px
  margin-mobile: 16px
  margin-tablet: 24px
---

## Brand & Style

The design system is crafted for a "Premium Utility" experience on Android. It evokes a sense of local security, speed, and precision. The brand personality is confident and calm, moving away from the typical cluttered utility app towards a refined, tool-centric aesthetic found in professional camera software.

The visual style is **Corporate Modern with Glassmorphic accents**. It utilizes a deep, dark-first palette to reduce eye strain and emphasize content. Depth is created through subtle tonal layering and soft, glowing elevations rather than traditional shadows. High-quality gradients are reserved for moments of action and completion, reinforcing a sense of progress and accomplishment.

**Key Principles:**
- **Local-First Trust:** The UI should feel self-contained and fast, emphasizing that data processing happens on-device.
- **Precision:** Controls like sliders and numeric inputs are balanced to feel tactile and professional.
- **Focused Utility:** Minimalist navigation ensures the user is never more than three taps away from a completed task.

## Colors

The color palette is strictly dark-first to cater to the power-user nature of utility apps. 

- **Primary Gradient:** Used for the most important actions (Main CTAs). It transitions from Purple (#7C3AED) to Pink (#EC4899).
- **Secondary Gradient:** Used for secondary progress indicators and active selection states. It transitions from Blue (#3B82F6) to Purple (#7C3AED).
- **Surface Strategy:** The background uses a near-black (#0E0E12). Interactive surfaces and cards use a slightly lighter charcoal (#1A1A22) to create a clear visual hierarchy.
- **Typography Contrast:** Primary text is off-white to prevent "halatting" (glow effect on dark backgrounds), while secondary body text is a muted gray (#A0A0AC) to ensure a comfortable reading experience during batch processing.

## Typography

This design system uses a multi-font approach to balance personality with technical clarity.

- **Headlines (Sora):** A geometric sans-serif that feels futuristic and bold. Used for page titles and large action cards.
- **Body (Inter):** A highly legible workhorse font used for descriptions, settings, and general UI labels.
- **Data (JetBrains Mono):** Used for file sizes (KB/MB), dimensions (px), and technical metadata. The monospaced nature helps users compare numbers easily during compression.

**Hierarchy Rules:**
- Use **Display-LG** for Home screen greetings and main category headers.
- Use **Headline-SM** for card titles and modal headers.
- **Label-Caps** should be used for technical specs (e.g., "ORIGINAL SIZE") to differentiate them from instructional text.

## Layout & Spacing

The layout follows a **Fluid Grid** model optimized for Android’s diverse screen sizes. 

- **Grid:** A 4-column grid for mobile and 8-column for tablets.
- **Rhythm:** All spacing must be a multiple of 4px. 16px is the standard container padding (`lg`).
- **Touch Targets:** All interactive elements (chips, buttons, crop handles) must maintain a minimum 48x48dp hit area.
- **Home Layout:** Features a 2x2 grid of action cards for rapid access, followed by a vertically scrolling list for recent activity and settings.

## Elevation & Depth

Hierarchy is established through **Tonal Layering and Soft Glows**. 

- **Level 0 (Background):** #0E0E12. Base layer for all screens.
- **Level 1 (Cards/Surfaces):** #1A1A22. Used for content containers. Instead of black shadows, use a 1px inner border of #FFFFFF at 5% opacity to define edges.
- **Level 2 (Active/Floating):** Use a "Soft Glow" effect. This is a drop shadow with the color of the primary gradient (Purple) at very low opacity (10-15%) and a large blur radius (20dp+).
- **Overlays (Glassmorphism):** For bottom sheets and image picker overlays, use #1A1A22 with 80% opacity and a 20px background blur (BackdropFilter). This maintains context of the screen below while providing focus.

## Shapes

The shape language is generous and modern, moving away from sharp edges to feel more approachable.

- **Standard Cards:** 16dp radius.
- **Bottom Sheets:** 24dp top-only radius to create a "drawer" appearance.
- **Pills/Chips:** 100dp (fully rounded) for status indicators, format toggles, and aspect-ratio selectors.
- **Image Previews:** Always clipped to 12dp or 16dp to match the container, never sharp-edged.

## Components

### Buttons
- **Primary CTA:** Gradient background (Purple to Pink), Bold Sora typography, white text. Large 56dp height for main actions.
- **Secondary/Ghost:** Outline style with 1px #A0A0AC border or subtle surface-tint background.
- **Icon Buttons:** Circular 48dp containers with centered icons, used for "close" or "delete" actions in batch lists.

### Cards (Action Cards)
- Large 2x2 grid cards on Home.
- Icon on top-left, Headline-SM title, and Caption-level description.
- Interactive state: A subtle scale-down (0.98) on press and a primary-color glow elevation.

### Progress Indicators
- **Progress Rings:** Used for batch processing. Use the Secondary Gradient (Blue to Purple) for the stroke. 
- **Progress Bars:** Thin 4px height, gradient filled, placed at the top of the "Compressing" screen or within a list item.

### Input Fields & Controls
- **Sliders:** Custom thick track with a prominent thumb. Track background #1A1A22, active fill using the Primary Gradient.
- **Segmented Control:** Used for JPG/PNG toggles. Pill-shaped container with a sliding background highlight for the active state.

### Bottom Sheets
- Use for "Share" intent flows. 
- Translucent glassmorphism effect.
- Includes a drag handle (40x4px, rounded, #A0A0AC) at the top center.

### Ad Units
- **Banner Ads:** Placed at the very bottom of the Home screen. Must have a "Sponsored" label in Label-Caps and a subtle divider separating it from the app content.