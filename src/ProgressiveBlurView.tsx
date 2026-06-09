import React, { Children } from 'react';
import { StyleSheet, View } from 'react-native';
import type { ColorValue, StyleProp, ViewStyle } from 'react-native';
import ReactNativeProgressiveBlurView, {
  type BlurType,
  type ProgressiveBlurDirection,
} from './ReactNativeProgressiveBlurViewNativeComponent';

export interface ProgressiveBlurViewProps {
  /**
   * The visual style of the blur.
   * @default 'regular'
   */
  blurType?: BlurType;

  /**
   * Maximum blur intensity at the most-blurred edge (0–100).
   * @default 20
   */
  blurAmount?: number;

  /**
   * Number of blur passes for smoother gradients (Android only, 1–15).
   * @default 5
   */
  blurRounds?: number;

  /**
   * Direction of the blur gradient:
   * - `'blurredTopClearBottom'` – blurred at the top, transparent at the bottom
   * - `'blurredBottomClearTop'` – blurred at the bottom, transparent at the top
   * - `'blurredCenterClearTopAndBottom'` – blurred in the center, clear at both edges
   * @default 'blurredTopClearBottom'
   */
  direction?: ProgressiveBlurDirection;

  /**
   * Fractional offset (0–1) where the gradient transition begins.
   * `0` starts immediately; `1` delays the transition to the far edge.
   * @default 0.0
   */
  startOffset?: number;

  /**
   * Fallback color when reduced transparency is enabled.
   * @default '#FFFFFF'
   * @platform iOS
   */
  reducedTransparencyFallbackColor?: string;

  /** Optional tint layered on top of the blur. */
  overlayColor?: ColorValue;

  style?: StyleProp<ViewStyle>;
  children?: React.ReactNode;
}

/**
 * A blur view whose intensity gradually fades across the view.
 *
 * - **iOS** – Backed by Core Animation variable-blur filters.
 * - **Android** – Implemented via `BlurView` + `LinearGradient` mask.
 *
 * When children are provided the blur layer sits absolutely behind them.
 *
 * @example
 * ```tsx
 * <ProgressiveBlurView
 *   blurType="light"
 *   blurAmount={30}
 *   direction="blurredTopClearBottom"
 *   style={{ height: 200 }}
 * >
 *   <Text>Content</Text>
 * </ProgressiveBlurView>
 * ```
 */
export const ProgressiveBlurView: React.FC<ProgressiveBlurViewProps> = ({
  blurType = 'regular',
  blurAmount = 20,
  blurRounds = 5,
  direction = 'blurredTopClearBottom',
  startOffset = 0.0,
  reducedTransparencyFallbackColor = '#FFFFFF',
  overlayColor,
  style,
  children,
  ...rest
}) => {
  const overlayStyle = overlayColor ? { backgroundColor: overlayColor } : undefined;

  const nativeProps = {
    blurType,
    blurAmount,
    blurRounds,
    direction,
    startOffset,
    reducedTransparencyFallbackColor,
  };

  if (!Children.count(children)) {
    return (
      <ReactNativeProgressiveBlurView
        style={[style, overlayStyle]}
        {...nativeProps}
        {...rest}
      />
    );
  }

  return (
    <View style={[styles.container, style, overlayStyle]}>
      <ReactNativeProgressiveBlurView
        style={StyleSheet.absoluteFill}
        {...nativeProps}
        {...rest}
      />
      {children}
    </View>
  );
};

export default ProgressiveBlurView;

const styles = StyleSheet.create({
  container: {
    overflow: 'hidden',
  },
});
