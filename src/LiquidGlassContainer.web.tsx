// Web implementation of LiquidGlassContainer.
import React from 'react';
import { View } from 'react-native';
import type { LiquidGlassContainerProps } from './LiquidGlassContainer';

/**
 * Web implementation of LiquidGlassContainer.
 *
 * The iOS 26+ UIGlassContainerEffect (which merges nearby glass views) has no
 * web equivalent, so this renders a plain View — the same fallback the native
 * component uses on Android and older iOS. `spacing` is native-only and
 * accepted for API compatibility.
 */
export const LiquidGlassContainer: React.FC<LiquidGlassContainerProps> = ({
  spacing: _spacing,
  style,
  children,
  ...rest
}) => {
  return (
    <View style={style} {...rest}>
      {children}
    </View>
  );
};

export default LiquidGlassContainer;
