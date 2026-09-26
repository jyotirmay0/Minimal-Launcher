# Claude Code Prompt --- Android Minimal Alphabet Launcher Assignment

You are implementing the **Android Developer Assignment --- Minimal
Launcher / Alphabet Launcher** in Kotlin + Jetpack Compose.

## 0. Critical instruction: inspect the reference video first

Before writing or changing code, **watch and analyze the provided
reference video**:

-   Reference video: `WhatsApp Video 2026-09-24 at 12.53.45.mp4`
-   Duration is approximately **12.5 seconds**
-   Portrait reference, approximately **720 × 1610**
-   The supplied screenshot is the resting-state visual reference.

Do not treat the written assignment alone as the visual specification.
The reference video is the source of truth for:

-   spacing
-   typography
-   app-row height
-   icon size
-   alphabet positioning
-   curve/bend shape
-   finger/letter relationship
-   bubble size and placement
-   animation timing
-   spring-back behavior
-   transition between home and letter-filtered screens
-   search interaction
-   overall minimal black launcher appearance



Tech stack - kotlin , jetpack compose , room , Hilt, MVVM and clean architech  , coil 

### Reference-video observations

I inspected the video. The important visual sequence is:

1.  **Resting state**
    -   Black/dark background.
    -   Large white digital time on the upper-left/left portion of the
        content.
    -   Smaller white date directly underneath.
    -   A compact vertical list of favourite/recent apps below the
        clock.
    -   App rows contain rounded-square icons and app names.
    -   A very narrow A--Z alphabet runs vertically near the right edge.
    -   A star is above the alphabet.
    -   A small circular/dot indicator appears near the lower end of the
        alphabet.
    -   Android system status/navigation areas remain visually
        compatible with the system theme.
2.  **Alphabet interaction**
    -   When the finger touches/drags over the right-side alphabet, the
        straight alphabet deforms.
    -   The deformation is continuous rather than a simple discrete
        jump.
    -   Letters closest to the finger move significantly toward the
        center.
    -   Letters farther away move progressively less.
    -   The result looks like a smooth curve/bulge/V following the
        finger.
    -   The selected letter is visually emphasized with a circular/dark
        bubble immediately to the left of the finger/curve.
    -   The selected letter is larger than normal alphabet letters.
3.  **Letter selection**
    -   While dragging, the main content changes from the
        clock/favourites to a letter header.
    -   Example observed states include a `Q` screen and a `Z` screen.
    -   Matching installed apps are listed vertically with their actual
        launcher icons and names.
    -   The list updates as the selected letter changes.
4.  **Release**
    -   Releasing the finger returns the alphabet to a straight vertical
        line using a smooth spring-like animation.
    -   The home/clock/favourites content returns.
5.  **Search**
    -   The reference also shows the search interaction.
    -   A search field appears near the top.
    -   The keyboard opens.
    -   Search results are filtered as text is entered.
    -   Keep the search UI visually consistent with the minimal dark
        launcher.

### Important visual goal

Do **not** build a generic Android launcher UI.

Build something visually close to the supplied reference: **minimal,
black, compact, typography-driven, with the alphabet curve being the
primary interaction.**

------------------------------------------------------------------------

# 1. Assignment requirements

Implement a real Android launcher that can be selected as the device's
default Home app.

Platform:

-   Android
-   Kotlin
-   Jetpack Compose
-   Prefer modern Compose APIs.
-   Use Material 3 only where useful; do not allow default Material
    styling to make the UI look unlike the reference.

Core requirements:

1.  Home screen:
    -   Current time.
    -   Current date.
    -   5--7 favourite/home apps.
    -   Live clock updates.
    -   Minimal dark/light appearance following the system theme.
2.  A--Z bar:
    -   Vertical A--Z column pinned to the right edge.
    -   Star at the top.
    -   Small dot/indicator near the bottom.
    -   Vertically centered like the reference.
    -   Hide or visually dim letters that have no installed matching
        apps.
    -   Preserve the compact typography/spacing from the reference.
3.  Installed apps:
    -   Read all real launchable apps using `PackageManager`.
    -   Use launcher intent resolution rather than assuming package
        names.
    -   Handle Android 11+ package visibility correctly.
    -   Store:
        -   package name
        -   activity name if required
        -   label
        -   application icon
    -   Sort/group once and cache.
    -   Never query `PackageManager` on every alphabet touch event.
