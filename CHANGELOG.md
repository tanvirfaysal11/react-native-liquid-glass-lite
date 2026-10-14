# Changelog

## 1.1.1 (2026-10-14)

### Bug fixes
- **Fabric crash on iOS**: Removed a shadowing `Props::Shared _props` ivar from all 5 Fabric view components (`ReactNativeBlurView`, `ReactNativeVibrancyView`, `ReactNativeLiquidGlassView`, `ReactNativeProgressiveBlurView`, `ReactNativeLiquidGlassContainer`). On React Native 0.87 the shadowing ivar left the superclass `_props` as the default `ViewProps`, tripping a native `NSAssert` in `RCTViewComponentView updateProps:oldProps:` on the first prop update. The views now use the inherited `_props`.
- **Header imports**: Fixed `#import "RCTBridge.h"` → `#import <React/RCTBridge.h>` in `ReactNativeLiquidGlassViewManager.h` and `ReactNativeLiquidGlassContainerManager.h`.
- **BlurView reduce-transparency fallback**: `reducedTransparencyFallbackColor` was accepted but never applied — the blur still rendered when the user had "Reduce Transparency" enabled. `AdvancedBlurView` now hides the blur and shows the fallback color, matching the behavior of `ProgressiveBlurView` / `VibrancyView` / `LiquidGlassView`.

## 1.1.0 (2026-10-13)

### Web support
- Every component now ships a `.web` implementation (CSS `backdrop-filter`) so `BlurView`, `ProgressiveBlurView`, `LiquidGlassView`, `VibrancyView`, `BlurSwitch`, and `LiquidGlassContainer` render on react-native-web with no extra setup. Progressive blur stacks masked backdrop-filter layers to reproduce a true blur-radius ramp; browsers without `backdrop-filter` degrade to the blur-type tint.

### iOS 27 / all-device compatibility
- **ProgressiveBlurView**: The private `CAFilter` `variableBlur` reflection is now crash-safe. Each key (`inputRadius`, `inputMaskImage`, `inputNormalizeEdges`) is probed before it is set, so a private-API change on any iOS release degrades to a uniform blur instead of raising an Objective-C exception. No `patch-package` needed.
- **Podspec**: Deployment target is clamped to iOS 13.0 (SwiftUI minimum) so the SwiftUI-backed views never resolve to an older, unsupported deployment target when the host app's `min_ios_version_supported` is lower.
- **LiquidGlassView / LiquidGlassContainer**: Verified against the iOS 27 SDK — `UIGlassEffect` / `UIGlassContainerEffect` remain the supported public API and work across all iPhone devices on iOS 26+.

## 1.0.0 (2026-06-03)

### Bug fixes
- **LiquidGlassView**: Alpha channel computation changed from `Math.floor` to `Math.round` for accurate opacity encoding (e.g. `glassOpacity=1.0` now correctly produces `FF` not `FE`)
- **LiquidGlassView**: `overlayColor` fallback only appended when `glassTintColor` is a hex string; `'clear'` tint no longer generates a malformed color string
- **LiquidGlassView**: `ignoreSafeArea` default corrected to `false` (was `true`); matching the native default
- **BlurView**: Removed redundant `position: 'relative'` from container style (React Native default)
- **BlurView**: `overlayStyle` only created when `overlayColor` is defined, avoiding unnecessary object allocation on every render
- **LiquidGlassContainer**: iOS version check moved to a dedicated `supportsGlassContainer` variable for readability and to avoid duplicated platform checks
- **Android ReactNativeBlurView**: Removed dead `glassTintColor`, `glassOpacity`, `viewType`, `glassType` fields that were never wired to JS props
- **Android ReactNativeProgressiveBlurView**: Fixed `centerLow`/`centerHigh` gradient stop clamping so stops are never inverted
- **Android ReactNativeBlurSwitchManager**: Replaced verbose try/catch blocks with idiomatic `runCatching`
- **Android ReactNativeBlurPackage**: Replaced mutable list with `listOf(...)` for immutability

### Improvements
- All TypeScript files: comprehensive JSDoc comments on every exported symbol
- `BlurType` Kotlin enum: added KDoc explaining the color mapping rationale
- All Kotlin files: consistent 4-space indentation, companion-object constants grouped, `logDebug`/`logError` helpers unified
- `ReactNativeBlurViewManager`: `setBlurRounds` now declares `defaultInt = 5` so the prop type matches the native default
- `ReactNativeProgressiveBlurViewManager`: `setStartOffset` now declares `defaultDouble = 0.0`
