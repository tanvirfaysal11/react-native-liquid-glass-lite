# Changelog

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
