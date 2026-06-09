import React from 'react';
import { Platform, StyleSheet } from 'react-native';
import type { StyleProp, ViewStyle } from 'react-native';
import ReactNativeVibrancyView, {
  type BlurType,
} from './ReactNativeVibrancyViewNativeComponent';
import BlurView from './BlurView';

export interface VibrancyViewProps {
  /**
   * Blur style that drives the vibrancy effect.
   * @default 'xlight'
   */
  blurType?: BlurType;
  /**
   * Blur intensity (0–100).
   * @default 10
   */
  blurAmount?: number;
  style?: StyleProp<ViewStyle>;
  children?: React.ReactNode;
}

/**
 * A UIVisualEffectView with a `UIVibrancyEffect` applied to children (iOS).
 *
 * Vibrancy makes content legible on top of blurred backgrounds by adapting
 * foreground colors to the content behind the blur layer.
 *
 * Falls back to a plain `BlurView` on Android.
 *
 * @platform iOS (fallback on Android)
 *
 * @example
 * ```tsx
 * <VibrancyView blurType="light" style={{ flex: 1 }}>
 *   <Text style={{ color: 'white' }}>Vibrant text</Text>
 * </VibrancyView>
 * ```
 */
export const VibrancyView: React.FC<VibrancyViewProps> = ({
  blurType = 'xlight',
  blurAmount = 10,
  style,
  children,
  ...rest
}) => {
  if (Platform.OS === 'android') {
    return (
      <BlurView blurType={blurType} blurAmount={blurAmount} style={style} {...rest}>
        {children}
      </BlurView>
    );
  }

  return (
    <ReactNativeVibrancyView
      blurType={blurType}
      blurAmount={blurAmount}
      style={[styles.transparent, style]}
      {...rest}
    >
      {children}
    </ReactNativeVibrancyView>
  );
};

export default VibrancyView;

const styles = StyleSheet.create({
  transparent: {
    backgroundColor: 'transparent',
  },
});
