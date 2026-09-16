# Accessibility requirements

These requirements apply to the current Compose launcher architecture.

- All primary controls—tabs, cards, toolbar icons, input rows, and card-menu actions—must have one useful accessible name. Decorative icons must be silent.
- Compose TV Material semantics must expose appropriate control roles and selected, enabled, and disabled states. A disabled reorder action must not activate.
- D-pad traversal must reach every enabled action in a predictable visual order, with a visible focus indicator. No control may require touch input.
- Moving focus between tabs must not navigate; activating a tab must navigate once. Selected tab state must match displayed content.
- On opening a dialog or card popup, focus moves into it. Back/dismissal restores focus to its invoking control; no focus trap may remain.
- Launching an app and returning to the launcher restores focus to the launching card when it still exists.
- List/grid refreshes, clock updates, favorite changes, and channel updates must not steal focus or create duplicate screen-reader announcements.
- App cards announce the app name once and do not expose their image and card as duplicate actionable targets.
- Any app-card option menu has a meaningful accessibility action label, so it is discoverable without knowing the long-press gesture.
- Input and app-menu actions announce a meaningful purpose and state, including whether the app is currently a favorite.
