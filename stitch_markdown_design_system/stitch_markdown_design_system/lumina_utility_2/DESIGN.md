---
name: Lumina Utility
colors:
  surface: '#131317'
  surface-dim: '#131317'
  surface-bright: '#39393d'
  surface-container-lowest: '#0e0e12'
  surface-container-low: '#1b1b1f'
  surface-container: '#1f1f23'
  surface-container-high: '#2a292e'
  surface-container-highest: '#353439'
  on-surface: '#e4e1e7'
  on-surface-variant: '#d0c6ab'
  inverse-surface: '#e4e1e7'
  inverse-on-surface: '#303034'
  outline: '#999077'
  outline-variant: '#4d4732'
  surface-tint: '#e9c400'
  primary: '#fff6df'
  on-primary: '#3a3000'
  primary-container: '#ffd700'
  on-primary-container: '#705e00'
  inverse-primary: '#705d00'
  secondary: '#ffb4a8'
  on-secondary: '#690100'
  secondary-container: '#ff5540'
  on-secondary-container: '#5c0000'
  tertiary: '#f7f4fe'
  on-tertiary: '#303037'
  tertiary-container: '#dbd8e2'
  on-tertiary-container: '#5f5e66'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffe16d'
  primary-fixed-dim: '#e9c400'
  on-primary-fixed: '#221b00'
  on-primary-fixed-variant: '#544600'
  secondary-fixed: '#ffdad4'
  secondary-fixed-dim: '#ffb4a8'
  on-secondary-fixed: '#410000'
  on-secondary-fixed-variant: '#930100'
  tertiary-fixed: '#e4e1eb'
  tertiary-fixed-dim: '#c7c5ce'
  on-tertiary-fixed: '#1b1b22'
  on-tertiary-fixed-variant: '#46464e'
  background: '#131317'
  on-background: '#e4e1e7'
  surface-variant: '#353439'
typography:
  display-lg:
    fontFamily: Sora
    fontSize: 48px
    fontWeight: '700'
    lineHeight: '1.1'
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Sora
    fontSize: 32px
    fontWeight: '700'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Sora
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.3'
  body-lg:
    fontFamily: Sora
    fontSize: 18px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Sora
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
  label-sm:
    fontFamily: Sora
    fontSize: 12px
    fontWeight: '600'
    lineHeight: '1'
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  xs: 4px
  sm: 12px
  md: 24px
  lg: 48px
  xl: 80px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 40px
---

## Brand & Style
The design system is a high-performance utility interface tailored for technology and creative startups. It leverages a **Glassmorphic** style layered over a deep, immersive background to create a sense of focused energy and technical sophistication. 

The brand personality is authoritative yet vibrant, transitioning from a previous cool-toned palette to a high-energy **Gold-to-Crimson** spectrum. This shift evokes a "forged" or "ignition" aesthetic, signaling power, precision, and urgency. The UI maintains extreme clarity through generous whitespace and a rigorous grid, ensuring that the bold color accents guide the user's eye toward critical actions and real-time data.

## Colors
The palette is anchored by a deep charcoal-black background (#0E0E12) to ensure maximum contrast for the vibrant accent system. 

- **Primary Gradient:** A high-impact transition from Gold (#FFD700) to Crimson (#FF0000). This is used for primary actions, progress indicators, and hero brand moments.
- **Accents:** Use Gold for success states or primary highlights, and Crimson for warnings or critical errors.
- **Surfaces:** Tertiary colors should be used for card backgrounds and container strokes, utilizing low-opacity alpha values to allow background blurs to bleed through.
- **Text:** Pure white (#FFFFFF) for headlines, with reduced opacity (70-80%) for secondary body text to maintain hierarchy against the dark canvas.

## Typography
This design system utilizes **Sora** exclusively to achieve a modern, geometric, and technical feel. 

- **Headlines:** Use Bold (700) or SemiBold (600) weights with tighter letter-spacing to create a "locked-in" editorial look.
- **Body:** Maintain a regular weight (400) with generous line-height for readability against dark backgrounds.
- **Labels:** Small labels should use SemiBold or Bold weight with increased tracking to ensure legibility at small scales. 
- **Scale:** On mobile devices, cap display sizes at 32px to ensure layout integrity.

## Layout & Spacing
The layout follows an 8px rhythmic grid. On desktop, a 12-column fluid grid is preferred with 24px gutters. 

- **Desktop:** 40px outer margins. Content should be grouped in high-contrast containers.
- **Mobile:** 16px outer margins. Use a single-column layout where cards stack vertically.
- **Consistency:** Use the `md` (24px) spacing unit for standard internal padding within cards and sections to maintain a breathable, premium feel.

## Elevation & Depth
Depth is created through **Tonal Layering** and **Backdrop Blurs** rather than traditional drop shadows.

- **Level 1 (Base):** The #0E0E12 background.
- **Level 2 (Containers):** A slightly lighter surface (#1A1A20) with a 1px inner border (10% white) to define edges.
- **Level 3 (Popovers/Modals):** Use a 20px Backdrop Blur with a semi-transparent dark fill (60% opacity). 
- **Accents:** High-elevation elements (like active buttons) utilize a subtle "outer glow" using the primary Crimson color at very low opacity (15%) to simulate light emission.

## Shapes
The shape language is consistently "Rounded" to soften the aggressive color palette and technical typography.

- **Standard Elements:** 0.5rem (8px) radius for input fields and small buttons.
- **Large Containers:** 1rem (16px) radius for cards and main UI panels.
- **Feature Elements:** 1.5rem (24px) radius for hero sections or floating action buttons to denote a distinct interactive tier.

## Components
- **Buttons:** Primary buttons use the Gold-to-Red gradient with white SemiBold text. Secondary buttons use a ghost style with a 1px Gold border.
- **Inputs:** Dark backgrounds (#1A1A20) with a 1px border that transitions to Gold on focus. Use Sora SemiBold for labels.
- **Chips:** Small, rounded containers with a Crimson-tinted background (15% opacity) and bright Gold text for high-visibility status indicators.
- **Cards:** Utilize the Level 2 elevation (slight lift) with a 1px border. On hover, the border color should shift toward the Crimson primary accent.
- **Lists:** Clean, borderless rows separated by a subtle 1px divider (5% white). Active list items should have a 4px Gold vertical indicator on the left edge.
- **Progress Bars:** The track should be the tertiary color, with the indicator being a vibrant Red-to-Gold gradient moving from left to right.