4.  Curve animation:
    -   Finger interaction must be smooth.
    -   Target 60 FPS.
    -   Letters near the finger move farther toward the center.
    -   Letters farther away move progressively less.
    -   The curve must follow the finger in real time.
    -   Avoid unnecessary recompositions/work during touch movement.
5.  Letter bubble:
    -   Show the selected letter enlarged.
    -   Put it in a circular bubble immediately to the left/near the
        finger.
    -   Follow the selected position during the drag.
    -   Make the bubble visually similar to the reference video.
6.  Filtered app list:
    -   During alphabet dragging:
        -   hide clock/favourites
        -   show selected letter as header
        -   show every installed launchable app whose name starts with
            that letter
    -   Case-insensitive matching.
    -   Sort alphabetically.
    -   Use actual application icons.
    -   Update immediately as the selected letter changes.
7.  Empty letters:
    -   Show a clear `No apps` state.
    -   Do not leave an apparently broken blank screen.
    -   Prefer hiding/dimming empty alphabet letters so the user can
        understand which letters contain apps.
8.  Release:
    -   On finger-up:
        -   animate alphabet back to its straight resting position
        -   remove selected-letter bubble
        -   return to clock/favourites
    -   Use spring physics.
    -   A small amount of overshoot/settling is desirable, matching the
        reference.
9.  Launch:
    -   Tapping an app opens its launcher activity.
    -   Handle activities that cannot be launched gracefully.
10. Performance:

-   Load/cache app metadata once.
-   Do not perform PackageManager queries during drag.
-   Avoid expensive icon loading during pointer movement.
-   Prefer stable immutable data structures/state where appropriate.

------------------------------------------------------------------------

# 2. Default launcher registration

The app must be capable of acting as the Home launcher.

Configure the main launcher activity with the appropriate intent
filters, including:

-   `ACTION_MAIN`
-   `CATEGORY_HOME`
-   `CATEGORY_DEFAULT`

Verify that the application can appear in Android's Home/default-app
selection UI.

Do not fake launcher behavior. It should behave as an actual Home
application.

Also consider:

-   configuration changes
-   process recreation
-   returning from another app
-   pressing Home
-   Android back behavior
-   app launch failures

------------------------------------------------------------------------

# 3. PackageManager / Android 11+ package visibility

Implement this correctly.

Use the appropriate `<queries>` configuration where needed for
discovering launchable applications on Android 11+.

Use the launcher intent:

``` kotlin
Intent(Intent.ACTION_MAIN).apply {
    addCategory(Intent.CATEGORY_LAUNCHER)
}
```

Resolve/query launchable activities appropriately.

Do not blindly request broad package visibility if a narrower
launcher-query declaration is sufficient.

Make the implementation compatible with modern Android versions.

------------------------------------------------------------------------

# 4. Architecture

Use a clean, maintainable architecture.
MVVM and clean architech ture 




Recommended pattern:

-   Repository owns installed-app loading.
-   ViewModel owns launcher state.
-   Composables render state.
-   Alphabet touch/geometry logic should be isolated and testable.
-   Persistent favourites should not be mixed into the rendering code.

Do not over-engineer the project.

------------------------------------------------------------------------

# 5. Data model

Create a stable app model similar to:

``` kotlin
data class LauncherApp(
    val packageName: String,
    val activityName: String?,
    val label: String,
    val icon: Drawable
)
```

If storing `Drawable` in ViewModel state causes problems, use an
appropriate Android-safe representation/caching approach.

Important:

-   App labels must be normalized for grouping.
-   Grouping must be case-insensitive.
-   Use the first alphabetic character of the app name.
-   Decide and document what happens for labels beginning with
    digits/symbols.
-   Do not query PackageManager repeatedly just to determine a letter
    group.

------------------------------------------------------------------------

# 6. Alphabet grouping

Create a pure/testable function.

Example conceptual API:

``` kotlin
fun groupAppsByLetter(
    apps: List<LauncherApp>
): Map<Char, List<LauncherApp>>
```

Requirements:

