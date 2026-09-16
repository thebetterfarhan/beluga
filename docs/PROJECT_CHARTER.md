# Project Charter

## Vision

Build a dependable Android TV launcher that blind and low-vision people can use independently from the first interaction. The launcher should feel calm, direct, and fast with a physical remote, while remaining a strong general-purpose big-screen launcher for everyone.

## Product principles

### Blind-first, not sighted-first with add-ons

- A task is complete only when a person can discover, operate, and recover from it with a D-pad and TalkBack.
- Spoken feedback must be concise, accurate, and non-duplicated. Visual polish must never replace semantic clarity.
- Focus is part of the product contract. A user must know where they are, where they will go, and how to return.
- Settings that affect orientation or focus belong in accessible pages with predictable navigation, not transient or touch-dependent dialogs.

### Reliable TV interaction

- Every enabled action is reachable by D-pad in a predictable order and has a visible focus indication.
- Moving focus must not activate or navigate. Activation happens once, on an intentional action.
- Back, popup dismissal, page return, and app return must restore a useful focus target whenever that target remains available.
- Data refreshes, clock changes, and asynchronous images must not move focus or create noisy announcements.

## Engineering principles

- Use the existing Kotlin, Jetpack Compose, Compose TV Material, Navigation 3, Koin, and SQLDelight architecture unless change is justified by a demonstrated need.
- Favor small, readable changes over broad rewrites. Preserve stable keys and isolate state updates.
- Treat accessibility semantics, D-pad focus, screen-reader focus, and lifecycle behavior as related but distinct concerns.
- Measure real responsiveness before optimizing. Keep diagnostics debug-only and out of release behavior.
- Avoid new dependencies unless their benefit is clear and durable.

## Accessibility standard

- Each actionable control exposes one meaningful accessible name, an appropriate role, and current state such as selected, enabled, or disabled.
- Decorative images and duplicate child labels are silent. A card is one actionable target unless multiple actions are intentionally exposed.
- Headings, pane changes, popups, and choices convey structure without repeated or incidental announcements.
- No enabled control requires touch input. No popup, page, or refresh may leave a D-pad or TalkBack focus trap.
- Accessibility regressions are release-blocking until their impact and resolution are understood.

## Performance goals

- Directional navigation should feel immediate; investigate a measured delay before changing code.
- Do not perform persistence, resolver work, or avoidable allocations on the main interaction path.
- Keep focus transitions and user-visible state stable while app, channel, and clock data update.
- Validate performance on a target TV device, not only on a development machine.

## Definition of done

- [ ] The implementation preserves intended D-pad order, visible focus, and restoration behavior.
- [ ] Semantics expose one useful label, role, and relevant state; decorative content is silent.
- [ ] Relevant automated build, lint, and test checks have run, with results recorded accurately.
- [ ] The physical-TV/TalkBack checks affected by the change are completed or explicitly recorded as pending.
- [ ] The milestone, polish log, and durable decisions are updated when applicable.

## Release philosophy

Do not publish because a build succeeds. A release candidate requires a clean staging build, consistent versioning, no accidental private data or local paths, and completed device accessibility validation. Public release always needs explicit approval.
