# Minimal Alphabet Launcher

> **Honest Developer Note & AI Disclosure:**  
> This project was developed fully agentically using **Claude Code** and **Antigravity (Google DeepMind)**. Due to experiencing severe stomach pain and being under continuous doctor consultation since September 24th, 2026, I leveraged autonomous agentic workflows to build, architect, and test this launcher application in approximately **2 hours**.
>
> ### AI Tools & Attribution
> * **Claude Code & Antigravity IDE**:
>   * **Architecture & Scaffolding:** Designed Clean Architecture + MVVM structure (Repositories, ViewModels, Composables, and pure Domain layers).
>   * **Alphabet Curve Math:** Formulated and implemented the Gaussian distance falloff function for real-time deformation and spring physics on release.
>   * **Jetpack Compose UI:** Implemented edge-to-edge layout, live clock updates, swipe-up search, and letter bubble indicator.
>   * **Unit Tests:** Scaffolded comprehensive unit tests covering grouping, case insensitivity, touch clamping, and curve displacement math.
>   * **Build & Manifest Debugging:** Diagnosed and resolved Android Studio default activity intent-filters and compile SDK configurations.
>
> No external tutorials or boilerplate repos were copied; all code was generated, iteratively reviewed, and tested via agentic workflows.

A high-performance, distraction-free Android launcher built with Kotlin and Jetpack Compose. Inspired by minimal typography-driven launchers, it combines an edge-to-edge dark interface, real-time live clock, pinned favourites, fast swipe-up search, and a physics-driven interactive A–Z alphabet bar with continuous Gaussian curve deformation.

---

## 1. Setup & Build Instructions

### Prerequisites
* **Android Studio**: Android Studio Ladybug (or newer) recommended.
* **JDK**: JDK 21 (bundled with Android Studio as JBR at `Android Studio/jbr`).
* **Android SDK**: Compile SDK 37, Min SDK 24 (Android 7.0+), Target SDK 36 (Android 16).

### Quick Start (Single Command / Fresh Clone)

1. **Clone the repository:**
   ```bash
   git clone <repo-url>
   cd Mini_Launcher
   ```

2. **Open in Android Studio:**
   - Open Android Studio and select **Open**.
   - Navigate to the `Mini_Launcher` root directory.
   - Android Studio will perform a single Gradle sync automatically.

3. **Run Unit Tests:**
   ```bash
   ./gradlew test
   ```

