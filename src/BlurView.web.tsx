// Web implementation of BlurView using CSS backdrop-filter.
import React from 'react';
import { StyleSheet, View } from 'react-native';
import type { BlurViewProps } from './BlurView';
import { getBlurLayerStyle, useBackdropFilterSupport } from './webBlurUtils';

/**
 * Web implementation of BlurView using CSS `backdrop-filter`.
 *
 * Renders a container with an absolutely-filled layer carrying
 * `backdrop-filter: blur() saturate()` plus a semi-transparent tint derived
 * from `blurType`, approximating the native UIVisualEffectView look. Children
 * render above the blur, sharp. In browsers without backdrop-filter support
 * (and during server-side rendering) only the tint remains.
 *
 * `blurRounds`, `reducedTransparencyFallbackColor` and `ignoreSafeArea` are
 * native-only and accepted for API compatibility.
 */
export const BlurView: React.FC<BlurViewProps> = ({
  blurType = 'xlight',
  blurAmount = 10,
  overlayColor,
  style,
  children,
  blurRounds: _blurRounds,
  reducedTransparencyFallbackColor: _reducedTransparencyFallbackColor,
  ignoreSafeArea: _ignoreSafeArea,
  ...rest
}) => {
  const hasBackdropFilter = useBackdropFilterSupport();

  return (
    <View style={[styles.container, style]} {...rest}>
      {/* Blur sits absolutely behind content; the tint and children paint above
          it via sibling order. */}
      <View
        style={[
          StyleSheet.absoluteFill,
          styles.layer,
          getBlurLayerStyle(blurType, blurAmount, hasBackdropFilter),
        ]}
      />
      {overlayColor != null && (
        <View
          style={[
            StyleSheet.absoluteFill,
            styles.layer,
            { backgroundColor: overlayColor },
          ]}
        />
      )}
      {children}
    </View>
  );
};

export default BlurView;

const styles = StyleSheet.create({
  container: {
    position: 'relative',
    overflow: 'hidden',
  },
  layer: {
    pointerEvents: 'none',
  },
});
