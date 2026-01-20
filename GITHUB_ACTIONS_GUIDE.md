# GitHub Actions Workflows - LiteRead

## 📋 Overview

LiteRead uses 7 automated GitHub Actions workflows to ensure code quality, security, performance, and reliability. All workflows are updated to use the latest stable versions (v4) of GitHub Actions.

---

## 🔄 Workflows Summary

| Workflow | Trigger | Purpose | Status |
|----------|---------|---------|--------|
| **build.yml** | Push to main/develop, PR | Build debug/release APK, run tests | 🟢 Active |
| **build-apk-multiarch.yml** | Push to main, tags, manual | Multi-architecture APK builds | 🟢 Active |
| **release.yml** | Tag push (v*) | Create releases with unsigned APK/AAB | 🟢 Active |
| **release-signed.yml** | Tag push (v*) | Production releases with signing | 🟢 Active |
| **lint.yml** | Push to main/develop, PR | Code quality (lint, detekt, ktlint) | 🟢 Active |
| **security.yml** | Push, PR, weekly schedule | Vulnerability scanning | 🟢 Active |
| **performance.yml** | Push, PR, daily schedule | APK size & memory analysis | 🟢 Active |

---

## 📦 Workflow Details

### 1️⃣ **build.yml** - Build & Test
**Location:** `.github/workflows/build.yml`

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`

**What it does:**
- ✅ Builds debug APK (`app-debug.apk`)
- ✅ Builds unsigned release AAB (`app-release.aab`)
- ✅ Runs unit tests (./gradlew test)
- ✅ Uploads artifacts (7-day retention)

**Key Steps:**
```yaml
- Checkout code
- Setup JDK 11 (Temurin)
- Make gradlew executable
- Build Debug APK
- Build Release AAB
- Run Unit Tests
- Upload artifacts
```

**Outputs:**
- `debug-apk/` - Debug APK for testing
- `release-aab/` - Unsigned AAB
- `test-results/` - Test reports

**Status Page:** Actions → build.yml

---

### 2️⃣ **build-apk-multiarch.yml** - Multi-Architecture Builds
**Location:** `.github/workflows/build-apk-multiarch.yml`

**Triggers:**
- Push to `main` branch
- Any tag push (v*)
- Manual trigger (workflow_dispatch)

**What it does:**
- 🏗️ Builds APK for **4 architectures**:
  - `arm64-v8a` (64-bit ARM - modern phones)
  - `armeabi-v7a` (32-bit ARM - older phones)
  - `x86` (Intel tablets)
  - `x86_64` (64-bit Intel tablets)
- 🏗️ Builds universal APK (all architectures)
- 📦 Creates 5 separate artifacts

**Key Matrix Strategy:**
```yaml
abi: ['arm64-v8a', 'armeabi-v7a', 'x86', 'x86_64']
```

**Outputs:**
- `apk-arm64-v8a` - 64-bit ARM APK
- `apk-armeabi-v7a` - 32-bit ARM APK
- `apk-x86` - Intel APK
- `apk-x86_64` - 64-bit Intel APK
- `universal-apk-debug` - All architectures

**Use Case:** Play Store distribution, device compatibility testing

**Status Page:** Actions → build-apk-multiarch.yml

---

### 3️⃣ **release.yml** - Release (Unsigned)
**Location:** `.github/workflows/release.yml`

**Triggers:**
- Tag push matching `v*` (e.g., `v1.0.0`, `v1.0.1`)

**What it does:**
- 📤 Builds release APK & AAB (unsigned)
- 📝 Creates GitHub Release automatically
- 📦 Uploads APK/AAB as release assets
- 📋 Generates release notes template

**Usage:**
```bash
git tag v1.0.0
git push origin v1.0.0
# Workflow triggers automatically
```

**Release Notes Includes:**
- Version number
- Feature highlights
- Download links
- Installation instructions
- Requirements
- Changelog reference

**Status Page:** Actions → release.yml

---

### 4️⃣ **release-signed.yml** - Release (Signed)
**Location:** `.github/workflows/release-signed.yml`

**Triggers:**
- Tag push matching `v*` (e.g., `v1.0.0`)

**What it does:**
- 🔐 Builds signed release APK & AAB
- 🔑 Uses keystore from secrets
- ✅ Verifies APK signatures
- 📤 Creates GitHub Release
- 📝 Generates comprehensive release notes
- 🏆 Production-ready distribution

**Required Secrets:**
```
SIGNING_KEYSTORE_BASE64    - Base64 encoded keystore file
SIGNING_KEYSTORE_PASSWORD  - Keystore password
SIGNING_KEY_ALIAS          - Key alias name
SIGNING_KEY_PASSWORD       - Key password
GITHUB_TOKEN              - (auto-provided)
```

**Setup Instructions:**
See [SIGNING.md](SIGNING.md) for keystore configuration

**Outputs:**
- Signed APKs (all architectures)
- Signed AAB
- GitHub Release with all artifacts
- Signature verification report

**Production Use:** Google Play Store distribution

**Status Page:** Actions → release-signed.yml

---

### 5️⃣ **lint.yml** - Code Quality & Style
**Location:** `.github/workflows/lint.yml`

**Triggers:**
- Push to `main` or `develop`
- Pull requests to `main` or `develop`

**What it does:**
- 🧹 **Android Lint** - Framework warnings/errors
- 🔍 **Detekt** - Kotlin static analysis
- 📏 **KtLint** - Kotlin code style

**Jobs:**
1. **Lint Job:**
   - Run: `./gradlew lint`
   - Uploads: `lint-results*.html`
   - Fails if lint errors found

2. **Detekt Job:**
   - Run: `./gradlew detekt`
   - Uploads: `app/build/reports/detekt/`
   - Continues on error (warnings OK)

3. **KtLint Job:**
   - Run: `./gradlew ktlintCheck`
   - Checks Kotlin code formatting
   - Continues on error (warnings OK)

**Artifacts:**
- `lint-reports/` - HTML lint reports
- `detekt-reports/` - Detekt analysis

**View Results:**
1. Go to Actions
2. Select "Code Quality & Lint"
3. Click workflow run
4. Download artifacts

**Status Page:** Actions → lint.yml

---

### 6️⃣ **security.yml** - Security & Vulnerability Scanning
**Location:** `.github/workflows/security.yml`

**Triggers:**
- Push to `main` or `develop`
- Pull requests
- Weekly schedule (Sunday 00:00 UTC)

**What it does:**
- 🔒 **Dependency Check** - Maven/Gradle dependency vulnerabilities
- 🛡️ **Trivy Scanner** - Container/filesystem vulnerability scanning
- 📊 **SARIF Reports** - Integration with GitHub Security tab

**Jobs:**
1. **dependency-check:**
   - Scans: `build/reports/dependency-check/`
   - Checks: Maven Central, NVD database
   - Continues on error

2. **security-scanning:**
   - Tool: Trivy (aquasecurity)
   - Format: SARIF (Security Analysis Results Format)
   - Upload: GitHub Security tab
   - View: Security → Code scanning alerts

**Security Tab Integration:**
- Results auto-uploaded to GitHub Security tab
- View vulnerabilities in repo settings
- Track vulnerability trends
- Set security policies

**View Results:**
1. Go to repo Settings
2. Code security → Code scanning
3. View active alerts
4. Or in Actions tab

**Status Page:** Actions → security.yml

---

### 7️⃣ **performance.yml** - Performance Testing
**Location:** `.github/workflows/performance.yml`

**Triggers:**
- Push to `main` or `develop`
- Pull requests
- Daily schedule (12:00 UTC)

**What it does:**
- 📦 **APK Size Check** - Validates APK size limits
- 🔍 **APK Analysis** - Bundletool manifest inspection
- 💾 **Memory Profiling** - Lint-based memory issue detection

**Jobs:**
1. **apk-size-check:**
   - Builds: Release APK
   - Checks size against limits:
     - ✅ OK: < 10 MB
     - ⚠️ Warning: 10-12 MB
     - ❌ Fail: > 15 MB
   - Tool: Bundletool (1.15.6)
   - Uploads: APK size report

2. **memory-profiling:**
   - Runs lint analysis
   - Detects memory issues
   - Generates reports
   - Continues on error

**Size Limits:**
```
Target:   < 10 MB
Warning:  10-12 MB
Fail:     > 15 MB
```

**Outputs:**
- `apk-size-report/` - APK files and analysis
- `memory-lint-report/` - Memory issue analysis
- Console output: APK size, memory stats

**View Results:**
1. Go to Actions
2. Select "Performance Testing"
3. View workflow run output
4. Download artifacts

**Status Page:** Actions → performance.yml

---

## 🔑 Environment Variables & Secrets

### For release-signed.yml:

**Required GitHub Secrets:**
```
SIGNING_KEYSTORE_BASE64
├─ Value: Base64 encoded keystore file
├─ Size: ~2-5 KB (when base64 encoded)
└─ Generate: openssl base64 -in literead-release.keystore