-   A--Z only.
-   Case-insensitive.
-   Sort apps within each group alphabetically.
-   Empty groups should still be representable internally.
-   UI can hide/dim empty groups.

Also create a pure/testable function mapping touch position to selected
letter.

For example:

``` kotlin
fun letterForTouchY(
    y: Float,
    top: Float,
    bottom: Float,
    letterCount: Int
): Char
```

Use the actual measured alphabet bounds rather than hardcoded screen
coordinates.

------------------------------------------------------------------------

# 7. Alphabet curve mathematics

This is the most important interaction.

Do not implement the curve as 26 independent arbitrary offsets.

Use a smooth mathematical displacement based on distance from the
finger.

A good starting model is a Gaussian/falloff function:

``` text
distance = abs(letterY - fingerY)

influence = exp(-(distance * distance) / (2 * sigma * sigma))

offsetX = maxOffset * influence
```

Then tune `sigma` and `maxOffset` by comparing directly with the
reference video.

Requirements:

-   selected letter has maximum displacement
-   nearby letters have strong displacement
-   distant letters approach zero displacement
-   curve remains smooth
-   no visible discontinuities
-   dragging continuously updates fingerY
-   use density-independent values where appropriate
-   adapt to different screen sizes

You may experiment with other smooth falloff functions if they visually
match the reference better.

### Important

The curve should be based on the **actual rendered letter center
positions**, not an assumed fixed screen coordinate.

Use `onSizeChanged`, `LayoutCoordinates`, or another robust measurement
mechanism where necessary.

------------------------------------------------------------------------

# 8. Touch handling

The alphabet must support:

-   ACTION_DOWN / pointer down
-   continuous drag
-   pointer move
-   pointer up
-   cancellation

In Compose, use an appropriate pointer input mechanism such as:

``` kotlin
Modifier.pointerInput(...)
```

or another suitable modern Compose gesture API.

Requirements:

-   Touching the alphabet selects a letter immediately.
-   Dragging changes the selected letter continuously.
-   The selected letter changes based on actual Y position.
-   Do not require the finger to hit the exact glyph.
-   Give the alphabet a reasonable touch target while keeping the visual
    column narrow.
-   The touch region can be wider than the visible letters.

The visible alphabet can remain near the right edge while the
interactive region extends slightly inward.

------------------------------------------------------------------------

# 9. Haptic feedback

Every time the selected letter changes:

-   trigger one light haptic tick
-   do not continuously vibrate while the finger remains on the same
    letter
-   do not trigger repeated haptics from recomposition

Track the previous selected letter and only perform haptic feedback on
an actual letter transition.

Use the appropriate Android/Compose haptic API.

Keep it subtle.

------------------------------------------------------------------------

# 10. Spring animation

When the user releases the alphabet:

-   selected letter state should clear
-   all letter offsets should animate back toward zero
-   curve should overshoot slightly and settle if that matches the
    reference

Use spring-based animation rather than a simple linear/tween animation.

Example direction:

``` kotlin
spring(
    dampingRatio = ...,
    stiffness = ...
)
```

Tune the values visually.

Do not blindly use default spring parameters.

The release should feel like the reference video.

------------------------------------------------------------------------

# 11. Home screen

Resting state should resemble the supplied screenshot/video.

Structure:

``` text
[status/system area]

12:51
Thu 24 Sept

[App icon] WhatsApp
[App icon] Chrome
[App icon] Camera
[App icon] ChatGPT
[App icon] CRED
[App icon] Calculator
[App icon] Gmail

                                      ☆
                                      A
                                      B
                                      C
                                      D
                                      ...
                                      Z
                                      •
```

The exact favourite apps must come from the user's selected favourites
after persistence is implemented.

For first launch, use a sensible fallback such as the first 5--7
launchable apps or a small default list.

Clock/date:

-   use the device's locale/time zone
-   update live
-   use Compose state appropriately
-   avoid an unnecessarily expensive timer loop
-   date formatting should respect the system locale

------------------------------------------------------------------------

# 12. Favourite apps

Long-press an app to toggle it as a favourite.

Requirements:

-   long-press adds/removes from home list
-   persist across app restarts
-   avoid storing non-serializable objects
-   persist package/activity identifiers
-   when the app is uninstalled, remove it from favourites automatically
-   if an app becomes unavailable, ignore/remove the stale favourite
-   show 5--7 favourites on the home screen
-   make the behavior obvious but visually minimal

