import React, { Children } from 'react';
import { Platform, StyleSheet, View } from 'react-native';
import type { ColorValue, StyleProp, ViewStyle } from 'react-native';
import ReactNativeBlurView, {
  type BlurType,
} from './ReactNativeBlurViewNativeComponent';

export interface BlurViewProps {
  /** The visual style of the blur effect. @default 'xlight' */
  blurType?: BlurType;
  /** Blur intensity (0–100). @default 10 */
  blurAmount?: number;
  /** Number of blur passes for smoother results (Android). @default 5 */
  blurRounds?: number;
  /**
   * Solid fallback color shown when the user has "Reduce Transparency" enabled
   * (iOS) or on older OS versions that do not support blur.
   * @default '#FFFFFF'
   */
  reducedTransparencyFallbackColor?: string;
  /** Optional tint layered on top of the blur. */
  overlayColor?: ColorValue;
  style?: StyleProp<ViewStyle>;
  /** When true the view expands behind safe-area insets (iOS). @default true */
  ignoreSafeArea?: boolean;
  children?: React.ReactNode;
}

/**
 * A cross-platform blur view backed by native implementations.
 *
 * - **iOS** – UIVisualEffectView (UIBlurEffect)
 * - **Android** – QmBlurView (high-performance RenderScript/Vulkan blur)
 *
 * When `children` are provided the component wraps them in an absolutely-
 * positioned blur layer so interactive children remain tappable.
 */
export const BlurView: React.FC<BlurViewProps> = ({
  blurType = 'xlight',
  blurAmount = 10,
  blurRounds = 5,
  reducedTransparencyFallbackColor = '#FFFFFF',
  overlayColor,
  style,
  children,
  ignoreSafeArea = true,
  ...rest
}) => {
  const overlayStyle = overlayColor ? { backgroundColor: overlayColor } : undefined;

  const nativeProps = {
    blurType,
    blurAmount,
    blurRounds,
    ignoreSafeArea,
    reducedTransparencyFallbackColor,
  };

  // No children – render the native view as a standalone background layer.
  if (!Children.count(children)) {
    return (
      <ReactNativeBlurView
        style={[style, overlayStyle]}
        {...nativeProps}
        {...rest}
      />
    );
  }

  // Android supports children directly inside the native view.
  if (Platform.OS === 'android') {
    return (
      <ReactNativeBlurView style={style} {...nativeProps} {...rest}>
        {overlayStyle && <View style={[StyleSheet.absoluteFill, overlayStyle]} />}
        {children}
      </ReactNativeBlurView>
    );
  }

  // iOS: wrap in a plain View so the blur sits behind the children.
  return (
    <View style={[styles.container, style, overlayStyle]}>
      <ReactNativeBlurView
        style={StyleSheet.absoluteFill}
        {...nativeProps}
        {...rest}
      />
      {children}
    </View>
  );
};

export default BlurView;

const styles = StyleSheet.create({
  container: {
    overflow: 'hidden',
  },
});