SIGNING_KEYSTORE_PASSWORD
├─ Value: Your keystore password
└─ Type: Secret (never logged)

SIGNING_KEY_ALIAS
├─ Value: Your key alias (e.g., "literead-key")
└─ Type: String

SIGNING_KEY_PASSWORD
├─ Value: Your key password
└─ Type: Secret (never logged)
```

**How to Add:**
1. Go to repo Settings
2. Secrets and variables → Actions
3. Click "New repository secret"
4. Add each secret

See [SIGNING.md](SIGNING.md) for detailed setup.

---

## 🚀 How to Trigger Workflows

### 1. Automatic on Push
```bash
git push origin main
# Triggers: build.yml, lint.yml, security.yml (if schedule)
```

### 2. Automatic on Pull Request
```bash
# Create and push PR to main/develop
# Triggers: build.yml, lint.yml
```

### 3. Multi-Arch Build (Manual or Tag)
```bash
# Option A: Push to main
git push origin main
# Triggers: build-apk-multiarch.yml

# Option B: Manual trigger in UI
# Actions → build-apk-multiarch.yml → Run workflow
```

### 4. Release (Automatic on Tag)
```bash
git tag v1.0.0
git push origin v1.0.0
# Triggers: release.yml (unsigned) OR release-signed.yml (with secrets)
```

### 5. Weekly Security Scan
```
Automatic - No action needed
Runs: Every Sunday at 00:00 UTC
```

### 6. Daily Performance Test
```
Automatic - No action needed
Runs: Every day at 12:00 UTC
```

---

## 📊 Workflow Status Dashboard

**View All Workflows:**
1. Go to repo on GitHub
2. Click "Actions" tab
3. View workflow runs and status

**Common Statuses:**
- 🟢 **Success** - All checks passed
- 🟡 **In Progress** - Workflow running
- 🔴 **Failed** - One or more checks failed
- ⚪ **Cancelled** - Workflow cancelled manually

---

## 🔧 GitHub Actions Versions (Updated)

All workflows use the latest stable versions:

| Action | Current | Previous |
|--------|---------|----------|
| `actions/checkout` | v4 | v3 ✗ (deprecated) |
| `actions/setup-java` | v4 | v3 ✗ (deprecated) |
| `actions/upload-artifact` | v4 | v3 ✗ (deprecated) |
| `softprops/action-gh-release` | v1 | v1 (stable) |
| `aquasecurity/trivy-action` | master | master (stable) |
| `github/codeql-action` | v2 | v2 (stable) |

**Why Updated?**
- v3 deprecated by GitHub (April 2024)
- v4 provides better performance
- v4 has improved security
- v4 better support and maintenance

---

## ⚙️ Customization

### Change Trigger Conditions
Edit workflow triggers in YAML:
```yaml
on:
  push:
    branches: [ main, develop ]  # Add/remove branches
  pull_request:
    branches: [ main, develop ]
  schedule:
    - cron: '0 0 * * 0'  # Change schedule