Use DataStore or another lightweight persistence mechanism.

Do not use a database unless there is a real reason.

------------------------------------------------------------------------

# 13. Live package updates

The launcher must automatically refresh when apps are:

-   installed
-   uninstalled
-   replaced/updated

Do not require restarting the launcher.

Use Android package-change broadcasts appropriately.

Important:

-   refresh the cached app list on package changes
-   update grouping
-   update visible filtered results if necessary
-   remove stale favourites
-   avoid excessive refreshes if multiple package events arrive together

Do not run the full PackageManager query on every touch event.

------------------------------------------------------------------------

# 14. Search

Implement swipe-up search from the home screen.

Behavior:

1.  User swipes upward on the home content.
2.  Search UI opens.
3.  Search field receives focus.
4.  Keyboard opens.
5.  User types.
6.  Apps filter live by app name.
7.  Tapping an app launches it.
8.  Back closes the keyboard/search first, then returns to home as
    appropriate.

The reference video shows a compact dark search UI.

Keep it visually consistent with the launcher.

Search matching should be:

-   case-insensitive
-   preferably substring matching
-   sorted consistently

For example:

``` text
Search apps

Toastr
Recently installed
```

Do not make search a separate generic Material screen that visually
conflicts with the launcher.

------------------------------------------------------------------------

# 15. System theme

Support:

-   light theme
-   dark theme
-   follow system setting

The supplied reference is dark, so make the dark version closely match
the reference.

Do not hard-code black/white everywhere.

Define theme tokens for:

-   background
-   primary text
-   secondary text
-   alphabet text
-   bubble background
-   bubble text
-   divider/indicator if used

However, do not introduce excessive visual decoration.

The launcher should remain minimal.

------------------------------------------------------------------------

# 16. App icons

Use real installed app icons.

Requirements:

-   preserve reasonable aspect ratio
-   use rounded/square presentation consistent with the reference
-   avoid loading icons repeatedly during recomposition
-   cache where appropriate
-   make sure icon loading does not block UI/gesture performance

The alphabet drag must remain smooth even when many apps are installed.

------------------------------------------------------------------------

# 17. Performance strategy

Treat the alphabet gesture as a high-frequency interaction.

During a drag:

### Allowed

-   update finger Y
-   calculate letter displacement
-   calculate selected letter
-   update Compose state needed for rendering

### Avoid

-   PackageManager queries
-   disk I/O
-   DataStore writes
-   expensive sorting
-   icon loading
-   network calls
-   database queries
-   large object allocations on every pointer event

Precompute:

``` text
all apps
sorted apps
apps grouped by A-Z
available letters
favourite identifiers
```

The gesture layer should operate only on these cached structures.

Use stable keys for app rows.

Use `derivedStateOf` where it actually reduces unnecessary work.

Do not add `derivedStateOf` everywhere blindly.

------------------------------------------------------------------------

# 18. UI state

Create an explicit state model.

For example:

``` kotlin
sealed interface LauncherMode {
    data object Home : LauncherMode
    data class Letter(val letter: Char) : LauncherMode
    data object Search : LauncherMode
}
```

You may choose a different representation.

State should include concepts such as:

``` text
current mode
selected letter
finger Y
alphabet interaction active
app groups
favourites
search query
search visibility
```

Avoid having several unrelated mutable states that can become
inconsistent.

------------------------------------------------------------------------

# 19. Avoid black flashes / transition issues

The launcher should feel immediate.

When switching from:

``` text
Home -> letter list
```

and:

``` text
Letter list -> Home
```

avoid:

-   black intermediate screens
-   unnecessary activity recreation
-   delayed content
-   visible layout jumps

Keep the launcher in a single activity/screen where possible.

The content should swap smoothly within the same Compose hierarchy.

------------------------------------------------------------------------

# 20. Accessibility and touch targets

Although the visual alphabet is tiny, the touch target should be usable.

Implement semantics/content descriptions where appropriate.

App rows should have accessible labels.

Long-press should not prevent normal tap launching.

