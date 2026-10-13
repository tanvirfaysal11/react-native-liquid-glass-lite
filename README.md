# @tanvir1133/react-native-liquid-glass-lite

A modern React Native library providing **six specialized components** for advanced visual effects: `BlurView` for native blur effects, `VibrancyView` for iOS vibrancy effects, `LiquidGlassView` for cutting-edge liquid glass effects on iOS 26+ (with Android fallback to enhanced blur), `LiquidGlassContainer` for iOS 26+ glass container effects with configurable spacing, `ProgressiveBlurView` for smooth, variable blur transitions, and `BlurSwitch` for beautiful blur switch buttons on Android.

---

<div align="center">
  <p>
    <img src="https://img.shields.io/npm/v/@tanvirfaysal11/react-native-liquid-glass-lite?style=for-the-badge&color=blue" alt="npm version" />
    <img src="https://img.shields.io/npm/dm/%40tanvirfaysal11%2Freact-native-liquid-glass-lite?style=for-the-badge" alt="downloads per month" />
    <img src="https://img.shields.io/github/license/tanvirfaysal11/react-native-liquid-glass-lite?style=for-the-badge&color=orange" alt="license" />
    <img src="https://img.shields.io/github/stars/tanvirfaysal11/react-native-liquid-glass-lite?style=for-the-badge&color=yellow" alt="stars" />
  </p>

  <p>
    <img src="https://img.shields.io/badge/New%20Architecture-Ready-purple?style=for-the-badge" alt="New Architecture" />
    <img src="https://img.shields.io/badge/iOS%2026+-Liquid%20Glass-blue?style=for-the-badge" alt="Liquid Glass" />
    <img src="https://img.shields.io/badge/SwiftUI-Powered-orange?style=for-the-badge" alt="SwiftUI" />
  </p>
</div>

