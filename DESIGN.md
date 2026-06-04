# Design System Strategy: The Electric Stage

## 1. Overview & Creative North Star
The core philosophy of this design system is **"The Electric Stage."** We are not building a utility-first booking tool; we are creating a digital venue that mirrors the anticipation of a live event. The system moves away from "template-style" layouts by embracing **The Layering Principle**—treating the interface as a series of physical, stacked surfaces that create depth and momentum.

By utilizing high-contrast typography, intentional asymmetry, and a "Spotlight" approach to color, we ensure that the user’s journey from browsing to booking feels as vibrant as the event itself. This design system rejects the "boxed-in" look of traditional web grids in favor of overlapping elements and fluid, glass-like transitions.

---

## 2. Colors & The Surface Manifesto
The palette is anchored by a sophisticated interplay between deep neutrals and a high-energy primary green.

### The Spotlight Palette
*   **Primary (`#006D3D`) & Primary Container (`#2DC275`):** Use the vibrant `primary_container` for high-action touchpoints (CTAs, live badges). The deeper `primary` is used for authoritative accents.
*   **Tonal Depth:** We utilize the full spectrum of `surface_container` tiers (`lowest` to `highest`) to guide the eye without the need for structural noise.

### The "No-Line" Rule
**Explicit Instruction:** You are prohibited from using 1px solid borders for sectioning. Boundaries must be defined solely through:
1.  **Background Color Shifts:** Use `surface_container_low` for the main canvas and `surface_container_highest` for a sidebar or featured section.
2.  **Tonal Transitions:** A section change is indicated by a shift from `surface` to `surface_container`.

### The "Glass & Gradient" Rule
To elevate the aesthetic, floating elements (like navigation bars or "Ticket Left" alerts) must use **Glassmorphism**. Apply `surface` colors at 80% opacity with a `backdrop-filter: blur(20px)`. 
*   **Signature Textures:** Use subtle linear gradients for Hero CTAs, transitioning from `primary` to `primary_container` at a 135-degree angle. This adds "soul" and prevents the UI from feeling flat.

---

## 3. Typography: Editorial Authority
We use **Inter** exclusively to create a clean, modern, and accessible hierarchy. 

*   **Display Scales (`display-lg` to `sm`):** Use these for event titles and headliners. They should be set with tight letter-spacing (-0.02em) to feel impactful and "poster-like."
*   **Headline & Title:** Used for navigation and section categorization. These provide the structure.
*   **Body & Labels:** Designed for high legibility. Use `body-md` for event descriptions and `label-md` for metadata (date, time, venue) to ensure a clear distinction between "The Story" and "The Facts."

The hierarchy is intentional: a `display-lg` headline should sit comfortably next to a `label-md` date tag, creating a high-end, editorial contrast that guides the user’s eye to the most important information first.

---

## 4. Elevation & Depth: Tonal Layering
Depth is achieved through "stacking" rather than traditional drop shadows.

*   **The Layering Principle:** Treat the UI as physical layers of fine paper. 
    *   **Level 0:** `surface` (The floor)
    *   **Level 1:** `surface_container_low` (Section backgrounds)
    *   **Level 2:** `surface_container_highest` (Cards and interactive modules)
*   **Ambient Shadows:** If an element must float (e.g., a "Buy Now" sticky bar), use an extra-diffused shadow: `box-shadow: 0 12px 40px rgba(0, 0, 0, 0.06)`. The shadow must be tinted with the `on_surface` color, never pure black.
*   **The "Ghost Border" Fallback:** If a container requires further definition for accessibility, use the `outline_variant` token at **15% opacity**. This creates a "Ghost Border" that defines the shape without interrupting the visual flow.

---

## 5. Components

### Buttons: The Kinetic Core
*   **Primary:** Fully rounded (`rounded-full`), using the signature gradient (`primary` to `primary_container`). These are the "Lead Actors" on the page.
*   **Secondary:** `surface_container_highest` background with `on_surface` text. These should feel like part of the interface until hovered.
*   **Tertiary:** No background, `primary` text. Use these for low-priority actions like "View More Details."

### Cards: The Event Passes
Cards must never have dividers. Separate the image, the title, and the metadata using vertical whitespace from the Spacing Scale (e.g., `xl` for top-level separation, `md` for internal metadata). Use `rounded-xl` for all event cards to maintain a friendly, approachable feel.

### Input Fields: Clean & Minimal
*   **Canvas:** Use `surface_container_lowest`.
*   **Active State:** A 2px "Ghost Border" using `primary` at 40% opacity. 
*   **Error:** Use `error` text and a subtle `error_container` background shift for the entire input field.

### Selection Chips
Use `rounded-full` for all chips. Unselected chips should be `surface_container_high`, while selected chips should pop using the `primary_container` background.

---

## 6. Do's and Don'ts

### Do:
*   **Do** use asymmetrical layouts (e.g., an event image that breaks the container bleed).
*   **Do** use `surface_container` tiers to create hierarchy.
*   **Do** lean into the `rounded-xl` and `rounded-full` scale to keep the energetic, friendly vibe.
*   **Do** prioritize white space over lines. If in doubt, add 16px of padding.

### Don't:
*   **Don't** use 1px solid, high-contrast borders for anything other than form accessibility.
*   **Don't** use standard "drop shadows" (0, 2, 4). They look cheap. Use large, ambient blurs.
*   **Don't** use dividers (`<hr>`) to separate list items; use background color shifts or `body-sm` metadata to create a natural break.
*   **Don't** crowd the typography. Let the `display-lg` headings breathe.

By following this system, we ensure every screen feels like an invitation to a premium experience, moving the user seamlessly from the excitement of discovery to the finality of the booking.