Do not make the visual UI large just for accessibility; keep the
reference appearance while providing a reasonable invisible/expanded
touch area.

------------------------------------------------------------------------

# 21. Testing

Write unit tests for the important pure logic.

At minimum:

### Test 1 --- grouping

Input:

``` text
Gmail
Google
Chrome
GPay
WhatsApp
```

Expected:

``` text
C -> Chrome
G -> Gmail, Google, GPay
W -> WhatsApp
```

Case-insensitive.

### Test 2 --- sorting

Verify apps in a group are alphabetically sorted.

### Test 3 --- empty letters

Verify letters without apps produce empty groups.

### Test 4 --- touch-to-letter

Given alphabet bounds and Y positions:

``` text
top
middle
bottom
```

verify the expected letter is returned.

### Test 5 --- touch clamping

Touch above the alphabet -\> first letter.

Touch below the alphabet -\> last letter.

### Test 6 --- case-insensitivity

Verify:

``` text
gmail
Gmail
GMAIL
```

all map to G.

### Test 7 --- favourite persistence logic

Test adding/removing package identifiers.

Do not write fragile screenshot tests unless they provide real value.

------------------------------------------------------------------------

# 22. Suggested Compose implementation

Use a single root layout similar to:

``` kotlin
Box(
    modifier = Modifier.fillMaxSize()
) {
    MainContent(...)

    AlphabetBar(
        ...
        modifier = Modifier.align(Alignment.CenterEnd)
    )
}
```

But adapt the actual layout to match the reference.

The alphabet should be independent of the main content so that it can
remain visually stable while the content changes.

For the alphabet:

``` kotlin
Canvas(...)
```

can be considered if it provides smoother/high-performance rendering.

However, normal Compose `Text` composables are acceptable if performance
is measured and remains smooth.

Do not use Canvas merely for complexity.

If Canvas is used, keep accessibility semantics separately.

------------------------------------------------------------------------

# 23. Important visual tuning checklist

After the first implementation, compare the app against the reference
video frame-by-frame.

Tune:

-   clock font size
-   clock baseline
-   date spacing
-   app row height
-   icon size
-   app text size
-   right-edge alphabet position
-   alphabet line spacing
-   star position
-   bottom dot position
-   alphabet touch width
-   maximum curve displacement
-   curve falloff
-   bubble diameter
-   bubble-to-finger distance
-   bubble opacity
-   spring stiffness
-   spring damping
-   release duration/settling
-   filtered-list spacing
-   header position
-   search field position
-   keyboard transition

Do not stop after getting the functionality working.

The assignment explicitly evaluates how closely the app feels to the
reference.

------------------------------------------------------------------------

# 24. Development workflow

Work in this order:

## Phase 1 --- Inspect existing project

Before editing:

-   inspect Gradle files
-   inspect package/application ID
-   inspect compile/target SDK
-   inspect Kotlin version
-   inspect Compose version
-   inspect current architecture
-   inspect existing resources
-   inspect existing manifest

Do not unnecessarily rewrite a working project.

## Phase 2 --- Build basic launcher

Implement:

-   HOME intent filter
-   black/dark root screen
-   clock/date
-   basic app list
-   alphabet

Make sure it can be selected as the default launcher.

## Phase 3 --- Installed app repository

Implement:

-   PackageManager discovery
-   Android 11+ package visibility
-   app metadata
-   icon loading
-   grouping
-   caching

## Phase 4 --- Alphabet interaction

Implement:

-   touch region
-   Y -\> letter mapping
-   curve displacement
-   selected-letter bubble
-   live filtering
-   haptic feedback

## Phase 5 --- Release animation

Implement spring-back.

Tune it against the reference video.

## Phase 6 --- Favourites

Implement long-press and persistence.

## Phase 7 --- Search

Implement swipe-up search + keyboard + filtering.

## Phase 8 --- Live package updates

Implement install/uninstall/update refresh.

## Phase 9 --- Tests

Implement pure unit tests.

## Phase 10 --- Visual/performance pass

Use the reference video and Android profiler/tools where useful.

Fix:

-   dropped frames
-   excessive recomposition
-   icon loading delays
-   touch latency
-   layout jumps
-   animation mismatch

------------------------------------------------------------------------

# 25. Claude Code working rules

