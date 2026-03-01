# Branching Strategy (GitFlow-Lite)

This repository uses a lightweight GitFlow model to improve rollback safety, merge quality, and update cadence.

## Branch Roles

- `main`: production-ready code only. Every commit here must be releasable.
- `develop`: integration branch for completed features before release.
- `feature/<ticket>-<short-name>`: short-lived feature branches from `develop`.
- `release/<version>`: release hardening branch from `develop` (bugfix, version bump, docs only).
- `hotfix/<version>`: urgent production fixes from `main`.

## Why This Helps

- Rollback: each release is tagged on `main` (`vX.Y.Z`), so rollback is `git checkout <tag>` or revert from known release points.
- Merge control: features merge to `develop` first, reducing risk on `main`.
- Update flow: release preparation is isolated in `release/*`, so ongoing feature work can continue.

## Standard Flows

## 1) Feature Development

```bash
git checkout develop
git pull
git checkout -b feature/123-dicom-mac-path
# coding...
git add .
git commit -m "feat: support configurable output path on mac"
git push -u origin feature/123-dicom-mac-path
```

Then open PR: `feature/*` -> `develop`.

## 2) Release

```bash
git checkout develop
git pull
git checkout -b release/1.1.0
# only release fixes/version/docs
git push -u origin release/1.1.0
```

After verification:

1. PR `release/1.1.0` -> `main` (merge commit, no squash).
2. Tag release on `main`: `v1.1.0`.
3. PR `release/1.1.0` -> `develop` (back-merge release fixes).

## 3) Hotfix

```bash
git checkout main
git pull
git checkout -b hotfix/1.1.1
# fix production issue
git add .
git commit -m "fix: resolve prod image conversion failure"
git push -u origin hotfix/1.1.1
```

After verification:

1. PR `hotfix/1.1.1` -> `main`.
2. Tag on `main`: `v1.1.1`.
3. PR `hotfix/1.1.1` -> `develop`.

## Merge Rules

- To `main`: use merge commit (keep release/hotfix context).
- To `develop`: prefer squash merge for feature branches (clean history).
- Keep PRs small and single-purpose.
- Require CI and at least one review before merging.

## Commit/Tag Conventions

- Commit prefixes: `feat:`, `fix:`, `refactor:`, `docs:`, `chore:`.
- Release tags: `vMAJOR.MINOR.PATCH` (example `v1.2.0`).

## Rollback Playbook

If a release fails:

1. Identify last good tag on `main` (for example `v1.1.0`).
2. Revert bad merge commit, or redeploy last good tag.
3. Create follow-up `hotfix/*` and merge back to both `main` and `develop`.
