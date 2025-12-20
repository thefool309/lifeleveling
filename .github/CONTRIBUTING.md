# Contributing

Thank you for your interest in contributing to this project.

Forks are welcome. **Pull requests from forks will not be accepted until at least February 2026**, and only once we have a clear way for outside contributors to reliably test the application. If you want to build on this project in the meantime, you are encouraged to fork it and modify it to use your own Firebase project.

For **major changes**, please open an issue first to discuss what you would like to change before doing significant work.

Please make sure to update tests as appropriate. We have settled on a **hybrid testing philosophy**, somewhere between waterfall and agile. Some tests may be written as we go, but we are not aiming for high code coverage initially. A later phase will focus on more rigorous testing, improved error handling, and refining the overall testing process.

---

## Code Style & Conventions

- Please follow **Kotlin naming and code conventions**.
- If you notice naming or convention mistakes, by anyone, including maintainers, please address them in an issue.
- We are all learning, especially when coming from languages with different conventions, and corrections are welcome.

---

## Documentation

- **All new or non-trivial code should include appropriate documentation.**
- Documentation may take the form of:
    - KDoc comments
    - Inline comments
    - README or other documentation updates, depending on the change
- Code should be understandable to someone unfamiliar with that part of the system without requiring out-of-band explanations (DMs, voice chat, etc.).

### KDoc Requirements

- Please write **KDoc comments on classes and functions** you implement.
- These comments are automatically included in generated documentation when merged into `dev`.
- If you are overriding an inherited class:
    - You only need to document the functions you override.
- If you do not implement a function, it is acceptable not to document it.
    - The documentation will still list all overridable functions.
    - If an override lacks documentation, consult the base class documentation.
    - If documentation exists but is unclear, open an issue for clarification; we will update the documentation accordingly.

If you encounter unclear or undocumented code while working in an area, please improve the documentation or request clarification as part of your change when possible.

---

## Pull Requests

When pull requests are enabled, they should include:

- A brief description of what changed and why
- Any relevant documentation updates
- Notes on anything that may need follow-up or clarification

Pull requests that introduce significant behavior changes without accompanying documentation may be asked to add documentation before being merged.

---

## Communication & Review

- Questions, concerns, or requested changes should be raised via:
    - Issues
    - Pull request reviews
    - Code comments  
      rather than private messages.
- If something is unclear in a PR or commit, please ask directly.
    - Silence will be interpreted as approval.
- Likewise, if feedback is needed on a change, it is expected to be surfaced through the review process.

---

## Shared Responsibility

- Communication and documentation are shared responsibilities across the project.
- The goal is to keep the codebase maintainable, understandable, and approachable for current and future contributors.