You are acting as the primary engineer for this assignment.

### Before coding

1.  Inspect the repository.
2.  Inspect the current Android/Compose setup.
3.  Inspect the reference video.
4.  Make a short implementation plan.
5.  Identify any existing code that can be reused.

### While coding

-   Make small, coherent changes.
-   Keep the project compiling after logical milestones.
-   Prefer simple production-quality code.
-   Do not introduce unnecessary libraries.
-   Follow Kotlin idioms.
-   Avoid deprecated Android APIs where modern replacements exist.
-   Keep package responsibilities clear.
-   Add comments only where the implementation is non-obvious.

### Verification after changes

Run appropriate:

``` text
./gradlew test
./gradlew lint
./gradlew assembleDebug
```

Use the available Android device/emulator to verify:

-   default launcher selection
-   app discovery
-   app launching
-   alphabet drag
-   haptic letter changes
-   spring release
-   empty letters
-   favourites
-   search
-   install/uninstall refresh
-   light/dark mode

If an emulator/device is available, test the gesture at different screen
sizes.

------------------------------------------------------------------------

# 26. Do not fake the required functionality

Do NOT:

-   hard-code Gmail/WhatsApp/etc. as the only installed apps
-   hard-code letter groups
-   fake PackageManager results
-   query only a handful of known packages
-   update the app list manually after installs
-   use a screenshot/video as the UI
-   simulate the alphabet curve with pre-recorded animation
-   use a static list instead of real launchable apps
-   rebuild/restart the activity whenever the selected letter changes

The assignment specifically expects a functional launcher.

------------------------------------------------------------------------

# 27. Definition of done

The implementation is complete only when all of these are true:

-   [ ] Can be selected as Android Home/default launcher.
-   [ ] Shows live time/date.
-   [ ] Shows 5--7 favourites.
-   [ ] Discovers real launchable installed apps.
-   [ ] Correctly handles Android 11+ package visibility.
-   [ ] Caches app list.
-   [ ] Groups apps A--Z.
-   [ ] A--Z bar is pinned on the right.
-   [ ] Empty letters are hidden/dimmed.
-   [ ] Alphabet responds to touch.
-   [ ] Alphabet forms a smooth curve around the finger.
-   [ ] Selected letter is enlarged in a circular bubble.
-   [ ] Letter-filtered list updates immediately.
-   [ ] Every matching installed app is displayed.
-   [ ] Empty letters show `No apps`.
-   [ ] Haptic tick occurs only when selected letter changes.
-   [ ] Release uses spring physics.
-   [ ] Alphabet returns to straight position.
-   [ ] Home content returns after release.
-   [ ] App rows launch applications.
-   [ ] Long-press toggles favourites.
-   [ ] Favourites persist across restarts.
-   [ ] App install/uninstall/update refreshes the list automatically.
-   [ ] Swipe-up opens search.
-   [ ] Search focuses the input and opens keyboard.
-   [ ] Search filters apps live.
-   [ ] Light theme follows system.
-   [ ] Dark theme follows system.
-   [ ] Unit tests cover grouping.
-   [ ] Unit tests cover touch-to-letter mapping.
-   [ ] No PackageManager queries occur during alphabet dragging.
-   [ ] No obvious gesture stutter.
-   [ ] Visual appearance is tuned against the supplied reference video.

------------------------------------------------------------------------

# 28. Final instruction to Claude

**Do not just implement the written requirements mechanically.**

First understand the reference video's interaction and visual language,
then implement the launcher so that the final result feels like the same
product.

The highest-priority experience is:

``` text
REST
  ↓
Touch A–Z
  ↓
Alphabet bends toward finger
  ↓
Selected letter bubbles up
  ↓
Matching apps appear immediately
  ↓
Finger moves
  ↓
Curve + selected letter + app list follow
  ↓
Finger releases
  ↓
Spring back
  ↓
Clock + favourites return
```

The launcher should feel **fast, minimal, responsive, and physically
connected to the user's finger**.

When you finish, provide:

1.  A concise summary of what was implemented.
2.  Files changed/created.
3.  Any important architectural decisions.
4.  Tests/build commands run and their results.
5.  Any remaining limitations.
6.  A short manual QA checklist for testing the launcher on a real
    Android device.