> 🎉 **New in v1.1.0 — Web Support.** Every component now renders on **react-native-web** using CSS `backdrop-filter` — no platform branches, no extra setup. See [Web usage](#web-support).

---

## Requirements

| Platform                  | Minimum Version                                        |
| ------------------------- | ------------------------------------------------------ |
| **iOS**                   | iOS 13.0+ (iOS 27 fully supported)                     |
| **Xcode**                 | min Xcode 16; Xcode 26.0+ for liquid glass support     |
| **React Native**          | 0.68+ (New Architecture)                               |
| **Android**               | API 24+ (Android 7.0)                                  |
| **Android Gradle Plugin** | 8.9.1+                                                 |

> ⚠️ `LiquidGlassView` requires Xcode 26.0+ and iOS 26+ for full glass effects. The component automatically falls back to enhanced blur on older versions.
>
> ℹ️ **iOS 27**: Liquid Glass is the system default. `LiquidGlassView` / `LiquidGlassContainer` use the public `UIGlassEffect` API, and `BlurView` / `VibrancyView` keep working via `UIBlurEffect` (auto-adopted by the system). No patches or private-API setup required — the package runs on every iPhone out of the box.
>
> 🌐 **Web**: every component ships a `.web` implementation using CSS `backdrop-filter`. Metro picks them up automatically on `web`; browsers without `backdrop-filter` support degrade to the blur-type tint.

---

## Installation

> ⚠️ **Android users**: This library requires Android Gradle Plugin **8.9.1 or newer**.

```bash
npm install @tanvir1133/react-native-liquid-glass-lite
# or
yarn add @tanvir1133/react-native-liquid-glass-lite
```
### iOS Setup

```bash
cd ios && pod install
```

### Android Setup

Ensure your root `android/build.gradle` has:

```gradle
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```

And your AGP version is 8.9.1+:

```gradle
classpath "com.android.tools.build:gradle:8.9.1"
```

Auto-linking handles everything else on React Native 0.60+.

---

## Features

- ✨ **Six Specialized Components**:
  - `BlurView` — dedicated blur effects with multiple blur types
  - `ProgressiveBlurView` — variable/gradient blur transitions (iOS & Android)
  - `LiquidGlassView` — iOS 26+ liquid glass effects with Android fallback
  - `LiquidGlassContainer` — iOS 26+ glass container with configurable spacing
  - `BlurSwitch` — blur-backed switch button (Android native, iOS system Switch)
  - `VibrancyView` — UIVibrancyEffect wrapper (iOS only, BlurView fallback on Android)

- 🌊 **Liquid Glass Effects** — iOS 26+ UIGlassEffect API
- 🎨 **Multiple Blur Types** — 21 blur styles including system materials
- 📱 **Cross-Platform** — iOS, Android, and web (CSS `backdrop-filter`)
- 🌐 **Web Support** — the same components render on react-native-web with a CSS blur + tint approximation, no extra setup
- ♿ **Accessibility** — automatic fallback for reduced transparency settings
- 🔧 **Full TypeScript** — separate prop types for every component
- 🚀 **New Architecture Ready** — built for Fabric / Turbo Modules
- 💡 **Performance Optimized** — hardware-accelerated rendering
- 🔄 **Smart Fallbacks** — graceful degradation from liquid glass → blur → solid color

---

## Comparison with Other Libraries

| Feature | `@tanvir1133/react-native-liquid-glass-lite-lite` | `@react-native-community/blur` | `expo-blur` |
|---|---|---|---|
| Dedicated components | ✅ | ❌ | ❌ |
| Liquid glass (iOS 26+) | ✅ | ❌ | ❌ |
| Real Android blur | ✅ | ⚠️ | ⚠️ |
| New Architecture | ✅ | ⚠️ | ✅ |
| Progressive blur | ✅ | ❌ | ❌ |
| No Expo dependency | ✅ | ✅ | ❌ |

---

## Usage

### BlurView — Standard Blur Effects

```tsx
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { BlurView } from '@tanvir1133/react-native-liquid-glass-lite';

export default function App() {
  return (
    <View style={{ flex: 1 }}>
      <BlurView
        blurType="light"
        blurAmount={20}
        style={{
          position: 'absolute',
          top: 100,
          left: 50,
          right: 50,
          height: 200,
          borderRadius: 20,
        }}
      >
        <Text>Content with blur background</Text>
      </BlurView>
    </View>
  );
}
```

#### Advanced Blur

```tsx
<BlurView
  blurType="systemMaterial"
  blurAmount={50}
  reducedTransparencyFallbackColor="#FFFFFF80"
  style={{ padding: 20, borderRadius: 15 }}
>
  <Text>Advanced blur with custom fallback</Text>
</BlurView>
```

---

### ProgressiveBlurView — Gradient Blur

> Progressive blur offset behaves differently between Android and iOS.

```tsx
import { ProgressiveBlurView } from '@tanvir1133/react-native-liquid-glass-lite';

<ProgressiveBlurView
  blurType="light"
  blurAmount={30}
  direction="blurredTopClearBottom"
  startOffset={0}
  style={{ height: 200 }}
>
  <Text>Blurred at top, clear at bottom</Text>
</ProgressiveBlurView>
```

#### Center Blur

```tsx
<ProgressiveBlurView
  blurType="regular"
  blurAmount={35}
  direction="blurredCenterClearTopAndBottom"
  startOffset={0}
  style={{ height: 220, borderRadius: 16 }}
>
  <Text>Clear at top</Text>
  <Text>Blurred at center</Text>
  <Text>Clear at bottom</Text>
</ProgressiveBlurView>
```

> **Tips:**
> - `startOffset={0}` = longest blur body; raise toward `0.3` to shorten
> - `blurAmount` controls peak intensity
> - Works on iOS and Android with matching props

#### Paywall / Locked Content Pattern

```tsx
<View style={{ position: 'relative' }}>
  <Text>Long content here…</Text>

  <ProgressiveBlurView
    blurType="light"
    blurAmount={20}
    direction="blurredBottomClearTop"
    startOffset={0.2}
    style={{ position: 'absolute', bottom: 0, left: 0, right: 0, height: 200 }}
  >
    <Text>🔒 Unlock to Read More</Text>
  </ProgressiveBlurView>
</View>
```

---

### LiquidGlassView — Liquid Glass Effects (iOS 26+)

```tsx
import { LiquidGlassView } from '@tanvir1133/react-native-liquid-glass-lite';

<LiquidGlassView
  glassType="regular"
  glassTintColor="#007AFF"
  glassOpacity={0.8}
  style={{ padding: 20, borderRadius: 20 }}
>
  <Text>Beautiful liquid glass effect</Text>
</LiquidGlassView>
```

#### Interactive Glass

```tsx
<LiquidGlassView
  glassType="regular"
  glassTintColor="#007AFF"
  glassOpacity={0.9}
  isInteractive={true}
  ignoreSafeArea={false}
  style={{ flex: 1, padding: 30 }}
>
  <Text>Interactive liquid glass that responds to touch</Text>
</LiquidGlassView>
```

> **Note:** Falls back to `BlurView` on Android and iOS < 26.

---

### LiquidGlassContainer — Glass Container (iOS 26+)

Groups multiple `LiquidGlassView` children into a single unified glass surface using iOS 26+ `UIGlassContainerEffect`.

```tsx
import { LiquidGlassContainer, LiquidGlassView } from '@tanvir1133/react-native-liquid-glass-lite';

<LiquidGlassContainer spacing={20} style={{ flex: 1 }}>
  <LiquidGlassView
    glassType="clear"
    glassTintColor="#000000"
    glassOpacity={0.3}
    style={{ width: 120, height: 120, borderRadius: 60 }}
  >
    <Text>Circle 1</Text>
  </LiquidGlassView>
  <LiquidGlassView
    glassType="clear"
    glassTintColor="#000000"
    glassOpacity={0.3}
    style={{ width: 120, height: 120, borderRadius: 60 }}
  >
    <Text>Circle 2</Text>
  </LiquidGlassView>
</LiquidGlassContainer>
```

#### Animated Glass Container

```tsx
import React, { useRef } from 'react';
import { Animated } from 'react-native';
import { LiquidGlassContainer, LiquidGlassView } from '@tanvir1133/react-native-liquid-glass-lite';

function AnimatedGlassContainer() {
  const translateX = useRef(new Animated.Value(0)).current;

  const animate = () => {
    Animated.sequence([
      Animated.timing(translateX, { toValue: -50, duration: 2000, useNativeDriver: true }),
      Animated.timing(translateX, { toValue: 30, duration: 2000, useNativeDriver: true }),
    ]).start();
  };

  return (
    <LiquidGlassContainer spacing={30} style={{ flexDirection: 'row' }}>
      <LiquidGlassView glassType="clear" style={{ width: 100, height: 100, borderRadius: 50 }}>
        <Text>Static</Text>
      </LiquidGlassView>
      <Animated.View style={{ transform: [{ translateX }] }}>
        <LiquidGlassView glassType="clear" style={{ width: 100, height: 100, borderRadius: 50 }}>
          <Text>Animated</Text>
        </LiquidGlassView>
      </Animated.View>
    </LiquidGlassContainer>
  );
}
```

> **Note:** Falls back to a regular `View` on Android and iOS < 26.

---

### VibrancyView — Vibrancy Effects (iOS)

```tsx
import { VibrancyView } from '@tanvir1133/react-native-liquid-glass-lite';

<VibrancyView blurType="light" style={{ flex: 1 }}>
  <Text style={{ color: 'white' }}>Vibrant text</Text>
</VibrancyView>
```

> Falls back to `BlurView` on Android.

---

### BlurSwitch — Blur Switch Button

```tsx
import React, { useState } from 'react';
import { View, Text } from 'react-native';
import { BlurSwitch } from '@tanvir1133/react-native-liquid-glass-lite';

function BlurSwitchExample() {
  const [isEnabled, setIsEnabled] = useState(false);

  return (
    <View style={{ flexDirection: 'row', alignItems: 'center', gap: 12 }}>
      <Text>Enable Feature</Text>
      <BlurSwitch
        value={isEnabled}
        onValueChange={setIsEnabled}
        blurAmount={20}
        trackColor={{ true: '#34C759' }}
      />
    </View>
  );
}
```

#### Disabled State

```tsx
<BlurSwitch
  value={true}
  onValueChange={() => {}}
  blurAmount={20}
  trackColor={{ true: '#10B981' }}
  disabled={true}
/>
```

> **Note:** On Android, only `trackColor.true` is used — QmBlurView auto-calculates on/off colors from the base color. `thumbColor` and `trackColor.false` are iOS-only.

### Web Support

The same components and props render on **react-native-web** via CSS `backdrop-filter`, which blurs whatever sits behind the view — exactly like native. No platform branching is required; Metro resolves the `.web` implementations automatically.

```tsx
import { View, Text } from 'react-native';
import {
  BlurView,
  ProgressiveBlurView,
} from '@tanvir1133/react-native-liquid-glass-lite';

function WebExample() {
  return (
    <View style={{ flex: 1, position: 'relative' }}>
      <BlurView
        blurType="light"
        blurAmount={30}
        style={{ position: 'absolute', top: 0, left: 0, right: 0, height: 120 }}
      />

      <ProgressiveBlurView
        blurType="regular"
        blurAmount={40}
        direction="blurredTopClearBottom"
        style={{ marginTop: 140, height: 200 }}
      >
        <Text style={{ color: '#fff', padding: 16 }}>
          Progressive blur, rendered with CSS
        </Text>
      </ProgressiveBlurView>
    </View>
  );
}
```

> **How it works:** `BlurView` uses a single `backdrop-filter: blur() saturate()` layer plus a blur-type tint. `ProgressiveBlurView` stacks several masked `backdrop-filter` layers to reproduce a true blur-radius ramp. Browsers without `backdrop-filter` support degrade gracefully to the tint alone.

---

## Props

### BlurView Props

| Prop | Type | Default | Description |
|---|---|---|---|
| `blurType` | `BlurType` | `'xlight'` | Visual style of the blur |
| `blurAmount` | `number` | `10` | Blur intensity (0–100) |
| `blurRounds` | `number` | `5` | Blur passes, Android only (1–15) |
| `ignoreSafeArea` | `boolean` | `true` | Expand behind safe-area insets (iOS) |
| `reducedTransparencyFallbackColor` | `string` | `'#FFFFFF'` | Fallback when system reduces transparency |
| `overlayColor` | `ColorValue` | — | Tint color layered over the blur |
| `style` | `ViewStyle` | — | Component style |
| `children` | `ReactNode` | — | Content rendered inside the blur |

### VibrancyView Props

| Prop | Type | Default | Description |
|---|---|---|---|
| `blurType` | `BlurType` | `'xlight'` | Blur/vibrancy style |
| `blurAmount` | `number` | `10` | Blur intensity (0–100) |
| `style` | `ViewStyle` | — | Component style |
| `children` | `ReactNode` | — | Content rendered inside |

### ProgressiveBlurView Props

| Prop | Type | Default | Description |
|---|---|---|---|
| `blurType` | `BlurType` | `'regular'` | Visual style |
| `blurAmount` | `number` | `20` | Max blur intensity at blurred edge (0–100) |
| `blurRounds` | `number` | `5` | Android blur passes (1–15) |
| `direction` | `ProgressiveBlurDirection` | `'blurredTopClearBottom'` | Gradient direction |
| `startOffset` | `number` | `0.0` | Gradient start delay (0–1) |
| `reducedTransparencyFallbackColor` | `string` | `'#FFFFFF'` | Fallback color (iOS) |
| `overlayColor` | `ColorValue` | — | Tint layered over blur |
| `style` | `ViewStyle` | — | Component style |
| `children` | `ReactNode` | — | Content rendered inside |

### LiquidGlassView Props

| Prop | Type | Default | Platform |
|---|---|---|---|
| `glassType` | `'clear' \| 'regular'` | `'clear'` | iOS 26+ |
| `glassTintColor` | `string` | `'clear'` | iOS 26+ |
| `glassOpacity` | `number` | `1.0` | iOS 26+ |
| `reducedTransparencyFallbackColor` | `string` | `'#FFFFFF'` | iOS |
| `isInteractive` | `boolean` | `true` | iOS 26+ |
| `ignoreSafeArea` | `boolean` | `false` | iOS |
| `style` | `ViewStyle` | — | All |
| `children` | `ReactNode` | — | All |

### LiquidGlassContainer Props

| Prop | Type | Default | Description |
|---|---|---|---|
| `spacing` | `number` | `0` | Spacing between glass elements (iOS 26+) |
| `style` | `ViewStyle` | — | Component style |
| `children` | `ReactNode` | — | Typically `LiquidGlassView` components |

### BlurSwitch Props

| Prop | Type | Default | Description |
|---|---|---|---|
| `value` | `boolean` | `false` | Controlled value |
| `onValueChange` | `(value: boolean) => void` | — | Change handler |
| `blurAmount` | `number` | `10` | Android blur intensity (0–100) |
| `blurRounds` | `number` | `5` | Android blur passes (1–15) |
| `thumbColor` | `ColorValue` | `'#FFFFFF'` | Thumb color (iOS only) |
| `trackColor` | `{ false?: ColorValue; true?: ColorValue }` | `{false: '#E5E5EA', true: '#34C759'}` | Track colors |
| `disabled` | `boolean` | `false` | Prevents interaction, maintains value |
| `style` | `ViewStyle` | — | Component style |

---

## Blur Types

| Value | Description |
|---|---|
| `xlight` | Extra light |
| `light` | Light |
| `dark` | Dark |
| `extraDark` | Extra dark |
| `regular` | Regular (iOS 10+) |
| `prominent` | Prominent (iOS 10+) |
| `systemUltraThinMaterial` | Ultra thin material (iOS 13+) |
| `systemThinMaterial` | Thin material (iOS 13+) |
| `systemMaterial` | Material (iOS 13+) |
| `systemThickMaterial` | Thick material (iOS 13+) |
| `systemChromeMaterial` | Chrome material (iOS 13+) |
| `systemUltraThinMaterialLight` | Ultra thin light (iOS 13+) |
| `systemThinMaterialLight` | Thin light (iOS 13+) |
| `systemMaterialLight` | Light material (iOS 13+) |
| `systemThickMaterialLight` | Thick light (iOS 13+) |
| `systemChromeMaterialLight` | Chrome light (iOS 13+) |
| `systemUltraThinMaterialDark` | Ultra thin dark (iOS 13+) |
| `systemThinMaterialDark` | Thin dark (iOS 13+) |
| `systemMaterialDark` | Dark material (iOS 13+) |
| `systemThickMaterialDark` | Thick dark (iOS 13+) |
| `systemChromeMaterialDark` | Chrome dark (iOS 13+) |

## Glass Types (LiquidGlassView)

| Value | Description |
|---|---|
| `clear` | Clear glass (default) |
| `regular` | More pronounced glass appearance |

> On Android and iOS < 26, `LiquidGlassView` automatically falls back to an enhanced blur that approximates the glass appearance.

---

## TypeScript Support

```tsx
import {
  BlurView,
  LiquidGlassView,
  LiquidGlassContainer,
  ProgressiveBlurView,
  VibrancyView,
  BlurSwitch,
  type BlurType,
  type GlassType,
  type ProgressiveBlurDirection,
  type BlurViewProps,
  type LiquidGlassViewProps,
  type LiquidGlassContainerProps,
  type ProgressiveBlurViewProps,
  type VibrancyViewProps,
  type BlurSwitchProps,
} from '@tanvir1133/react-native-liquid-glass-lite';

const blurType: BlurType = 'systemMaterial';
const glassType: GlassType = 'regular';
const direction: ProgressiveBlurDirection = 'blurredTopClearBottom';

const blurProps: BlurViewProps = {
  blurType: 'systemMaterial',
  blurAmount: 50,
  blurRounds: 10,
  reducedTransparencyFallbackColor: '#FFFFFF',
  overlayColor: '#FF000040',
};

const glassProps: LiquidGlassViewProps = {
  glassType: 'regular',
  glassTintColor: '#007AFF',
  glassOpacity: 0.8,
  isInteractive: true,
};

const switchProps: BlurSwitchProps = {
  value: true,
  onValueChange: (value) => console.log(value),
  blurAmount: 20,
  trackColor: { true: '#34C759' },
  disabled: false,
};
```

---

## Platform Differences

### iOS

All components are backed by **SwiftUI** for modern performance:

- **BlurView** — `UIVisualEffectView` with precise intensity control (iOS 13+)
- **VibrancyView** — `UIVibrancyEffect` nested inside `UIVisualEffectView`
- **LiquidGlassView** — Native `UIGlassEffect` API (iOS 26+), falls back to `BlurView`
- **LiquidGlassContainer** — Native `UIGlassContainerEffect` (iOS 26+), falls back to `View`
- **ProgressiveBlurView** — Core Animation variable-blur filters

### Android

- **BlurView / ProgressiveBlurView** — QmBlurView with hardware-accelerated RenderScript/Vulkan blur
- **LiquidGlassView** — Falls back to `BlurView` with tint overlay
- **LiquidGlassContainer** — Falls back to plain `View`
- **VibrancyView** — Falls back to `BlurView`
- **BlurSwitch** — Native `BlurSwitchButtonView` from QmBlurView

### Accessibility (Reduce Transparency)

All components respect the OS "Reduce Transparency" setting. Use `reducedTransparencyFallbackColor` to control the solid-color fallback. Accepted values: hex strings and named colors (`black`, `white`, `clear`, `blue`, `green`, `red`, `gray`, `orange`, `purple`, `cyan`, `magenta`, `brown`, `yellow`, `transparent`).

---

## Performance Considerations

- Avoid stacking many blur/glass views on lower-end devices
- Set `reducedTransparencyFallbackColor` for better accessibility
- `LiquidGlassView` and `LiquidGlassContainer` add near-zero overhead when falling back
- All components are optimized for React Native New Architecture (Fabric)
- QmBlurView uses native blur algorithms for optimal Android performance

---

## Example App

The example app demonstrates all components with live prop controls:

- **BlurView Demo** — interactive blur type selector with live preview
- **LiquidGlassView Demo** — customizable glass effects (iOS 26+)
- **LiquidGlassContainer Demo** — spacing animations and grouped glass
- **ProgressiveBlurView Demo** — gradient blur use cases
- **BlurSwitch Demo** — color configurations and disabled state
- **Practical Use Cases** — cards, modals, paywalls

```bash
cd example
yarn install
yarn ios      # or yarn android
```

---

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT © [Tanvir Faysal](https://github.com/tanvirfaysal11)

---

## Credits

**Progressive Blur Implementation:**
- VariableBlur by [@nikstar](https://github.com/nikstar): https://github.com/nikstar/VariableBlur

**Android Blur:**
- QmBlurView library: https://github.com/QmDeve/QmBlurView

Built with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
