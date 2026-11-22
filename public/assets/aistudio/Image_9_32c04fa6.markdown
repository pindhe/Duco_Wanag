---
name: Serene Devotion
colors:
  surface: '#f8f9ff'
  surface-dim: '#d0dbed'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e6eeff'
  surface-container-high: '#dee9fc'
  surface-container-highest: '#d9e3f6'
  on-surface: '#121c2a'
  on-surface-variant: '#404940'
  inverse-surface: '#27313f'
  inverse-on-surface: '#eaf1ff'
  outline: '#707a6f'
  outline-variant: '#bfc9bd'
  surface-tint: '#1f6c3a'
  primary: '#004c22'
  on-primary: '#ffffff'
  primary-container: '#166534'
  on-primary-container: '#93e0a2'
  inverse-primary: '#8bd79b'
  secondary: '#506353'
  on-secondary: '#ffffff'
  secondary-container: '#cfe5d0'
  on-secondary-container: '#546757'
  tertiary: '#004b1e'
  on-tertiary: '#ffffff'
  tertiary-container: '#00662b'
  on-tertiary-container: '#54e97d'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#a6f4b5'
  primary-fixed-dim: '#8bd79b'
  on-primary-fixed: '#00210b'
  on-primary-fixed-variant: '#005226'
  secondary-fixed: '#d2e8d3'
  secondary-fixed-dim: '#b6ccb8'
  on-secondary-fixed: '#0d1f12'
  on-secondary-fixed-variant: '#384b3c'
  tertiary-fixed: '#6bff8f'
  tertiary-fixed-dim: '#4ae176'
  on-tertiary-fixed: '#002109'
  on-tertiary-fixed-variant: '#005321'
  background: '#f8f9ff'
  on-background: '#121c2a'
  surface-variant: '#d9e3f6'
typography:
  display-counter:
    fontFamily: Plus Jakarta Sans
    fontSize: 72px
    fontWeight: '700'
    lineHeight: 80px
    letterSpacing: -0.04em
  display-counter-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 56px
    fontWeight: '700'
    lineHeight: 64px
    letterSpacing: -0.03em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '600'
    lineHeight: 34px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  title-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.03em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-tablet: 1.5rem
  margin: 1.25rem
  margin-tablet: 2rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
  space-xxl: 3rem
---

## Brand & Style

