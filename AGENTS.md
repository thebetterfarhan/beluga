
# Android TV Launcher Working Guide

This is a **blind-first Android TV launcher**. Accessibility is an architectural requirement, not a final QA pass. Every enabled feature must remain fully usable with a D-pad and the supported Android TV screen reader.

---

# Engineering Goal

Each engineering session should leave the project in a better state than it started.

When a task completes successfully:

- Continue to the next highest-priority task in the current milestone.
- Stop only when blocked by missing information, a failing prerequisite, conflicting requirements, or an explicit user instruction.
- Prefer completing related work within the same subsystem before switching context.
- Keep the repository in a releasable state whenever practical.

---

# Read First

Before changing behavior, read these files in order:

1. `docs/PROJECT_CHARTER.md`
2. `docs/ROADMAP.md`
3. `docs/CURRENT_MILESTONE.md`
4. `docs/DECISIONS.md`
5. `docs/TALKBACK_TEST_PLAN.md` whenever accessibility, focus, spoken feedback, or navigation may change.

Use `docs/BACKLOG.md` only for future work. Do not pull work into the current milestone without an explicit decision.

---

# Working Rules

- Start with targeted searches and only the relevant source files and tests.
- Preserve unrelated user changes.
- Keep the smallest safe change that solves the problem.
- Do not change package identity, SDK levels, signing configuration, launcher registration, release behavior, or project structure unless explicitly requested.
- Measure suspected performance problems before optimizing.
- Avoid speculative optimization.
- Prefer readable, maintainable code over clever implementations unless profiling demonstrates a measurable benefit.
- Leave the repository building successfully whenever practical.

---

# Accessibility Rules

Accessibility always takes priority over visual polish.

Priority order:

1. Prevent inaccessible behavior.
2. Preserve predictable D-pad focus.
3. Preserve predictable accessibility focus.
4. Preserve meaningful spoken feedback.
5. Improve responsiveness.
6. Improve visual polish.

Rules:

- Keep D-pad focus and accessibility focus conceptually separate while ensuring both follow a predictable order.
- Every enabled control must expose one useful name, one role, and one meaningful state.
- Decorative content must remain silent.
- Never introduce duplicate announcements.
- Never introduce duplicate actionable semantics.
- Opening a popup or screen must move focus to the most useful element.
- Closing a popup or screen must restore focus predictably.
- Clock updates, recomposition, and data refreshes must never steal focus.
- When multiple technically correct implementations exist, choose the one that reduces future accessibility complexity.

---

# Performance

- Measure before optimizing.
- Prefer stable keys.
- Prefer scoped state updates.
- Keep diagnostics debug-only.
- Do not optimize based on assumptions.

---

# Testing

Testing priority:

1. Build successfully.
2. Run relevant automated tests.
3. Validate on a physical Android TV.
4. Validate with TalkBack and the physical remote.

Accessibility work is **not complete** until physical TalkBack validation succeeds.

When reporting results:

- State exactly which checks were executed.
- Do not imply accessibility validation from a successful build.
- A build, lint, or `NO-SOURCE` test task does not prove accessibility.

---

# Collaboration

Use one agent by default.

Only use multiple agents when work naturally separates into independent tasks.

Examples:

- implementation + documentation
- investigation + implementation
- implementation + testing

Rules:

- Only one helper may modify source code.
- Review helper output before accepting changes.
- Avoid multiple agents investigating the same files.
- Delegate only bounded tasks with:
  - paths
  - constraints
  - completion criteria

---

# Engineering Decisions

Make reasonable engineering decisions without requesting confirmation.

Only ask the user when:

- requirements conflict
- user intent is ambiguous
- architecture changes significantly
- destructive actions or data loss are possible

Otherwise continue implementation.

---

# Documentation

Update documentation only when:

- user-visible behavior changed
- architecture changed
- milestone progress changed
- accessibility behavior changed
- a durable engineering decision was made

Avoid documentation-only edits unless requested.

After a meaningful engineering session:

- Add a concise dated entry to `docs/POLISH_LOG.md`.
- Update `docs/CURRENT_MILESTONE.md` with verified progress, blockers, and the next concrete action.
- Update `docs/ROADMAP.md` only if roadmap status changes.
- Record durable architectural or accessibility decisions in `docs/DECISIONS.md`.

---

# Milestone Completion

When the current milestone is finished:

1. Verify every success criterion.
2. Complete all required testing.
3. Update milestone documentation.
4. Begin the next roadmap milestone automatically.
5. Report the milestone transition.

Do not stop simply because the current task completed.

---

# Repository Quality

Keep the project releasable whenever practical.

Avoid leaving behind:

- broken builds
- failing tests
- incomplete refactors
- unreachable code
- temporary debugging artifacts
- partially implemented accessibility behavior

Leave the repository cleaner than you found it.