4. **Build Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```
   The APK will be generated at:
   `app/build/outputs/apk/debug/app-debug.apk`

5. **Install on Device/Emulator:**
   ```bash
   ./gradlew installDebug
   ```
   Or click **Run (▶)** in Android Studio.

---

## 2. How the Curve Animation Was Built

The right-aligned vertical A–Z alphabet bar deforms smoothly around the user's touch rather than jumping discretely between fixed states. Here is the technical breakdown:

### A. Mathematical Model (Gaussian Falloff)
Each letter's horizontal displacement towards the center is calculated using a continuous Gaussian distance falloff function:

$$\text{distance} = |y_{\text{letterCenter}} - y_{\text{finger}}|$$

$$\text{influence} = \exp\left(-\frac{\text{distance}^2}{2\sigma^2}\right)$$

$$\text{displacement} = \text{maxOffset} \cdot \text{influence}$$

* **`maxOffset`** ($48\text{ dp}$): The peak horizontal inward displacement of the letter directly beneath the finger.
* **$\sigma$** ($60\text{ dp}$): The standard deviation controlling the width of the bulge. Letters farther away approach zero displacement asymptotically, producing a continuous, organic curve without sharp angles or discontinuities.

### B. Dynamic Coordinate Measurement
Instead of hardcoding screen heights or assuming uniform letter spacing:
* Each letter records its actual rendered vertical center in parent coordinates using Compose's `onGloballyPositioned`.
* The touch position in parent space is mapped against the actual measured bounds (`alphabetTop` to `alphabetBottom`) through `AlphabetTouchMapper.letterForTouchY`, adapting automatically to different screen sizes, densities, and display scalings.

### C. Spring-Back Release Physics
When the finger lifts:
* `AlphabetBar` captures and preserves `lastActiveFingerY` during the release transition.
* An `Animatable(1f)` spring executes towards `0f` with customized physical constants:
  * `dampingRatio = 0.65f` (produces a subtle, realistic elastic bounce)
  * `stiffness = 380f` (quick, responsive return)
* The curve displacement multiplies against `springProgress.value`, physically springing the curve back to a straight line with subtle overshoot before settling.
* The selected-letter circular bubble floats next to the selected letter and dismisses on release.

### D. Haptic Boundary Feedback
Haptic feedback is triggered using `LocalView.current.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)` exclusively when the selected character changes. It avoids firing during steady-state drag or recomposition cycles.

---

## 3. List of Libraries Used

| Library | Version | Why It Was Used |
| :--- | :--- | :--- |
| **`androidx.core:core-ktx`** | `1.19.1` | Provides idiomatic Kotlin extensions for Android framework APIs, intents, and system services. |
| **`androidx.lifecycle:lifecycle-runtime-ktx`** | `2.11.0` | Manages lifecycle-aware coroutine scopes and flows tied to Activity and ViewModel lifecycles. |
| **`androidx.lifecycle:lifecycle-viewmodel-compose`** | `2.11.0` | Integrates Android Architecture Component ViewModels into Jetpack Compose UI hierarchies. |
| **`androidx.activity:activity-compose`** | `1.13.0` | Provides `ComponentActivity.setContent()`, edge-to-edge system bar configuration, and Compose `BackHandler`. |
| **`androidx.compose:compose-bom`** | `2026.02.01` | Bill of Materials ensuring all Jetpack Compose UI libraries use compatible, synchronized versions. |
| **`androidx.compose.ui:ui`** | *(via BOM)* | Core Jetpack Compose UI framework for layout measurement, drawing, input, and pointer gestures. |
| **`androidx.compose.ui:ui-graphics`** | *(via BOM)* | Handles color palettes, graphics shaders, canvases, and hardware-accelerated drawing primitives. |
| **`androidx.compose.material3:material3`** | *(via BOM)* | Provides Material 3 design tokens, typography, dark theme color schemes, and basic surface components. |
| **`androidx.datastore:datastore-preferences`** | `1.1.4` | Asynchronous, transactional key-value persistence for saving user's favourite apps across restarts. |
| **`junit:junit`** | `4.13.2` | Standard testing framework used for unit testing pure domain logic (grouping, sorting, touch mapping, curve displacement). |
| **`androidx.test.ext:junit`** | `1.3.0` | AndroidX extensions for JUnit testing on Android platforms. |
| **`androidx.test.espresso:espresso-core`** | `3.7.0` | UI testing framework for automated Android instrumentation tests. |
| **`androidx.compose.ui:ui-tooling`** *(debug)* | *(via BOM)* | IDE support for Jetpack Compose previews and layout inspection inside Android Studio. |
| **`androidx.compose.ui:ui-test-manifest`** *(debug)* | *(via BOM)* | Manifest configuration required for Compose UI instrumentation testing. |

---

## 4. Architecture & Key Design Decisions

* **Pattern:** MVVM + Clean Architecture with Unidirectional Data Flow (`StateFlow<LauncherState>`).
* **Package Discovery & Caching:** Real launchable apps are discovered using `PackageManager.queryIntentActivities` with `<queries>` for Android 11+ visibility. Results and icons are cached in memory so no queries occur during alphabet drag.
* **Package Change Updates:** A dynamic runtime `BroadcastReceiver` listens for `ACTION_PACKAGE_ADDED`, `ACTION_PACKAGE_REMOVED`, and `ACTION_PACKAGE_REPLACED` to refresh the cached list automatically on Android 8 through 15.
* **Home Default App Registration:** Declares both `CATEGORY_HOME`/`CATEGORY_DEFAULT` (to act as the device's default launcher) and `CATEGORY_LAUNCHER` (so Android Studio can launch the activity directly as Default Activity).
* **Single Hierarchy Content Transitions:** Home, letter-filtered results, and search swap within a single Compose hierarchy to eliminate black flashes, layout jumps, and unnecessary activity recreation.