This design system establishes a quiet, contemplative sanctuary within a handheld screen. The target audience encompasses Muslims seeking mindfulness, focus, and spiritual discipline through daily dhikr, supplication (du'a), and tasbih tracking. The visual environment prioritizes tranquility, dignity, and frictionless focus over gamification or visual noise.

The style unites **Minimalism** with a **Modern Islamic Architectural Aesthetic**—anchored by Material Design 3 ergonomic standards. Tactile clarity, serene breathing room, rhythmic geometric framing, and organic rounded contours shape the layout. Interactions are meditative and non-jarring, mirroring the tactile calm of physical prayer beads slipping between fingertips.

## Colors

The palette is tuned around restorative natural tones reminiscent of olive groves, polished stone courtyards, and deep palm canopies.

### Light Theme
- **Primary:** Deep Emerald Green (`#166534`) serves as the dominant anchor for critical active states, active tab indicators, and progress tracks.
- **Secondary:** Soft Sage (`#D1E7D2`) functions as an organic fill for soft chip backgrounds, secondary counters, and subtle highlights.
- **Tertiary & Accents:** Vibrant Emerald (`#22C55E`) and Forest Accent (`#15803D`) mark milestones, target completions, and interactive ripples.
- **Canvas & Surfaces:** Warm Off-white (`#F8FAF7`) provides a warm, glare-free background layer, while pristine white cards (`#FFFFFF`) establish structural elevations.
- **Typography & Neutral:** Dark Charcoal (`#1F2937`) provides high-contrast legibility for body text and primary numerical indicators without the harshness of pure black.

### Dark Theme
- **Background Surface:** Deep Forest (`#101814`) diminishes eye strain during tahajjud (night prayers) and low-light environments.
- **Card Surface:** Muted Moss Charcoal (`#1B2721`) lifts content gently above the canvas.
- **Primary Interactive:** Radiant Mint (`#34D399`) delivers a luminous focal point for primary counts and progress dials.
- **Borders & Dividers:** Subtle Dark Emerald (`#1F382B`) frames cards and pills with structural discipline.
- **Text:** Crisp White (`#F9FAFB`) for primary titles and counts; Muted Slate (`#9CA3AF`) for secondary transliterations and metadata.

## Typography

The typographic hierarchy combines contemporary Latin UI clarity with fluid Arabic calligraphic rhythm. 

- **Primary Latin & Numerals:** Plus Jakarta Sans provides friendly geometry, deep legibility at compact scales, and modern balance for Somali and English copy.
- **Arabic Script Pairing:** For all Quranic verses, Adhkar, and Dhikr phrases, render text using an authentic Naskh or traditional script (such as Amiri or Scheherazade New) with generous line heights (`1.8` to `2.2`) to prevent diacritic clashing (Tashkeel).
- **Tabular Figures:** All counter values must use `font-variant-numeric: tabular-nums` to maintain horizontal stability during rapid tapping.

## Layout & Spacing

The layout adheres to a 4-column fluid mobile grid scaling to an 8-column layout on foldable devices and tablets, locked to an 8dp baseline grid rhythm.

- **Thumb Zone Ergonomics:** Counter tapping targets and primary manipulation surfaces reside entirely within the lower two-thirds of the viewport. Secondary controls, meta-information, and verse details sit at the top.
- **Touch Boundaries:** Critical interactive elements (such as the Tasbih counter surface or quick-increment targets) mandate a minimum touch target size of 64dp × 64dp, well beyond baseline mobile ergonomics, preventing mis-taps during closed-eye or meditative recitation.
- **Padding Discipline:** Cards preserve a spacious inner padding of `space-lg` (24px) to ensure text does not feel constrained.

## Elevation & Depth

Visual hierarchy uses tonal surface layering complemented by ambient, tinted soft shadows rather than high-contrast structural dividers.

- **Level 0 (Base Canvas):** Warm Off-white (`#F8FAF7`) in light mode; Deep Forest (`#101814`) in dark mode. Flat, non-elevated.
- **Level 1 (Cards & Modules):** Pristine White (`#FFFFFF`) with an ambient diffused shadow: `0 4px 20px -2px rgba(22, 101, 52, 0.04), 0 2px 6px -1px rgba(0, 0, 0, 0.02)`. In dark mode, depth is expressed purely tonally via the Moss Charcoal (`#1B2721`) surface bordered by a 1px stroke of `rgba(52, 211, 153, 0.08)`.
- **Level 2 (Active Counter & Floating Controls):** Deep Emerald or Mint pill controls elevated by a focused glow: `0 8px 24px -4px rgba(22, 101, 52, 0.18)` (light mode) or `0 8px 24px -4px rgba(52, 211, 153, 0.25)` (dark mode).
- **Geometric Motif Underlays:** Watermark-inspired eight-pointed stars (Rub el Hizb) or arabesque lattice lines are rendered with an opacity of 0.03 to 0.05 on card backgrounds, grounding cards without cluttering readability.

## Shapes

The geometric approach balances modern curvature with gentle rounded silhouettes.

- **Primary Cards:** Styled with a continuous organic radius between `24px` and `32px` (`rounded-xl` to `rounded-2xl`), evoking river stones and smooth palm stones.
- **Tasbih Main Interactive Area:** A large organic pill or perfect circle (`rounded-full`) designed for thumb engagement.
- **Tabs & Filter Chips:** Fully pill-shaped (`rounded-full`), providing clean horizontal sliding lanes.
- **Internal Micro-elements:** Small icons, badges, and progress pills maintain soft rounded terminations (`8px` to `12px`).

## Components

### 1. Primary Tasbih Counter Button
- **Structure:** Expansive circular or rounded oblong button taking up the central interaction region.
- **Styling:** In light mode, Soft Sage background (`#D1E7D2`) with a subtle Deep Emerald (`#166534`) active rim. In dark mode, Moss Charcoal (`#1B2721`) with a neon mint pulse on increment.
- **Haptic & Visual Feedback:** Soft inward scale down to `0.97` on press with immediate light haptic impact. A concentric ripple ring briefly expands from touch coordinates.

### 2. Cards (Dhikr & Du'a Item)
- **Structure:** Encased in 24px-32px rounded containers. Top section hosts the Arabic text in balanced serif typography, followed by a light horizontal hairline divider, Latin transliteration, translation, and bottom metadata row (target count and current completion status).
- **Actions:** Quick bookmark toggle on the top-right corner; circular progress ring indicating current lap count (e.g., 33/33, 100/100).

### 3. Navigation & Tabs
- **Segmented Filter Pills:** Floating pill containers with a Soft Sage or Charcoal background. Active state is filled with Deep Emerald text in pristine white; inactive state is transparent with muted typography.
- **Bottom Navigation Bar:** Pill-shaped floating island elevated above the canvas, featuring subtle green tinting and icons for Dhikr, Tasbih, Du'a, and Settings.

### 4. Input Fields & Search
- **Styling:** Fully rounded pill fields with an interior background matching Soft Sage at 30% opacity. 
- **Focus State:** 1.5px border of Deep Emerald (`#166534`) without sharp drop shadows; leading icons colored in muted emerald.

### 5. Checkboxes, Radios & Switches
- **Form Factors:** Rounded circular checkboxes and switches with smooth pill geometries. 
- **Toggle Switches:** Oval track with an off-center round thumb that smoothly expands when active, shaded in Deep Emerald (light) or Neon Mint (dark).

### 6. Circular Progress Ring
- **Styling:** Non-distracting stroke track in 15% opacity primary tone; active stroke terminates in rounded caps and advances dynamically with each tap toward the target (33, 99, 1000).