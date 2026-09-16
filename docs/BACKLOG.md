# Backlog

This document protects the current milestone. Do not start an item merely because it appears here. Promote work only after it supports the roadmap and has a defined accessibility and validation plan.

## High priority

- [ ] Implement complete launcher-state restoration: tab, focused item, scroll position, and relevant expanded state.
- [ ] Verify and refine all focus-restoration modes with blind users; remove or defer modes that cannot be described honestly.
- [ ] Add focused regression coverage for persisted focus preference and restoration fallbacks where practical.
- [ ] Decide the preferred Back path from All Apps after manual TalkBack testing.

## Medium priority

- [ ] Search designed for D-pad and TalkBack use.
- [ ] Recently used apps with clear ordering and concise announcements.
- [ ] Hidden-app management with discoverable recovery.

- [ ] Navigation sounds, only if user research shows they improve orientation without masking TalkBack.
- [ ] Voice search, custom layouts, and plugin support with an accessibility design review.

## Low priority

- [ ] Themes and animations that preserve focus, contrast, reduced-motion needs, and speech stability.
- [ ] Additional widgets and optional visual customization.

## Intake template

```markdown
## [Feature or problem]

- User value:
- Accessibility impact:
- D-pad/focus behavior:
- Screen-reader semantics and speech:
- Performance or lifecycle risk:
- Dependencies:
- Acceptance checks:
- Decision needed before work:
```
