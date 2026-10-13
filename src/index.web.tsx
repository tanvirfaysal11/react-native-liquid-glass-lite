// Web entry point. Metro's platform-extension resolution reaches this file for
// web bundles; the wrapper components resolve to their `.web` implementations,
// while the deprecated raw native components resolve to `.web` shims that keep
// the public API importable without touching codegenNativeComponent.
// Type-only re-exports from the native spec modules are erased at compile time
// and pull in no native code.

export { BlurView as default, BlurView } from './BlurView.web';
export type { BlurViewProps } from './BlurView';

export { LiquidGlassView } from './LiquidGlassView.web';
export type { LiquidGlassViewProps } from './LiquidGlassView';

export { ProgressiveBlurView } from './ProgressiveBlurView.web';
export type { ProgressiveBlurViewProps } from './ProgressiveBlurView';

export { LiquidGlassContainer } from './LiquidGlassContainer.web';
export type { LiquidGlassContainerProps } from './LiquidGlassContainer';

export { BlurSwitch } from './BlurSwitch.web';
export type { BlurSwitchProps } from './BlurSwitch';

export { VibrancyView } from './VibrancyView.web';
export type { VibrancyViewProps } from './VibrancyView';

export { default as ReactNativeBlurView } from './ReactNativeBlurViewNativeComponent.web';
export type { BlurType } from './ReactNativeBlurViewNativeComponent';

export { default as ReactNativeLiquidGlassView } from './ReactNativeLiquidGlassViewNativeComponent.web';
export type { GlassType } from './ReactNativeLiquidGlassViewNativeComponent';

export { default as ReactNativeProgressiveBlurView } from './ReactNativeProgressiveBlurViewNativeComponent.web';
export type { ProgressiveBlurDirection } from './ReactNativeProgressiveBlurViewNativeComponent';

export { default as ReactNativeLiquidGlassContainer } from './ReactNativeLiquidGlassContainerNativeComponent.web';

export { default as ReactNativeBlurSwitch } from './ReactNativeBlurSwitchNativeComponent.web';
export type { ValueChangeEvent } from './ReactNativeBlurSwitchNativeComponent';

export { default as ReactNativeVibrancyView } from './ReactNativeVibrancyViewNativeComponent.web';