```

### Modify Build Configuration
Edit gradle settings:
- `build.gradle.kts` - Build config
- `app/build.gradle.kts` - App config
- `gradle/wrapper/gradle-wrapper.properties` - Gradle version

### Adjust Size Limits
Edit `performance.yml`:
```yaml
if [ $APK_MB -gt 15 ]; then  # Change 15 to your limit
  echo "❌ APK size exceeds..."
```

---

## 📚 Documentation

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Workflow Syntax Reference](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)
- [Android CI/CD Best Practices](https://developer.android.com/studio/build)
- [Kotlin Static Analysis](https://pinterest.github.io/ktlint/)

---

## 🆘 Troubleshooting

### Workflow Fails to Start
- Check workflow YAML syntax (use [GitHub YAML validator](https://yamllint.com))
- Ensure branch name matches trigger conditions
- Check file path: `.github/workflows/name.yml`

### Upload Artifact Fails
- Ensure path exists in build output
- Check file permissions
- Verify artifact name is unique

### Build Takes Too Long
- Check network connectivity
- Gradle caching might help
- Review build configuration

### Signing Fails
- Verify secrets are correctly added
- Check keystore password is correct
- Ensure key alias exists in keystore
- See [SIGNING.md](SIGNING.md)

---

## 📝 Summary Table

```
┌─────────────────┬──────────────┬─────────────────────┬───────────────┐
│ Workflow        │ Trigger      │ Main Purpose        │ Duration      │
├─────────────────┼──────────────┼─────────────────────┼───────────────┤
│ build.yml       │ Push/PR      │ Build & test        │ ~5-10 min     │
│ build-apk-ma... │ Push/tag     │ Multi-arch builds   │ ~15-20 min    │
│ release.yml     │ Tag (v*)     │ Unsigned release    │ ~10-15 min    │
│ release-sig...  │ Tag (v*)     │ Signed release      │ ~15-20 min    │
│ lint.yml        │ Push/PR      │ Code quality        │ ~5-10 min     │
│ security.yml    │ Push/PR/wk   │ Vulnerability scan  │ ~10-15 min    │
│ performance.yml │ Push/PR/day  │ APK size check      │ ~8-12 min     │
└─────────────────┴──────────────┴─────────────────────┴───────────────┘
```

---

## ✅ All Actions Updated

- ✅ `actions/checkout@v4`
- ✅ `actions/setup-java@v4`
- ✅ `actions/upload-artifact@v4`
- ✅ All workflows tested and ready
- ✅ No deprecated versions used
- ✅ Latest stable versions only

**Status:** 🟢 All systems operational

---

**Last Updated:** January 2026  
**LiteRead Version:** 1.0.0+  
**GitHub Actions:** Latest (v4)
